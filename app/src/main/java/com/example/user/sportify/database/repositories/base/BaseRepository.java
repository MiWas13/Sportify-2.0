package com.example.user.sportify.database.repositories.base;

import java.util.List;

public interface BaseRepository<T> {
    void putAll(List<T> data);

    List<T> getAll();

    void deleteAll();

    void put(T item);
}
