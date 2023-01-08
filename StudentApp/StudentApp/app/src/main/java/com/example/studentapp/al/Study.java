package com.example.studentapp.al;

import java.time.LocalDate;
import java.util.ArrayList;

public class Study {
    private PlanToSub planToSub; //предмет передаем ТОЛЬКО из PLAN_TO_SUB
    private int planLearned; //сколько нужно выучить сегодня //

    private int nowLearned; //сколько выучено уже
    private ArrayList<Integer> positionsQuestion;
    private int nom;
    private int nomQuestNow;
    private boolean flagRepeat;

    public Study(PlanToSub planToSub){
        this.planToSub=planToSub;

        if(planToSub.getFuturePlan().size()>0)
            planLearned = planToSub.getFuturePlan().get(0).getSizeOfQuetion();
        else planLearned=0;

        nowLearned = planToSub.getTodayLearned();
        positionsQuestion=new ArrayList<Integer>();
        nom=0; //какой по счету вопрос, каждый 3й вопрос- повтор.
        flagRepeat=false; //f- самый старый, е-самый сложный.
        positionsQuestion=planToSub.getSub().getSortArrListVievAndNoKnow();
        nomQuestNow=-1;
    }
    public Question GetNewQuestion(){
        Question res;
        if(positionsQuestion.size()>nom){
            res=planToSub.getSub().getQuestion(positionsQuestion.get(nom));
            nomQuestNow=positionsQuestion.get(nom);
            nom++;
            return res;
        } else {//Если нет недоученных
            Integer n=planToSub.getSub().getPositionNoKnowNoViev();
            if(n!=-1){
                nomQuestNow=n;
                nom=0;
                return planToSub.getSub().getQuestion(n); //новый
            } else
            //если нет новых, то забиваем повтором.
            {
                nom=0;
                if(flagRepeat){
                    n=planToSub.getSub().getPositionHardQuestionAndNotToDay();
                    if(n!=-1) {
                        nomQuestNow=n;
                        flagRepeat=false;
                        return planToSub.getSub().getQuestion(n);
                    }//самый сложный
                    //если за сегодня нет и мы все самые сложные повторили
                    else{
                        n=planToSub.getSub().getPositionOldestDay();
                        if(n!=-1) return planToSub.getSub().getQuestion(n); //повторяем самый старый
                        else{
                            // Вот тут начинается рандом...
                            n = 0 + (int) ( Math.random() * planToSub.getSub().getSizeAllQuest());
                            nomQuestNow=n;
                            return planToSub.getSub().getQuestion(n);
                        }
                    }
                } else {
                    n = planToSub.getSub().getPositionOldestDay();
                    if (n != -1) {
                        nomQuestNow=n;
                        flagRepeat=false;
                        return planToSub.getSub().getQuestion(n); //самый сложный
                    }
                    //если за сегодня нет и мы все самые сложные повторили
                    else {
                        n = planToSub.getSub().getPositionHardQuestionAndNotToDay();
                        if (n != -1){
                            nomQuestNow=n;
                            return planToSub.getSub().getQuestion(n); //повторяем самый старый
                        }
                        else {
                            // Вот тут начинается рандом...
                            n = 0 + (int) (Math.random() * planToSub.getSub().getSizeAllQuest());
                            nomQuestNow=n;
                            return planToSub.getSub().getQuestion(n);
                        }
                    }
                }
            }
        }
    }

    public void clickReady(Double res){
        if(res==1.0&&planToSub.getSub().getQuestion(nomQuestNow).getPercentKnow()!=1.0)
            nowLearned++;
        if(res!=1.0&&planToSub.getSub().getQuestion(nomQuestNow).getPercentKnow()==1.0)
            nowLearned--;
        if(nomQuestNow!=-1) planToSub.getSub().getQuestion(nomQuestNow).сhangePrcentAndSize(res);
        planToSub.progress();
    }

    public boolean isEndOfPlan(){
        if(planLearned==nowLearned) return true;
        return false;
    }

}