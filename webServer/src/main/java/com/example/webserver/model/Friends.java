package com.example.webserver.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "friend", schema = "public")
@Getter
@Setter
@ToString
public class Friends {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_friend")
    private User friendId;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User userId;

    public Friends() {
    }

    public Friends(Long id, String status, User friendId, User userId) {
        this.id = id;
        this.status = status;
        this.friendId = friendId;
        this.userId = userId;
    }

}
