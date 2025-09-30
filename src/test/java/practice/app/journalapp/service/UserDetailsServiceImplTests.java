package practice.app.journalapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_returnsUserDetails_whenUserExists() {

        User user = User.builder()
                .username("ram")
                .password("safoiwhesr")
                .role(Arrays.asList("USER"))
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        UserDetails result = service.loadUserByUsername("ram");

        assertNotNull(result);
        assertEquals("ram", result.getUsername());
        assertEquals("safoiwhesr", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_throwsException_whenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("shyam"));
    }
}
