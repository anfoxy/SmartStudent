package com.example.studentapp.al;

import java.time.LocalDate;
import java.util.ArrayList;

public class Study {
    private Subject sub; //предмет передаем ТОЛЬКО из PLAN_TO_SUB
    private int planLearned; //сколько нужно выучить сегодня
    private int nowLearned; //сколько выучено уже
    private ArrayList<Integer> positionsQuestion;
    private int nom;
    private int nomQuestNow;
    private boolean flagRepeat;

    public Study(Subject s, int plan_learned_today, int now_learn){
        sub=s;
        planLearned =plan_learned_today;
        nowLearned =now_learn;
        positionsQuestion=new ArrayList<Integer>();
        nom=0; //какой по счету вопрос, каждый 3й вопрос- повтор.
        flagRepeat=false; //f- самый старый, е-самый сложный.
        positionsQuestion=sub.getSortArrListVievAndNoKnow();
        nomQuestNow=-1;
    }
    public Question GetNewQuestion(){
        Question res;
        if(positionsQuestion.size()>nom){
            res=sub.getQuestion(positionsQuestion.get(nom));
            nomQuestNow=positionsQuestion.get(nom);
            nom++;
            return res;
        } else {//Если нет недоученных
            Integer n=sub.getPositionNoKnowNoViev();
            if(n!=-1){
                nomQuestNow=n;
                nom=0;
                return sub.getQuestion(n); //новый
            } else
            //если нет новых, то забиваем повтором.
            {
                nom=0;
                if(flagRepeat){
                    n=sub.getPositionHardQuestionAndNotToDay();
                    if(n!=-1) {
                        nomQuestNow=n;
                        flagRepeat=false;
                        return sub.getQuestion(n);
                    }//самый сложный
                    //если за сегодня нет и мы все самые сложные повторили
                    else{
                        n=sub.getPositionOldestDay();
                        if(n!=-1) return sub.getQuestion(n); //повторяем самый старый
                        else{
                            // Вот тут начинается рандом...
                            n = 0 + (int) ( Math.random() * sub.getSizeAllQuest());
                            nomQuestNow=n;
                            return sub.getQuestion(n);
                        }
                    }
                } else {
                    n = sub.getPositionOldestDay();
                    if (n != -1) {
                        nomQuestNow=n;
                        flagRepeat=false;
                        return sub.getQuestion(n); //самый сложный
                    }
                    //если за сегодня нет и мы все самые сложные повторили
                    else {
                        n = sub.getPositionHardQuestionAndNotToDay();
                        if (n != -1){
                            nomQuestNow=n;
                            return sub.getQuestion(n); //повторяем самый старый
                        }
                        else {
                            // Вот тут начинается рандом...
                            n = 0 + (int) (Math.random() * sub.getSizeAllQuest());
                            nomQuestNow=n;
                            return sub.getQuestion(n);
                        }
                    }
                }
            }
        }
    }

    public void clickReady(Double res){
        if(res==1.0&&sub.getQuestion(nomQuestNow).getPercentKnow()!=1.0)
            nowLearned++;
        if(res!=1.0&&sub.getQuestion(nomQuestNow).getPercentKnow()==1.0)
            nowLearned--;
        if(nomQuestNow!=-1) sub.getQuestion(nomQuestNow).сhangePrcentAndSize(res);
    }

    public boolean isEndOfPlan(){
        if(planLearned==nowLearned) return true;
        return false;
    }

}