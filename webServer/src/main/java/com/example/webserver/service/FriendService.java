package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Friends;
import com.example.webserver.model.Plan;
import com.example.webserver.model.User;
import com.example.webserver.repository.FriendsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {
    @Autowired
    FriendsRepository friendsRepository;

    @Autowired
    CustomerMapper mapper;
    @Autowired
    UserService userService;

    public String addFriends(Friends friends){
       User usFriends = userService.findByLogin(friends.getFriendId().getLogin());
       if (usFriends == null) return "Not login";
       if (usFriends.getLogin().equals(friends.getUserId().getLogin())) return "your username";
       friends.setFriendId(usFriends);
       friendsRepository.save(friends);
       friendsRepository.save(new Friends(null,"INVITATION_RECEIVED",friends.getUserId(),friends.getFriendId()));
       return "OK";
    }



    public Friends putMet(Long id, Friends res) throws ResourceNotFoundException {
        Friends friends = findById(id);
        friends.setStatus(res.getStatus());
        friends.setFriendId(res.getFriendId());
        friends.setUserId(res.getUserId());
        friendsRepository.save(friends);
        return friends;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        Friends friends = findById(id);
        friendsRepository.delete(friends);
    }
    public Friends save(Friends friends){
        return friendsRepository.save(friends);
    }

    public Friends findById(Long id) throws ResourceNotFoundException {
        return friendsRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Friends not found for id:" + id.toString() + ""));
    }
    public List<Friends> findAll() {
        return friendsRepository.findAll();
    }

}
