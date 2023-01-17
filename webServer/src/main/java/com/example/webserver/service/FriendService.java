package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Friends;
import com.example.webserver.model.Plan;
import com.example.webserver.model.User;
import com.example.webserver.repository.FriendsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService {
    @Autowired
    FriendsRepository friendsRepository;

    @Autowired
    CustomerMapper mapper;
    @Autowired
    UserService userService;


    public String deleteFriends(Friends friends){
        if(friends.getFriendId() != null && friends.getUserId() != null){
            Friends friends1 = friendsRepository.findByUserIdAndFriendId(friends.getUserId(),friends.getFriendId());
            friendsRepository.delete(friends1);
            Friends friends2 = friendsRepository.findByUserIdAndFriendId(friends.getFriendId(),friends.getUserId());
            friendsRepository.delete(friends2);
            return "ok";
        } else
            return "no";
    }

    public void acceptFriends(Friends friends){
        Friends friends1 = friendsRepository.findByUserIdAndFriendId(friends.getUserId(),friends.getFriendId());
        friends1.setStatus("ACCEPTED");
        friendsRepository.save(friends1);
        Friends friends2 = friendsRepository.findByUserIdAndFriendId(friends.getFriendId(),friends.getUserId());
        friends2.setStatus("ACCEPTED");
        friendsRepository.save(friends2);
    }
    public void refuseFriends(Friends friends){

        Friends friends1 = friendsRepository.findByUserIdAndFriendId(friends.getUserId(),friends.getFriendId());
        if(friends1 != null) friendsRepository.delete(friends1);
        Friends friends2 = friendsRepository.findByUserIdAndFriendId(friends.getFriendId(),friends.getUserId());
        if(friends2 != null) friendsRepository.delete(friends2);
    }
    public void deleteIsFriends(Friends friends){

        Friends friends1 = friendsRepository.findByUserIdAndFriendId(friends.getUserId(),friends.getFriendId());
        friendsRepository.delete(friends1);
    }

    public String addFriends(Friends friends){
       User usFriends = userService.findByEmail(friends.getFriendId().getEmail());
       if (usFriends == null) return "Not email";
       if (usFriends.getEmail().equals(friends.getUserId().getEmail())) return "your username";
       if (friendsRepository.findByUserIdAndFriendId(friends.getUserId(),usFriends) != null ||
               friendsRepository.findByUserIdAndFriendId(usFriends,friends.getUserId()) != null) return "exists";
       friends.setFriendId(usFriends);
       friendsRepository.save(friends);
       friendsRepository.save(new Friends(null,"INVITATION_RECEIVED",friends.getUserId(),friends.getFriendId()));
       return "OK";
    }


    public ArrayList<User> findIn(Long id) throws ResourceNotFoundException {

        ArrayList<Friends> friends =  friendsRepository.findAllByUserId( userService.findById(id));

        friends.removeIf(date -> {
            if(date.getStatus().equals("ACCEPTED")) return true;
            else return date.getStatus().equals("REQUEST_SENT");
        });

        ArrayList<User> users = new ArrayList<>();
        for (Friends user:friends) {
            users.add(user.getFriendId());
        }
        return users;
    }
    public ArrayList<User> findIs(Long id) throws ResourceNotFoundException {

        ArrayList<Friends> friends =  friendsRepository.findAllByUserId( userService.findById(id));

        friends.removeIf(date -> {
            if(date.getStatus().equals("ACCEPTED")) return true;
            else return date.getStatus().equals("INVITATION_RECEIVED");
        });

        ArrayList<User> users = new ArrayList<>();
        for (Friends user:friends) {
            users.add(user.getFriendId());
        }
        System.out.println("friends_is "+users);
        return users;
    }
    public ArrayList<User> findByUserId(Long id) throws ResourceNotFoundException {

        ArrayList<Friends> friends =  friendsRepository.findAllByUserId( userService.findById(id));

        friends.removeIf(date -> !date.getStatus().equals("ACCEPTED"));
        ArrayList<User> users = new ArrayList<>();
        for (Friends user:friends) {
            users.add(user.getFriendId());
        }
        return users;
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
   /* public void deleteAll(User user) throws ResourceNotFoundException {
        friendsRepository.deleteAll(friendsRepository.findAllByUserIdOrFriendId(user, user));
    }*/
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

    public Friends findByUserIdAndFriendId(User user, User friend) {
       return friendsRepository.findByUserIdAndFriendId(user,friend);
    }
}
