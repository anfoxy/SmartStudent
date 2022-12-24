package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.dto.UserDTO;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.User;
import com.example.webserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerMapper mapper;

    public User putMet(Long id, User s) throws ResourceNotFoundException {
        User user = findById(id);
        user.setLogin(s.getLogin());
        user.setPassword(s.getPassword());
        user.setMatchingPassword(s.getMatchingPassword());
        userRepository.save(user);
        return user;
    }

/*    public User updateUser(User s, Long id) throws ResourceNotFoundException {
        UserDTO dto = new UserDTO(s);
        dto.setId(id);
        User user = findById(id);
        mapper.updateUserFromDto(dto, user);
        userRepository.save(user);
        return user;
    }*/

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

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        User user = findById(id);
        userRepository.delete(user);
    }

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

}
