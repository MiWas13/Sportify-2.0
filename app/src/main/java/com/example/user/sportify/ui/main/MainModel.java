package com.example.user.sportify.ui.main;

import com.example.user.sportify.network.session.AuthSessionManager;
import com.example.user.sportify.network.session.SessionComponent;
import com.example.user.sportify.network.session.SessionData;

public class MainModel {

    private AuthSessionManager authSessionManager;
    private SessionData sessionData;
    private SessionComponent daggerSessionComponent;


    private void initDatabase() {
        authSessionManager = daggerSessionComponent.getAuthSessionManager();
    }

    public void saveSessionData(String authToken, String email, String password, String userId, String name) {
        authSessionManager.saveSessionData(authToken, email, password, userId, name);
    }

    MainModel(SessionComponent daggerSessionComponent) {
        this.daggerSessionComponent = daggerSessionComponent;
        initDatabase();
    }

    public SessionData getSessionData() {
        return authSessionManager.getSessionData();
    }
}
