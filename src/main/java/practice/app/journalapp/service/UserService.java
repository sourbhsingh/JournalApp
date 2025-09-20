package practice.app.journalapp.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.app.journalapp.entity.JournalEntry;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.JournalEntryRepository;
import practice.app.journalapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

public void saveEntry(User user)
{
    userRepository.save(user);
}

public List<User> getAll(){
   return  userRepository.findAll();
}

    public User findById(ObjectId id) {
    return userRepository.findById(id).orElse(null);
    }

    public void deleteById(ObjectId id) {
     userRepository.deleteById(id);
    }

    public User updatePassword( User user) {
      User old= userRepository.findByUsername(user.getUsername());
      if(old!=null){
          old.setPassword(user.getPassword());
          userRepository.save(old);
      }
        return old;
    }

    public User findByUsername(String username) {
    return userRepository.findByUsername(username);

    }
}
