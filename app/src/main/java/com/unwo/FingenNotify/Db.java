package com.unwo.FingenNotify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Db {
    private DBHelper dbHelper;

    public Db(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    public List<Package> getApplications()
    {
        List<Package> applications=new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(Constants.TABLE_PACKAGES, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(Constants.TABLE_PACKAGES_COLUMN_ID);
            int nameColIndex = c.getColumnIndex(Constants.TABLE_PACKAGES_COLUMN_PACKAGE);
            int senderColIndex = c.getColumnIndex(Constants.TABLE_PACKAGES_COLUMN_SENDER);

            do {
                Package a = new Package();
                a.setId(c.getInt(idColIndex));
                a.setName(c.getString(nameColIndex));
                a.setSender(c.getString(senderColIndex));
                applications.add(a);
            } while (c.moveToNext());
        }
        c.close();
        dbHelper.close();
        return applications;
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public List<Notify> getNotifyByDate(long startMs, long endMs)
    {
        List<Notify> notifications=new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(Constants.TABLE_NOTIFY, null,
                Constants.TABLE_NOTIFY_COLUMN_DATETIME + " >= ? AND " + Constants.TABLE_NOTIFY_COLUMN_DATETIME + " < ?",
                new String[]{String.valueOf(startMs), String.valueOf(endMs)},
                null, null, "dt desc");
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_ID);
            int nameColIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_PACKAGE);
            int senderColIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_SENDER);
            int messageColIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_MESSAGE);
            int dateTimeIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_DATETIME);

            do {
                Notify a = new Notify();
                a.setId(c.getInt(idColIndex));
                a.setName(c.getString(nameColIndex));
                a.setSender(c.getString(senderColIndex));
                a.setMessage(c.getString(messageColIndex));
                a.setDatetime(getDate(c.getLong(dateTimeIndex),Constants.DATETIME_FORMAT));
                notifications.add(a);
            } while (c.moveToNext());
        }
        c.close();
        dbHelper.close();
        return notifications;
    }

    public List<Notify> getNotify()
    {
        List<Notify> notifications=new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(Constants.TABLE_NOTIFY, null, null, null, null, null, "dt desc");
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_ID);
            int nameColIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_PACKAGE);
            int senderColIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_SENDER);
            int messageColIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_MESSAGE);
            int dateTimeIndex = c.getColumnIndex(Constants.TABLE_NOTIFY_COLUMN_DATETIME);

            do {
                Notify a = new Notify();
                a.setId(c.getInt(idColIndex));
                a.setName(c.getString(nameColIndex));
                a.setSender(c.getString(senderColIndex));
                a.setMessage(c.getString(messageColIndex));
                a.setDatetime(getDate(c.getLong(dateTimeIndex),Constants.DATETIME_FORMAT));
                notifications.add(a);
            } while (c.moveToNext());
        }
        c.close();
        dbHelper.close();
        return notifications;
    }

    public long addApplication(String name,String sender)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Constants.TABLE_PACKAGES_COLUMN_PACKAGE, name);
        cv.put(Constants.TABLE_PACKAGES_COLUMN_SENDER, sender);
        long rowID = db.insert(Constants.TABLE_PACKAGES, null, cv);
        dbHelper.close();
        return rowID;
    }

    public void editApplication(long id,String name,String sender)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(Constants.TABLE_PACKAGES_COLUMN_PACKAGE,name);
        updatedValues.put(Constants.TABLE_PACKAGES_COLUMN_SENDER,sender);
        db.update(Constants.TABLE_PACKAGES, updatedValues, Constants.TABLE_PACKAGES_COLUMN_ID+"= ?", new String[] {String.valueOf(id)});
        dbHelper.close();
    }

    public void deleteApplication(long id)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete(Constants.TABLE_PACKAGES, Constants.TABLE_PACKAGES_COLUMN_ID+"= ?", new String[] {String.valueOf(id)});
        dbHelper.close();
    }

    public long addNotify(String name,String sender,String message)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Constants.TABLE_NOTIFY_COLUMN_PACKAGE, name);
        cv.put(Constants.TABLE_NOTIFY_COLUMN_SENDER, sender);
        cv.put(Constants.TABLE_NOTIFY_COLUMN_MESSAGE, message);
        cv.put(Constants.TABLE_NOTIFY_COLUMN_DATETIME,System.currentTimeMillis());
        long rowID = db.insert(Constants.TABLE_NOTIFY, null, cv);
        dbHelper.close();
        return rowID;
    }

    public void deleteMessage(long id)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete(Constants.TABLE_NOTIFY, Constants.TABLE_NOTIFY_COLUMN_ID+"= ?", new String[] {String.valueOf(id)});
        dbHelper.close();
    }

    public void deleteAllMessage()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int clearCount = db.delete(Constants.TABLE_NOTIFY, null, null);
        dbHelper.close();
    }

    public void deleteAllApplications()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Constants.TABLE_PACKAGES, null, null);
        dbHelper.close();
    }

    public boolean getPreferenceValueBool(String name,Boolean value_default)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(Constants.TABLE_PREFERENCE, null, Constants.TABLE_PREFERENCE_COLUMN_NAME+"= ?", new String[] {name}, null, null, null);
        if (c.moveToFirst()) {
            int valueColIndex = c.getColumnIndex(Constants.TABLE_PREFERENCE_COLUMN_VALUE);

            do {
                value_default=Boolean.valueOf(c.getString(valueColIndex));
            } while (c.moveToNext());
        }
        c.close();

        dbHelper.close();
        return value_default;
    }

    public void setPreferenceValueBool(String name,Boolean value)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Constants.TABLE_PREFERENCE_COLUMN_NAME, name);
        cv.put(Constants.TABLE_PREFERENCE_COLUMN_VALUE, String.valueOf(value));

        int u = db.update(Constants.TABLE_PREFERENCE, cv, Constants.TABLE_PREFERENCE_COLUMN_NAME+"= ?", new String[] {name});
        if (u == 0) {
            db.insertWithOnConflict(Constants.TABLE_PREFERENCE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }

        dbHelper.close();
    }
}
