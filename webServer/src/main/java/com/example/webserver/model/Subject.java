package com.example.webserver.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "subject", schema = "public")
@Getter
@Setter

public class Subject {

    @Id
    @Column(name = "id_subject",nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "subject_name",nullable = false)
    private String name;
    @Column(name = "subject_date",nullable = false)
    private String days;
    @Column(name = "today_learned",nullable = false)
    private int todayLearned;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    transient private List<Question> questions;
    transient private List<Plan> plans;
    public Subject() {}

    public Subject(Long id, String name, String date, int todayLearned, User idUser) {
        this.id = id;
        this.name = name;
        this.days = date;
        this.todayLearned = todayLearned;
        this.userId = idUser;
    }

    public Subject(String name, String date, int todayLearned, User idUser) {
        this.name = name;
        this.days = date;
        this.todayLearned = todayLearned;
        this.userId = idUser;
    }

    public Subject(Subject subject) {
        this.id = subject.id;
        this.name = subject.name;
        this.days = subject.days;
        this.todayLearned = subject.todayLearned;
        this.userId = subject.userId;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", days='" + days + '\'' +
                ", todayLearned=" + todayLearned +
                ", userId=" + userId +
                ", questions=" + questions +
                ", plans=" + plans +
                '}';
    }
}
