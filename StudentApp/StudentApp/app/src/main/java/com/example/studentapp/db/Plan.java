package com.example.studentapp.db;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.paperdb.Paper;

public class Plan {

    @SerializedName("id")
    private Integer id;
    @SerializedName("date")
    private String date;
    @SerializedName("numberOfQuestions")
    private int numberOfQuestions;
    @SerializedName("subId")
    private Subjects subId;
    @SerializedName("boolDate")
    private boolean boolDate;

    public Plan(Integer id, String date, int numberOfQuestions, Subjects subId,boolean boolDate) {
        this.id = id;
        this.date = date;
        this.numberOfQuestions = numberOfQuestions;
        this.subId = subId;
        this.boolDate = boolDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int isNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Subjects getSubId() {
        return subId;
    }

    public void setSubId(Subjects subId) {
        this.subId = subId;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public boolean isBoolDate() {
        return boolDate;
    }

    public void setBoolDate(boolean boolDate) {
        this.boolDate = boolDate;
    }

    public static void saveListQuestions(ArrayList<Plan> plans){
        Paper.book("plan").write("plan", plans);
    }

    public static ArrayList<Plan> getPlans(){
        ArrayList<Plan> plans =  Paper.book("plan").read("plan");
        if (plans == null){
            return new ArrayList<Plan>();
        }else {
            return plans;
        }
    }

    public static void addPlan(Plan plan){
        ArrayList<Plan> questions = getPlans();
        questions.add(plan);
        saveListQuestions(questions);
    }

    public static void deletePlan(int position){
        ArrayList<Plan> plan = getPlans();
        plan.remove(plan.get(position));
        saveListQuestions(plan);
    }

    public static void updatePlan(int position, String date, int count){
        ArrayList<Plan> questions = getPlans();
        Plan questions1  = new Plan(0, date, count,  null,false);
        questions.remove(questions.get(position));
        questions.add(questions1);
        saveListQuestions(questions);
    }

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", NumberOfQuestions=" + numberOfQuestions +
            //    ", subId=" + subId +
                '}';
    }
}