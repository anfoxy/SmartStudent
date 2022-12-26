package com.example.webserver.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "friend_sub", schema = "public")
@Getter
@Setter
@ToString
public class FriendsSubjects {
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

    @ManyToOne
    @JoinColumn(name = "id_sub")
    private Subject subId;


    public FriendsSubjects() {
    }

    public FriendsSubjects(Long id, String status, User friendId, User userId, Subject subId) {
        this.id = id;
        this.status = status;
        this.friendId = friendId;
        this.userId = userId;
        this.subId = subId;
    }
}
