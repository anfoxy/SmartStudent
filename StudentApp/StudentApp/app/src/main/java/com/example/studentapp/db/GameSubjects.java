package com.example.studentapp.db;

import com.google.gson.annotations.SerializedName;

public class GameSubjects {

    @SerializedName("id")
    private Integer id;

    @SerializedName("gameId")
    private Game gameId;


    @SerializedName("question")
    private String question;


    @SerializedName("answer")
    private String answer;

    @SerializedName("answerHost")
    private String answerHost;

    @SerializedName("answerFriend")
    private String answerFriend;

    @SerializedName("resultHost")
    private Integer resultHost;

    @SerializedName("resultFriend")
    private Integer resultFriend;


    public GameSubjects(Integer id, Game gameId,
                        String question, String answer,
                        String answerHost, String answerFriend,
                        Integer resultHost, Integer resultFriend) {
        this.id = id;
        this.gameId = gameId;
        this.question = question;
        this.answer = answer;
        this.answerHost = answerHost;
        this.answerFriend = answerFriend;
        this.resultHost = resultHost;
        this.resultFriend = resultFriend;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGameId() {
        return gameId;
    }

    public void setGameId(Game gameId) {
        this.gameId = gameId;
    }


    public String getAnswerHost() {
        return answerHost;
    }

    public void setAnswerHost(String answerHost) {
        this.answerHost = answerHost;
    }

    public String getAnswerFriend() {
        return answerFriend;
    }

    public void setAnswerFriend(String answerFriend) {
        this.answerFriend = answerFriend;
    }

    public Integer getResultHost() {
        return resultHost;
    }

    public void setResultHost(Integer resultHost) {
        this.resultHost = resultHost;
    }

    public Integer getResultFriend() {
        return resultFriend;
    }

    public void setResultFriend(Integer resultFriend) {
        this.resultFriend = resultFriend;
    }

    @Override
    public String toString() {
        return "GameSubjects{" +
                "id=" + id +
                ", gameId=" + gameId +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", answerHost='" + answerHost + '\'' +
                ", answerFriend='" + answerFriend + '\'' +
                ", resultHost=" + resultHost +
                ", resultFriend=" + resultFriend +
                '}';
    }
}
