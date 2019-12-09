package com.example.user.sportify.ui.mygames;

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

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGamesModel {
	
	
	private AuthSessionManager authSessionManager;
	private SessionComponent daggerSessionComponent;
	
	
	private void initDatabase() {
		authSessionManager = daggerSessionComponent.getAuthSessionManager();
	}
	
	MyGamesModel(SessionComponent daggerSessionComponent) {
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
	
	public void unAttachUserFromGame(BaseCallback callback, String token, String gameId) {
		UnAttachUserTask unAttachUserTask = new UnAttachUserTask(callback, token, gameId);
		unAttachUserTask.execute();
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
	
	private List<GameDataApi> fetchDataGamesOrginazerResults(Response<BaseServerAnswer<MyGamesData>> response) {
		List<GameDataApi> gamesParticipantData;
		BaseServerAnswer<MyGamesData> baseDataUserTokenApi = response.body();
		assert baseDataUserTokenApi != null;
		gamesParticipantData = baseDataUserTokenApi.getMessage().getOwner();
		return gamesParticipantData;
	}
	
	public void getGamesParticipant(GamesParticipantCallback callback, String token) {
		GamesParticipantTask gamesParticipantTask = new GamesParticipantTask(callback, token);
		gamesParticipantTask.execute();
	}
	
	public void getGamesOrganizer(GameDataCallback callback, String token) {
		GamesOrganizerTask gamesOrganizerTask = new GamesOrganizerTask(callback, token);
		gamesOrganizerTask.execute();
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
	
	
	interface GameDataCallback {
		
		void onSendGamesData(List<GameDataApi> gameData, int pagesQuantity);
	}
	
	interface GamesParticipantCallback {
		
		void onSendPaticipantGames(List<GamesParticipantData> gamesParticipantData);
	}
	
	interface BaseCallback {
		
		void onSendResponse(String response);
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
					Log.e("", "ошибка");
				}
			});
			
			return null;
		}
	}
	
	
	class GamesParticipantTask extends AsyncTask<Void, Void, Void> {
		
		private Boolean isSended = false;
		private GamesParticipantCallback callback;
		private String token;
		
		GamesParticipantTask(GamesParticipantCallback callback, String token) {
			this.callback = callback;
			this.token = token;
		}
		
		
		@Override
		protected Void doInBackground(Void... voids) {
			callBaseDataGamesParticipantApi(token).enqueue(new Callback<BaseServerAnswer<MyGamesData>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull Response<BaseServerAnswer<MyGamesData>> response
				) {
					List<GamesParticipantData> results = fetchDataGamesParticipantResults(response);
					
					if (callback != null) {
						callback.onSendPaticipantGames(results);
						isSended = true;
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull Throwable t
				) {
					callback.onSendPaticipantGames(null);
				}
			});
			return null;
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
		}
	}
	
	class GamesOrganizerTask extends AsyncTask<Void, Void, Void> {
		
		private Boolean isSended = false;
		private GameDataCallback callback;
		private String token;
		
		GamesOrganizerTask(GameDataCallback callback, String token) {
			this.callback = callback;
			this.token = token;
		}
		
		
		@Override
		protected Void doInBackground(Void... voids) {
			callBaseDataGamesParticipantApi(token).enqueue(new Callback<BaseServerAnswer<MyGamesData>>() {
				
				@Override
				public void onResponse(
					@NonNull Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull Response<BaseServerAnswer<MyGamesData>> response
				) {
					List<GameDataApi> results = fetchDataGamesOrginazerResults(response);
					
					if (callback != null) {
						callback.onSendGamesData(results, 0);
						isSended = true;
					}
				}
				
				@Override
				public void onFailure(
					@NonNull Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull Throwable t
				) {
					callback.onSendGamesData(null, 0);
				}
			});
			return null;
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
//            if (!isSended)
//                callback.onSendGamesData();
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
						callback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess().toString());
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
						callback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess().toString());
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
