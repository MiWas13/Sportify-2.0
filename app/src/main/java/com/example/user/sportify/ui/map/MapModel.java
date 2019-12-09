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

    MapModel(SessionComponent daggerSessionComponent) {
        super(daggerSessionComponent);
    }

    public void getGamesPerPage(GameDataCallback callback, int categoryId, int page) {
        GetGamesPerPageTask getGamesPerPageTask = new GetGamesPerPageTask(callback, categoryId, page);
        getGamesPerPageTask.execute();
    }

    public void getGamesParticipant(GamesParticipantCallback callback, String token) {
        GamesParticipantTask gamesParticipantTask = new GamesParticipantTask(callback, token);
        gamesParticipantTask.execute();
    }

    interface GameDataCallback {
        void onSendGamesData(List<GameDataApi> gameData, int pagesQuantity);
    }

    private Call<BaseServerAnswer<BaseGameDataApi>> callBaseGameDataApi(int categoryId, int page) {
        return AppBase.getBaseService().getGames(categoryId, page);
    }

    private Call<BaseServerAnswer<MyGamesData>> callBaseDataGamesParticipantApi(String token) {
        return AppBase.getBaseService().getMyGames(token);
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

    interface GamesParticipantCallback {
        void onSendPaticipantGames(List<GamesParticipantData> gamesParticipantData);
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

            callBaseGameDataApi(categoryId, page).enqueue(new Callback<BaseServerAnswer<BaseGameDataApi>>() {
                @Override
                public void onResponse(@NonNull Call<BaseServerAnswer<BaseGameDataApi>> call, @NonNull Response<BaseServerAnswer<BaseGameDataApi>> response) {
                    int pagesQuantity = fetchPagesQuantity(response);
                    List<GameDataApi> results = fetchResults(response);
                    if (callback != null) {
                        callback.onSendGamesData(results, pagesQuantity);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BaseServerAnswer<BaseGameDataApi>> call, @NonNull Throwable t) {

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
                public void onResponse(@NonNull Call<BaseServerAnswer<MyGamesData>> call, @NonNull Response<BaseServerAnswer<MyGamesData>> response) {
                    List<GamesParticipantData> results = fetchDataGamesParticipantResults(response);

                    if (callback != null) {
                        callback.onSendPaticipantGames(results);
                        isSended = true;
                    }
                }
//                    Log.e("res", results.get(0).getId());

                @Override
                public void onFailure(@NonNull Call<BaseServerAnswer<MyGamesData>> call, @NonNull Throwable t) {
                    Log.e("Zz", "я пришел");
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isSended)
                callback.onSendPaticipantGames(new ArrayList<>());
        }
    }


}
