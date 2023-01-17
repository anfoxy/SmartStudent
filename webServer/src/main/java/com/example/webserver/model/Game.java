package com.example.webserver.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "game", schema = "public")
@Getter
@Setter
public class Game {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "id_friend")
    private Friends friendId;

    @JoinColumn(name = "status")
    private String status;
    //STARTED - начата
    //END - закончена
    //EXPECTED - хост ожидает друга
    //ACCEPTED - друг принял, ожидаем запуск от хоста

    @JoinColumn(name = "date")
    private String date;

    @JoinColumn(name = "number_of_que")
    private Integer numberOfQue;
    transient private Subject subId;
    public Game() {
    }

    public Game(Long id, Subject subId, Friends friendId, String status) {
        this.id = id;
        this.subId = subId;
        this.friendId = friendId;
        this.status = status;
    }
    public Game(String status) {

        this.status = status;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", subId=" + subId +
                ", friendId=" + friendId +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                ", numberOfQue=" + numberOfQue +
                '}';
    }
}
