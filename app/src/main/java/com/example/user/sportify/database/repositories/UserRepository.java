package com.example.user.sportify.database.repositories;

import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.sportify.database.models.MyGamesModel;
import com.example.user.sportify.database.models.UserModel;
import com.example.user.sportify.database.repositories.base.BaseRepository;
import com.example.user.sportify.ui.utils.DatabaseUtils;
import com.squareup.sqldelight.SqlDelightQuery;

import java.util.List;

interface UserRepository extends BaseRepository<UserModel> {

}

class SQLiteUserRepository implements UserRepository {

    private SQLiteOpenHelper dbHelper;
    private DatabaseUtils databaseUtils;


    SQLiteUserRepository(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void putAll(List<UserModel> data) {

    }

    @Override
    public List<UserModel> getAll() {
        SqlDelightQuery query = MyGamesModel.FACTORY.select_all();
        databaseUtils = new DatabaseUtils<UserModel>(dbHelper.getReadableDatabase(), query.getSql(), UserModel.FACTORY.select_allMapper(), null);

        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void put(UserModel item) {

    }

}
