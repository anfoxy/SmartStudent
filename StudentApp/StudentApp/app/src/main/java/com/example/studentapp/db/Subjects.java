package com.example.studentapp.db;

import com.example.studentapp.MainActivity;
import com.example.studentapp.al.PlanToSub;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Subjects {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("days")
    private String days;
    @SerializedName("todayLearned")
    private int todayLearned;
    @SerializedName("userId")
    private Users userId;

    @SerializedName("questions")
    private ArrayList<Questions> questions;
    @SerializedName("plans")
    private ArrayList<Plan> plans;

    public Subjects(Users userId) {
        this.userId = userId;
    }

    public Subjects(int id, String name, String days, int todayLearned, Users userId, ArrayList<Questions> questions, ArrayList<Plan> plans) {
        this.id = id;
        this.name = name;
        this.days = days;
        this.todayLearned = todayLearned;
        this.userId = userId;
        this.questions = questions;
        this.plans = plans;
    }

    public Subjects(int id, String name, String days, int todayLearned, Users userId) {
        this.id = id;
        this.name = name;
        this.days = days;
        this.todayLearned = todayLearned;
        this.userId = userId;
    }
    public Subjects(Subjects subjects) {
        this.id = subjects.id;
        this.name =  subjects.name;
        this.days =  subjects.days;
        this.todayLearned =  subjects.todayLearned;
        this.userId =  subjects.userId;
        this.questions =  subjects.questions;
        this.plans =  subjects.plans;
    }



    public ArrayList<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Questions> questions) {
        this.questions = questions;
    }

    public String getDaysString() {
        return days;
    }

    public void setDaysString(String days) {
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public ArrayList<Plan> getPlans() {
        return plans;
    }

    public void setPlans(ArrayList<Plan> plans) {
        this.plans = plans;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public int getTodayLearned() {
        return todayLearned;
    }

    public void setTodayLearned(int todayLearned) {
        this.todayLearned = todayLearned;
    }

    @Override
    public String toString() {
        return "Subjects{" +
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
