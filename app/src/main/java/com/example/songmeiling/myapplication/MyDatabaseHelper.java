package com.example.songmeiling.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by songmeiling on 2015/12/24.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_USER = "create table User("
            + "_id integer primary key autoincrement,"
            + "username text,"
            + "password text"
            + ")";
    public static final String CREATE_NOTE = "create table Note("
            + "_id integer primary key autoincrement,"
            + "title text,"
            + "content text,"
            + "author text,"
            + "date text" + ")";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_NOTE);
        //Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
