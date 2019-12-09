package com.example.user.sportify.ui.utils;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqldelight.RowMapper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseUtils<T> {

    private String sql;
    private String[] params;
    private SQLiteDatabase sqLiteDatabase;
    private RowMapper<T> mapper;
    private List<T> list = new ArrayList<>();

    public DatabaseUtils(SQLiteDatabase sqLiteDatabase, String sql, RowMapper<T> mapper, String[] params) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.sql = sql;
        this.mapper = mapper;
        this.params = params;
    }

    public List<T> getData() {

        Cursor cursor = sqLiteDatabase.rawQuery(sql, params);

        while (!cursor.isAfterLast()) {
            list.add(mapper.map(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }
}
