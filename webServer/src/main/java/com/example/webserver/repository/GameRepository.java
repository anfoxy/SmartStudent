package com.example.webserver.repository;

import com.example.webserver.model.Friends;
import com.example.webserver.model.Game;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface GameRepository extends JpaRepository<Game,Long> {

    Game findByFriendId(Friends friends);

}
