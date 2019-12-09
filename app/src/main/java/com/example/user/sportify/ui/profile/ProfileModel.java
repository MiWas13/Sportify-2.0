package com.example.user.sportify.ui.profile;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.example.user.sportify.network.api.AppBase;
import com.example.user.sportify.network.session.AuthSessionManager;
import com.example.user.sportify.network.session.SessionComponent;
import com.example.user.sportify.network.session.SessionData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileModel {
	
	private AuthSessionManager authSessionManager;
	private SessionComponent daggerSessionComponent;
	
	
	private void initDatabase() {
		authSessionManager = daggerSessionComponent.getAuthSessionManager();
	}
	
	public void saveSessionData(
		String authToken,
		String phone,
		String password,
		String userId,
		String name
	) {
		authSessionManager.saveSessionData(authToken, phone, password, userId, name);
	}
	
	ProfileModel(SessionComponent daggerSessionComponent) {
		this.daggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return authSessionManager.getSessionData();
	}
	
	
	public void updateProfile(
		UpdateProfileCallback callback,
		String token,
		String name,
		String phone,
		String password
	) {
		UpdateProfileTask updateProfileTask = new UpdateProfileTask(
			callback,
			token,
			name,
			phone,
			password);
		updateProfileTask.execute();
	}
	
	
	private Call<ResponseBody> callUpdateProfile(
		String token,
		String name,
		String phone,
		String password
	) {
		return AppBase.getBaseService().updateProfile(token, name, phone, password);
	}
	
	interface UpdateProfileCallback {
		
		void onSendResponse(String response);
	}
	
	class UpdateProfileTask extends AsyncTask<Void, Void, Void> {
		
		private String token, name, phone, password;
		private UpdateProfileCallback callback;
		
		UpdateProfileTask(
			UpdateProfileCallback callback,
			String token,
			String name,
			String phone,
			String password
		) {
			this.token = token;
			this.name = name;
			this.phone = phone;
			this.password = password;
			this.callback = callback;
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			callUpdateProfile(token, name, phone, password).enqueue(new Callback<ResponseBody>() {
				
				@Override
				public void onResponse(
					@NonNull Call<ResponseBody> call,
					@NonNull Response<ResponseBody> response
				) {
					callback.onSendResponse(response.message());
				}
				
				@Override
				public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
				
				}
			});
			return null;
		}
	}
	
}
