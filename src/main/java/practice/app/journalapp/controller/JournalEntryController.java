package practice.app.journalapp.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.service.JournalEntryService;
import practice.app.journalapp.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    private JournalEntryService journalEntryService ;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAll(){
        List<JournalEntry> e =journalEntryService.getAll();
        if(e.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(e);
    }
    @GetMapping("/{username}")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser(@PathVariable String username){
        User user = userService.findByUsername(username);
        List<JournalEntry> e = user.getJournalEntries();
        if(e.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(e);
    }

    @PostMapping("/{username}")
    public JournalEntry createEntry(@PathVariable String username,@RequestBody JournalEntry journalEntry){
        journalEntryService.saveEntry(journalEntry , username);
        return journalEntry ;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId id){
       JournalEntry je =  journalEntryService.findById(id);
       if(je==null) return ResponseEntity.notFound().build();
       return ResponseEntity.ok(je);
    }

    @DeleteMapping("/id/{id}/{username}")
    public void deleteEntryById(@PathVariable ObjectId id , @PathVariable String username){
        journalEntryService.deleteById(id,username);
        ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/id/{id}/{username}")
    public ResponseEntity<JournalEntry> updateEntry(@PathVariable ObjectId id , @RequestBody JournalEntry updateEntry){
        JournalEntry js = journalEntryService.updateEntry(id,updateEntry);
        if(js==null) return ResponseEntity.notFound().build();
        return ResponseEntity.status(HttpStatus.OK).body(js);
    }
}
