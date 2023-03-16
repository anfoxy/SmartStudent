package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Msg;
import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import com.example.webserver.repository.UserRepository;
import com.example.webserver.service.DeleteSercice;
import com.example.webserver.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    DeleteSercice deleteSercice;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users/update")
    public ResponseEntity<ArrayList<Subject>> update(@Valid @RequestBody ArrayList<Subject> subjects) throws ResourceNotFoundException {

        if(subjects.isEmpty()) return ResponseEntity.ok(subjects);

        User user1 = userService.findById(subjects.get(0).getUserId().getId());
        User user2 = subjects.get(0).getUserId();
        subjects.remove(0);
        ArrayList<Subject> t = userService.updateDBTime(user2,user1,subjects);
        System.out.println(t);
        return ResponseEntity.ok(t);
    }
    @PostMapping("/users/auth")
    public ResponseEntity<User> login(@Valid @RequestBody User  user){

        return ResponseEntity.ok(userService.login(user.getEmail(),user.getPassword()));
    }
    @PostMapping("/users/register")
    public ResponseEntity<String> register(@Valid @RequestBody User  user){
      return ResponseEntity.ok(userService.register(user));
    }

    @GetMapping("/user")
    public List<User> getAllUser() {
        return userService.findAll();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user){
        return  ResponseEntity.ok(userService.save(user));
    }
    @Transactional
    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        User u = userService.findById(id);
        deleteSercice.deleteUser(id);
        return ResponseEntity.ok(u);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<User> putUser(@PathVariable Long id,@RequestBody User req) throws ResourceNotFoundException {
        return ResponseEntity.ok(userService.putMet(id,req));
    }
}















