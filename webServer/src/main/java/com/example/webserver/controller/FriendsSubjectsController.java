package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Friends;
import com.example.webserver.model.FriendsSubjects;
import com.example.webserver.repository.FriendsSubjectsRepository;
import com.example.webserver.service.FriendSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class FriendsSubjectsController {

    @Autowired
    FriendSubjectsService friendsSubjectsRepository;


    @GetMapping("/friends_subjects")
    public List<FriendsSubjects> getAllFriendsSubjects() {
        return friendsSubjectsRepository.findAll();
    }

    @GetMapping("/friends_subjects/{id}")
    public FriendsSubjects getFriendsSubjectsById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return friendsSubjectsRepository.findById(id);
    }

    @PostMapping("/friends_subjects")
    public FriendsSubjects createFriendsSubjects(@RequestBody FriendsSubjects friends){
        System.out.println("Вопрос "+ friends);
        return  friendsSubjectsRepository.save(friends);
    }

    @DeleteMapping("/friends_subjects/{id}")
    public FriendsSubjects deleteFriendsSubjects(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        FriendsSubjects q =  friendsSubjectsRepository.findById(id);
        friendsSubjectsRepository.delete(id);
        return q;
    }

    @PutMapping("/friends_subjects/{id}")
    public FriendsSubjects putFriendsSubjects(@PathVariable Long id,@RequestBody FriendsSubjects req) throws ResourceNotFoundException {
        System.out.println(req);
        return friendsSubjectsRepository.putMet(id,req);
    }

}














