package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Game;
import com.example.webserver.model.GameHistory;
import com.example.webserver.model.Question;
import com.example.webserver.model.User;
import com.example.webserver.service.GameHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class GameHistoryController {

    @Autowired
    GameHistoryService gameHistoryService;


    @GetMapping("/game_history")
    public List<GameHistory> getAllGameHistory() {
        return gameHistoryService.findAll();
    }

    @GetMapping("/game_history/{id}")
    public GameHistory getGameHistoryById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return gameHistoryService.findById(id);
    }

    @PostMapping("/game_history/get_all/{id}")
    public List<Game> getAllGamesByUserId(@PathVariable(value = "id") Long id,@RequestBody User user) throws ResourceNotFoundException {
        return  gameHistoryService.getAllGamesByUserId(user,id);
    }

    @PostMapping("/game_history")
    public GameHistory createGameHistory(@RequestBody GameHistory gameHistory){
        return  gameHistoryService.save(gameHistory);
    }

    @DeleteMapping("/game_history")
    public Long deleteGameHistory(@RequestBody GameHistory gameHistory) throws ResourceNotFoundException {

        return gameHistoryService.delete(gameHistory);
    }


    @PutMapping("/game_history/{id}")
    public GameHistory putGameHistory(@PathVariable Long id,@RequestBody GameHistory req) throws ResourceNotFoundException {
        return gameHistoryService.putMet(id,req);
    }

}














