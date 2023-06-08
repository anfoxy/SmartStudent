package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.*;
import com.example.webserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
@Service
public class DeleteSercice {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    GameSubjectsService gameSubjectsService;
    @Autowired
    GameHistoryService gameHistoryService;
    @Autowired
    private GameHistoryRepository gameHistoryRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    FriendsRepository friendsRepository;
    @Autowired
    FriendsSubjectsRepository friendsSubjectsRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    PlanRepository planRepository;
    @Transactional
    public void deleteUser(Long id) throws ResourceNotFoundException {
        User user = userService.findById(id);
        deleteGame(user);
        deleteAllFriends(user);
        deleteAllFriendsSubjects(user);
        deleteAllSub(subjectService.findAllByUserId(user));
        userRepository.delete(user);
    }
    @Transactional
    public void deleteAllFriends(User user) throws ResourceNotFoundException {
        friendsRepository.deleteAll(friendsRepository.findAllByUserIdOrFriendId(user, user));
    }

    @Transactional
    public void deleteGame(User user) throws ResourceNotFoundException {

          ArrayList<GameHistory> gameHistoryList = gameHistoryRepository.findAllByUserId(user);
        for (GameHistory gameHistory: gameHistoryList) {
            Game game = gameRepository.findById(gameHistory.getGameId().getId()).orElse(new Game());
            gameSubjectsService.deleteAllByGameId(game);
            gameHistoryRepository.deleteAllByGameId(game);
            gameRepository.delete(game);

        }

    }

    @Transactional
    public void deleteAllFriendsSubjects(User user) throws ResourceNotFoundException {
        friendsSubjectsRepository.deleteAll(friendsSubjectsRepository.findAllByUserIdOrFriendId(user, user));
    }
    @Transactional
    public void deleteSub(Long id) throws ResourceNotFoundException {
        Subject subject = subjectRepository.findById(id).orElse(null);
        questionRepository.deleteAllBySubId(subject);
        planRepository.deleteAllBySubId(subject);
        subjectRepository.delete(subject);
    }
    @Transactional
    public void deleteAllSub(ArrayList<Subject> subjects) throws ResourceNotFoundException {
        for (Subject subject: subjects) {
            questionRepository.deleteAllBySubId(subject);
            planRepository.deleteAllBySubId(subject);
            subjectRepository.delete(subject);
        }
    }

    public void deleteAllSubId(Subject subject) throws ResourceNotFoundException {
        questionRepository.deleteAllBySubId(subject);
        planRepository.deleteAllBySubId(subject);
    }

}
