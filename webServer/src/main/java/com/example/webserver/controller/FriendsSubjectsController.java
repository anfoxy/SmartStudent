package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Friends;
import com.example.webserver.model.FriendsSubjects;
import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import com.example.webserver.service.DeleteSercice;
import com.example.webserver.service.FriendSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController

public class FriendsSubjectsController {

    @Autowired
    FriendSubjectsService friendSubjectsService;



    @PostMapping("/friends_subjects_in")
    public ArrayList<Subject> getFriendsIn(@RequestBody FriendsSubjects friends) throws ResourceNotFoundException {
        return friendSubjectsService.findIn(friends);
    }

    @PostMapping("/friends_subjects_is")
    public ArrayList<Subject> getFriendsIs(@RequestBody FriendsSubjects friends) throws ResourceNotFoundException {
        return friendSubjectsService.findIs(friends);
    }

    @PostMapping("/friends_subjects_accept")
    public Subject getFriendsAccept(@RequestBody FriendsSubjects friends) throws ResourceNotFoundException {
        return friendSubjectsService.acceptFriendsSubjects(friends);
    }

    @PostMapping("/friends_subjects_refuse")
    public FriendsSubjects getFriendsRefuse(@RequestBody FriendsSubjects friends) throws ResourceNotFoundException {
        friendSubjectsService.refuseFriendsSubjects(friends);
        return friends;
    }
    @PostMapping("/friends_subjects_delete_is")
    public FriendsSubjects getFriendsDeleteIs(@RequestBody FriendsSubjects friends) throws ResourceNotFoundException {
        friendSubjectsService.deleteIsFriendsSubjects(friends);
        return friends;
    }

    @GetMapping("/friends_subjects")
    public List<FriendsSubjects> getAllFriendsSubjects() {
        return friendSubjectsService.findAll();
    }

    @PostMapping("/friends_subjects_by_id_not_table/{id}")
    public List<Subject> getAllFriendsSubjectsNotTabl(@PathVariable(value = "id") Long id,@RequestBody User user) throws ResourceNotFoundException {
        return friendSubjectsService.findAllFriendsSubjectsNotTablByIdFriend(id,user);
    }
    @PostMapping("/friends_subjects_sent")
    public String sentFriendsSubjects(@RequestBody ArrayList<FriendsSubjects> friends){

        return friendSubjectsService.sentFriendsSubjects(friends);
    }

    @GetMapping("/friends_subjects/{id}")
    public FriendsSubjects getFriendsSubjectsById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return friendSubjectsService.findById(id);
    }

    @PostMapping("/friends_subjects")
    public FriendsSubjects createFriendsSubjects(@RequestBody FriendsSubjects friends){
        System.out.println("Вопрос "+ friends);
        return  friendSubjectsService.save(friends);
    }

    @DeleteMapping("/friends_subjects/{id}")
    public FriendsSubjects deleteFriendsSubjects(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        FriendsSubjects q =  friendSubjectsService.findById(id);
        friendSubjectsService.delete(id);
        return q;
    }

    @PutMapping("/friends_subjects/{id}")
    public FriendsSubjects putFriendsSubjects(@PathVariable Long id,@RequestBody FriendsSubjects req) throws ResourceNotFoundException {
        System.out.println(req);
        return friendSubjectsService.putMet(id,req);
    }

}














