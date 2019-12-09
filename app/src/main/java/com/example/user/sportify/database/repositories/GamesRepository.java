package com.example.user.sportify.database.repositories;

import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.sportify.database.models.CategoriesModel;
import com.example.user.sportify.database.models.GamesModel;
import com.example.user.sportify.database.repositories.base.BaseRepository;
import com.example.user.sportify.ui.utils.DatabaseUtils;
import com.squareup.sqldelight.SqlDelightQuery;

import java.util.ArrayList;
import java.util.List;

interface GamesRepository extends BaseRepository<GamesModel> {
    List<GamesModel> getAllByCategoryId(int categoryId);
}

class SQLiteGamesRepository implements GamesRepository {

    private SQLiteOpenHelper dbHelper;
    private DatabaseUtils databaseUtils;

    SQLiteGamesRepository(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void putAll(List<GamesModel> data) {

    }

    @Override
    public List<GamesModel> getAll() {

        SqlDelightQuery query = GamesModel.FACTORY.select_all();

        databaseUtils = new DatabaseUtils<GamesModel>(dbHelper.getReadableDatabase(), query.getSql(), GamesModel.FACTORY.select_allMapper(), null);
        return databaseUtils.getData();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void put(GamesModel item) {

    }

    @Override
    public List<GamesModel> getAllByCategoryId(int categoryId) {
        return null;
    }

//    private getAll() {
//
//    }
}
