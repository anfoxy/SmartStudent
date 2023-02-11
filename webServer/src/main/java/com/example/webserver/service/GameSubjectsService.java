package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Game;
import com.example.webserver.model.GameSubjects;
import com.example.webserver.model.Question;
import com.example.webserver.model.Subject;
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
        if(questionsSize<1 || questions.size()< 1) return  false;
        for(int i = 0; i<questionsSize && i<questions.size();i++){
            System.out.println("в вопросе  "+ questions.get(i));

            GameSubjects gameSubjects = new GameSubjects(null,
                    game,questions.get(i).getQuestion(),questions.get(i).getAnswer(),
                    null,null,null,null);
            System.out.println("в GameSubjects  "+ gameSubjects);
            save(gameSubjects);
        }

        return true;
    }

    @Transactional
    public void deleteAllByGameId(Game game) {
        if(!findAllByGameId(game).isEmpty()) gameSubjectsRepository.deleteAllByGameId(game);
    }

    private ArrayList<GameSubjects> findAllByGameId(Game game) {
        return gameSubjectsRepository.findAllByGameId(game);
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

    public boolean deleteGame(Long userId) {
        if (gameSubjectsRepository.findById(userId).isPresent()) {
            gameSubjectsRepository.deleteById(userId);
            return true;
        }
        return false;
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
                new ResourceNotFoundException("GameSubjects not found for id:" + id.toString() + ""));
    }
    public List<GameSubjects> findAll() {
        return gameSubjectsRepository.findAll();
    }

    public GameSubjects getQuestion(Game game) {

        Game g = gameRepository.findById(game.getId()).orElse(null);
        if(g.getStatus().equals("END"))  return new GameSubjects((long) -1);
        ArrayList<GameSubjects> gameSubjectsArrayList = null;
        if(g != null) {
            gameSubjectsArrayList = findAllByGameId(g);
        } else return null;


        if (gameSubjectsArrayList!= null && !gameSubjectsArrayList.isEmpty()){
            for (GameSubjects gameSub: gameSubjectsArrayList) {
                if(game.getStatus().equals("HOST") && gameSub.getAnswerHost() == null) {
                    gameSub.getGameId().setDate(getTimeToEnd(gameSub.getGameId()));
                    return gameSub;
                }
                if(game.getStatus().equals("FRIEND") && gameSub.getAnswerFriend() == null) {
                    gameSub.getGameId().setDate(getTimeToEnd(gameSub.getGameId()));
                    return gameSub;
                }
            }
            if (game.getStatus().equals("HOST")) {
                if (g.getStatus().equals("STARTED")) {
                    g.setStatus("QUESTION_HOST");
                    gameRepository.save(g);
                } else {
                    g.setStatus("RESULT_START");
                    gameRepository.save(g);
                }
                return new GameSubjects((long) -1);
            }
            if (g.getStatus().equals("STARTED")) {
                g.setStatus("QUESTION_FRIEND");
                gameRepository.save(g);
            } else {
                g.setStatus("RESULT_START");
                gameRepository.save(g);
            }
            return new GameSubjects((long) -1);
        }
        return null;
    }
    private String getTimeToEnd(Game game){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        LocalDateTime date = LocalDateTime.parse(game.getDate(), formatter);
        System.out.println("g "+game.getDate());
        System.out.println("g2 "+date);
        return "" + SECONDS.between(LocalDateTime.now(), date);
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

    public GameSubjects getResult(Game game) {
        // вынести в отдельную функцию, т.к код повторяется в другой функции. отличается только if
        Game g = gameRepository.findById(game.getId()).orElse(null);
        ArrayList<GameSubjects> gameSubjectsArrayList = null;
        if (g != null) {
            gameSubjectsArrayList = findAllByGameId(g);
        } else return null;

        if (gameSubjectsArrayList != null && !gameSubjectsArrayList.isEmpty()) {
            for (GameSubjects gameSub : gameSubjectsArrayList) {
                if (game.getStatus().equals("HOST") && gameSub.getResultHost() == null)
                    return gameSub;
                if (game.getStatus().equals("FRIEND") && gameSub.getResultFriend() == null)
                    return gameSub;
            }
            if (game.getStatus().equals("HOST")) {
                if (g.getStatus().equals("RESULT_START")) {
                    g.setStatus("RESULT_HOST");
                    gameRepository.save(g);
                } else {
                    g.setStatus("END");
                    gameRepository.save(g);
                }
                return new GameSubjects((long) -1);
            }
            if (g.getStatus().equals("RESULT_START")) {
                g.setStatus("RESULT_FRIEND");
                gameRepository.save(g);
            } else {
                g.setStatus("END");
                gameRepository.save(g);
            }
            return new GameSubjects((long) -1);
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
