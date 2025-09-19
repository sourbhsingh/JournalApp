package practice.app.journalapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.service.JournalEntryService;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    private JournalEntryService journalEntryService ;

    @GetMapping
    public List<JournalEntry> getAll(){
        return journalEntryService.getAll();
    }

    @PostMapping
    public Boolean createEntry(@RequestBody JournalEntry journalEntry){
         journalEntryService.saveEntry(journalEntry);
         return true ;
    }

}
