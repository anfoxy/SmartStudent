package com.example.studentapp.db_local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentapp.databinding.ActivityAuthBinding;
import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;
import com.example.studentapp.al.Study;
import com.example.studentapp.al.PlanToDay;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.al.Question;
import com.example.studentapp.al.Subject;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyDBManager {
    private Context context;
    private MyDBHelper myDBHelper;
    private SQLiteDatabase db;
    private ActivityAuthBinding binding;

    public MyDBManager(Context context) {
        this.context = context;
        myDBHelper = new MyDBHelper(context);
    }

    public void openDB() {
        db = myDBHelper.getWritableDatabase();
    }

    public void insert_TABLE_PLAN(Plan p, Subjects s) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.KEY_SUB_ID,  s.getId());
        cv.put(MyConstants.KEY_PLAN_ID, p.getId());
        cv.put(MyConstants.KEY_DATE_PLAN, p.getDate());
        cv.put(MyConstants.KEY_NUM_QUE_PLAN, p.isNumberOfQuestions());

        db.insert(MyConstants.TABLE_QUESTION, null, cv);
    }

    public void insert_TABLE_QUESTION(String name, Questions q, Subjects s) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.KEY_SUBJECT_NAME,  name);
        cv.put(MyConstants.KEY_SUB_ID, s.getId());
        cv.put(MyConstants.KEY_QUESTION_ID, q.getId());
        cv.put(MyConstants.KEY_TEXT_QUESTION, q.getQuestion());
        cv.put(MyConstants.KEY_TEXT_ANSWER, q.getAnswer());
        cv.put(MyConstants.KEY_DATE_QUESTION, q.getDate());
        cv.put(MyConstants.KEY_SIZE_OF_VIEW, q.getSizeOfView());
        cv.put(MyConstants.KEY_PERCENT_KNOW, q.getPercentKnow());


        db.insert(MyConstants.TABLE_QUESTION, null, cv);
    }

    public void insert_TABLE_SUBJECT(Users user, Subjects s) {
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.KEY_SUB_ID, s.getId());
        cv.put(MyConstants.KEY_SUBJECT_NAME, s.getName());
        cv.put(MyConstants.KEY_SUBJECT_DATE, s.getDaysString());
        cv.put(MyConstants.KEY_USER_ID, user.getId());
        cv.put(MyConstants.KEY_TODAY_LEARNED, s.getTodayLearned());

        db.insert(MyConstants.TABLE_QUESTION, null, cv);
    }

    public Cursor getYourTableContents() {
        openDB();
        Cursor data = db.rawQuery("SELECT * FROM " + "name_sub", null);
        return data;
    }
//    public ArrayList<PlanToSub> set() {
        //     private  Subject sub; //предмет
        //    private  ArrayList<PlanToDay> lastPlan; //прошлое
        //    private  ArrayList<PlanToDay> futurePlan; //планы на будущие дни
        //    private  int learnedBefore;
        //    private  int todayLearned; //сколько выучили именно сегодня //Это передается из бд
        //    private LocalDate dateOfExams; //Дата, когда будет экзамен //Это из бд

//        ArrayList<PlanToSub> pl = new ArrayList<>();
//        //Будем использовать данные из таблицы SUBJECT - последнее ID. ID нам нужен для того, чтобы мы смогли знать точное кол-во предметов для цикла for.
//        String queryID_sub = "SELECT " + MyConstants._ID + ", " + MyConstants.SUBJECT + ", " + MyConstants.DAY + ", " + MyConstants.MONTH + ", " + MyConstants.YEAR + ", " + MyConstants.TODAYLEARNED + ", " + MyConstants.TDAY + ", " + MyConstants.TMONTH + ", " + MyConstants.TYEAR + " FROM " + MyConstants.TABLE_NAME_SUBJECTS;
//        //Вывод из таблицы все ID
//        Cursor yourCursor = getYourTableContents();
//        String query_today = "SELECT " + MyConstants._ID + ", " + MyConstants.SUBJECT + ", " + MyConstants.TODAY + ", " + MyConstants.TOMONTH + ", " + MyConstants.TOYEAR + ", " + MyConstants.TODAY_OST_QUE + " FROM " + MyConstants.TABLE_NAME_TODAY;

//    }
}
