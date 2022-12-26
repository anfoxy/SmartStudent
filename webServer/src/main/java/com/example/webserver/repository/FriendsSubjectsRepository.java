package com.example.webserver.repository;

import com.example.webserver.model.Friends;
import com.example.webserver.model.FriendsSubjects;
import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface FriendsSubjectsRepository extends JpaRepository<FriendsSubjects,Long> {

ArrayList<FriendsSubjects> findAllByUserIdAndFriendId(User user, User friend);

FriendsSubjects findByUserIdAndFriendIdAndSubId(User user, User friend, Subject subject);

    ArrayList<FriendsSubjects> findAllByUserIdOrFriendId(User userId1,User userId2);
}
