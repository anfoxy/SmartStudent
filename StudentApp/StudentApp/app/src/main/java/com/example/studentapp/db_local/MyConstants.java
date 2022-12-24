package com.example.studentapp.db_local;

public class MyConstants {
    // При изменении в бд +1
    private static final int DATABASE_VERSION = 1;

    // Название бд
    private static final String DATABASE_NAME = "smart_students";

    // Название таблиц в бд
    private static final String TABLE_PLAN = "plan";
    private static final String TABLE_QUESTION = "question";
    private static final String TABLE_SUBJECT = "subject";

    // Общие колонны таблиц SUBJECT и QUESTION
    private static final String KEY_SUB_ID = "sub_id";
    private static final String KEY_IS_COMPLETED = "is_completed";

    // Название колонн для таблицы TABLE_PLAN
    private static final String KEY_PLAN_ID = "plan_id";
    private static final String KEY_DATE_PLAN = "date_plan";
    private static final String KEY_NUM_QUE_PLAN = "num_que_plan";

    // Название колонн для таблицы TABLE_QUESTION
    private static final String KEY_QUESTION_ID = "question_id";
    private static final String KEY_TEXT_QUESTION = "text_question";
    private static final String KEY_TEXT_ANSWER = "text_answer";

    // Название колонн для таблицы TABLE_SUBJECT
    private static final String KEY_SUBJECT_NAME = "subject_name";
    private static final String KEY_SUBJECT_DATE = "subject_date";
    private static final String KEY_USER_ID = "user_id";

    private static final String CREATE_TABLE_PLAN = "CREATE TABLE " + TABLE_PLAN
            + "(" + KEY_PLAN_ID + " INTEGER PRIMARY KEY," + KEY_DATE_PLAN + " TEXT,"
            + KEY_NUM_QUE_PLAN + " INTEGER," + KEY_SUB_ID + " INTEGER" + ")";

    private static final String CREATE_TABLE_QUESTION = "CREATE TABLE " + TABLE_QUESTION
            + "(" + KEY_QUESTION_ID + " INTEGER PRIMARY KEY," + KEY_TEXT_QUESTION + " TEXT,"
            + KEY_TEXT_ANSWER + " TEXT," + KEY_IS_COMPLETED + " BOOLEAN," + KEY_SUB_ID + " INTEGER" + ")";

    private static final String CREATE_TABLE_SUBJECT = "CREATE TABLE " + TABLE_SUBJECT
            + "(" + KEY_SUB_ID + " INTEGER PRIMARY KEY," + KEY_SUBJECT_NAME + " TEXT,"
            + KEY_SUBJECT_DATE + " TEXT," + KEY_IS_COMPLETED + " BOOLEAN," + KEY_USER_ID + " INTEGER" + ")";
}
