package com.example.webserver.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
public class SubjectQuestion {


    private Long id;

    private String name;

    private String days;

    private Boolean completed;

    private User userId;

    transient private List<Question> questions;

    public SubjectQuestion() {}


    public SubjectQuestion(Subject subject, ArrayList<Question> questions) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.days = subject.getDays();
        this.completed = subject.getCompleted();
        this.userId = subject.getUserId();
        this.questions = questions;
    }
}
