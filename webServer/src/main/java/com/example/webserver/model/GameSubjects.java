package com.example.webserver.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "game_question", schema = "public")
@Getter
@Setter
public class GameSubjects {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_game")
    private Game gameId;


    @JoinColumn(name = "question")
    private String question;


    @JoinColumn(name = "answer")
    private String answer;


    @JoinColumn(name = "answer_host")
    private String answerHost;

    @JoinColumn(name = "answer_friend")
    private String answerFriend;

    @JoinColumn(name = "result_host")
    private Integer resultHost;

    @JoinColumn(name = "result_friend")
    private Integer resultFriend;



    public GameSubjects() {
    }

    public GameSubjects(Long id) {
        this.id = id;
    }

    public GameSubjects(Long id, Game gameId, String question, String answer,
                        String answerHost, String answerFriend,
                        Integer resultHost, Integer resultFriend) {
        this.id = id;
        this.gameId = gameId;
        this.answer = answer;
        this.question = question;
        this.answerHost = answerHost;
        this.answerFriend = answerFriend;
        this.resultHost = resultHost;
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
