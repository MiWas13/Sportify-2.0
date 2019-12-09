package com.example.user.sportify.database.adapters;

import android.support.annotation.NonNull;

import com.squareup.sqldelight.ColumnAdapter;

import java.sql.Time;


public class TimeAdapter implements ColumnAdapter<Time, Long> {
    @NonNull
    @Override
    public Time decode(Long databaseValue) {
        return new Time(databaseValue);
    }

    @Override
    public Long encode(@NonNull Time value) {
        return value.getTime();
    }
}
