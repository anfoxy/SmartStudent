package com.example.studentapp.db_local;

import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.Subjects;

public class MyConstants {
    // При изменении в бд +1
    public static final int DATABASE_VERSION = 1;

    // Название бд
    public static final String DB_NAME = "smart_students";

    // Название таблиц в бд
    public static final String TABLE_PLAN = "plan";
    public static final String TABLE_QUESTION = "question";
    public static final String TABLE_SUBJECT = "subject";

    // Общие колонны таблиц SUBJECT и QUESTION
    public static final String KEY_SUB_ID = "sub_id";


    // Название колонн для таблицы TABLE_PLAN
    public static final String KEY_PLAN_ID = "plan_id";
    public static final String KEY_DATE_PLAN = "date_plan";
    public static final String KEY_NUM_QUE_PLAN = "num_que_plan";

    // Название колонн для таблицы TABLE_QUESTION
 // private static final String KEY_SUB_ID = "sub_id";
    public static final String KEY_QUESTION_ID = "question_id";
    public static final String KEY_TEXT_QUESTION = "text_question";
    public static final String KEY_TEXT_ANSWER = "text_answer";
    public static final String KEY_DATE_QUESTION = "date_question";
    public static final String KEY_SIZE_OF_VIEW = "size_of_view";
    public static final String KEY_PERCENT_KNOW = "percent_know";


    // Название колонн для таблицы TABLE_SUBJECT
 // private static final String KEY_SUB_ID = "sub_id";
    public static final String KEY_SUBJECT_NAME = "subject_name";
    public static final String KEY_SUBJECT_DATE = "subject_date";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_TODAY_LEARNED = "today_learned";

    public static final String CREATE_TABLE_PLAN = "CREATE TABLE " + TABLE_PLAN
            + "(" + KEY_PLAN_ID + " INTEGER PRIMARY KEY," + KEY_DATE_PLAN + " TEXT,"
            + KEY_NUM_QUE_PLAN + " INTEGER," + KEY_SUB_ID + " INTEGER" + ")";

    public static final String CREATE_TABLE_QUESTION = "CREATE TABLE " + TABLE_QUESTION
            + "(" + KEY_QUESTION_ID + " INTEGER PRIMARY KEY," + KEY_TEXT_QUESTION + " TEXT,"
            + KEY_TEXT_ANSWER + " TEXT," + KEY_PERCENT_KNOW + " INTEGER," +  KEY_DATE_QUESTION + " INTEGER,"
            + KEY_SIZE_OF_VIEW + " INTEGER," + KEY_SUB_ID + " INTEGER" + ")";

    public static final String CREATE_TABLE_SUBJECT = "CREATE TABLE " + TABLE_SUBJECT
            + "(" + KEY_SUB_ID + " INTEGER PRIMARY KEY," + KEY_SUBJECT_NAME + " TEXT,"
            + KEY_SUBJECT_DATE + " TEXT," + KEY_TODAY_LEARNED + " INTEGER," + KEY_USER_ID + " INTEGER" + ")";

    public static final String DROP_TABLE_PLAN = "DROP TABLE IF EXISTS " + TABLE_PLAN;
    public static final String DROP_TABLE_QUESTION = "DROP TABLE IF EXISTS " + TABLE_QUESTION;
    public static final String DROP_TABLE_SUBJECT = "DROP TABLE IF EXISTS " + TABLE_SUBJECT;
}
