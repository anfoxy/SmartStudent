package com.example.studentapp.al;

import java.time.LocalDate;

public class PlanToDay {
    private LocalDate date;
    private int sizeOfQuetion;
    private Integer id;

    public Boolean getBoolPlan() {
        return boolPlan;
    }

    public void setBoolPlan(Boolean boolPlan) {
        this.boolPlan = boolPlan;
    }

    private Boolean boolPlan;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PlanToDay(LocalDate date_, int size_of_quet){
        date=  date_;
        sizeOfQuetion =size_of_quet;
    }

    public void changeSizeOfQuetion(int a){
        sizeOfQuetion =a;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate localDate) {
        date=localDate;
    }

    public int getSizeOfQuetion(){
        return sizeOfQuetion;
    }
    public void setSizeOfQuetion(int sizeOfQ){
        sizeOfQuetion=sizeOfQ;
    }
    public String dateToString() {
        String dateStr;
        if (date.getMonthValue() >=10){
            if (date.getDayOfMonth()>=10){
                dateStr = date.getYear()+"-"+(date.getMonthValue())+"-"+date.getDayOfMonth();
            }else {
                dateStr = date.getYear()+"-"+date.getMonthValue()+"-0"+date.getDayOfMonth();
            }
        } else {
            if (date.getDayOfMonth()>=10){
                dateStr = date.getYear()+"-0"+date.getMonthValue()+"-"+date.getDayOfMonth();
            }else {
                dateStr = date.getYear()+"-0"+date.getMonthValue()+"-0"+date.getDayOfMonth();
            }
        }
        return dateStr;
    }
}
