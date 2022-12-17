package com.example.studentapp.db;

import com.google.gson.annotations.SerializedName;

import io.paperdb.Paper;

public class Users {

    @SerializedName("id")
    private int id;
    @SerializedName("login")
    private String login;
    @SerializedName("password")
    private String password;

    public Users(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public static void writeUser(Users user){
        Paper.book("user").write("user", user);
    }

    public static Users getUser(){
        return Paper.book("user").read("user");
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
