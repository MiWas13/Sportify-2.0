package com.example.user.sportify.network.session;


interface SessionStorage {
    String saveValue(String key, String value);

    String getValue(String key);

    Boolean removeValue(String key);
}

