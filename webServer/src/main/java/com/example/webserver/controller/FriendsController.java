package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Friends;
import com.example.webserver.model.Question;
import com.example.webserver.model.User;
import com.example.webserver.service.DeleteSercice;
import com.example.webserver.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PostMapping("/friends_delete")
    public String friendsDelete(@RequestBody Friends friends) throws ResourceNotFoundException {
        return friendService.deleteFriends(friends);
    }
    @PostMapping("/friends_accept")
    public Friends getFriendsAccept(@RequestBody Friends friends) throws ResourceNotFoundException {
        friendService.acceptFriends(friends);
        return friends;
    }

    @PostMapping("/friends_refuse")
    public Friends getFriendsRefuse(@RequestBody Friends friends) throws ResourceNotFoundException {
        friendService.refuseFriends(friends);
        return friends;
    }

    @PostMapping("/friends_delete_is")
    public Friends getFriendsDeleteIs(@RequestBody Friends friends) throws ResourceNotFoundException {
        friendService.deleteIsFriends(friends);
        return friends;
    }

    @GetMapping("/friends_by_user/{id}")
    public ArrayList<User> getFriendsByUserId(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {

        return friendService.findByUserId(id);
    }

    @GetMapping("/friends_in/{id}")
    public ArrayList<User> getFriendsIn(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return friendService.findIn(id);
    }

    @GetMapping("/friends_is/{id}")
    public ArrayList<User> getFriendsIs(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return friendService.findIs(id);
    }

    @PostMapping("/friends")
    public Friends createFriends(@RequestBody Friends friends){
        return  friendService.save(friends);
    }

    @PostMapping("/friends_add")
    public String addFriends(@RequestBody Friends friends){
        String str = friendService.addFriends(friends);
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














