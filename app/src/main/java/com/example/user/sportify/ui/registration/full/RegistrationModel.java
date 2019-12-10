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
	
	public RegistrationModel(final SessionComponent daggerSessionComponent) {
		mDaggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return authSessionManager.getSessionData();
	}
	
	public static void signUp(
		final SingCallback callback,
		final String name,
		final String age,
		final String phone,
		final String password
	) {
		final SignUpTask signUpTask = new SignUpTask(callback, name, age, phone, password);
		signUpTask.execute();
	}
	
	public static void signIn(
		final SingCallback callback,
		final String phone,
		final String password
	) {
		final TryToSignIn tryToSignIn = new TryToSignIn(callback, phone, password);
		tryToSignIn.execute();
	}
	
	public static void getProfileInfo(final ProfileDataCallback callback, final String token) {
		final ProfileTask profileTask = new ProfileTask(callback, token);
		profileTask.execute();
	}
	
	private static Call<BaseServerAnswer<DataUserToken>> callBaseDataUserTokenApi(
		final String name,
		final String age,
		final String phone,
		final String password
	) {
		return AppBase.getBaseService().signUp(name, age, phone, password);
	}
	
	private static Call<BaseServerAnswer<DataUserToken>> callBaseSingInApi(
		final String phone,
		final String password
	) {
		return AppBase.getBaseService().signIn(phone, password);
	}
	
	private static Call<BaseServerAnswer<ProfileData>> callBaseDataProfileApi(final String token) {
		return AppBase.getBaseService().getProfile(token);
	}
	
	private static DataUserToken fetchDataUserTokenResults(final Response<BaseServerAnswer<DataUserToken>> response) {
		final BaseServerAnswer<DataUserToken> baseDataUserTokenApi = response.body();
		assert baseDataUserTokenApi != null;
		return baseDataUserTokenApi.getMessage();
	}
	
	private static ProfileData fetchProfileDataResults(final Response<BaseServerAnswer<ProfileData>> response) {
		final BaseServerAnswer<ProfileData> baseProfileDataApi = response.body();
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
	
	static class SignUpTask extends AsyncTask<Void, Void, Void> {
		
		private final SingCallback mSingCallback;
		private final String mName;
		private final String mAge;
		private final String mPhone;
		private final String mPassword;
		
		private SignUpTask(
			final SingCallback callback,
			final String name,
			final String age,
			final String phone,
			final String password
		) {
			mSingCallback = callback;
			mName = name;
			mAge = age;
			mPhone = phone;
			mPassword = password;
		}
		
		@Override
		protected Void doInBackground(final Void... params) {
			callBaseDataUserTokenApi(
				mName,
				mAge,
				mPhone,
				mPassword).enqueue(new Callback<BaseServerAnswer<DataUserToken>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<DataUserToken>> call,
					@NonNull final Response<BaseServerAnswer<DataUserToken>> response
				) {
					if (response.isSuccessful()) {
						final String token = fetchDataUserTokenResults(response).getToken();
						if (mSingCallback != null) {
							mSingCallback.onSendToken(token);
						}
					} else {
						if (mSingCallback != null) {
							mSingCallback.onSendToken(REGISTRATION_ERROR);
						}
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<DataUserToken>> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			return null;
		}
		
		@Override
		protected void onPostExecute(final Void aVoid) {
			super.onPostExecute(aVoid);
		}
	}
	
	
	static class ProfileTask extends AsyncTask<Void, Void, Void> {
		
		private final ProfileDataCallback mProfileDataCallback;
		private final String mToken;
		
		private ProfileTask(final ProfileDataCallback callback, final String token) {
			mProfileDataCallback = callback;
			mToken = token;
		}
		
		@Override
		protected Void doInBackground(final Void... params) {
			callBaseDataProfileApi(mToken).enqueue(new Callback<BaseServerAnswer<ProfileData>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<ProfileData>> call,
					@NonNull final Response<BaseServerAnswer<ProfileData>> response
				) {
					final ProfileData profileData = fetchProfileDataResults(response);
					if (mProfileDataCallback != null) {
						mProfileDataCallback.onSendProfileData(profileData);
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<ProfileData>> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			return null;
		}
		
		@Override
		protected void onPostExecute(final Void aVoid) {
			super.onPostExecute(aVoid);
		}
	}
	
	static class TryToSignIn extends AsyncTask<Void, Void, Void> {
		
		private final String mPhone;
		private final String mPassword;
		private final SingCallback mSingCallback;
		
		private TryToSignIn(
			final SingCallback callback,
			final String phone,
			final String password
		) {
			mPhone = phone;
			mPassword = password;
			mSingCallback = callback;
		}
		
		
		@Override
		protected Void doInBackground(final Void... voids) {
			
			callBaseSingInApi(
				mPhone,
				mPassword).enqueue(new Callback<BaseServerAnswer<DataUserToken>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<DataUserToken>> call,
					@NonNull final Response<BaseServerAnswer<DataUserToken>> response
				) {
					if (response.isSuccessful()) {
						final String token = fetchDataUserTokenResults(response).getToken();
						if (token != null) {
							mSingCallback.onSendToken(token);
						} else {
							mSingCallback.onSendToken(AUTH_ERROR);
						}
						
					} else {
						mSingCallback.onSendToken(AUTH_ERROR);
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<DataUserToken>> call,
					@NonNull final Throwable t
				) {
					mSingCallback.onSendToken(AUTH_ERROR);
				}
			});
			return null;
		}
	}
	
}



