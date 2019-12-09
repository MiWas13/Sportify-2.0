package com.example.user.sportify.database.repositories;

import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.sportify.database.models.MyGamesModel;
import com.example.user.sportify.database.repositories.base.BaseRepository;
import com.example.user.sportify.ui.utils.DatabaseUtils;
import com.squareup.sqldelight.SqlDelightQuery;

import java.util.List;

interface MyGamesRepository extends BaseRepository<MyGamesModel> {

}

class SQLiteMyGamesRepository implements MyGamesRepository {

    private SQLiteOpenHelper dbHelper;
    private DatabaseUtils databaseUtils;


    SQLiteMyGamesRepository(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void putAll(List<MyGamesModel> data) {

    }

    @Override
    public List<MyGamesModel> getAll() {
        SqlDelightQuery query = MyGamesModel.FACTORY.select_all();

        databaseUtils = new DatabaseUtils<MyGamesModel>(dbHelper.getReadableDatabase(), query.getSql(), MyGamesModel.FACTORY.select_allMapper(), null);
        return databaseUtils.getData();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void put(MyGamesModel item) {

    }

}
