package practice.app.journalapp.service;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.UserRepository;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveEntry(User user)
    {
        try {
            log.info("Saving user: {}", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(List.of("USER"));
            userRepository.save(user);
            log.info("User saved successfully: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Error saving user: {}", user != null ? user.getUsername() : "null", e);
        }
    }

public List<User> getAll(){
   log.info("Fetching All Users");
   try {
       return userRepository.findAll();
   }
   catch (Exception e){
       log.error("Error Fetching User List" ,e);
       return java.util.Collections.emptyList();
   }
}

    public User findById(ObjectId id) {
        log.info("Finding user by id: {}", id);
        try {
            return userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Error finding user by id: {}", id, e);
            return null;
        }
    }

    public void deleteById(ObjectId id) {
        try {
            log.info("Deleting user by id: {}", id);
            userRepository.deleteById(id);
            log.info("User deleted successfully: {}", id);
        } catch (Exception e) {
            log.error("Error deleting user by id: {}", id, e);
        }
    }

    public User updatePassword(User user) {
        User old = null;
        try {
            log.info("Updating password for user: {}", user.getUsername());
            old = userRepository.findByUsername(user.getUsername());
            if (old != null) {
                old.setPassword(passwordEncoder.encode(user.getPassword())); //  encode!
                userRepository.save(old);
                log.info("Password updated successfully for user: {}", user.getUsername());
            } else {
                log.warn("User not found for password update: {}", user.getUsername());
            }
        } catch (Exception e) {
            log.error("Error updating password for user: {}", user != null ? user.getUsername() : "null", e);
        }
        return old;
    }

    public User updateUser(User user, String username) {
        User old = null;
        try {
            log.info("Updating user: {}", username);
            old = userRepository.findByUsername(username);
            if (old != null) {
                old.setUsername(user.getUsername());
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    old.setPassword(passwordEncoder.encode(user.getPassword())); //  encode
                }
                userRepository.save(old);
                log.info("User updated successfully: {}", username);
            } else {
                log.warn("User not found for update: {}", username);
            }
        } catch (Exception e) {
            log.error("Error updating user: {}", username, e);
        }
        return old;
    }


    public User findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            log.error("Error finding user by username: {}", username, e);
            return null;
        }
    }

    public User saveAdmin(User user) {
        try {
            log.info("Saving admin user: {}", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Arrays.asList("ADMIN","USER"));
            User savedUser = userRepository.save(user);
            log.info("Admin user saved successfully: {}", user.getUsername());
            return savedUser;
        } catch (Exception e) {
            log.error("Error saving admin user: {}", user != null ? user.getUsername() : "null", e);
            return null;
        }
    }
}
