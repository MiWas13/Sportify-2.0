package com.example.user.sportify.database.models;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CategoriesModel implements CategoriesDbModel {
    public static final Creator<CategoriesModel> CREATOR = new Creator<CategoriesModel>() {


        @Override
        public CategoriesModel create(int _ID, int category_id, @NonNull String name, @NonNull String image_url) {
            return new AutoValue_CategoriesModel(_ID, category_id, name, image_url);
        }
    };

    public static final Factory<CategoriesModel> FACTORY = new Factory<>(CREATOR);

    public static final Mapper<CategoriesModel> MAPPER = new Mapper<>(FACTORY);
}
