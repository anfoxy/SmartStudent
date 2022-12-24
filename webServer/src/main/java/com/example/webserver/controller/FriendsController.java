package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Friends;
import com.example.webserver.model.Question;
import com.example.webserver.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class FriendsController {

    @Autowired
    FriendService friendService;


    @GetMapping("/friends")
    public List<Friends> getAllFriends() {
        return friendService.findAll();
    }

    @GetMapping("/friends/{id}")
    public Friends getFriendsById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return friendService.findById(id);
    }

    @PostMapping("/friends")
    public Friends createFriends(@RequestBody Friends friends){
        System.out.println("Вопрос "+ friends);
        return  friendService.save(friends);
    }

    @PostMapping("/friends_add")
    public String addFriends(@RequestBody Friends friends){
        System.out.println("друг "+ friends);
        String str = friendService.addFriends(friends);
        System.out.println(str);
        return str;
    }

    @DeleteMapping("/friends/{id}")
    public Friends deleteFriends(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Friends q =  friendService.findById(id);
        friendService.delete(id);
        return q;
    }

    @PutMapping("/friends/{id}")
    public Friends putFriends(@PathVariable Long id,@RequestBody Friends req) throws ResourceNotFoundException {
        System.out.println(req);
        return friendService.putMet(id,req);
    }

}














