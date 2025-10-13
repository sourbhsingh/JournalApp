package practice.app.journalapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import practice.app.journalapp.dto.UserDTO;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.mappers.UserMapper;
import practice.app.journalapp.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Long CACHE_TTL_SECONDS = 3600L; // 1 hour

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    private static String userKey(ObjectId id) {
        return "user:" + id.toHexString();
    }

    private static String usernameKey(String username) {
        return "username:" + username;
    }

    private static final String ALL_USERS_KEY = "users:all";

    /** Save a normal user */
    public void saveEntry(User user) {
        try {
            log.info("Saving user: {}", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(List.of("USER"));
            userRepository.save(user);
            log.info("User saved successfully: {}", user.getUsername());

            // Invalidate caches
            redisService.delete(userKey(user.getId()));
            redisService.delete(usernameKey(user.getUsername()));
            redisService.delete(ALL_USERS_KEY);

        } catch (Exception e) {
            log.error("Error saving user: {}", user != null ? user.getUsername() : "null", e);
        }
    }

    /** Get all users */
    public List<User> getAll() {
        log.info("Fetching all users");
        try {
            // Use TypeReference to read List<UserDTO> safely
            List<UserDTO> cachedDTOs = redisService.get(ALL_USERS_KEY, new TypeReference<List<UserDTO>>() {});

            if (cachedDTOs != null) {
                log.info("Loaded users from Redis cache");
                return cachedDTOs.stream()
                        .map(UserMapper::toEntity)
                        .toList();
            }

            // Fetch from DB if not in cache
            List<User> users = userRepository.findAll();
            if (users != null && !users.isEmpty()) {
                List<UserDTO> dtos = users.stream()
                        .map(UserMapper::toDTO)
                        .toList();
                redisService.set(ALL_USERS_KEY, dtos, CACHE_TTL_SECONDS);
            }
            return users;
        } catch (Exception e) {
            log.error("Error fetching user list", e);
            return Collections.emptyList();
        }
    }


    /** Find by ID */
    public User findById(ObjectId id) {
        String key = userKey(id);
        log.info("Finding user by id: {}", id);
        try {
            UserDTO cachedDTO = redisService.get(key);
            if (cachedDTO != null) return UserMapper.toEntity(cachedDTO);

            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                redisService.set(key, UserMapper.toDTO(user), CACHE_TTL_SECONDS);
            }
            return user;
        } catch (Exception e) {
            log.error("Error finding user by id: {}", id, e);
            return null;
        }
    }

    /** Find by username */
    public User findByUsername(String username) {
        String key = usernameKey(username);
        log.info("Finding user by username: {}", username);
        try {
            UserDTO cachedDTO = redisService.get(key);
            if (cachedDTO != null) return UserMapper.toEntity(cachedDTO);

            User user = userRepository.findByUsername(username);
            if (user != null) {
                redisService.set(key, UserMapper.toDTO(user), CACHE_TTL_SECONDS);
            }
            return user;
        } catch (Exception e) {
            log.error("Error finding user by username: {}", username, e);
            return null;
        }
    }

    /** Update password */
    public User updatePassword(User user) {
        try {
            log.info("Updating password for user: {}", user.getUsername());
            User existing = userRepository.findByUsername(user.getUsername());
            if (existing != null) {
                existing.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(existing);
                log.info("Password updated successfully for user: {}", user.getUsername());

                // Invalidate caches
                redisService.delete(userKey(existing.getId()));
                redisService.delete(usernameKey(existing.getUsername()));
                redisService.delete(ALL_USERS_KEY);
                return existing;
            } else {
                log.warn("User not found for password update: {}", user.getUsername());
                return null;
            }
        } catch (Exception e) {
            log.error("Error updating password for user: {}", user != null ? user.getUsername() : "null", e);
            return null;
        }
    }

    /** Update user info */
    public User updateUser(User user, String username) {
        try {
            log.info("Updating user: {}", username);
            User existing = userRepository.findByUsername(username);
            if (existing != null) {
                if (user.getUsername() != null && !user.getUsername().isBlank()) {
                    existing.setUsername(user.getUsername());
                }
                if (user.getPassword() != null && !user.getPassword().isBlank()) {
                    existing.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                userRepository.save(existing);
                log.info("User updated successfully: {}", username);

                // Invalidate caches
                redisService.delete(userKey(existing.getId()));
                redisService.delete(usernameKey(existing.getUsername()));
                redisService.delete(ALL_USERS_KEY);
                return existing;
            } else {
                log.warn("User not found for update: {}", username);
                return null;
            }
        } catch (Exception e) {
            log.error("Error updating user: {}", username, e);
            return null;
        }
    }

    /** Delete by ID */
    public void deleteById(ObjectId id) {
        try {
            log.info("Deleting user by id: {}", id);
            User existing = userRepository.findById(id).orElse(null);
            userRepository.deleteById(id);
            log.info("User deleted successfully: {}", id);

            // Invalidate caches
            redisService.delete(userKey(id));
            if (existing != null) redisService.delete(usernameKey(existing.getUsername()));
            redisService.delete(ALL_USERS_KEY);

        } catch (Exception e) {
            log.error("Error deleting user by id: {}", id, e);
        }
    }

    /** Save admin */
    public User saveAdmin(User user) {
        try {
            log.info("Saving admin user: {}", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Arrays.asList("ADMIN", "USER"));
            User savedUser = userRepository.save(user);
            log.info("Admin user saved successfully: {}", user.getUsername());

            // Invalidate caches
            redisService.delete(userKey(savedUser.getId()));
            redisService.delete(usernameKey(savedUser.getUsername()));
            redisService.delete(ALL_USERS_KEY);

            return savedUser;
        } catch (Exception e) {
            log.error("Error saving admin user: {}", user != null ? user.getUsername() : "null", e);
            return null;
        }
    }
}
