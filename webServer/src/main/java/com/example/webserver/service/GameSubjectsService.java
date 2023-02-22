package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.*;
import com.example.webserver.repository.GameRepository;
import com.example.webserver.repository.GameSubjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class GameSubjectsService {
    @Autowired
    GameSubjectsRepository gameSubjectsRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    public boolean setSubjectsGame(Integer questionsSize, Game game) throws ResourceNotFoundException {

       ArrayList<Question> questions = questionService.findAllBySubId(game.getSubId());
        if(questionsSize<1 || questions.isEmpty()) return  false;
        for(int i = 0; i<questionsSize && i<questions.size();i++){
            GameSubjects gameSubjects = new GameSubjects(null,
                    game,questions.get(i).getQuestion(),questions.get(i).getAnswer(),
                    null,null,null,null);
            save(gameSubjects);
        }
        return true;
    }

    @Transactional
    public void deleteAllByGameId(Game game) {
        if(!findAllByGameId(game).isEmpty()) gameSubjectsRepository.deleteAllByGameId(game);
    }

    private ArrayList<GameSubjects> findAllByGameId(Game game) {
        return game != null ? gameSubjectsRepository.findAllByGameId(game) : null;
    }

    public GameSubjects putMet(Long id, GameSubjects req) throws ResourceNotFoundException {
        GameSubjects game = findById(id);
        game.setGameId(req.getGameId());
        game.setAnswer(req.getAnswer());
        game.setQuestion(req.getQuestion());
        game.setAnswerHost(req.getAnswerHost());
        game.setAnswerFriend(req.getAnswerFriend());
        game.setResultHost(req.getResultHost());
        game.setResultFriend(req.getResultFriend());

        gameSubjectsRepository.save(game);
        return game;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        GameSubjects game = findById(id);
        gameSubjectsRepository.delete(game);
    }

    public GameSubjects save(GameSubjects game){
        return gameSubjectsRepository.save(game);
    }
    public GameSubjects findById(Long id) throws ResourceNotFoundException {
        return gameSubjectsRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("GameSubjects not found for id:" + id + ""));
    }
    public List<GameSubjects> findAll() {
        return gameSubjectsRepository.findAll();
    }

    public GameSubjects getQuestion(Long id_game, User user) {

        Game g = gameRepository.findById(id_game).orElse(null);
        ArrayList<GameSubjects> gameSubjectsArrayList = findAllByGameId(g);


        if (gameSubjectsArrayList!= null && !gameSubjectsArrayList.isEmpty()){

            if (g.getStatus().equals("END")) return new GameSubjects((long) -1);
            for (GameSubjects gameSub: gameSubjectsArrayList) {
                if(g.getFriendId().getUserId().getId().equals(user.getId()) && gameSub.getAnswerHost() == null) {
                    gameSub.getGameId().setDate(getTimeToEnd(gameSub.getGameId()));
                    return gameSub;
                }
                if(g.getFriendId().getFriendId().getId().equals(user.getId()) && gameSub.getAnswerFriend() == null) {
                    gameSub.getGameId().setDate(getTimeToEnd(gameSub.getGameId()));
                    return gameSub;
                }
            }
            updateStatusForQuestionEnd(g,user);
            return new GameSubjects((long) -1);
        }
        return null;
    }
    private void updateStatusForQuestionEnd(Game g,User user){
        if (g.getFriendId().getUserId().getId().equals(user.getId())) {
            if (g.getStatus().equals("STARTED")) {
                g.setStatus("QUESTION_HOST");
                gameRepository.save(g);
            } else {
                g.setStatus("RESULT_START");
                gameRepository.save(g);
            }
        } else {
            if (g.getStatus().equals("STARTED")) {
                g.setStatus("QUESTION_FRIEND");
                gameRepository.save(g);
            } else {
                g.setStatus("RESULT_START");
                gameRepository.save(g);
            }
        }
    }

    private String getTimeToEnd(Game game){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime date = LocalDateTime.parse(game.getDate(), formatter);
        System.out.println("g "+game.getDate());
        System.out.println("g2 "+date);
        return "" + SECONDS.between(LocalDateTime.now(), date);
    }

    public GameSubjects setQuestion(GameSubjects game,Long id_usr) throws ResourceNotFoundException {
        System.out.println("ghbikb");
        GameSubjects g = findById(game.getId());
        if(g != null) {
            if(game.getGameId().getFriendId().getUserId().getId().equals(id_usr)) {
                g.setAnswerHost(game.getAnswerHost());
                return save(g);
            }
            if(game.getGameId().getFriendId().getFriendId().getId().equals(id_usr)) {
                g.setAnswerFriend(game.getAnswerHost());
                return save(g);
            }
        }
        return  null;
    }
    public GameSubjects setQuestionHost(GameSubjects game) throws ResourceNotFoundException {

        GameSubjects g = findById(game.getId());
        if(g != null) {
            g.setAnswerHost(game.getAnswerHost());
            return save(g);
        }
        return  null;
    }
    public GameSubjects setQuestionFriend(GameSubjects game) throws ResourceNotFoundException {

        GameSubjects g = findById(game.getId());
        if(g != null) {
            g.setAnswerFriend(game.getAnswerFriend());
            return save(g);
        }
        return  null;
    }

    public GameSubjects getResult(Long id_game, User user) {

        Game g = gameRepository.findById(id_game).orElse(null);
        ArrayList<GameSubjects> gameSubjectsArrayList = findAllByGameId(g);

        if (gameSubjectsArrayList != null && !gameSubjectsArrayList.isEmpty()) {
            for (GameSubjects gameSub : gameSubjectsArrayList) {

                if (g.getFriendId().getUserId().getId().equals(user.getId()) && gameSub.getResultHost() == null)
                    return gameSub;
                if (g.getFriendId().getFriendId().getId().equals(user.getId()) && gameSub.getResultFriend() == null)
                    return gameSub;
            }
            updateStatusForResultEnd(g,user);
            return new GameSubjects((long) -1);
        }
        return  null;
    }

    private void updateStatusForResultEnd(Game g,User user){
        if (g.getFriendId().getUserId().getId().equals(user.getId())) {
            if (g.getStatus().equals("RESULT_START")) {
                g.setStatus("RESULT_HOST");
                gameRepository.save(g);
            } else {
                g.setStatus("END");
                gameRepository.save(g);
            }
        } else {
            if (g.getStatus().equals("RESULT_START")) {
                g.setStatus("RESULT_FRIEND");
                gameRepository.save(g);
            } else {
                g.setStatus("END");
                gameRepository.save(g);
            }
        }
    }
    public GameSubjects setResult(GameSubjects game,Long id_usr) throws ResourceNotFoundException {
        GameSubjects g = findById(game.getId());
        if(g != null) {
            if(game.getGameId().getFriendId().getUserId().getId().equals(id_usr)) {
                g.setResultHost(game.getResultHost());
                return save(g);
            }
            if(game.getGameId().getFriendId().getFriendId().getId().equals(id_usr)) {
                g.setResultFriend(game.getResultHost());
                return save(g);
            }
        }
        return  null;
    }

    public GameSubjects setResultFriend(GameSubjects game) throws ResourceNotFoundException {
        GameSubjects g = findById(game.getId());
        if(g != null) {
            g.setResultFriend(game.getResultFriend());
            return save(g);
        }
        return  null;
    }

    public GameSubjects setResultHost(GameSubjects game) throws ResourceNotFoundException {
        GameSubjects g = findById(game.getId());
        if(g != null) {
            g.setResultHost(game.getResultHost());
            return save(g);
        }
        return  null;
    }

    public ArrayList<GameSubjects> getAllByGameId(Game byId) {
        return gameSubjectsRepository.findAllByGameId(byId);
    }
}
