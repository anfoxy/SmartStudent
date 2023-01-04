package com.example.studentapp.db_local;

import com.example.studentapp.db.Plan;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.Subjects;

public class MyConstants {
    // При изменении в бд +1
    public static final int DATABASE_VERSION = 4;

    // Название бд
    public static final String DB_NAME = "smart_student";

    // Название таблиц в бд
    public static final String TABLE_PLAN = "table_plan";
    public static final String TABLE_QUESTION = "table_question";
    public static final String TABLE_SUBJECT = "table_subject";

    // Название колонн для таблицы TABLE_PLAN
    public static final String KEY_ID_PLAN = "plan_id";
    public static final String KEY_DATE_PLAN = "date_plan";
    public static final String KEY_NUM_QUE_PLAN = "num_que_plan";
    public static final String KEY_BOOL_DATE = "bool_date";

    // Название колонн для таблицы TABLE_QUESTION
    public static final String KEY_ID_QUESTION = "question_id";
    public static final String KEY_TEXT_QUESTION = "text_question";
    public static final String KEY_TEXT_ANSWER = "text_answer";
    public static final String KEY_DATE_QUESTION = "date_question";
    public static final String KEY_SIZE_OF_VIEW = "size_of_view";
    public static final String KEY_PERCENT_KNOW = "percent_know";


    // Название колонн для таблицы TABLE_SUBJECT
    public static final String KEY_ID_SUBJECT = "subject_id";
    public static final String KEY_SUBJECT_NAME = "subject_name";
    public static final String KEY_SUBJECT_DATE = "subject_date";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_TODAY_LEARNED = "today_learned";

    public static final String CREATE_TABLE_PLAN = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAN + " (" + KEY_ID_PLAN + " INTEGER PRIMARY KEY," + KEY_SUBJECT_NAME + " TEXT," + KEY_DATE_PLAN + " TEXT," + KEY_NUM_QUE_PLAN + " INTEGER,"
            + KEY_BOOL_DATE + " BOOLEAN" + ")";

    public static final String CREATE_TABLE_QUESTION = "CREATE TABLE IF NOT EXISTS " + TABLE_QUESTION + " (" + KEY_ID_QUESTION + " INTEGER PRIMARY KEY," + KEY_SUBJECT_NAME + " TEXT," + KEY_TEXT_QUESTION + " TEXT," + KEY_TEXT_ANSWER + " TEXT,"
            + KEY_PERCENT_KNOW + " DOUBLE," +  KEY_DATE_QUESTION + " INTEGER,"
            + KEY_SIZE_OF_VIEW + " INTEGER" + ")";

    public static final String CREATE_TABLE_SUBJECT = "CREATE TABLE IF NOT EXISTS " + TABLE_SUBJECT
            + " (" + KEY_ID_SUBJECT + " INTEGER PRIMARY KEY," + KEY_SUBJECT_NAME + " TEXT," + KEY_SUBJECT_DATE + " TEXT,"
            + KEY_TODAY_LEARNED + " INTEGER," + KEY_USER_ID + " INTEGER" + ")";

    public static final String DROP_TABLE_PLAN = "DROP TABLE IF EXISTS " + TABLE_PLAN;
    public static final String DROP_TABLE_QUESTION = "DROP TABLE IF EXISTS " + TABLE_QUESTION;
    public static final String DROP_TABLE_SUBJECT = "DROP TABLE IF EXISTS " + TABLE_SUBJECT;
}
