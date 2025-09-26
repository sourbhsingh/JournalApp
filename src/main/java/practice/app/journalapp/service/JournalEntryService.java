package practice.app.journalapp.service;

import org.bson.types.ObjectId;
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
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
public void saveEntry(JournalEntry journalEntry, String username)
{
    User user = userRepository.findByUsername(username);
    journalEntry.setDate(LocalDateTime.now());
    JournalEntry saved =journalEntryRepository.save(journalEntry);
    user.getJournalEntries().add(saved);
    userRepository.save(user);

}

public List<JournalEntry> getAll(){
   return  journalEntryRepository.findAll();
}

    public JournalEntry findById(ObjectId id) {
    return journalEntryRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteById(ObjectId id, String username) {
     User user = userRepository.findByUsername(username);
     user.getJournalEntries().removeIf(n->n.getId().equals(id));
     userRepository.save(user);
     journalEntryRepository.deleteById(id);
    }

    @Transactional
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
