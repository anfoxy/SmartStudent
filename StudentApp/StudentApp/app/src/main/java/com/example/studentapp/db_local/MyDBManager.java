package com.example.studentapp.db_local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentapp.databinding.ActivityAuthBinding;

import com.example.studentapp.db.Users;

import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.al.Subject;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class MyDBManager {
    private Context context;
    private MyDBHelper myDBHelper;
    private SQLiteDatabase db;

    public MyDBManager(Context context) {
        this.context = context;
        myDBHelper = new MyDBHelper(context);
    }

    public void deleteDatabase (Context context) {
        context.deleteDatabase(MyConstants.DB_NAME);
    }


    public void openDB() {
        db = myDBHelper.getWritableDatabase();
    }

    public void setFromDB(PlanToSub pl) {
        Subject s = pl.getSub();
        ArrayList<PlanToDay> Date_Plan = pl.getLastPlan();
        ArrayList<PlanToDay> futurePlan = pl.getFuturePlan();
        int bool_plan;

        Users user = Users.getUser();

        insert_TABLE_SUBJECT(user, pl);
        for (int i = 0; i < s.getSizeAllQuest(); i++) {
            insert_TABLE_QUESTION(pl, i);
        }

        Date_Plan.addAll(futurePlan);


        for (int i = 0; i < Date_Plan.size(); i++) {
            if (Date_Plan.get(i) == futurePlan.get(0)) {
                bool_plan = 1;
            } else {
                bool_plan = 0;
            }
            insert_TABLE_PLAN(pl, Date_Plan.get(i), bool_plan);
        }
    }



    public void insert_TABLE_PLAN(PlanToSub pl, PlanToDay pld, int bool_plan) {
        ContentValues cv = new ContentValues();
        String date = pld.dateToString();
        cv.put(MyConstants.KEY_ID_PLAN, pld.getId());
        cv.put(MyConstants.KEY_SUBJECT_NAME, pl.getSub().getNameOfSubme());
        cv.put(MyConstants.KEY_DATE_PLAN, date);
        cv.put(MyConstants.KEY_BOOL_DATE, bool_plan);
        cv.put(MyConstants.KEY_NUM_QUE_PLAN, pld.getSizeOfQuetion());

        db.insert(MyConstants.TABLE_PLAN, null, cv);
    }

    public void insert_TABLE_QUESTION(PlanToSub pl, int i) {
        ContentValues cv = new ContentValues();
        String date = pl.getSub().getQuestion(i).dateToString();
        cv.put(MyConstants.KEY_ID_QUESTION, pl.getSub().getQuestion(i).getId());
        cv.put(MyConstants.KEY_SUBJECT_NAME, pl.getSub().getNameOfSubme());
        cv.put(MyConstants.KEY_TEXT_QUESTION, pl.getSub().getQuestion(i).getQuestion());
        cv.put(MyConstants.KEY_TEXT_ANSWER, pl.getSub().getQuestion(i).getAnswer());
        cv.put(MyConstants.KEY_DATE_QUESTION, date);
        cv.put(MyConstants.KEY_SIZE_OF_VIEW, pl.getSub().getQuestion(i).getSizeOfView());
        cv.put(MyConstants.KEY_PERCENT_KNOW, pl.getSub().getQuestion(i).getPercentKnow());

        db.insert(MyConstants.TABLE_QUESTION, null, cv);
    }

    public void insert_TABLE_SUBJECT(Users user, PlanToSub pl) {
        ContentValues cv = new ContentValues();
        String st = pl.dateToString();
        cv.put(MyConstants.KEY_ID_SUBJECT, pl.getId());
        cv.put(MyConstants.KEY_USER_ID, user.getId());
        cv.put(MyConstants.KEY_SUBJECT_NAME, pl.getSub().getNameOfSubme());
        cv.put(MyConstants.KEY_SUBJECT_DATE, st);
        cv.put(MyConstants.KEY_TODAY_LEARNED, pl.getTodayLearned());
        db.insert(MyConstants.TABLE_SUBJECT, null, cv);
    }

    public Cursor getYourTableContents() {
        openDB();
        Cursor data = db.rawQuery("SELECT * FROM " + "table_subject", null);
        return data;
    }

    public ArrayList<PlanToSub> set() {
        String query_que = "SELECT " + MyConstants.KEY_ID_QUESTION + ", " + MyConstants.KEY_SUBJECT_NAME + ", " + MyConstants.KEY_TEXT_QUESTION + ", " + MyConstants.KEY_TEXT_ANSWER + ", " + MyConstants.KEY_PERCENT_KNOW + ", " + MyConstants.KEY_DATE_QUESTION + ", " + MyConstants.KEY_SIZE_OF_VIEW + " FROM " + MyConstants.TABLE_QUESTION;
        String query_sub = "SELECT " + MyConstants.KEY_ID_SUBJECT + ", " +MyConstants.KEY_SUBJECT_NAME + ", " + MyConstants.KEY_SUBJECT_DATE + ", " + MyConstants.KEY_TODAY_LEARNED + " FROM " + MyConstants.TABLE_SUBJECT;
        String query_pl = "SELECT " + MyConstants.KEY_ID_PLAN + ", " +MyConstants.KEY_SUBJECT_NAME + ", " + MyConstants.KEY_DATE_PLAN + ", " + MyConstants.KEY_NUM_QUE_PLAN  + ", " + MyConstants.KEY_BOOL_DATE + " FROM " + MyConstants.TABLE_PLAN;

        //Вывод из таблицы все ID
        Cursor yourCursor = getYourTableContents();
        int count_sub = 0;

        while (yourCursor.moveToNext()) {
            count_sub += 1;
        }
        ArrayList<PlanToSub> GlSub = new ArrayList<>();
        if (count_sub > 0) {
            //Вывод из таблицы
            Cursor cursor1 = db.rawQuery(query_pl, null);
            Cursor cursor3 = db.rawQuery(query_sub, null);
            Cursor cursor2 = db.rawQuery(query_que, null);

            cursor3.moveToFirst();
            cursor1.moveToFirst();
            cursor2.moveToFirst();

            int count_plan = 0;
            int count_que = 0;
            do {
                count_plan++;
            } while (cursor1.moveToNext());
            do {
                count_que++;
            } while (cursor2.moveToNext());

            cursor1.moveToFirst();
            cursor2.moveToFirst();

            ArrayList<String> date = new ArrayList<String>();

            ArrayList<String> name_sub_plan = new ArrayList<String>(); // название предмета в таблице Plan
            ArrayList<String> name_sub_subject = new ArrayList<String>(); // название предмета в таблице Subject
            ArrayList<String> name_sub_question = new ArrayList<String>(); // название предмета в таблице Question
            ArrayList<String> quest = new ArrayList<>(); // вопросы в таблице Question
            ArrayList<String> answer = new ArrayList<>(); // ответы в таблице Question
            ArrayList<String> String_Date_que = new ArrayList<>(); // Даты в таблице Question типа String
            ArrayList<String> Arr_date_sub = new ArrayList<String>(); // Дата экзамена в таблице Subject типа String

            ArrayList<Integer> bool = new ArrayList<Integer>(); // Проверочная переменная, 0 0 0 1 0 0
            ArrayList<Integer> todaylearned = new ArrayList<Integer>(); //
            ArrayList<Integer> Arr_num_que_plan = new ArrayList<Integer>();
            ArrayList<Integer> sizeOfView = new ArrayList<>();

            ArrayList<LocalDate> Local_Date_que = new ArrayList<>();
            ArrayList<Double> percentKnow = new ArrayList<>();

            int col_idx_quest = cursor2.getColumnIndex("text_question");
            int col_idx_answer = cursor2.getColumnIndex("text_answer");
            int col_idx_Local_Date_que = cursor2.getColumnIndex("date_question");
            int col_idx_sizeOfView = cursor2.getColumnIndex("size_of_view");
            int col_idx_percentKnow = cursor2.getColumnIndex("percent_know");
            do {
                quest.add(cursor2.getString(col_idx_quest));
                answer.add(cursor2.getString(col_idx_answer));
                String_Date_que.add(cursor2.getString(col_idx_Local_Date_que));
                sizeOfView.add(cursor2.getInt(col_idx_sizeOfView));
                percentKnow.add(cursor2.getDouble(col_idx_percentKnow));
            } while (cursor2.moveToNext());
            cursor2.moveToFirst();

            int col_idx_name_sub_plan = cursor1.getColumnIndex("subject_name");
            int col_idx_date_plan = cursor1.getColumnIndex("date_plan");
            for (int i = 0; i < count_plan; i++) {
                name_sub_plan.add(cursor1.getString(col_idx_name_sub_plan));
                date.add(cursor1.getString(col_idx_date_plan));
                cursor1.moveToNext();
            }
            cursor1.moveToFirst();

            int col_idx_name_sub_subject = cursor3.getColumnIndex("subject_name");
            for (int i = 0; i < count_sub; i++) {
                name_sub_subject.add(cursor3.getString(col_idx_name_sub_subject));
                cursor3.moveToNext();
            }
            cursor3.moveToFirst();

            int col_idx_name_sub_question = cursor2.getColumnIndex("subject_name");
            for (int i = 0; i < count_que; i++) {
                name_sub_question.add(cursor2.getString(col_idx_name_sub_question));
                cursor2.moveToNext();
            }
            cursor2.moveToFirst();

            int col_idx_date_sub = cursor3.getColumnIndex("subject_date");
            for (int i = 0; i < count_sub; i++) {
                Arr_date_sub.add(cursor3.getString(col_idx_date_sub));
                cursor3.moveToNext();
            }
            cursor3.moveToFirst();

            ArrayList<LocalDate> date_local = new ArrayList<>();
            for(int i=0; i< count_sub; i++) { // Проходимся по всем предметам
                String[] parts = Arr_date_sub.get(i).split("-");
                int year, month, day;

                year = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]);
                day = Integer.parseInt(parts[2]);

                date_local.add(LocalDate.of(year, month, day));
            }

            int col_idx_bool_plan = cursor1.getColumnIndex("bool_date");
            for (int i = 0; i < count_plan; i++) {
                bool.add(cursor1.getInt(col_idx_bool_plan));
                cursor1.moveToNext();
            }
            cursor1.moveToFirst();

            int col_idx_num_que_plan = cursor1.getColumnIndex("num_que_plan");
            for (int i = 0; i < count_plan; i++) {
                Arr_num_que_plan.add(cursor1.getInt(col_idx_num_que_plan));
                cursor1.moveToNext();
            }
            cursor1.moveToFirst();

            int col_idx_todaylearned = cursor3.getColumnIndex("today_learned");
            for (int i = 0; i < count_sub; i++) {
                todaylearned.add(cursor3.getInt(col_idx_todaylearned));
                cursor3.moveToNext();
            }
            cursor3.moveToFirst();

            ArrayList<Subject> sub = new ArrayList<Subject>();
            for(int i=0; i < count_sub; i++) {
                ArrayList<Question> Arr_que = new ArrayList<Question>();
                ArrayList<PlanToDay> lastPlan = new ArrayList<>();
                ArrayList<PlanToDay> futurePlan = new ArrayList<>();
                int flag = 0;
                int j = 0;

                for (int k = 0; k < count_plan; k++) {
                    if (name_sub_subject.get(i).equals(name_sub_plan.get(k))) {
                        String[] parts = date.get(k).split("-");

                        int year, month, day;

                        year = Integer.parseInt(parts[0]);
                        month = Integer.parseInt(parts[1]);
                        day = Integer.parseInt(parts[2]);

                        LocalDate date_local1 = LocalDate.of(year, month, day);
                        PlanToDay plan = new PlanToDay(date_local1, Arr_num_que_plan.get(k));
                        if (flag == 0) {
                            if (bool.get(k) == 0) {
                                lastPlan.add(plan);
                            } else {
                                flag = 1;
                                futurePlan.add(plan);
                                j++;
                            }
                        } else {
                            futurePlan.add(plan);
                            j++;
                        }
                    }
                }
                for (int m=0; m< count_que; m++) {
                    if (name_sub_subject.get(i).equals(name_sub_question.get(m))) {
                        String[] parts = String_Date_que.get(m).split("-");

                        int year, month, day;

                        year = Integer.parseInt(parts[0]);
                        month = Integer.parseInt(parts[1]);
                        day = Integer.parseInt(parts[2]);

                        LocalDate date_local1 = LocalDate.of(year, month, day);
                        Local_Date_que.add(date_local1);
                        Arr_que.add(new Question(quest.get(m), answer.get(m), Local_Date_que.get(m), sizeOfView.get(m), percentKnow.get(m))); // Local date функцию for преобразование
                    }
                }
                sub.add(new Subject(name_sub_subject.get(i), Arr_que));
                GlSub.add(new PlanToSub(sub.get(i), todaylearned.get(i), date_local.get(i), futurePlan, lastPlan));
            }
        }
        return GlSub;
    }

    public void delete_SUB(String sub_name) {
        db.delete(MyConstants.TABLE_SUBJECT, MyConstants.KEY_SUBJECT_NAME + " = ?", new String[]{sub_name});
        db.delete(MyConstants.TABLE_PLAN, MyConstants.KEY_SUBJECT_NAME + " = ?", new String[]{sub_name});
        db.delete(MyConstants.TABLE_QUESTION, MyConstants.KEY_SUBJECT_NAME + " = ?", new String[]{sub_name});
    }

    // функция обновления плана (изменение) кнопка AddPlan в SettingPlanFragment
    public void updatePlan(PlanToSub pl) {
        Subject s = pl.getSub();
        ArrayList<PlanToDay> pld = pl.getLastPlan();
        pld.addAll(pl.getFuturePlan());
        db.delete(MyConstants.TABLE_PLAN, MyConstants.KEY_SUBJECT_NAME + " = ?", new String[]{s.getNameOfSubme()});
        int bool_plan;
        if (pl.getFuturePlan().isEmpty()) {
            for (int i = 0; i < pld.size(); i++) {
                bool_plan = 0;
                insert_TABLE_PLAN(pl, pld.get(i), bool_plan);
            }
        } else {
            for (int i = 0; i < pld.size(); i++) {
                if ((pld.get(i) == pl.getFuturePlan().get(0))) {
                    bool_plan = 1;
                } else {
                    bool_plan = 0;
                }
                insert_TABLE_PLAN(pl, pld.get(i), bool_plan);
            }
        }
    }

    public void updateQuestionsToSubject(PlanToSub pl) {
        Subject s = pl.getSub();
        db.delete(MyConstants.TABLE_QUESTION, MyConstants.KEY_SUBJECT_NAME + " = ?", new String[]{s.getNameOfSubme()});
        for (int i = 0; i < s.getSizeAllQuest(); i++) {
            insert_TABLE_QUESTION(pl, i);
        }
    }

    public void updateNameSubAndDateExams(PlanToSub pl, String name_sub_new) {
        ContentValues cv = new ContentValues();
        ContentValues cv1 = new ContentValues();

        cv.put(MyConstants.KEY_SUBJECT_NAME, name_sub_new);
        cv1.put(MyConstants.KEY_SUBJECT_DATE, pl.dateToString());
        db.update(MyConstants.TABLE_SUBJECT, cv, MyConstants.KEY_SUBJECT_NAME + "= ?",new String[] {pl.getSub().getNameOfSubme()});
        db.update(MyConstants.TABLE_PLAN, cv, MyConstants.KEY_SUBJECT_NAME + "= ?",new String[] {pl.getSub().getNameOfSubme()});
        db.update(MyConstants.TABLE_QUESTION, cv, MyConstants.KEY_SUBJECT_NAME + "= ?",new String[] {pl.getSub().getNameOfSubme()});
        db.update(MyConstants.TABLE_SUBJECT, cv1, MyConstants.KEY_SUBJECT_NAME + "= ?",new String[] {pl.getSub().getNameOfSubme()});
    }


}
