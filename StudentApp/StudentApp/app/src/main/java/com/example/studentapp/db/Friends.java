package com.example.studentapp.db;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.paperdb.Paper;

public class Friends {

    @SerializedName("id")
    private Integer id;
    @SerializedName("status")
    private String status;
    @SerializedName("friendId")
    private Users friendId;
    @SerializedName("userId")
    private Users userId;

    public Friends(Integer id, String status, Users friendId, Users userId) {
        this.id = id;
        this.status = status;
        this.friendId = friendId;
        this.userId = userId;
    }

    public Friends() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users getFriendId() {
        return friendId;
    }

    public void setFriendId(Users friendId) {
        this.friendId = friendId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", friendId=" + friendId +
                ", userId=" + userId +
                '}';
    }
}
