package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.*;
import com.example.webserver.repository.GameHistoryRepository;
import com.example.webserver.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameHistoryService {
    @Autowired
    GameHistoryRepository gameHistoryRepository;

    @Autowired
    GameSubjectsService gameSubjectsService;
    @Autowired
    GameRepository gameRepository;

    public ArrayList<GameHistory> findByUserId(User id) throws ResourceNotFoundException {
        return gameHistoryRepository.findAllByUserId(id);
    }

    public GameHistory putMet(Long id, GameHistory res) throws ResourceNotFoundException {
        GameHistory game = findById(id);
        game.setGameId(res.getGameId());
        game.setUserId(res.getUserId());

        gameHistoryRepository.save(game);
        return game;
    }

    public void delete(GameHistory gameHistory) throws ResourceNotFoundException {
        Game game = gameRepository.findById(gameHistory.getGameId().getId()).orElse(new Game());

        ArrayList<GameHistory> gameHistoryArrayList = gameHistoryRepository.findAllByGameId(game);
        GameHistory gameHistory2 = gameHistoryRepository.findByGameIdAndUserId(game,gameHistory.getUserId());

        if(gameHistoryArrayList.size() < 2) {
            gameSubjectsService.deleteAllByGameId(game);
            gameRepository.delete(game);
        }
        gameHistoryRepository.delete(gameHistory2);
    }
    public GameHistory save(GameHistory game){
        return gameHistoryRepository.save(game);
    }

    public GameHistory findById(Long id) throws ResourceNotFoundException {
        return gameHistoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Game not found for id:" + id.toString() + ""));
    }
    public List<GameHistory> findAll() {
        return gameHistoryRepository.findAll();
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
        gameHistoryRepository.deleteAllByGameId(game);
    }
}
