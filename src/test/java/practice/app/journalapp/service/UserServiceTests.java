package practice.app.journalapp.service;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.repository.UserRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(new ObjectId())
                .username("testuser")
                .password("plain123")
                .build();
    }

    @Test
    void saveEntry_ShouldEncodePasswordAndSetRole() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        service.saveEntry(user);

        assertThat(user.getPassword()).isNotEqualTo("plain123"); // encoded
        assertThat(user.getRole()).containsExactly("USER");
        verify(userRepository).save(user);
    }

    @Test
    void saveAdmin_ShouldEncodePasswordAndSetAdminRole() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = service.saveAdmin(user);

        assertThat(result.getPassword()).isNotEqualTo("plain123");
        assertThat(result.getRole()).containsExactly("ADMIN", "USER");
        verify(userRepository).save(user);
    }

    @Test
    void getAll_ShouldReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = service.getAll();

        assertThat(result).hasSize(1).contains(user);
        verify(userRepository).findAll();
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User found = service.findById(user.getId());

        assertThat(found).isEqualTo(user);
    }

    @Test
    void findById_ShouldReturnNull_WhenNotExists() {
        when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

        User found = service.findById(new ObjectId());

        assertThat(found).isNull();
    }

    @Test
    void deleteById_ShouldCallRepository() {
        service.deleteById(user.getId());

        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void updatePassword_ShouldEncodePassword_WhenUserFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        User input = User.builder()
                .username("testuser")
                .password("newpass")
                .build();

        User result = service.updatePassword(input);

        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isNotEqualTo("newpass"); // encoded
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_ShouldReturnNull_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        User input = User.builder()
                .username("ghost")
                .password("whatever")
                .build();

        User result = service.updatePassword(input);

        assertThat(result).isNull();
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdateUsernameAndPassword_WhenProvided() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        User update = User.builder()
                .username("newname")
                .password("newpassword")
                .build();

        User result = service.updateUser(update, "testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newname");
        assertThat(result.getPassword()).isNotEqualTo("newpassword"); // encoded
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldUpdateUsernameOnly_WhenPasswordBlank() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        String oldPassword = user.getPassword();

        User update = User.builder()
                .username("newname")
                .password("") // blank
                .build();

        User result = service.updateUser(update, "testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newname");
        assertThat(result.getPassword()).isEqualTo(oldPassword);
        verify(userRepository).save(user);
    }



    @Test
    void updateUser_ShouldReturnNull_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        User update = User.builder()
                .username("newname")
                .password("234")

                .build();

        User result = service.updateUser(update, "ghost");

        assertThat(result).isNull();
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        User found = service.findByUsername("testuser");

        assertThat(found).isEqualTo(user);
    }
}
