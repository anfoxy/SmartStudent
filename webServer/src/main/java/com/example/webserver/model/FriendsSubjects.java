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



    public FriendsSubjects() {
    }

}
