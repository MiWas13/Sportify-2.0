package com.example.user.sportify.ui.main;

import com.example.user.sportify.network.session.AuthSessionManager;
import com.example.user.sportify.network.session.SessionComponent;
import com.example.user.sportify.network.session.SessionData;

public class MainModel {
	
	private AuthSessionManager authSessionManager;
	private SessionData sessionData;
	private final SessionComponent mDaggerSessionComponent;
	
	
	private void initDatabase() {
		authSessionManager = mDaggerSessionComponent.getAuthSessionManager();
	}
	
	public void saveSessionData(
		final String authToken,
		final String email,
		final String password,
		final String userId,
		final String name
	) {
		authSessionManager.saveSessionData(authToken, email, password, userId, name);
	}
	
	MainModel(final SessionComponent daggerSessionComponent) {
		mDaggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return authSessionManager.getSessionData();
	}
}
