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
	
	private AuthSessionManager authSessionManager;
	private SessionData sessionData;
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
	
	FeedModel(SessionComponent daggerSessionComponent) {
		this.daggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return authSessionManager.getSessionData();
	}
	
	public void getGamesPerPage(GameDataCallback callback, int categoryId, int page) {
		GetGamesPerPageTask getGamesPerPageTask = new GetGamesPerPageTask(
			callback,
			categoryId,
			page);
		getGamesPerPageTask.execute();
	}
	
	public void getGamesParticipant(GamesParticipantCallback callback, String token) {
		GamesParticipantTask gamesParticipantTask = new GamesParticipantTask(callback, token);
		gamesParticipantTask.execute();
	}
	
	public void attachUserToGame(BaseCallback callback, String token, String gameId) {
		AttachUserTask attachUserTask = new AttachUserTask(callback, token, gameId);
		attachUserTask.execute();
	}
	
	public void unAttachUserFromGame(BaseCallback callback, String token, String gameId) {
		UnAttachUserTask attachUserTask = new UnAttachUserTask(callback, token, gameId);
		attachUserTask.execute();
	}
	
	public void cancelGame(BaseCallback callback, String token, int cancel, String gameId) {
		CancelGameTask cancelGameTask = new CancelGameTask(callback, token, cancel, gameId);
		cancelGameTask.execute();
	}
	
	private Call<BaseServerAnswer<MyGamesData>> callBaseDataGamesParticipantApi(String token) {
		return AppBase.getBaseService().getMyGames(token);
	}
	
	private Call<BaseServerAnswer<BaseGameDataApi>> callBaseGameDataApi(int categoryId, int page) {
		return AppBase.getBaseService().getGames(categoryId, page);
	}
	
	private Call<BaseServerAnswer<String>> callAttachToGameApi(String token, String gameId) {
		return AppBase.getBaseService().attachToGame(token, gameId);
	}
	
	private Call<BaseServerAnswer<String>> callUnAttachToGameApi(String token, String gameId) {
		return AppBase.getBaseService().unAttachFromGame(token, gameId);
	}
	
	private Call<BaseServerAnswer<String>> callCancelGameApi(
		String token,
		int cancel,
		String gameId
	) {
		return AppBase.getBaseService().cancelGame(token, cancel, gameId);
	}
	
	private List<GameDataApi> fetchResults(Response<BaseServerAnswer<BaseGameDataApi>> response) {
		BaseServerAnswer<BaseGameDataApi> baseGameDataApi = response.body();
		assert baseGameDataApi != null;
		return baseGameDataApi.getMessage().getPosts();
	}
	
	private int fetchPagesQuantity(Response<BaseServerAnswer<BaseGameDataApi>> response) {
		BaseServerAnswer<BaseGameDataApi> baseGameDataApi = response.body();
		assert baseGameDataApi != null;
		return baseGameDataApi.getMessage().getInfo().getPages();
	}
	
	private List<GamesParticipantData> fetchDataGamesParticipantResults(Response<BaseServerAnswer<MyGamesData>> response) {
		List<GamesParticipantData> gamesParticipantData;
		BaseServerAnswer<MyGamesData> baseDataUserTokenApi = response.body();
		assert baseDataUserTokenApi != null;
		gamesParticipantData = baseDataUserTokenApi.getMessage().getAttached();
		return gamesParticipantData;
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
	
	class GetGamesPerPageTask extends AsyncTask<Void, Void, Void> {
		
		private GameDataCallback callback;
		private int categoryId;
		private int page;
		
		GetGamesPerPageTask(GameDataCallback callback, int categoryId, int page) {
			this.callback = callback;
			this.categoryId = categoryId;
			this.page = page;
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			
			callBaseGameDataApi(
				categoryId,
				page).enqueue(new Callback<BaseServerAnswer<BaseGameDataApi>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<BaseGameDataApi>> call,
					@NonNull Response<BaseServerAnswer<BaseGameDataApi>> response
				) {
					int pagesQuantity = fetchPagesQuantity(response);
					List<GameDataApi> results = fetchResults(response);
					if (callback != null) {
						callback.onSendGamesData(results, pagesQuantity);
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<BaseGameDataApi>> call,
					@NonNull Throwable t
				) {
				
				}
			});
			
			return null;
		}
	}
	
	class GamesParticipantTask extends AsyncTask<Void, Void, Void> {
		
		private Boolean isSended = false;
		private GamesParticipantCallback callback;
		private String token;
		private List<GamesParticipantData> gamesParticipantDataList;
		
		GamesParticipantTask(GamesParticipantCallback callback, String token) {
			this.callback = callback;
			this.token = token;
			this.gamesParticipantDataList = new ArrayList<>();
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			callBaseDataGamesParticipantApi(token).enqueue(new Callback<BaseServerAnswer<MyGamesData>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull Response<BaseServerAnswer<MyGamesData>> response
				) {
					try {
						gamesParticipantDataList = fetchDataGamesParticipantResults(response);
						callback.onSendPaticipantGames(gamesParticipantDataList);
					} catch (Exception e) {
						callback.onSendPaticipantGames(null);
					}
//                    if (callback != null) {
//                        callback.onSendPaticipantGames(gamesParticipantDataList);
//                        isSended = true;
//                    }
				}
//                    Log.e("res", results.get(0).getId());
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull Throwable t
				) {
					Log.e("Zz", "я пришел");
					callback.onSendPaticipantGames(null);
				}
			});
			return null;
		}

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (!isSended)
//                callback.onSendPaticipantGames(gamesParticipantDataList);
//        }
	}
	
	class AttachUserTask extends AsyncTask<Void, Void, Void> {
		
		private Boolean isSended = false;
		private BaseCallback callback;
		private String token;
		private String gameId;
		
		AttachUserTask(BaseCallback callback, String token, String gameId) {
			this.callback = callback;
			this.token = token;
			this.gameId = gameId;
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			callAttachToGameApi(token, gameId).enqueue(new Callback<BaseServerAnswer<String>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<String>> call,
					@NonNull Response<BaseServerAnswer<String>> response
				) {
					if (callback != null && response.body() != null) {
						callback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess());
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<String>> call,
					@NonNull Throwable t
				) {
				
				}
			});
			return null;
		}
		
	}
	
	class UnAttachUserTask extends AsyncTask<Void, Void, Void> {
		
		private Boolean isSended = false;
		private BaseCallback callback;
		private String token;
		private String gameId;
		
		UnAttachUserTask(BaseCallback callback, String token, String gameId) {
			this.callback = callback;
			this.token = token;
			this.gameId = gameId;
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			callUnAttachToGameApi(token, gameId).enqueue(new Callback<BaseServerAnswer<String>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<String>> call,
					@NonNull Response<BaseServerAnswer<String>> response
				) {
					if (callback != null && response.body() != null) {
						callback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess());
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<String>> call,
					@NonNull Throwable t
				) {
				
				}
			});
			return null;
		}
		
	}
	
	class CancelGameTask extends AsyncTask<Void, Void, Void> {
		
		private Boolean isSended = false;
		private BaseCallback callback;
		private String token;
		private int cancel;
		private String gameId;
		
		CancelGameTask(BaseCallback callback, String token, int cancel, String gameId) {
			this.callback = callback;
			this.token = token;
			this.cancel = cancel;
			this.gameId = gameId;
		}
		
		@Override
		protected Void doInBackground(Void... voids) {
			callCancelGameApi(
				token,
				cancel,
				gameId).enqueue(new Callback<BaseServerAnswer<String>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<String>> call,
					@NonNull Response<BaseServerAnswer<String>> response
				) {
					if (callback != null && response.body() != null) {
						callback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess());
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<String>> call,
					@NonNull Throwable t
				) {
				
				}
			});
			return null;
		}
		
	}
	
}
