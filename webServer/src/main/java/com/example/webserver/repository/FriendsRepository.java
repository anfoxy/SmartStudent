package com.example.webserver.repository;

import com.example.webserver.model.Friends;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface FriendsRepository extends JpaRepository<Friends,Long> {


    ArrayList<Friends> findAllByUserId(User user);

    //  Friends findByUserIdAndFriendId(User userId,User friends);
    Friends findByUserIdAndFriendId(User userId,User friendsId);

    ArrayList<Friends> findAllByUserIdOrFriendId(User userId1,User userId2);
}
