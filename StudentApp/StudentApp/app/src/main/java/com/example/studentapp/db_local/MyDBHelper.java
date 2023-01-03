package com.example.studentapp.db_local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(@Nullable Context context) {
        super(context, MyConstants.DB_NAME,null, MyConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyConstants.CREATE_TABLE_PLAN);
        db.execSQL(MyConstants.CREATE_TABLE_QUESTION);
        db.execSQL(MyConstants.CREATE_TABLE_SUBJECT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MyConstants.DROP_TABLE_PLAN);
        db.execSQL(MyConstants.DROP_TABLE_QUESTION);
        db.execSQL(MyConstants.DROP_TABLE_SUBJECT);
        onCreate(db);
    }
}
