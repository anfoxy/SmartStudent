package com.example.webserver.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "plan", schema = "public")
@Getter
@Setter
public class Plan {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "number_of_questions", nullable = false)
    private int numberOfQuestions;
    @ManyToOne
    @JoinColumn(name = "id_sub")
    private Subject subId;

    @Column(name = "bool_date",nullable = false)
    private boolean boolDate;

    public Plan() {
    }

    public Plan(Long id, String date, Integer numberOfQuestions, Subject subId) {
        this.id = id;
        this.date = date;
        this.numberOfQuestions = numberOfQuestions;
        this.subId = subId;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", numberOfQuestions=" + numberOfQuestions +
                ", boolDate=" + boolDate +
                '}';
    }
}
