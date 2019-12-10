package com.example.user.sportify.ui.map;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import android.util.Log;

import com.example.user.sportify.network.api.AppBase;
import com.example.user.sportify.network.models.BaseGameDataApi;
import com.example.user.sportify.network.models.BaseServerAnswer;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.models.GamesParticipantData;
import com.example.user.sportify.network.models.MyGamesData;
import com.example.user.sportify.network.session.SessionComponent;
import com.example.user.sportify.ui.concretgame.ConcretGameModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapModel extends ConcretGameModel {
	
	MapModel(final SessionComponent daggerSessionComponent) {
		super(daggerSessionComponent);
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
	
	public void getGamesParticipant(final GamesParticipantCallback callback, final String token) {
		final GamesParticipantTask gamesParticipantTask = new GamesParticipantTask(callback, token);
		gamesParticipantTask.execute();
	}
	
	interface GameDataCallback {
		
		void onSendGamesData(List<GameDataApi> gameData, int pagesQuantity);
	}
	
	private static Call<BaseServerAnswer<BaseGameDataApi>> callBaseGameDataApi(
		final int categoryId,
		final int page
	) {
		return AppBase.getBaseService().getGames(categoryId, page);
	}
	
	private static Call<BaseServerAnswer<MyGamesData>> callBaseDataGamesParticipantApi(final String token) {
		return AppBase.getBaseService().getMyGames(token);
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
		final List<GamesParticipantData> gamesParticipantData;
		final BaseServerAnswer<MyGamesData> baseDataUserTokenApi = response.body();
		assert baseDataUserTokenApi != null;
		gamesParticipantData = baseDataUserTokenApi.getMessage().getAttached();
		return gamesParticipantData;
	}
	
	interface GamesParticipantCallback {
		
		void onSendPaticipantGames(List<GamesParticipantData> gamesParticipantData);
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
			
			callBaseGameDataApi(
				mCategoryId,
				mPage).enqueue(new Callback<BaseServerAnswer<BaseGameDataApi>>() {
				
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
	
	
	class GamesParticipantTask extends AsyncTask<Void, Void, Void> {
		
		private Boolean isSended = false;
		private final GamesParticipantCallback mGamesParticipantCallback;
		private final String mToken;
		
		private GamesParticipantTask(final GamesParticipantCallback callback, final String token) {
			mGamesParticipantCallback = callback;
			mToken = token;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callBaseDataGamesParticipantApi(mToken).enqueue(new Callback<BaseServerAnswer<MyGamesData>>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull final Response<BaseServerAnswer<MyGamesData>> response
				) {
					final List<GamesParticipantData> results = fetchDataGamesParticipantResults(
						response);
					
					if (mGamesParticipantCallback != null) {
						mGamesParticipantCallback.onSendPaticipantGames(results);
						isSended = true;
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<BaseServerAnswer<MyGamesData>> call,
					@NonNull final Throwable t
				) {
					Log.e("Zz", "я пришел");
				}
			});
			return null;
		}
		
		@Override
		protected void onPostExecute(final Void aVoid) {
			super.onPostExecute(aVoid);
			if (!isSended) {
				mGamesParticipantCallback.onSendPaticipantGames(new ArrayList<>());
			}
		}
	}
	
	
}
