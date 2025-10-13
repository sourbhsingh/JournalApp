package practice.app.journalapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.app.journalapp.dto.UserDTO;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.mappers.UserMapper;
import practice.app.journalapp.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    /** Get all users as DTOs */
    @GetMapping("/all-users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> allUsers = userService.getAll();
        if (allUsers != null && !allUsers.isEmpty()) {
            List<UserDTO> dtos = allUsers.stream()
                    .map(UserMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }
        return ResponseEntity.notFound().build();
    }

    /** Create an admin user from DTO */
    @PostMapping("/create-admin-user")
    public ResponseEntity<UserDTO> makeAdmin(@RequestBody UserDTO dto) {
        // Map DTO -> Entity
        User user = UserMapper.toEntity(dto);
        // Save admin
        User saved = userService.saveAdmin(user);
        // Map back to DTO for response
        UserDTO savedDTO = UserMapper.toDTO(saved);
        return ResponseEntity.ok(savedDTO);
    }
}
