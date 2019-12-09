package com.example.user.sportify.database.models;

import android.support.annotation.NonNull;

import com.example.user.sportify.database.adapters.DateAdapter;
import com.example.user.sportify.database.adapters.TimeAdapter;
import com.google.auto.value.AutoValue;

import java.sql.Time;
import java.util.Date;

@AutoValue
public abstract class GamesModel implements GamesDbModel {
    private static final DateAdapter dateAdapter = new DateAdapter();
    private static final TimeAdapter timeAdapter = new TimeAdapter();
    public static final Creator<GamesModel> CREATOR = new Creator<GamesModel>() {

        @Override
        public GamesModel create(int _ID, int game_id, int category_id, int creator_id, @NonNull String description, @NonNull Date date, @NonNull Time time, @NonNull String location, @NonNull String location_photo_url, int max_people_quantity, int current_people_quantity) {
            return new AutoValue_GamesModel(_ID, game_id, category_id, creator_id, description, date, time, location, location_photo_url, max_people_quantity, current_people_quantity);
        }
    };

    public static final Factory<GamesModel> FACTORY = new Factory<>(CREATOR, dateAdapter, timeAdapter);

    public static final Mapper<GamesModel> MAPPER = new Mapper<>(FACTORY);
}
