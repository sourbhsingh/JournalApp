package practice.app.journalapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.service.UserService;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    UserService userService;
    
    @PostMapping("/create-user")
    public ResponseEntity<?> createEntry(@RequestBody User user){
        userService.saveEntry(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    @GetMapping("/health-check")
    public String healthCheck(){
        return "ok";
    }
}
