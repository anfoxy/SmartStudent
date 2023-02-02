package com.example.studentapp.db;

import com.google.gson.annotations.SerializedName;

public class GameHistory {

    @SerializedName("id")
    private Integer id;

    @SerializedName("userId")
    private Users userId;


    @SerializedName("gameId")
    private Game gameId;

    public GameHistory(Integer id, Users userId, Game gameId) {
        this.id = id;
        this.userId = userId;
        this.gameId = gameId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public Game getGameId() {
        return gameId;
    }

    public void setGameId(Game gameId) {
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        return "GameHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", gameId=" + gameId +
                '}';
    }
}
