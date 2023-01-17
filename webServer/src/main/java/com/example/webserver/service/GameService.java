package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.*;
import com.example.webserver.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class GameService {
    @Autowired
    GameRepository gameRepository;

    @Autowired
    FriendService friendService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    UserService userService;

    @Autowired
    GameSubjectsService gameSubjectsService;

    public Game putMet(Long id, Game req) throws ResourceNotFoundException {
        Game game = findById(id);
        game.setFriendId(req.getFriendId());
        //game.setSubId(req.getSubId());
        game.setStatus(req.getStatus());
        game.setDate(req.getDate());
        game.setNumberOfQue(req.getNumberOfQue());
        gameRepository.save(game);
        return game;
    }

    public boolean deleteGame(Long userId) {
        if (gameRepository.findById(userId).isPresent()) {
            gameRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        Game game = findById(id);
        gameRepository.delete(game);
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }

    public Game findById(Long id) throws ResourceNotFoundException {
        return gameRepository.findById(id).orElse(null);
    }

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public Game setGame(Game game) throws ResourceNotFoundException {


        Friends friends = friendService.
                findByUserIdAndFriendId(game.getFriendId().getUserId(), game.getFriendId().getFriendId());
        if (friends == null) return null;
        Subject subject = subjectService.findById(game.getSubId().getId());
        if (subject == null) return null;


        Integer questions = game.getNumberOfQue();

        game.setFriendId(friends);
        game.setSubId(subject);
        game.setStatus("EXPECTED");
        game.setId(null);
        game.setDate(game.getDate());
        gameRepository.save(game);

        if(!gameSubjectsService.setSubjectsGame(questions,game)) {
            //deleted
            gameSubjectsService.deleteAllByGameId(game);
            gameRepository.delete(game);
            return null;
        }

        return game;
    }


    public String checkStart(Long game) throws ResourceNotFoundException {
        Game game1 = findById(game);
        return game1 != null ? game1.getStatus() : "NOT";
    }

    public Game checkingAvailability(Long id,User friend) throws ResourceNotFoundException {

        User usr = userService.findById(id);
        Friends friends;
        Game game = null;

        friends = friendService.findByUserIdAndFriendId(usr, friend);
        if(friends!=null) {
            game = gameRepository.findByFriendId(friends);
        }

        if(game == null) {
            friends = friendService.findByUserIdAndFriendId(friend, usr);
            if (friends != null) game = gameRepository.findByFriendId(friends);
            if(game != null) game.setStatus("FRIEND");
        }else  game.setStatus("HOST");

        if(game == null) game = new Game("NOT");
        return game;
    }

    public Game gameSetStatus(Long id, String status) throws ResourceNotFoundException {

        Game game = findById(id);
        if(game!= null){
            game.setStatus(status);
            save(game);
        }
        return game;
    }

    public Game gameStart(Long id) throws ResourceNotFoundException {
        Game game = findById(id);
        if(game!= null && !game.getStatus().equals("STARTED")){
            game.setStatus("STARTED");
            game.setDate(updateTimeGame(Integer.parseInt(game.getDate())));
            save(game);
        }
        return game;
    }

    public String updateTimeGame(int min) {

        System.out.println("min "+min);
        LocalDateTime localDate =LocalDateTime.now();
        localDate.plusMinutes(min);

        String time = "" + checkDateFor0(localDate.getYear())+
                "-" +  checkDateFor0(localDate.getMonthValue())+
                "-" +  checkDateFor0(localDate.getDayOfMonth())+
                "-" +  checkDateFor0(localDate.getHour())+
                "-" +  checkDateFor0(localDate.getMinute())+
                "-" +  checkDateFor0(localDate.getSecond());
        System.out.println("time "+time);
        return time;
    }
    private String checkDateFor0(int figure){
        return figure < 10 ? "0" + figure : "" + figure;
    }
}