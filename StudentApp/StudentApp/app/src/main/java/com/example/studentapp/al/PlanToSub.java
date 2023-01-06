package com.example.studentapp.al;


import com.example.studentapp.db.Plan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class PlanToSub {
    private Subject sub; //предмет
    private  ArrayList<PlanToDay> lastPlan; //прошлое
    private  ArrayList<PlanToDay> futurePlan; //планы на будущие дни
    private  int learnedBefore;
    private  int todayLearned; //сколько выучили именно сегодня //Это передается из бд
    private LocalDate dateOfExams; //Дата, когда будет экзамен //Это из бд
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PlanToSub(){
        sub=new Subject();
        futurePlan =new ArrayList<PlanToDay>();
        lastPlan=new ArrayList<PlanToDay>();
        todayLearned =0;
        this.dateOfExams=LocalDate.now();
    }

    public PlanToSub(Subject sub_, LocalDate dateOfExams){
        sub=sub_;
        futurePlan =new ArrayList<PlanToDay>();
        lastPlan=new ArrayList<PlanToDay>();
        todayLearned =0;
        this.dateOfExams=dateOfExams;
    }
    public PlanToSub(Subject sub_, int todaylearned1, LocalDate date_of_exams1, ArrayList<PlanToDay> planFuture, ArrayList<PlanToDay> planLast){
        sub=sub_;
        todayLearned =todaylearned1;
        dateOfExams =date_of_exams1;
        futurePlan =planFuture;
        lastPlan =planLast;
        learnedBefore=sub.getSizeKnow();
    }

    public void plusDayToPlan(LocalDate date){
        if(date.isBefore(dateOfExams)&&
                (date.isAfter(LocalDate.now())||date.isEqual(LocalDate.now())))
            if(!isHavePlan(date)){
                PlanToDay planToDay=new PlanToDay(date, 0);
                futurePlan.add(planToDay);
                newSizeQuestionOnFuture();
            }
    }

    public void minusDayToPlan(LocalDate date) {
        for (int i = 0; i < futurePlan.size(); i++) {
            if (futurePlan.get(i).getDate().isEqual(date)) {
                futurePlan.remove(i);
                break;
            }
        }
    }

    public boolean nextDay(){
        learnedBefore=sub.getSizeKnow();
        Boolean res=false;
        if(futurePlan.size()!=0){
            sortFuturePlan();
            if(futurePlan.get(0).getDate().isBefore(LocalDate.now())){
                futurePlan.get(0).setSizeOfQuetion(todayLearned);
                lastPlan.add(futurePlan.get(0));
                futurePlan.remove(0);
                todayLearned=0;
                res=true;
            }
            if(futurePlan.size()>0){
                while(futurePlan.get(0).getDate().isBefore(LocalDate.now())){
                    futurePlan.get(0).setSizeOfQuetion(todayLearned);
                    lastPlan.add(futurePlan.get(0));
                    futurePlan.remove(0);
                    todayLearned=0;
                    if(futurePlan.size()==0)break;
                }
                res=true;
            }
            newSizeQuestionOnFuture();
        }

        //должен план перейти из будущего в прошлое
        //Тут мы должны проверить, все ли прошедшие дни перешли в прошлое. LocalDate.now()
        return res;
    }

    public boolean FORTESTnextDay(LocalDate date){
        learnedBefore=sub.getSizeKnow();
        Boolean res=false;
        if(futurePlan.size()!=0){
            sortFuturePlan();
            if(futurePlan.get(0).getDate().isBefore(date)){
                futurePlan.get(0).setSizeOfQuetion(todayLearned);
                lastPlan.add(futurePlan.get(0));
                futurePlan.remove(0);
                todayLearned=0;
                res=true;
            }
            if(futurePlan.size()>0){
                while(futurePlan.get(0).getDate().isBefore(date)){
                    futurePlan.get(0).setSizeOfQuetion(todayLearned);
                    lastPlan.add(futurePlan.get(0));
                    futurePlan.remove(0);
                    todayLearned=0;
                    if(futurePlan.size()==0)break;
                }
                res=true;
            }
            newSizeQuestionOnFuture();
        }
        return res;
        //должен план перейти из будущего в прошлое
        //Тут мы должны проверить, все ли прошедшие дни перешли в прошлое.
    }

    //----------------Вспомогательные функции------------------

    public void setFuture(ArrayList<PlanToDay> sFuturePlan){
        futurePlan=sFuturePlan;
        newSizeQuestionOnFuture();
    }

    private boolean isHavePlan(LocalDate date) {
        for (int i = 0; i < futurePlan.size(); i++) {
            if (futurePlan.get(i).getDate().isEqual(date))
                return true;
        }
        return false;
    }
    private void sortFuturePlan(){
        for(int i=0; i<futurePlan.size()-1; i++){
            for(int j=i; j<futurePlan.size(); j++){
                LocalDate a=futurePlan.get(i).getDate();
                LocalDate b=futurePlan.get(j).getDate();
                if(a.isAfter(b))
                    Collections.swap(futurePlan, i, j);
            }
        }
    }
    private void newSizeQuestionOnFuture() {
        if(futurePlan.size()>0){
            sortFuturePlan();
            //всего нужно выучить
            int v=sub.getSizeNoKnow();
            int d=futurePlan.size();

            //если делится на цело
            if(v%d==0){
                int r=v/d;
                for (int i=0; i<futurePlan.size(); i++){
                    futurePlan.get(i).setSizeOfQuetion(r);
                }
            }else {
                //если вопросов больше чем дней
                if(v>d){
                    int i=0;
                    int r=v/d+1;
                    futurePlan.get(i).setSizeOfQuetion(r);
                    i++;
                    int sum=futurePlan.get(0).getSizeOfQuetion();
                    while((v-sum)%(d-i)!=0){
                        r=(v-sum)/(d-i);
                        futurePlan.get(i).setSizeOfQuetion(r);
                        i++;
                        sum=0;
                        for(int j=0; j<i; j++){
                            sum=sum+futurePlan.get(j).getSizeOfQuetion();
                        }
                    }
                    r=(v-sum)/(d-i);
                    for(int j=i; j<futurePlan.size(); j++){
                        futurePlan.get(j).setSizeOfQuetion(r);
                    }
                    //если вопросов меньше чем дней
                }else{
                    for(int i=0; i<futurePlan.size(); i++){
                        if(i<v) futurePlan.get(i).setSizeOfQuetion(1);
                        else futurePlan.get(i).setSizeOfQuetion(0);
                    }
                }
            }

        }

    }

    //--------------Функци Set и Get
    public void setNewQuestion(Question question){
        sub.addQuestion(question);
        newSizeQuestionOnFuture();
    }
    public void delQuestion(Integer nom){
        sub.deleteQuestion(nom);
        newSizeQuestionOnFuture();
    }
    public void changeQuestion(Integer nom, String quest, String answer){
        if(quest!=null) sub.changeQuestion(nom, quest, sub.getQuestion(nom).getAnswer());
        if(answer!=null) sub.changeQuestion(nom,  sub.getQuestion(nom).getQuestion(), answer);
        newSizeQuestionOnFuture();
    }

    public LocalDate getDateOfExams() {
        return dateOfExams;
    }

    public void setDateOfExams(LocalDate dateOfExams) {
        this.dateOfExams = dateOfExams;
    }

    public ArrayList<PlanToDay> getLastPlan(){
        ArrayList<PlanToDay> res=new ArrayList<>(lastPlan);
        return res;
    }
    public ArrayList<PlanToDay> getFuturePlan(){
        ArrayList<PlanToDay> res=new ArrayList<>(futurePlan);
        return res;
    }
    public Subject getSub(){
        return sub;
    }
    public int getTodayLearned() {
        return todayLearned;
    }
    public void progress() {
        if(learnedBefore<sub.getSizeKnow()) {
            learnedBefore=sub.getSizeKnow();
            todayLearned++;
        }else
        if(learnedBefore>sub.getSizeKnow()) {
            learnedBefore = sub.getSizeKnow();
            todayLearned--;
        }
    }

    public  ArrayList<Plan> getPlans(){
        ArrayList<Plan> res=new ArrayList<>();

        for(PlanToDay planToDay:lastPlan){
            res.add(new Plan(planToDay.getId(), planToDay.dateToString(), planToDay.getSizeOfQuetion(), null,false));
        }
        if(futurePlan.size()>0) res.add(new Plan(futurePlan.get(0).getId(), futurePlan.get(0).dateToString(), futurePlan.get(0).getSizeOfQuetion(), null,true));

        for( int i = 1 ; i < futurePlan.size(); i++){
            res.add(new Plan(futurePlan.get(i).getId(), futurePlan.get(i).dateToString(), futurePlan.get(i).getSizeOfQuetion(), null,false));
        }

        return res;
    }

    public String dateToString() {
        String dateStr;
        if (dateOfExams.getMonthValue() >=10){
            if (dateOfExams.getDayOfMonth()>=10){
                dateStr = dateOfExams.getYear()+"-"+(dateOfExams.getMonthValue())+"-"+dateOfExams.getDayOfMonth();
            }else {
                dateStr = dateOfExams.getYear()+"-"+dateOfExams.getMonthValue()+"-0"+dateOfExams.getDayOfMonth();
            }
        } else {
            if (dateOfExams.getDayOfMonth()>=10){
                dateStr = dateOfExams.getYear()+"-0"+dateOfExams.getMonthValue()+"-"+dateOfExams.getDayOfMonth();
            }else {
                dateStr = dateOfExams.getYear()+"-0"+dateOfExams.getMonthValue()+"-0"+dateOfExams.getDayOfMonth();
            }
        }
        return dateStr;
    }


    public PlanToDay checkPlanToDay(LocalDate localDate) {
        for(int i=0; i<lastPlan.size(); i++){
            if(lastPlan.get(i).getDate().isEqual(localDate)) return lastPlan.get(i);
        }
        for(int i=0; i<futurePlan.size(); i++){
            if(futurePlan.get(i).getDate().isEqual(localDate)) return futurePlan.get(i);
        }
       return null;
    }
}