package com.example.user.sportify.ui.concretgame;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.example.user.sportify.network.api.AppBase;
import com.example.user.sportify.network.models.BaseServerAnswer;
import com.example.user.sportify.network.models.GamesParticipantData;
import com.example.user.sportify.network.models.UserParticipantData;
import com.example.user.sportify.network.session.AuthSessionManager;
import com.example.user.sportify.network.session.SessionComponent;
import com.example.user.sportify.network.session.SessionData;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConcretGameModel {
	
	private AuthSessionManager mAuthSessionManager;
	private SessionData sessionData;
	private final SessionComponent mDaggerSessionComponent;
	
	
	private void initDatabase() {
		mAuthSessionManager = mDaggerSessionComponent.getAuthSessionManager();
	}
	
	public void saveSessionData(
		final String authToken,
		final String phone,
		final String password,
		final String userId,
		final String name
	) {
		mAuthSessionManager.saveSessionData(authToken, phone, password, userId, name);
	}
	
	public ConcretGameModel(final SessionComponent daggerSessionComponent) {
		mDaggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return mAuthSessionManager.getSessionData();
	}
	
	public static void attachUserToGame(
		final AttachUserToGameCallback callback,
		final String token,
		final String gameId
	) {
		final AttachUserTask attachUserTask = new AttachUserTask(callback, token, gameId);
		attachUserTask.execute();
	}
	
	public static void unAttachUserFromGame(
		final AttachUserToGameCallback callback,
		final String token,
		final String gameId
	) {
		final UnAttachUserTask attachUserTask = new UnAttachUserTask(callback, token, gameId);
		attachUserTask.execute();
	}
	
	public static void getGameParticipants(
		final GameParticipantsCallback callback,
		final String gameId
	) {
		final GetGameParticipantsTask getGameParticipantsTask
			= new GetGameParticipantsTask(callback, gameId);
		getGameParticipantsTask.execute();
	}
	
	public static void getUserPhone(final UserPhoneCallback callback, final String userId) {
		final GetUserPhoneTask getUserPhoneTask = new GetUserPhoneTask(callback, userId);
		getUserPhoneTask.execute();
	}
	
	private static Call<BaseServerAnswer<List<GamesParticipantData>>> callGetGameParticipantsApi(
		final String gameId
	) {
		return AppBase.getBaseService().getGameParticipants(gameId);
	}
	
	private static Call<BaseServerAnswer<String>> callAttachToGameApi(
		final String token,
		final String gameId
	) {
		return AppBase.getBaseService().attachToGame(token, gameId);
	}
	
	private static Call<BaseServerAnswer<String>> callUnAttachToGameApi(
		final String token,
		final String gameId
	) {
		return AppBase.getBaseService().unAttachFromGame(token, gameId);
	}
	
	private static Call<BaseServerAnswer<UserParticipantData>> callGetUserPhoneApi(final String userId) {
		return AppBase.getBaseService().getUserPhone(userId);
	}
	
	private static List<GamesParticipantData> fetchGameParticipantsResults(final Response<BaseServerAnswer<List<GamesParticipantData>>> response) {
		final BaseServerAnswer<List<GamesParticipantData>> baseGameParticipants = response.body();
		assert baseGameParticipants != null;
		return baseGameParticipants.getMessage();
	}
	
	public interface GameParticipantsCallback {
		
		void onSendGamesParticipants(List<GamesParticipantData> gamesParticipantData);
	}
	
	public interface UserPhoneCallback {
		
		void onSendUserPhone(UserParticipantData user);
	}
	
	public interface AttachUserToGameCallback {
		
		void onSendResponse(String response);
	}
	
	static class AttachUserTask extends AsyncTask<Void, Void, Void> {
		
		private final AttachUserToGameCallback mAttachUserToGameCallback;
		private final String mToken;
		private final String mGameId;
		
		private AttachUserTask(
			final AttachUserToGameCallback callback,
			final String token,
			final String gameId
		) {
			mAttachUserToGameCallback = callback;
			mToken = token;
			mGameId = gameId;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callAttachToGameApi(mToken, mGameId).enqueue(new Callback<BaseServerAnswer<String>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<String>> call,
					@NonNull final Response<BaseServerAnswer<String>> response
				) {
					if (mAttachUserToGameCallback != null && response.body() != null) {
						mAttachUserToGameCallback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess().toString());
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<String>> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			return null;
		}
		
	}
	
	static class UnAttachUserTask extends AsyncTask<Void, Void, Void> {
		
		private final AttachUserToGameCallback mAttachUserToGameCallback;
		private final String mToken;
		private final String mGameId;
		
		private UnAttachUserTask(
			final AttachUserToGameCallback callback,
			final String token,
			final String gameId
		) {
			mAttachUserToGameCallback = callback;
			mToken = token;
			mGameId = gameId;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callUnAttachToGameApi(
				mToken,
				mGameId).enqueue(new Callback<BaseServerAnswer<String>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<String>> call,
					@NonNull final Response<BaseServerAnswer<String>> response
				) {
					if (mAttachUserToGameCallback != null && response.body() != null) {
						mAttachUserToGameCallback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess().toString());
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<String>> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			return null;
		}
		
	}
	
	static class GetGameParticipantsTask extends AsyncTask<Void, Void, Void> {
		
		private final String mGameId;
		private final GameParticipantsCallback mCallback;
		
		private GetGameParticipantsTask(
			final GameParticipantsCallback callback,
			final String gameId
		) {
			mCallback = callback;
			mGameId = gameId;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callGetGameParticipantsApi(mGameId).enqueue(new Callback<BaseServerAnswer<List<GamesParticipantData>>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<List<GamesParticipantData>>> call,
					@NonNull final Response<BaseServerAnswer<List<GamesParticipantData>>> response
				) {
					if (mCallback != null) {
						mCallback.onSendGamesParticipants(fetchGameParticipantsResults(response));
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<List<GamesParticipantData>>> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			return null;
		}
	}
	
	static class GetUserPhoneTask extends AsyncTask<Void, Void, Void> {
		
		private final String mUserId;
		private final UserPhoneCallback mUserPhoneCallback;
		
		private GetUserPhoneTask(final UserPhoneCallback callback, final String userId) {
			mUserPhoneCallback = callback;
			mUserId = userId;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callGetUserPhoneApi(mUserId).enqueue(new Callback<BaseServerAnswer<UserParticipantData>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<UserParticipantData>> call,
					@NonNull final Response<BaseServerAnswer<UserParticipantData>> response
				) {
					if (mUserPhoneCallback != null) {
						mUserPhoneCallback.onSendUserPhone(Objects.requireNonNull(response.body()).getMessage());
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<UserParticipantData>> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			return null;
		}
	}
	
}
