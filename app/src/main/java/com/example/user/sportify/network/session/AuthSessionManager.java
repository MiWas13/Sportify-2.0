package com.example.user.sportify.network.session;


public class AuthSessionManager {
	
	private static final String KEY_AUTH_TOKEN = "auth_token";
	private static final String KEY_PHONE = "phone";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_NAME = "name";
	
	
	private SharedPreferencesSessionStorage sessionStorage;
	private SessionData sessionData;
	
	AuthSessionManager(
		final SharedPreferencesSessionStorage sessionStorage,
		final SessionData sessionData
	) {
		this.sessionStorage = sessionStorage;
		this.sessionData = sessionData;
	}
	
	private String getSessionToken() {
		return sessionStorage.getValue(KEY_AUTH_TOKEN);
	}
	
	private void clearSessionData() {
		sessionStorage.removeValue(KEY_AUTH_TOKEN);
		sessionStorage.removeValue(KEY_PHONE);
		sessionStorage.removeValue(KEY_PASSWORD);
		sessionStorage.removeValue(KEY_USER_ID);
	}
	
	public void saveSessionData(
		final String authToken,
		final String phone,
		final String password,
		final String userId,
		final String name
	) {
		sessionStorage.saveValue(KEY_AUTH_TOKEN, authToken);
		sessionStorage.saveValue(KEY_PHONE, phone);
		sessionStorage.saveValue(KEY_PASSWORD, password);
		sessionStorage.saveValue(KEY_USER_ID, userId);
		sessionStorage.saveValue(KEY_NAME, name);
//        if (sessionData.phone != null) {
//            sessionStorage.saveValue(KEY_PHONE, sessionData.phone);
//        }
//        if (sessionData.password != null) {
//            sessionStorage.saveValue(KEY_PASSWORD, sessionData.password);
//        }
//        if (sessionData.name != null) {
//            sessionStorage.saveValue(KEY_USER_ID, sessionData.name);
//        }
//
//        if (sessionData.)
//
//        if (sessionData.userId != null) {
//            sessionStorage.saveValue(KEY_USER_ID, sessionData.userId);
//        }
	}
	
	
	public SessionData getSessionData() {
		sessionData.authToken = sessionStorage.getValue(KEY_AUTH_TOKEN);
		sessionData.userId = sessionStorage.getValue(KEY_USER_ID);
		sessionData.password = sessionStorage.getValue(KEY_PASSWORD);
		sessionData.phone = sessionStorage.getValue(KEY_PHONE);
		sessionData.name = sessionStorage.getValue(KEY_NAME);
		return sessionData;
	}
	
}
