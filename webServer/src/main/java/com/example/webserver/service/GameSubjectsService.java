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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        ArrayList<GameSubjects> gameSubjectsArrayList = null;
        if(g != null) {
            gameSubjectsArrayList = findAllByGameId(g);
        } else return null;

        if (gameSubjectsArrayList!= null && !gameSubjectsArrayList.isEmpty()){
            for (GameSubjects gameSub: gameSubjectsArrayList) {
                if(game.getStatus().equals("HOST") && gameSub.getAnswerHost() == null)
                    return gameSub;
                if(game.getStatus().equals("FRIEND") && gameSub.getAnswerFriend() == null)
                    return gameSub;
            }

        }
        return new GameSubjects((long) -1);
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
}
