package com.example.studentapp.db;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.paperdb.Paper;

public class Questions {

    @SerializedName("id")
    private Integer id;
    @SerializedName("question")
    private String question;
    @SerializedName("answer")
    private String answer;

    @SerializedName("date")
    private String date;

    @SerializedName("percentKnow")
    private double percentKnow;

    @SerializedName("sizeOfView")
    private int sizeOfView;

    @SerializedName("subId")
    private Subjects subId;

    public Questions(Integer id, String question, String answer, String date,double percentKnow,int sizeOfView, Subjects sub_id) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.percentKnow = percentKnow;
        this.sizeOfView = sizeOfView;
        this.date = date;
        this.subId = sub_id;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPercentKnow() {
        return percentKnow;
    }

    public void setPercentKnow(double percentKnow) {
        this.percentKnow = percentKnow;
    }

    public int getSizeOfView() {
        return sizeOfView;
    }

    public void setSizeOfView(int sizeOfView) {
        this.sizeOfView = sizeOfView;
    }

    public Subjects getSubId() {
        return subId;
    }

    public void setSubId(Subjects subId) {
        this.subId = subId;
    }

    public static void saveListQuestions(ArrayList<Questions> questions){
        Paper.book("questions").write("questions", questions);
    }

    public static ArrayList<Questions> getQuestions(){
       ArrayList<Questions> questions =  Paper.book("questions").read("questions");
       if (questions == null){
           return new ArrayList<Questions>();
       }else {
           return questions;
       }
    }

    public static void addQuestion(Questions question){
        ArrayList<Questions> questions = getQuestions();
        questions.add(question);
        saveListQuestions(questions);
    }

    public static void deleteQuestion(int position){
        ArrayList<Questions> questions = getQuestions();
        questions.remove(questions.get(position));
        saveListQuestions(questions);
    }

    public static void updateQuestion(int position, String text, String answer){
        ArrayList<Questions> questions = getQuestions();
        Questions questions1  = new Questions(0, text, answer, "",0,0, null);
        questions.remove(questions.get(position));
        questions.add(questions1);
        saveListQuestions(questions);
    }

    @Override
    public String toString() {
        return "Questions{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", date='" + date + '\'' +
                ", percentKnow='" + percentKnow + '\'' +
                ", sizeOfView='" + sizeOfView + '\'' +
                ", subId=" + subId +
                '}';
    }
}
