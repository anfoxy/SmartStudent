package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.*;
import com.example.webserver.repository.FriendsSubjectsRepository;
import com.example.webserver.repository.GameHistoryRepository;
import com.example.webserver.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


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
    @Autowired
    GameHistoryService gameHistoryService;
    @Autowired
    private FriendsSubjectsRepository friendsSubjectsRepository;


    public Game putMet(Long id, Game req) throws ResourceNotFoundException {
        Game game = findById(id);
        game.setFriendId(req.getFriendId());
        game.setName(req.getName());
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
        gameSubjectsService.deleteAllByGameId(game);
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
        game.setName(game.getName());
        game.setStatus("EXPECTED");
        game.setId(null);
        game.setDate(game.getDate());
        gameRepository.save(game);

        gameHistoryService.save(new GameHistory(null,game.getFriendId().getUserId(),game));
        gameHistoryService.save(new GameHistory(null,game.getFriendId().getFriendId(),game));

        if(!gameSubjectsService.setSubjectsGame(questions,game)) {
            //deleted
            gameSubjectsService.deleteAllByGameId(game);
            gameHistoryService.deleteByGameId(game);
            gameRepository.delete(game);
            return null;
        }

        return game;
    }


    public String checkStart(Long game) throws ResourceNotFoundException {
        Game game1 = findById(game);
        if((game1.getStatus().equals("STARTED")
                        ||game1.getStatus().equals("QUESTION_HOST")
                        ||game1.getStatus().equals("QUESTION_FRIEND"))&&checkTime(game1.getDate())){
            game1.setStatus("RESULT_START");
            save(game1);
        }
        return game1 != null ? game1.getStatus() : "NOT";
    }
    private boolean checkTime(String date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime date1 = LocalDateTime.parse(date, formatter);
        date1 = date1.minusSeconds(4);
        return date1.isBefore(LocalDateTime.now());
    }

    public Game checkingAvailability(Long id,User friend) throws ResourceNotFoundException {

        User usr = userService.findById(id);

        System.out.println("друг "+friend);
        System.out.println("я "+usr);
        ArrayList<GameHistory> gameHistories = gameHistoryService.findByUserId(usr);

        if(gameHistories.isEmpty()) return new Game("NOT");

        for (GameHistory g : gameHistories) {
            if(!g.getGameId().getStatus().equals("END") &&
                    (g.getGameId().getFriendId().getFriendId().getEmail().equals(friend.getEmail())
                            ||g.getGameId().getFriendId().getUserId().getEmail().equals(friend.getEmail()))){
                if(g.getGameId().getFriendId().getUserId().getEmail().equals(usr.getEmail()))  {
                    g.getGameId().setStatus("HOST");
                    return g.getGameId();
                }else {
                    g.getGameId().setStatus("FRIEND");
                    return g.getGameId();
                }

            }
        }
        return new Game("NOT");
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

        LocalDateTime localDate =LocalDateTime.now().plusMinutes(min);

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

    public ArrayList<GameSubjects> gameGetQuestionList(Game game) throws ResourceNotFoundException {
        Game g = findById(game.getId());
        return g != null ? gameSubjectsService.getAllByGameId(g) : null;
    }

    public Long exitingTheGame(Long usr,Game game) {
        game.setStatus("END");
        gameRepository.save(game);
        if(game.getFriendId().getFriendId().getId().equals(usr)) return game.getFriendId().getUserId().getId();
        return game.getFriendId().getFriendId().getId();
    }
}