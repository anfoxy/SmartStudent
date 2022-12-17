package com.example.webserver.dto;

import com.example.webserver.model.User;
import lombok.Getter;

import javax.persistence.Column;

@Getter
public class UserDTO {

    private Long id;

    private String login;

    private String password;

    private String matchingPassword;


    public UserDTO(Long id, String login,String password,  String matchingPassword) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.matchingPassword = matchingPassword;
    }

    public UserDTO(User c) {
        this.id = c.getId();
        this.login = c.getLogin();
        this.password = c.getPassword();
        this.matchingPassword = c.getMatchingPassword();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User convertToEntity() {
        User c = new User();
        c.setId(id);
        c.setLogin(login);
        c.setPassword(password);
        c.setMatchingPassword(matchingPassword);
        return c;
    }
}
