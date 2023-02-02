package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Game;
import com.example.webserver.model.GameSubjects;
import com.example.webserver.service.GameService;
import com.example.webserver.service.GameSubjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class GameSubjectsController {

    @Autowired
    GameSubjectsService gameService;

    @PostMapping("/game_subjects/set_result_host")
    public GameSubjects setResultHost(@RequestBody GameSubjects game) throws ResourceNotFoundException {
        return  gameService.setResultHost(game);
    }
    @PostMapping("/game_subjects/set_result_friend")
    public GameSubjects setResultFriend(@RequestBody GameSubjects game) throws ResourceNotFoundException {
        return  gameService.setResultFriend(game);
    }

    @PostMapping("/game_subjects/set_question_host")
    public GameSubjects setQuestionHost(@RequestBody GameSubjects game) throws ResourceNotFoundException {
        return  gameService.setQuestionHost(game);
    }
    @PostMapping("/game_subjects/set_question_friend")
    public GameSubjects setQuestionFriend(@RequestBody GameSubjects game) throws ResourceNotFoundException {
        return  gameService.setQuestionFriend(game);
    }

    @PostMapping("/game_subjects/get_question")
    public GameSubjects getQuestion(@RequestBody Game game){

        return  gameService.getQuestion(game);
    }

    @PostMapping("/game_subjects/get_result")
    public GameSubjects getResult(@RequestBody Game game){

        return  gameService.getResult(game);
    }
    @GetMapping("/game_subjects")
    public List<GameSubjects> getAllGame() {
        return gameService.findAll();
    }

    @GetMapping("/game_subjects/{id}")
    public GameSubjects getGameById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return gameService.findById(id);
    }

    @PostMapping("/game_subjects")
    public GameSubjects createGame(@RequestBody GameSubjects game){
        return  gameService.save(game);
    }

    @DeleteMapping("/game_subjects/{id}")
    public GameSubjects deleteGame(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        GameSubjects q =  gameService.findById(id);
        gameService.delete(id);
        return q;
    }

    @PutMapping("/game_subjects/{id}")
    public GameSubjects putGame(@PathVariable Long id,@RequestBody GameSubjects req) throws ResourceNotFoundException {
        System.out.println(req);
        return gameService.putMet(id,req);
    }

}














