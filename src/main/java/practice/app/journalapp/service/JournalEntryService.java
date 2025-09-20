package practice.app.journalapp.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.repository.JournalEntryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

public void saveEntry(JournalEntry journalEntry)
{
    journalEntry.setDate(LocalDateTime.now());
    journalEntryRepository.save(journalEntry);
}

public List<JournalEntry> getAll(){
   return  journalEntryRepository.findAll();
}

    public JournalEntry findById(ObjectId id) {
    return journalEntryRepository.findById(id).orElse(null);
    }

    public void deleteById(ObjectId id) {
     journalEntryRepository.deleteById(id);
    }

    public JournalEntry updateEntry(ObjectId id, JournalEntry updateEntry) {
      JournalEntry oldEntry= journalEntryRepository.findById(id).orElse(null);
      if(oldEntry!=null){
          oldEntry.setTitle( (updateEntry.getTitle()!=null && !updateEntry.getTitle().isBlank())? updateEntry.getTitle() : oldEntry.getTitle());
          oldEntry.setContent( (updateEntry.getContent()!=null && !updateEntry.getContent().isBlank())? updateEntry.getContent() : oldEntry.getContent());
          journalEntryRepository.save(oldEntry);
      }

        return oldEntry;
    }
}
