package practice.app.journalapp.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import practice.app.journalapp.dto.JournalEntryDTO;
import practice.app.journalapp.dto.UserDTO;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.mappers.JournalMapper;
import practice.app.journalapp.mappers.JournalMapper;
import practice.app.journalapp.mappers.UserMapper;
import practice.app.journalapp.service.JournalEntryService;
import practice.app.journalapp.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    /** Get all journal entries in system */
    @GetMapping("/get-all")
    public ResponseEntity<List<JournalEntryDTO>> getAll() {
        List<JournalEntry> entries = journalEntryService.getAll();
        if (entries.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        List<JournalEntryDTO> dtos = entries.stream()
                .map(JournalMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** Get journal entries of the logged-in user */
    @GetMapping
    public ResponseEntity<List<JournalEntryDTO>> getAllJournalEntriesOfUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null || user.getJournalEntries().isEmpty())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        List<JournalEntryDTO> dtos = user.getJournalEntries().stream()
                .map(JournalMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** Create multiple journal entries for the logged-in user */
    @PostMapping("/add-all")
    public ResponseEntity<List<JournalEntryDTO>> createMultipleEntry(@RequestBody List<JournalEntryDTO> journalEntryDTOs) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<JournalEntry> entities = journalEntryDTOs.stream()
                .map(JournalMapper::toEntity)
                .collect(Collectors.toList());
        journalEntryService.saveEntry(entities, username);

        List<JournalEntryDTO> savedDTOs = entities.stream()
                .map(JournalMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(savedDTOs);
    }

    /** Create a single journal entry for the logged-in user */
    @PostMapping
    public ResponseEntity<JournalEntryDTO> createEntry(@RequestBody JournalEntryDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        JournalEntry entity = JournalMapper.toEntity(dto);
        journalEntryService.saveEntry(entity, username);
        return ResponseEntity.ok(JournalMapper.toDTO(entity));
    }

    /** Get a single journal entry by ID */
    @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntryDTO> getJournalEntryById(@PathVariable ObjectId myId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();

        boolean belongsToUser = user.getJournalEntries().stream().anyMatch(j -> j.getId().equals(myId));
        if (!belongsToUser) return ResponseEntity.notFound().build();

        JournalEntry je = journalEntryService.findById(myId);
        return ResponseEntity.ok(JournalMapper.toDTO(je));
    }

    /** Delete a journal entry by ID */
    @DeleteMapping("/id/{myId}")
    public ResponseEntity<Void> deleteEntryById(@PathVariable ObjectId myId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        journalEntryService.deleteById(myId, username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /** Update a journal entry by ID */
    @PutMapping("/id/{myId}")
    public ResponseEntity<JournalEntryDTO> updateEntry(@PathVariable ObjectId myId,
                                                       @RequestBody JournalEntryDTO updateDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();

        boolean belongsToUser = user.getJournalEntries().stream().anyMatch(j -> j.getId().equals(myId));
        if (!belongsToUser) return ResponseEntity.notFound().build();

        JournalEntry updatedEntity = JournalMapper.toEntity(updateDTO);
        JournalEntry updated = journalEntryService.updateEntry(myId, updatedEntity, username);
        return ResponseEntity.ok(JournalMapper.toDTO(updated));
    }
}
