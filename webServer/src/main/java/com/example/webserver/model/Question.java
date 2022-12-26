package com.example.webserver.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "question", schema = "public")
@Getter
@Setter
@ToString
public class Question {

    @Id
    @Column(name = "id_questions", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "question_text", nullable = false)
    private String question;

    @Column(name = "question_answer", nullable = false)
    private String answer;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "percent_know", nullable = false)
    private double percentKnow;

    @Column(name = "size_of_view", nullable = false)
    private int sizeOfView;

    @ManyToOne
    @JoinColumn(name = "subId")
    private Subject subId;


    public Question() {
    }

    public Question(Long id, String question, String answer, String date, double percentKnow, int sizeOfView, Subject subId) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.date = date;
        this.percentKnow = percentKnow;
        this.sizeOfView = sizeOfView;
        this.subId = subId;
    }

    public Question(Question q) {
        this.id = q.id;
        this.question = q.question;
        this.answer = q.answer;
        this.percentKnow = q.percentKnow;
        this.sizeOfView = q.sizeOfView;
        this.date = q.date;
        this.subId = q.subId;
    }

}
