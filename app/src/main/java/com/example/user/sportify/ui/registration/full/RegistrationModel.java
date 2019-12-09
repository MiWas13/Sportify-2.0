package com.example.user.sportify.ui.registration.full;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.example.user.sportify.network.api.AppBase;
import com.example.user.sportify.network.models.BaseServerAnswer;
import com.example.user.sportify.network.models.DataUserToken;

import com.example.user.sportify.network.models.ProfileData;
import com.example.user.sportify.network.session.AuthSessionManager;
import com.example.user.sportify.network.session.SessionComponent;
import com.example.user.sportify.network.session.SessionData;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.user.sportify.ui.utils.Constants.AUTH_ERROR;
import static com.example.user.sportify.ui.utils.Constants.REGISTRATION_ERROR;

public class RegistrationModel {
	
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
	
	public RegistrationModel(SessionComponent daggerSessionComponent) {
		this.daggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return authSessionManager.getSessionData();
	}
	
	public void signUp(
		SingCallback callback,
		String name,
		String age,
		String phone,
		String password
	) {
		SignUpTask signUpTask = new SignUpTask(callback, name, age, phone, password);
		signUpTask.execute();
	}
	
	public void signIn(SingCallback callback, String phone, String password) {
		TryToSignIn tryToSignIn = new TryToSignIn(callback, phone, password);
		tryToSignIn.execute();
	}
	
	public void getProfileInfo(ProfileDataCallback callback, String token) {
		ProfileTask profileTask = new ProfileTask(callback, token);
		profileTask.execute();
	}
	
	private Call<BaseServerAnswer<DataUserToken>> callBaseDataUserTokenApi(
		String name,
		String age,
		String phone,
		String password
	) {
		return AppBase.getBaseService().signUp(name, age, phone, password);
	}
	
	private Call<BaseServerAnswer<DataUserToken>> callBaseSingInApi(String phone, String password) {
		return AppBase.getBaseService().signIn(phone, password);
	}
	
	private Call<BaseServerAnswer<ProfileData>> callBaseDataProfileApi(String token) {
		return AppBase.getBaseService().getProfile(token);
	}
	
	private DataUserToken fetchDataUserTokenResults(Response<BaseServerAnswer<DataUserToken>> response) {
		BaseServerAnswer<DataUserToken> baseDataUserTokenApi = response.body();
		assert baseDataUserTokenApi != null;
		return baseDataUserTokenApi.getMessage();
	}
	
	private ProfileData fetchProfileDataResults(Response<BaseServerAnswer<ProfileData>> response) {
		BaseServerAnswer<ProfileData> baseProfileDataApi = response.body();
		if (baseProfileDataApi != null) {
			return baseProfileDataApi.getMessage();
		} else {
			return null;
		}
	}
	
	
	public interface SingCallback {
		
		void onSendToken(String token);
	}
	
	public interface ProfileDataCallback {
		
		void onSendProfileData(ProfileData profileData);
	}
	
	class SignUpTask extends AsyncTask<Void, Void, Void> {
		
		private final SingCallback callback;
		private String name, age, phone, password;
		
		SignUpTask(SingCallback callback, String name, String age, String phone, String password) {
			this.callback = callback;
			this.name = name;
			this.age = age;
			this.phone = phone;
			this.password = password;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			callBaseDataUserTokenApi(
				name,
				age,
				phone,
				password).enqueue(new Callback<BaseServerAnswer<DataUserToken>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<DataUserToken>> call,
					@NonNull Response<BaseServerAnswer<DataUserToken>> response
				) {
					if (response.isSuccessful()) {
						String token = fetchDataUserTokenResults(response).getToken();
						if (callback != null) {
							callback.onSendToken(token);
						}
					} else {
						if (callback != null) {
							callback.onSendToken(REGISTRATION_ERROR);
						}
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<DataUserToken>> call,
					@NonNull Throwable t
				) {
				
				}
			});
			return null;
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
		}
	}
	
	
	class ProfileTask extends AsyncTask<Void, Void, Void> {
		
		private final ProfileDataCallback callback;
		private String token;
		
		ProfileTask(ProfileDataCallback callback, String token) {
			this.callback = callback;
			this.token = token;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			callBaseDataProfileApi(token).enqueue(new Callback<BaseServerAnswer<ProfileData>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<ProfileData>> call,
					@NonNull Response<BaseServerAnswer<ProfileData>> response
				) {
					ProfileData profileData = fetchProfileDataResults(response);
					if (callback != null) {
						callback.onSendProfileData(profileData);
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<ProfileData>> call,
					@NonNull Throwable t
				) {
				
				}
			});
			return null;
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
		}
	}
	
	class TryToSignIn extends AsyncTask<Void, Void, Void> {
		
		private String phone;
		private String password;
		private SingCallback callback;
		
		TryToSignIn(SingCallback callback, String phone, String password) {
			this.phone = phone;
			this.password = password;
			this.callback = callback;
		}
		
		
		@Override
		protected Void doInBackground(Void... voids) {
			
			callBaseSingInApi(
				phone,
				password).enqueue(new Callback<BaseServerAnswer<DataUserToken>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<DataUserToken>> call,
					@NonNull Response<BaseServerAnswer<DataUserToken>> response
				) {
					if (response.isSuccessful()) {
						String token = fetchDataUserTokenResults(response).getToken();
						if (token != null) {
							callback.onSendToken(token);
						} else {
							callback.onSendToken(AUTH_ERROR);
						}
						
					} else {
						callback.onSendToken(AUTH_ERROR);
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<DataUserToken>> call,
					@NonNull Throwable t
				) {
					callback.onSendToken(AUTH_ERROR);
				}
			});
			return null;
		}
	}
	
}



