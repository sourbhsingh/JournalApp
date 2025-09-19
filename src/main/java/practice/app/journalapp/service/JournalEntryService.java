package practice.app.journalapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.repository.JournalEntryRepository;

import java.util.List;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

public void saveEntry(JournalEntry journalEntry)
{  journalEntryRepository.save(journalEntry);
}

public List<JournalEntry> getAll(){
   return  journalEntryRepository.findAll();
}

    }