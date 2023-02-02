package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.*;
import com.example.webserver.repository.GameHistoryRepository;
import com.example.webserver.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameHistoryService {
    @Autowired
    GameHistoryRepository gameHistory;

    public ArrayList<GameHistory> findByUserId(User id) throws ResourceNotFoundException {
        return gameHistory.findAllByUserId(id);
    }

    public GameHistory putMet(Long id, GameHistory res) throws ResourceNotFoundException {
        GameHistory game = findById(id);
        game.setGameId(res.getGameId());
        game.setUserId(res.getUserId());

        gameHistory.save(game);
        return game;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        GameHistory game = findById(id);
        gameHistory.delete(game);
    }
    public GameHistory save(GameHistory game){
        return gameHistory.save(game);
    }

    public GameHistory findById(Long id) throws ResourceNotFoundException {
        return gameHistory.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Game not found for id:" + id.toString() + ""));
    }
    public List<GameHistory> findAll() {
        return gameHistory.findAll();
    }

    public List<Game> getAllGamesByUserId(User user,Long idFriend) throws ResourceNotFoundException {
      ArrayList<GameHistory> games = findByUserId(user);

        List<Game> gameList =  games.stream()
                .map(GameHistory::getGameId).collect(Collectors.toList());


        gameList.removeIf((Game g) ->{
            if(g.getFriendId().getUserId().getEmail().equals(user.getEmail())
                    && g.getFriendId().getFriendId().getId().equals(idFriend)) {
                g.setStatus("HOST");
                return false;
            }else if(g.getFriendId().getFriendId().getEmail().equals(user.getEmail())
                    &&g.getFriendId().getUserId().getId().equals(idFriend)) {
                g.setStatus("FRIEND");
                return false;
            }
            else return true;
        });

        return gameList;
    }

    public void deleteByGameId(Game game) {
        gameHistory.deleteAllByGameId(game);
    }
}
