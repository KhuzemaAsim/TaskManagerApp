package com.example.taskmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "taskmanager.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {

        sqLiteDb.execSQL(
                "CREATE TABLE tasks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT, " +
                        "description TEXT, " +
                        "datetime INTEGER, " +
                        "status TEXT)"
        );

        sqLiteDb.execSQL(
                "CREATE TABLE notifications (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "message TEXT, " +
                        "datetime INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDb, int oldVer, int newVer) {

        sqLiteDb.execSQL("DROP TABLE IF EXISTS tasks");

        sqLiteDb.execSQL("DROP TABLE IF EXISTS notifications");

        onCreate(sqLiteDb);
    }
}
