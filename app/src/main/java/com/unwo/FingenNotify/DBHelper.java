package com.unwo.FingenNotify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, Constants.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+Constants.TABLE_PACKAGES+" ("
                + Constants.TABLE_PACKAGES_COLUMN_ID+" integer primary key autoincrement,"
                + Constants.TABLE_PACKAGES_COLUMN_PACKAGE+" text,"
                + Constants.TABLE_PACKAGES_COLUMN_SENDER+" text"
                +  ");");
        sqLiteDatabase.execSQL("create table "+Constants.TABLE_NOTIFY+" ("
                + Constants.TABLE_NOTIFY_COLUMN_ID+" integer primary key autoincrement,"
                + Constants.TABLE_NOTIFY_COLUMN_PACKAGE +" text,"
                + Constants.TABLE_NOTIFY_COLUMN_SENDER+" text,"
                + Constants.TABLE_NOTIFY_COLUMN_MESSAGE+" text,"
                + Constants.TABLE_NOTIFY_COLUMN_DATETIME+" long"+");");
        sqLiteDatabase.execSQL("create table "+Constants.TABLE_PREFERENCE+" ("
                + Constants.TABLE_PREFERENCE_COLUMN_ID+" integer primary key autoincrement,"
                + Constants.TABLE_PREFERENCE_COLUMN_NAME+" text,"
                + Constants.TABLE_PREFERENCE_COLUMN_VALUE+" text"
                +  ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
