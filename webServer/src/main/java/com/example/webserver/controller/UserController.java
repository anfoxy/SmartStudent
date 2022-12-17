package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Msg;
import com.example.webserver.model.User;
import com.example.webserver.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/users/auth")
    public ResponseEntity<User> login(@Valid @RequestBody User  user){
        System.out.println("ПОЛЬЗОВАТЕЛЬ: " + user);
        return ResponseEntity.ok(userService.login(user.getLogin(),user.getPassword()));
    }

    @PostMapping("/users/register")
    public String register(@Valid @RequestBody User  user){
        if(!user.getPassword().equals(user.getMatchingPassword())) return "password doesn't match";
        if(userService.register(user)) return "OK";
       else return "NO";
    }

    @GetMapping("/user")
    public List<User> getAllUser() {
        return userService.findAll();
    }

    @PostMapping("/test")
    public String get(@Valid @RequestBody Msg  user) {
        return user.getMsg()+"ОтветОтСервера";
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return userService.findById(id);

    }

    @PostMapping("/user")
    public User createUser(@RequestBody User user){
        return  userService.save(user);
    }

    @DeleteMapping("/user/{id}")
    public User deleteUser(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        User u = userService.findById(id);
        userService.delete(id);
        return u;
    }

    @PatchMapping(value = "/user/{id}")
    public User patchUser(@PathVariable Long id, @RequestBody User req) throws ResourceNotFoundException {
        return userService.updateUser(req,id);
    }

    @PutMapping("/user/{id}")
    public User putUser(@PathVariable Long id,@RequestBody User req) throws ResourceNotFoundException {
        return userService.putMet(id,req);
    }
}















