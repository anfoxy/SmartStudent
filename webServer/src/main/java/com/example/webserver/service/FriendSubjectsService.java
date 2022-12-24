package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Friends;
import com.example.webserver.model.FriendsSubjects;
import com.example.webserver.repository.FriendsSubjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendSubjectsService {
    @Autowired
    FriendsSubjectsRepository friendsSubjectsRepository;

    @Autowired
    CustomerMapper mapper;


    public FriendsSubjects putMet(Long id, FriendsSubjects res) throws ResourceNotFoundException {
        FriendsSubjects friends = findById(id);

        friendsSubjectsRepository.save(friends);
        return friends;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        FriendsSubjects friends = findById(id);
        friendsSubjectsRepository.delete(friends);
    }
    public FriendsSubjects save(FriendsSubjects friends){
        return friendsSubjectsRepository.save(friends);
    }
    public FriendsSubjects findById(Long id) throws ResourceNotFoundException {
        return friendsSubjectsRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("FriendsSubjects not found for id:" + id.toString() + ""));
    }
    public List<FriendsSubjects> findAll() {
        return friendsSubjectsRepository.findAll();
    }

}
