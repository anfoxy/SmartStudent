package com.example.webserver.repository;

import com.example.webserver.model.Friends;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface FriendsRepository extends JpaRepository<Friends,Long> {


}
