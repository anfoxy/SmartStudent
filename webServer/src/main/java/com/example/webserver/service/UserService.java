package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.dto.UserDTO;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Subject;
import com.example.webserver.model.User;
import com.example.webserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    SubjectService subjectService;
    @Autowired
    CustomerMapper mapper;

    public User putMet(Long id, User s) throws ResourceNotFoundException {
        User user = findById(id);
        user.setLogin(s.getLogin());
        user.setPassword(s.getPassword());
        user.setMatchingPassword(s.getMatchingPassword());
        user.setUpdateDbTime(s.getUpdateDbTime());
        userRepository.save(user);
        return user;
    }


    public User findByLogin(String login){
        User user = userRepository.findByLogin(login);
        return user;
    }

    public User login(String userName, String password) {
        String str =  userRepository.searchUserByEmailAndPassword(userName, password);
        return userRepository.findById(Long.valueOf(str)).orElseThrow(null);


    }
    public String register(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) return "email exists";
        if(!user.getPassword().equals(user.getMatchingPassword())) return "password doesn't match";
        if (userRepository.findByLogin(user.getLogin()) != null) return "login exists";
        userRepository.save(user);
        return "ok";
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByLogin(user.getLogin());

        if (userFromDB != null) return false;

        userRepository.save(user);
        return true;
    }



/*    public void delete(Long id) throws ResourceNotFoundException {
        User user = findById(id);
        subjectService.deleteAll(subjectService.findAllByUserId(user));
        friendService.deleteAll(user);
        friendSubjectsService.deleteAll(user);
        userRepository.delete(user);
    }*/

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found for id:" + id + ""));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public ArrayList<Subject> updateDBTime(User userLoc, User userSer) {
        if(checkTime(userLoc,userSer)){
            return subjectService.findAllByUserId(userSer);
        } else {


            return null;
        }
    }

    private boolean checkTime(User userLoc, User userSer){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        try {
            // Парсим строки с временем в объекты Date
            Date date1 = format.parse(userLoc.getUpdateDbTime());
            Date date2 = format.parse(userSer.getUpdateDbTime());
            // Сравниваем объекты Date
            return date1.before(date2);
        } catch (Exception e) {
            // Обрабатываем ошибки
            e.printStackTrace();
            return false;
        }

    }
}


