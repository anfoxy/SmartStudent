package com.example.webserver.repository;

import com.example.webserver.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface GameRepository extends JpaRepository<Game,Long> {

    Game findByFriendId(Friends friends);
    ArrayList<Game> findAllByFriendId(Friends friends);
}
