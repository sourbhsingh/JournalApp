package practice.app.journalapp.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        List<User> e =userService.getAll();
        if(e.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.ok(e);
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody User user){
         userService.saveEntry(user);
         return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable ObjectId id){
       User je =  userService.findById(id);
       if(je==null) return ResponseEntity.notFound().build();
       return ResponseEntity.ok(je);
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByName(@PathVariable String username){
        User je =  userService.findByUsername(username);
        if(je==null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(je);
    }



    @PutMapping()
    public ResponseEntity<User> updateEntry(@RequestBody User user){
        User js = userService.updatePassword(user);
        if(js==null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(js);
    }
}
