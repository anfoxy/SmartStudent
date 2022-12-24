package com.example.webserver.repository;

import com.example.webserver.model.Friends;
import com.example.webserver.model.FriendsSubjects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendsSubjectsRepository extends JpaRepository<FriendsSubjects,Long> {


}
