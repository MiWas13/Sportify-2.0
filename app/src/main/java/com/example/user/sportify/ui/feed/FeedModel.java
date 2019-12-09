package com.example.user.sportify.ui.feed;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import android.util.Log;

import com.example.user.sportify.network.api.AppBase;
import com.example.user.sportify.network.models.BaseGameDataApi;
import com.example.user.sportify.network.models.BaseServerAnswer;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.models.GamesParticipantData;
import com.example.user.sportify.network.models.MyGamesData;
import com.example.user.sportify.network.session.AuthSessionManager;
import com.example.user.sportify.network.session.SessionComponent;
import com.example.user.sportify.network.session.SessionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedModel {
	
	private AuthSessionManager mAuthSessionManager;
	private SessionData mSessionData;
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
	
	FeedModel(final SessionComponent daggerSessionComponent) {
		mDaggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return mAuthSessionManager.getSessionData();
	}
	
	public static void getGamesPerPage(
		final GameDataCallback callback,
		final int categoryId,
		final int page
	) {
		final GetGamesPerPageTask getGamesPerPageTask = new GetGamesPerPageTask(
			callback,
			categoryId,
			page);
		getGamesPerPageTask.execute();
	}
	
	public static void getGamesParticipant(
		final GamesParticipantCallback callback,
		final String token
	) {
		final GamesParticipantTask gamesParticipantTask = new GamesParticipantTask(callback, token);
		gamesParticipantTask.execute();
	}
	
	public static void attachUserToGame(
		final BaseCallback callback,
		final String token,
		final String gameId
	) {
		final AttachUserTask attachUserTask = new AttachUserTask(callback, token, gameId);
		attachUserTask.execute();
	}
	
	public void unAttachUserFromGame(final BaseCallback callback, final String token, final String gameId) {
		final UnAttachUserTask attachUserTask = new UnAttachUserTask(callback, token, gameId);
		attachUserTask.execute();
	}
	
	public void cancelGame(final BaseCallback callback, final String token, final int cancel, final String gameId) {
		final CancelGameTask cancelGameTask = new CancelGameTask(callback, token, cancel, gameId);
		cancelGameTask.execute();
	}
	
	private static Call<BaseServerAnswer<MyGamesData>> callBaseDataGamesParticipantApi(final String token) {
		return AppBase.getBaseService().getMyGames(token);
	}
	
	private static Call<BaseServerAnswer<BaseGameDataApi>> callBaseGameDataApi(
		final int categoryId,
		final int page
	) {
		return AppBase.getBaseService().getGames(categoryId, page);
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
	
	private static Call<BaseServerAnswer<String>> callCancelGameApi(
		final String token,
		final int cancel,
		final String gameId
	) {
		return AppBase.getBaseService().cancelGame(token, cancel, gameId);
	}
	
	private static List<GameDataApi> fetchResults(final Response<BaseServerAnswer<BaseGameDataApi>> response) {
		final BaseServerAnswer<BaseGameDataApi> baseGameDataApi = response.body();
		assert baseGameDataApi != null;
		return baseGameDataApi.getMessage().getPosts();
	}
	
	private static int fetchPagesQuantity(final Response<BaseServerAnswer<BaseGameDataApi>> response) {
		final BaseServerAnswer<BaseGameDataApi> baseGameDataApi = response.body();
		assert baseGameDataApi != null;
		return baseGameDataApi.getMessage().getInfo().getPages();
	}
	
	private static List<GamesParticipantData> fetchDataGamesParticipantResults(final Response<BaseServerAnswer<MyGamesData>> response) {
		final BaseServerAnswer<MyGamesData> baseDataUserTokenApi = response.body();
		assert baseDataUserTokenApi != null;
		return baseDataUserTokenApi.getMessage().getAttached();
	}
	
	interface GameDataCallback {
		
		void onSendGamesData(List<GameDataApi> gameData, int pagesQuantity);
	}
	
	interface GamesParticipantCallback {
		
		void onSendPaticipantGames(List<GamesParticipantData> gamesParticipantData);
	}
	
	interface BaseCallback {
		
		void onSendResponse(Boolean response);
	}
	
	static class GetGamesPerPageTask extends AsyncTask<Void, Void, Void> {
		
		private final GameDataCallback mGameDataCallback;
		private final int mCategoryId;
		private final int mPage;
		
		private GetGamesPerPageTask(
			final GameDataCallback callback,
			final int categoryId,
			final int page
		) {
			mGameDataCallback = callback;
			mCategoryId = categoryId;
			mPage = page;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			
			callBaseGameDataApi(mCategoryId, mPage).enqueue(new Callback<BaseServerAnswer<BaseGameDataApi>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<BaseGameDataApi>> call,
					@NonNull final Response<BaseServerAnswer<BaseGameDataApi>> response
				) {
					final int pagesQuantity = fetchPagesQuantity(response);
					final List<GameDataApi> results = fetchResults(response);
					if (mGameDataCallback != null) {
						mGameDataCallback.onSendGamesData(results, pagesQuantity);
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<BaseGameDataApi>> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			
			return null;
		}
	}
	
	static class GamesParticipantTask extends AsyncTask<Void, Void, Void> {
		
		private final GamesParticipantCallback mGamesParticipantCallback;
		private final String mToken;
		private List<GamesParticipantData> mGamesParticipantDataList;
		
		private GamesParticipantTask(final GamesParticipantCallback callback, final String token) {
			mGamesParticipantCallback = callback;
			mToken = token;
			mGamesParticipantDataList = new ArrayList<>();
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callBaseDataGamesParticipantApi(mToken).enqueue(new Callback<BaseServerAnswer<MyGamesData>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull final Response<BaseServerAnswer<MyGamesData>> response
				) {
					try {
						mGamesParticipantDataList = fetchDataGamesParticipantResults(response);
						mGamesParticipantCallback.onSendPaticipantGames(mGamesParticipantDataList);
					} catch (final Exception e) {
						mGamesParticipantCallback.onSendPaticipantGames(null);
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull final Throwable t
				) {
					Log.e("Zz", "я пришел");
					mGamesParticipantCallback.onSendPaticipantGames(null);
				}
			});
			return null;
		}
	}
	
	static class AttachUserTask extends AsyncTask<Void, Void, Void> {
		
		private final BaseCallback mBaseCallback;
		private final String mToken;
		private final String mGameId;
		
		private AttachUserTask(final BaseCallback callback, final String token, final String gameId) {
			mBaseCallback = callback;
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
					if (mBaseCallback != null && response.body() != null) {
						mBaseCallback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess());
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
		
		private final BaseCallback mBaseCallback;
		private final String mToken;
		private final String mGameId;
		
		private UnAttachUserTask(
			final BaseCallback callback,
			final String token,
			final String gameId
		) {
			mBaseCallback = callback;
			mToken = token;
			mGameId = gameId;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callUnAttachToGameApi(mToken, mGameId).enqueue(new Callback<BaseServerAnswer<String>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<String>> call,
					@NonNull final Response<BaseServerAnswer<String>> response
				) {
					if (mBaseCallback != null && response.body() != null) {
						mBaseCallback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess());
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
	
	static class CancelGameTask extends AsyncTask<Void, Void, Void> {
		
		private final BaseCallback mBaseCallback;
		private final String mToken;
		private final int mCancel;
		private final String mGameId;
		
		private CancelGameTask(
			final BaseCallback callback,
			final String token,
			final int cancel,
			final String gameId
		) {
			mBaseCallback = callback;
			mToken = token;
			mCancel = cancel;
			mGameId = gameId;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callCancelGameApi(
				mToken,
				mCancel,
				mGameId).enqueue(new Callback<BaseServerAnswer<String>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<String>> call,
					@NonNull final Response<BaseServerAnswer<String>> response
				) {
					if (mBaseCallback != null && response.body() != null) {
						mBaseCallback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess());
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
	
}
