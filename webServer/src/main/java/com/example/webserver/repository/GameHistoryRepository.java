package com.example.webserver.repository;

import com.example.webserver.model.Friends;
import com.example.webserver.model.Game;
import com.example.webserver.model.GameHistory;
import com.example.webserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface GameHistoryRepository extends JpaRepository<GameHistory,Long> {


    ArrayList<GameHistory> findAllByUserId(User user);

    void deleteAllByGameId(Game game);
}
