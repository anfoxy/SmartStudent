package com.example.studentapp.db;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.paperdb.Paper;

public class Game {

    @SerializedName("id")
    private Integer id;
    @SerializedName("subId")
    private Subjects subId;
    @SerializedName("friendId")
    private Friends friendId;
    @SerializedName("status")
    private String status;
    @SerializedName("date")
    private String date;
    @SerializedName("name")
    private String name;
    @SerializedName("numberOfQue")
    private Integer numberOfQue;

    public Game(Integer id, Subjects subId, Friends friendId, String date,Integer numberOfQue,String name) {
        this.id = id;
        this.subId = subId;
        this.friendId = friendId;
        this.date = date;
        this.numberOfQue = numberOfQue;
        this.name = name;
    }

    public Game(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Subjects getSubId() {
        return subId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSubId(Subjects subId) {
        this.subId = subId;
    }

    public Friends getFriendId() {
        return friendId;
    }

    public void setFriendId(Friends friendId) {
        this.friendId = friendId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNumberOfQue() {
        return numberOfQue;
    }

    public void setNumberOfQue(Integer numberOfQue) {
        this.numberOfQue = numberOfQue;
    }


    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", subId=" + subId +
                ", friendId=" + friendId +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", numberOfQue=" + numberOfQue +
                '}';
    }
}
