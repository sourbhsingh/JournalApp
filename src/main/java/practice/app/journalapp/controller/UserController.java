package practice.app.journalapp.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService ;

    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User js = userService.updateUser(user,username);
        if(js==null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(js);
    }
}
