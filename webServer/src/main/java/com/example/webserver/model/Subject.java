package com.example.webserver.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subject", schema = "public")
@Getter
@Setter
@ToString
public class Subject {

    @Id
    @Column(name = "id_subject",nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "subject_name",nullable = false)
    private String name;
    @Column(name = "subject_date",nullable = false)
    private String days;
    @Column(name = "is_completed",nullable = false)
    private Boolean completed;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    transient private List<Question> questions;
    transient private List<Plan> plans;
    public Subject() {}

    public Subject(Long id, String name, String date, Boolean isCompleted, User idUser) {
        this.id = id;
        this.name = name;
        this.days = date;
        this.completed = isCompleted;
        this.userId = idUser;
    }

    public Subject(String name, String date, Boolean isCompleted, User idUser) {
        this.name = name;
        this.days = date;
        this.completed = isCompleted;
        this.userId = idUser;
    }

    public Subject(Subject subject) {
        this.id = subject.id;
        this.name = subject.name;
        this.days = subject.days;
        this.completed = subject.completed;
        this.userId = subject.userId;
    }
}
