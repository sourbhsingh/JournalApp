package practice.app.journalapp.service;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.app.journalapp.dto.JournalEntryDTO;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.mappers.JournalMapper;
import practice.app.journalapp.repository.JournalEntryRepository;
import practice.app.journalapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalEntryService {

    private static final Logger logger = LoggerFactory.getLogger(JournalEntryService.class);

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    private static final Long CACHE_TTL_SECONDS = 3600L; // 1 hour

    private static String journalKey(ObjectId id) {
        return "journal:" + id.toString();
    }

    private static String userJournalsKey(String username) {
        return "journals:user:" + username;
    }

    @Transactional
    public void saveEntry(List<JournalEntry> journalEntry, String username) {
        try {
            User user = userRepository.findByUsername(username);
            List<JournalEntry> newJournalEntry = new ArrayList<>();
            for (JournalEntry je : journalEntry) {
                je.setDate(LocalDateTime.now());
                newJournalEntry.add(je);
            }

            List<JournalEntry> saved = journalEntryRepository.saveAll(newJournalEntry);
            user.getJournalEntries().addAll(saved);
            userRepository.save(user);

            // Invalidate user's journal cache
            redisService.delete(userJournalsKey(username));
        } catch (Exception e) {
            logger.error("Error saving journal entry for user: {}", username, e);
            throw e;
        }
    }

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userRepository.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userRepository.save(user);

            // Invalidate caches
            redisService.delete(userJournalsKey(username));
            redisService.delete(journalKey(saved.getId()));
        } catch (Exception e) {
            logger.error("Error saving journal entry for user: {}", username, e);
            throw e;
        }
    }

    public List<JournalEntry> getAll() {
        try {
            String key = "journals:all";

            // Check cache (DTOs)
            List<JournalEntryDTO> cachedDTOs = redisService.get(key);
            if (cachedDTOs != null) {
                return cachedDTOs.stream()
                        .map(JournalMapper::toEntity)
                        .collect(Collectors.toList());
            }

            // Fetch from DB
            List<JournalEntry> allEntries = journalEntryRepository.findAll();

            // Convert to DTOs for caching
            List<JournalEntryDTO> dtoList = allEntries.stream()
                    .map(JournalMapper::toDTO)
                    .collect(Collectors.toList());

            redisService.set(key, dtoList, CACHE_TTL_SECONDS);
            return allEntries;

        } catch (Exception e) {
            logger.error("Error fetching all journal entries", e);
            throw e;
        }
    }

    public JournalEntry findById(ObjectId id) {
        try {
            String key = journalKey(id);

            // Try cache
            JournalEntryDTO cachedDTO = redisService.get(key);
            if (cachedDTO != null) {
                return JournalMapper.toEntity(cachedDTO);
            }

            // Fallback: DB
            JournalEntry entry = journalEntryRepository.findById(id).orElse(null);
            if (entry != null) {
                redisService.set(key, JournalMapper.toDTO(entry), CACHE_TTL_SECONDS);
            }
            return entry;

        } catch (Exception e) {
            logger.error("Error finding journal entry by id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public void deleteById(ObjectId id, String username) {
        try {
            User user = userRepository.findByUsername(username);
            user.getJournalEntries().removeIf(n -> n.getId().equals(id));
            userRepository.save(user);
            journalEntryRepository.deleteById(id);

            // Invalidate caches
            redisService.delete(journalKey(id));
            redisService.delete(userJournalsKey(username));

        } catch (Exception e) {
            logger.error("Error deleting journal entry with id: {} for user: {}", id, username, e);
            throw e;
        }
    }

    @Transactional
    public JournalEntry updateEntry(ObjectId id, JournalEntry updateEntry, String username) {
        try {
            JournalEntry oldEntry = journalEntryRepository.findById(id).orElse(null);
            if (oldEntry != null) {
                oldEntry.setTitle(
                        (updateEntry.getTitle() != null && !updateEntry.getTitle().isBlank())
                                ? updateEntry.getTitle()
                                : oldEntry.getTitle()
                );
                oldEntry.setContent(
                        (updateEntry.getContent() != null && !updateEntry.getContent().isBlank())
                                ? updateEntry.getContent()
                                : oldEntry.getContent()
                );
                journalEntryRepository.save(oldEntry);

                // Invalidate caches
                redisService.delete(journalKey(id));
                redisService.delete(userJournalsKey(username));
            }
            return oldEntry;

        } catch (Exception e) {
            logger.error("Error updating journal entry with id: {}", id, e);
            throw e;
        }
    }
}
