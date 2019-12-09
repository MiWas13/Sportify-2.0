package com.example.user.sportify.database.repositories;

import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.sportify.database.models.CategoriesModel;
import com.example.user.sportify.database.repositories.base.BaseRepository;
import com.example.user.sportify.ui.utils.DatabaseUtils;
import com.squareup.sqldelight.SqlDelightQuery;
import com.squareup.sqldelight.SqlDelightStatement;

import java.util.List;

interface CategoriesRepository extends BaseRepository<CategoriesModel> {
}

class SQLiteCategoriesRepository implements CategoriesRepository {

    private SQLiteOpenHelper dbHelper;
    private DatabaseUtils databaseUtils;

    SQLiteCategoriesRepository(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void putAll(List<CategoriesModel> data) {

    }

    @Override
    public List<CategoriesModel> getAll() {
        SqlDelightQuery query = CategoriesModel.FACTORY.select_all();
        databaseUtils = new DatabaseUtils<CategoriesModel>(dbHelper.getReadableDatabase(), query.getSql(), CategoriesModel.FACTORY.select_allMapper(), null);
        return databaseUtils.getData();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void put(CategoriesModel item) {

    }

}
