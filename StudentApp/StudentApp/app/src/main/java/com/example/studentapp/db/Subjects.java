package com.example.studentapp.db;

import com.example.studentapp.MainActivity;
import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.al.Subject;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
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


    public PlanToSub getPlanToSub(){
        ArrayList<PlanToDay> f = new ArrayList<>();
        ArrayList<PlanToDay> l = new ArrayList<>();
        ArrayList<Question> question= new ArrayList<>();
        boolean flag = true;
        for (int i = 0 ; i < getPlans().size();i++){
            if(getPlans().get(i).isBoolDate()) flag = false;

            String[] parts = getPlans().get(i).getDate().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int  day = Integer.parseInt(parts[2]);
            LocalDate date_local1 = LocalDate.of(year, month, day);
            PlanToDay plan = new PlanToDay(date_local1, getPlans().get(i).getNumberOfQuestions());
            plan.setId(getPlans().get(i).getId());

            if(flag) l.add(plan);
            else f.add(plan);

        }
        for(Questions q : getQuestions()){
            String[] parts = q.getDate().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            LocalDate date_local1 = LocalDate.of(year, month, day);
            question.add(new Question(q.getQuestion(), q.getAnswer(),  date_local1, q.getSizeOfView(), q.getPercentKnow()));

        }
        Subject sub_ = new Subject(getName(),question);

        String[] parts = getDays().split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int  day = Integer.parseInt(parts[2]);
        LocalDate date_of_exams1 = LocalDate.of(year, month, day);
        PlanToSub planToSub = new PlanToSub(sub_, getTodayLearned(), date_of_exams1, f, l);
        planToSub.setId(getId());
       return planToSub;
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
