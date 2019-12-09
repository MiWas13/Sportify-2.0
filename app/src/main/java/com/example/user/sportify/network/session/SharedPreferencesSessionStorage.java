package com.example.user.sportify.network.session;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesSessionStorage implements SessionStorage {
	
	private Context context;
	
	SharedPreferencesSessionStorage(final Context context) {
		this.context = context;
	}
	
	@Override
	public String saveValue(final String key, final String value) {
		getPrefs().edit().putString(key, value).apply();
		return value;
	}
	
	@Override
	public String getValue(final String key) {
		return getPrefs().getString(key, null);
	}
	
	@Override
	public Boolean removeValue(final String key) {
		if (getPrefs().contains(key)) {
			getPrefs().edit().remove(key).apply();
			return true;
		} else {
			return false;
		}
	}
	
	private SharedPreferences getPrefs() {
		return context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
	}
	
}
