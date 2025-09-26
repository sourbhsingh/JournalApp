package practice.app.journalapp.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.service.JournalEntryService;
import practice.app.journalapp.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    private JournalEntryService journalEntryService ;

    @Autowired
    private UserService userService;

    @GetMapping("get-all")
    public ResponseEntity<List<JournalEntry>> getAll(){
        List<JournalEntry> e =journalEntryService.getAll();
        if(e.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(e);
    }

    @GetMapping()
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        List<JournalEntry> e = user.getJournalEntries();
        if(e.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(e);
    }

    @PostMapping()
    public JournalEntry createEntry(@RequestBody JournalEntry journalEntry){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        journalEntryService.saveEntry(journalEntry , username);
        return journalEntry ;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(n-> n.getId().equals(id)).toList();
        if(!collect.isEmpty()) {
            JournalEntry je = journalEntryService.findById(id);
            return ResponseEntity.ok(je);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}}")
    public void deleteEntryById(@PathVariable ObjectId id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        journalEntryService.deleteById(id,username);
        ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<JournalEntry> updateEntry(@PathVariable ObjectId id , @RequestBody JournalEntry updateEntry){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(n-> n.getId().equals(id)).toList();
        if(!collect.isEmpty()) {
            JournalEntry je = journalEntryService.updateEntry(id,updateEntry);
            return ResponseEntity.ok(je);
        }
        return ResponseEntity.notFound().build();
    }
}
