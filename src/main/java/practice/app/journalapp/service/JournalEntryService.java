package practice.app.journalapp.service;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.JournalEntryRepository;
import practice.app.journalapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalEntryService {
    private static final Logger logger = LoggerFactory.getLogger(JournalEntryService.class);

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userRepository.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Error saving journal entry for user: {}", username, e);
            throw e;
        }
    }

    public List<JournalEntry> getAll() {
        try {
            return journalEntryRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all journal entries", e);
            throw e;
        }
    }

    public JournalEntry findById(ObjectId id) {
        try {
            return journalEntryRepository.findById(id).orElse(null);
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
        } catch (Exception e) {
            logger.error("Error deleting journal entry with id: {} for user: {}", id, username, e);
            throw e;
        }
    }

    @Transactional
    public JournalEntry updateEntry(ObjectId id, JournalEntry updateEntry) {
        try {
            JournalEntry oldEntry = journalEntryRepository.findById(id).orElse(null);
            if (oldEntry != null) {
                oldEntry.setTitle((updateEntry.getTitle() != null && !updateEntry.getTitle().isBlank()) ? updateEntry.getTitle() : oldEntry.getTitle());
                oldEntry.setContent((updateEntry.getContent() != null && !updateEntry.getContent().isBlank()) ? updateEntry.getContent() : oldEntry.getContent());
                journalEntryRepository.save(oldEntry);
            }
            return oldEntry;
        } catch (Exception e) {
            logger.error("Error updating journal entry with id: {}", id, e);
            throw e;
        }
    }
}