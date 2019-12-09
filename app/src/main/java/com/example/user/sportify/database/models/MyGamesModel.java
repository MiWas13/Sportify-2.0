package com.example.user.sportify.database.models;

import android.support.annotation.NonNull;

import com.example.user.sportify.database.adapters.DateAdapter;
import com.example.user.sportify.database.adapters.TimeAdapter;
import com.google.auto.value.AutoValue;

import java.sql.Time;
import java.util.Date;

@AutoValue
public abstract class MyGamesModel implements MyGamesDbModel {
    private static final DateAdapter dateAdapter = new DateAdapter();
    private static final TimeAdapter timeAdapter = new TimeAdapter();

    public static final Creator<MyGamesModel> CREATOR = (_ID, game_id, category_id, creator_id, description, date, time, location, location_photo_url, max_people_quantity, current_people_quantity) -> new AutoValue_MyGamesModel(_ID, game_id, category_id, creator_id, description, date, time, location, location_photo_url, max_people_quantity, current_people_quantity);

    public static final MyGamesDbModel.Factory<MyGamesModel> FACTORY = new MyGamesDbModel.Factory<>(CREATOR, dateAdapter, timeAdapter);

    public static final MyGamesDbModel.Mapper<MyGamesModel> MAPPER = new MyGamesDbModel.Mapper<>(FACTORY);
}
