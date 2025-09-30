package practice.app.journalapp.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allusers = userService.getAll();
        if (allusers != null) {
            return  ResponseEntity.ok(allusers);

        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<User> makeAdmin(@RequestBody User user){
            User s =userService.saveAdmin(user);
          return  ResponseEntity.ok(s);
    }




}
