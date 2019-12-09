package com.example.user.sportify.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.sportify.database.models.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sportify.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserModel.CREATE_TABLE);
        sqLiteDatabase.execSQL(GamesModel.CREATE_TABLE);
        sqLiteDatabase.execSQL(MyGamesModel.CREATE_TABLE);
        sqLiteDatabase.execSQL(CategoriesModel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
