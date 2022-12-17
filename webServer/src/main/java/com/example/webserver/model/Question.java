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

    @Column(name = "is_completed", nullable = false)
    private Boolean completed;

    @ManyToOne
    @JoinColumn(name = "subId")
    private Subject subId;

    public Question() {
    }

    public Question(Question q) {
        this.id = q.id;
        this.question = q.question;
        this.answer = q.answer;
        this.completed = q.completed;
        this.subId = q.subId;
    }

}
