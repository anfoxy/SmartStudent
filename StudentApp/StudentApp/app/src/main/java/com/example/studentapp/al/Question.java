package com.example.studentapp.al;

import java.time.LocalDate;

public class Question {

    private String question; //Вопрос
    private String answer; //Ответ
    private LocalDate lastDate; //Дата, когда попадался этот вопрос последний раз
    private Integer sizeOfView; //сколько раз попадался этот вопрос //так же это показатель был вопрос или нет.
    private Double percentKnow; //Процент знаний
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question() {
        question = "";
        answer = "";
        lastDate = LocalDate.now();
        sizeOfView = 0;
        percentKnow=0.0;
    }

    public Question(String quest, String answer, Integer id) {
        this.question = quest;
        this.answer = answer;
        lastDate = LocalDate.now();
        sizeOfView = 0;
        percentKnow=0.0;
        this.id= id;
    }

    public void Copy(Question quest) {
        this.answer = quest.answer;
        this.question = quest.question;
        lastDate = quest.lastDate;
        sizeOfView = quest.sizeOfView;
        percentKnow=quest.percentKnow;
        this.id= quest.id;
    }

    public Question(String quest, String answer) {
        this.question = quest;
        this.answer = answer;
        lastDate = LocalDate.now();
        sizeOfView = 0;
        percentKnow=0.0;
    }

    public Question(String quest, String answer,  LocalDate lastDate, Integer sizeOfView, Double percentKnow) {
        this.question = quest;
        this.answer = answer;
        this.lastDate=lastDate;
        if(sizeOfView>=0) this.sizeOfView = sizeOfView;
        else throw new ArithmeticException
                ("Question.sizeOfView отрицательное, мы не можем посмотреть предмет -1 раз");
        if(percentKnow>=0&&percentKnow<=1){
            this.percentKnow=percentKnow;
        } else {
            throw new ArithmeticException
                    ("Нельзя в вероятность засовывать отрицательное число, либо больше 1.");
        }
        if(lastDate.isAfter(LocalDate.now()))
            throw new ArithmeticException
                    ("Сегодня- "+LocalDate.now().toString()+", а дата повторения-"+lastDate.toString()+
                            "/n Каким образом мы могли повторить вопрос в будущем?");
        //?? Забиваем question,  answer, last_date, size_of_view, know
    }

    //--------------Для обработчика нажатия на кнопку----------------
    public void сhangePrcentAndSize(Double percentKnow)
    {
        if(percentKnow>=0&&percentKnow<=1){
            lastDate=LocalDate.now();
            //должен установить текущее время
            if(this.percentKnow<1.0) sizeOfView++;
            this.percentKnow=percentKnow;
        } else{
            throw new ArithmeticException
                    ("Нельзя в вероятность засовывать отрицательное число либо больше 1.");
        }
    }

    //-----------------Возврат значений----------------
    public String getQuestion(){
        return question;
    }
    public String getAnswer(){
        return answer;
    }
    public LocalDate getLastDate(){
        return lastDate;
    }

    public Integer getSizeOfView(){
        return sizeOfView;
    }
    public Double getPercentKnow(){
        return percentKnow;
    }

    //----------------Установка значений-----------------
    public void setQuestion(String question){
        this.question=question;
        lastDate=LocalDate.now();
        sizeOfView = 0;
        percentKnow=0.0;
    }
    public void setAnswer(String answer){
        this.answer=answer;
        lastDate=LocalDate.now();
        sizeOfView = 0;
        percentKnow=0.0;
    }
    public String dateToString() {
        String dateStr;
        if (lastDate.getMonthValue() >=10){
            if (lastDate.getDayOfMonth()>=10){
                dateStr = lastDate.getYear()+"-"+(lastDate.getMonthValue())+"-"+lastDate.getDayOfMonth();
            }else {
                dateStr = lastDate.getYear()+"-"+lastDate.getMonthValue()+"-0"+lastDate.getDayOfMonth();
            }
        } else {
            if (lastDate.getDayOfMonth()>=10){
                dateStr = lastDate.getYear()+"-0"+lastDate.getMonthValue()+"-"+lastDate.getDayOfMonth();
            }else {
                dateStr = lastDate.getYear()+"-0"+lastDate.getMonthValue()+"-0"+lastDate.getDayOfMonth();
            }
        }
        return dateStr;
    }

}