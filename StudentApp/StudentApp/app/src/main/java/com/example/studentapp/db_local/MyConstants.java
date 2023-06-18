package com.example.studentapp.db_local;

public class MyConstants {
    // При изменении в бд +1
    public static final int DATABASE_VERSION = 16;

    // Название бд
    public static final String DB_NAME = "smart_student";

    // Название таблиц в бд
    public static final String TABLE_PLAN = "table_plan";
    public static final String TABLE_QUESTION = "table_question";
    public static final String TABLE_SUBJECT = "table_subject";

    public static final String TABLE_TRAINING = "table_training";

    // Название колонн для таблицы TABLE_TRAINING
    public static final String KEY_CALENDAR = "tr_calendar";
    public static final String KEY_LIST_SUB = "tr_list_sub";
    public static final String KEY_ADD_SUB = "tr_add_sub";
    public static final String KEY_MENU = "tr_menu";
    public static final String KEY_PROFILE = "tr_profile";
    public static final String KEY_FRIENDS = "tr_friends";
    public static final String KEY_PROFILE_FRIEND = "tr_profile_friends";
    public static final String KEY_GIVE_SUB = "tr_give_sub";
    public static final String KEY_BEGIN_GAME = "tr_begin_game";
    public static final String KEY_STATISTIC = "tr_statistic";


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

    public static final String CREATE_TABLE_TRAINING = "CREATE TABLE IF NOT EXISTS " + TABLE_TRAINING
            + " (" + KEY_CALENDAR + " INTEGER," + KEY_LIST_SUB + " INTEGER," + KEY_ADD_SUB
            + " INTEGER," + KEY_STATISTIC + " INTEGER," + KEY_MENU + " INTEGER," + KEY_PROFILE + " INTEGER," + KEY_FRIENDS
            + " INTEGER," + KEY_PROFILE_FRIEND + " INTEGER," + KEY_GIVE_SUB + " INTEGER,"
            + KEY_BEGIN_GAME + " INTEGER" + ")";

    public static final String DROP_TABLE_PLAN = "DROP TABLE IF EXISTS " + TABLE_PLAN;
    public static final String DROP_TABLE_QUESTION = "DROP TABLE IF EXISTS " + TABLE_QUESTION;
    public static final String DROP_TABLE_SUBJECT = "DROP TABLE IF EXISTS " + TABLE_SUBJECT;
    public static final String DROP_TABLE_TRAINING = "DROP TABLE IF EXISTS " + TABLE_TRAINING;
}
