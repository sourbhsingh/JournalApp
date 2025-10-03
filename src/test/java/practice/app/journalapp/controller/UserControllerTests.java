package practice.app.journalapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.app.journalapp.entity.User;
import practice.app.journalapp.service.UserService;

@RestController
@RequestMapping("/user")
public class UserControllerTests {
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
