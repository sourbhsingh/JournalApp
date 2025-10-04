package practice.app.journalapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.UserRepository;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found with Username :{}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
            try {
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole().stream()
                                .map(String::toUpperCase)   // ensure uppercase
                                .toArray(String[]::new))
                        .build();
                return userDetails;
            }
            catch(Exception e){
                log.error("Error Occured for user : {}",username,e);
                throw e;
            }

    }
}
