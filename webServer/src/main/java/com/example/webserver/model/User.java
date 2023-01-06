package com.example.webserver.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Table(name = "user", schema = "public")
@Getter
@Setter
@ToString
public class User implements Serializable {

    @Id
    @Column(name = "id_user",nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "user_name",nullable = false)
    private String login;

    @Column(name = "email",nullable = false)
    private String email;
    @Column(name = "user_password",nullable = false)
    private String password;
    @Column(name = "update_db_time",nullable = false)
    private String updateDbTime;
    transient private String matchingPassword;

    public User(Long id, String userName, String password, String matchingPassword) {
        this.id = id;
        this.login = userName;
        this.password = password;
        this.matchingPassword = matchingPassword;
    }

    public User(Long id, String userName, String password) {
        this.id = id;
        this.login = userName;
        this.password = password;
    }

    public User(String userName, String password) {
        this.login = userName;
        this.password = password;
    }

    public User() {}


}
