package com.example.user.sportify.database.models;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UserModel implements UserDbModel {
    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel create(int _ID, int user_id, @NonNull String token, @NonNull String name, int phone, @NonNull String password, int age, boolean show_phone, int games_created, int games_played) {
            return new AutoValue_UserModel(_ID, user_id, token, name, phone, password, age, show_phone, games_created, games_played);
        }
    };

    public static final Factory<UserModel> FACTORY = new Factory<>(CREATOR);

    public static final Mapper<UserModel> MAPPER = new Mapper<>(FACTORY);
}
