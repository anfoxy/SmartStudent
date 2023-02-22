package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Game;
import com.example.webserver.model.GameSubjects;
import com.example.webserver.model.Question;
import com.example.webserver.model.User;
import com.example.webserver.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController

public class GameController {

    @Autowired
    GameService gameService;


    @GetMapping("/game")
    public List<Game> getAllGame() {
        return gameService.findAll();
    }

    @GetMapping("/game/{id}")
    public Game getGameById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return gameService.findById(id);
    }


    @PostMapping("/game/set_status/{id}")
    public Game gameSetStatus(@PathVariable(value = "id") Long id,@RequestBody String status) throws ResourceNotFoundException {
        return  gameService.gameSetStatus(id,status);
    }
    @PostMapping("/game/exiting_the_game/{id}")
    public Long gameSetStatus(@PathVariable(value = "id") Long id,@RequestBody Game game) throws ResourceNotFoundException {
        return  gameService.exitingTheGame(id,game);
    }
    @GetMapping("/game/abandon_the_game/{id}")
    public Game abandonTheGame(@PathVariable(value = "id") Long id)  {
        return  gameService.abandonTheGame(id);
    }
    @GetMapping("/game/check_end/{id_game}")
    public Boolean gameCheckEnd(@PathVariable(value = "id_game") Long id)  {
        return  gameService.gameCheckEnd(id);
    }


    @GetMapping("/game/start/{id}")
    public Game gameStart(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return gameService.gameStart(id);
    }

    @PostMapping("/game")
    public Game createGame(@RequestBody Game game){
        return  gameService.save(game);
    }

    @PostMapping("/game/checking_availability/{id}")
    public Game checkingAvailability(@PathVariable(value = "id") Long id,@RequestBody User friend) throws ResourceNotFoundException {
        return  gameService.checkingAvailability(id,friend);
    }

    @PostMapping("/game/set")
    public Game setGame(@RequestBody Game game) throws ResourceNotFoundException {
        return  gameService.setGame(game);
    }

    @GetMapping("/game/check_start/{id_game}/{id_user}")
    public String checkStartGame(@PathVariable(value = "id_game") Long game_id,@PathVariable(value = "id_user") Long user_id) throws ResourceNotFoundException {
        System.out.println("статус ");
        return  gameService.checkStart(game_id,user_id);
    }

    @GetMapping("/game/get_question_list/{id}")
    public ArrayList<GameSubjects> gameGetQuestionList(@PathVariable(value = "id") Long game_id) throws ResourceNotFoundException {
        return  gameService.gameGetQuestionList(game_id);
    }


    @DeleteMapping("/game/{id}")
    public Game deleteGame(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Game q =  gameService.findById(id);
        gameService.delete(id);
        return q;
    }

    @PutMapping("/game/{id}")
    public Game putGame(@PathVariable Long id,@RequestBody Game req) throws ResourceNotFoundException {
        System.out.println(req);
        return gameService.putMet(id,req);
    }

}














