package com.example.webserver.repository;

import com.example.webserver.model.Game;
import com.example.webserver.model.GameSubjects;
import com.example.webserver.model.Plan;
import com.example.webserver.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface GameSubjectsRepository extends JpaRepository<GameSubjects,Long> {

    ArrayList<GameSubjects> findAllByGameId(Game game);
    void deleteAllByGameId(Game game);

}
