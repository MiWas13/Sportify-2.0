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
	private final SessionComponent mDaggerSessionComponent;
	
	
	private void initDatabase() {
		authSessionManager = mDaggerSessionComponent.getAuthSessionManager();
	}
	
	public void saveSessionData(
		final String authToken,
		final String phone,
		final String password,
		final String userId,
		final String name
	) {
		authSessionManager.saveSessionData(authToken, phone, password, userId, name);
	}
	
	ProfileModel(final SessionComponent daggerSessionComponent) {
		this.mDaggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return authSessionManager.getSessionData();
	}
	
	
	public static void updateProfile(
		final UpdateProfileCallback callback,
		final String token,
		final String name,
		final String phone,
		final String password
	) {
		final UpdateProfileTask updateProfileTask = new UpdateProfileTask(
			callback,
			token,
			name,
			phone,
			password);
		updateProfileTask.execute();
	}
	
	
	private static Call<ResponseBody> callUpdateProfile(
		final String token,
		final String name,
		final String phone,
		final String password
	) {
		return AppBase.getBaseService().updateProfile(token, name, phone, password);
	}
	
	interface UpdateProfileCallback {
		
		void onSendResponse(String response);
	}
	
	static class UpdateProfileTask extends AsyncTask<Void, Void, Void> {
		
		private final String mToken;
		private final String mName;
		private final String mPhone;
		private final String mPassword;
		private final UpdateProfileCallback mUpdateProfileCallback;
		
		private UpdateProfileTask(
			final UpdateProfileCallback callback,
			final String token,
			final String name,
			final String phone,
			final String password
		) {
			mToken = token;
			mName = name;
			mPhone = phone;
			mPassword = password;
			mUpdateProfileCallback = callback;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callUpdateProfile(
				mToken,
				mName,
				mPhone,
				mPassword).enqueue(new Callback<ResponseBody>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<ResponseBody> call,
					@NonNull final Response<ResponseBody> response
				) {
					mUpdateProfileCallback.onSendResponse(response.message());
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<ResponseBody> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			return null;
		}
	}
	
}
