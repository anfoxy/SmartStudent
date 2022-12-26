package com.example.studentapp.al;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class Subject {

    private String nameOfSub;
    private ArrayList<Question> question;

    public Subject() {
        nameOfSub ="";
        question=new ArrayList<Question>();
    }
    public Subject(String name) {
        this.nameOfSub =name;
        this.question=new ArrayList<Question>();
    }
    public Subject(String name, ArrayList<Question> arr_questions) {
        nameOfSub =name;
        question=arr_questions;
    }

    public void addQuestion(Question quest)
    {
        question.add(quest);
    }
    public void deleteQuestion(int nom)
    {
        if(nom>=0&&nom<question.size()) question.remove(nom);
    }
    public void changeQuestion (int nom, String quest, String answer){
        if(nom>=0&&nom<question.size()) {
            question.get(nom).setAnswer(answer);
            question.get(nom).setQuestion(quest);
        }else{
            throw new ArithmeticException
                    ("В предмете "+nameOfSub+" "+ question.size()+" вопросов, а ты пытаешься работать с "+ nom+1);
        }
    }

    public Question getQuestion (int nom){
        if(nom < question.size() && nom >= 0) return question.get(nom);
        else throw new ArithmeticException
                ("В предмете "+nameOfSub+" "+ question.size()+" вопросов, а ты пытаешься работать с "+ nom+1);
    }


    //--------------------Возврат по размерам-------------------
    public int getSizeAllQuest(){
        //количество вопросов
        return question.size();
    }
    public int getSizeKnow(){
        int d=0;
        for(int i=0; i<question.size(); i++){
            if(question.get(i).getPercentKnow()==1.0) d++;
        }
        return d;
    }
    public int getSizeNoKnow() {
        int d=0;
        for(int i=0; i<question.size(); i++){
            if(question.get(i).getPercentKnow()!=1.0) d++;
        }
        return d;
    }

    //----------------------Работа с именем---------------------
    public String getNameOfSubme(){
        return nameOfSub;
    }

    public void setNameOfSub(String nameOfSub) {
        this.nameOfSub = nameOfSub;
    }

    //--------------------Выдача вопросов для алгоритма---------
    public int getPositionOldestDay(){
        Integer r=-1;
        LocalDate res=LocalDate.now();
            for (int i = 0; i < question.size(); i++) {
                if(res.isAfter(question.get(i).getLastDate())&&question.get(i).getPercentKnow()==1.0) {
                    res = question.get(i).getLastDate();
                    r=i;
                }
            }
            return r;
    }

    public int getPositionHardQuestionAndNotToDay(){
        Integer r=-1;
        Integer hard=0;
        LocalDate today=LocalDate.now();
        if(question.size()>0){
            for (Integer i = 0; i < question.size(); i++) {
                if(question.get(i).getPercentKnow()==1.0
                        &&!question.get(i).getLastDate().isEqual(today)) {
                    if(hard<question.get(i).getSizeOfView())
                    r=i;
                    hard=question.get(i).getSizeOfView();
                }
            }
        }
        return  r;
    }

    public int getPositionNoKnowNoViev(){
        for (int i = 0; i < question.size(); i++) {
            if(question.get(i).getSizeOfView()==0) return i;
        }
        return -1;
    }
    public ArrayList<Integer> getArrListVievAndNoKnow(){
        ArrayList<Integer> res=new ArrayList<>();
        for (int i = 0; i < question.size(); i++) {
            if(question.get(i).getSizeOfView()>0
            && question.get(i).getPercentKnow()<1.0){
                res.add(i);
            }
        }
        return res;
    }
    public ArrayList<Integer> getSortArrListVievAndNoKnow(){
        ArrayList<Integer> res=new ArrayList<>();
        for (int i = 0; i < question.size(); i++) {
            if(question.get(i).getSizeOfView()>0
                    && question.get(i).getPercentKnow()<1.0){
                res.add(i);
            }
        }
        if(res.size()>0){
            int i1=0;
            Double hard=
            question.get(res.get(0)).getPercentKnow();
            for(int i=0; i<res.size()-1; i++){
                for(int j=i+1; j<res.size(); j++){
                    double a=question.get(res.get(i)).getPercentKnow();
                    double b=question.get(res.get(j)).getPercentKnow();
                    if(a>b) Collections.swap(res, i, j);
                }
            }
        }
        return res;
    }
    public ArrayList<Question> getQuestion(){
        return question;
    }
}
