package com.example.webserver.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "game_history", schema = "public")
@Getter
@Setter
public class GameHistory {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "id_user")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "id_game")
    private Game gameId;

    public GameHistory() {
    }

    public GameHistory(Long id, User userId, Game gameId) {
        this.id = id;
        this.userId = userId;
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
