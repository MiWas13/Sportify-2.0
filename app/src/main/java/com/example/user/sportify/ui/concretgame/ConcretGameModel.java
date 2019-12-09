package com.example.user.sportify.ui.concretgame;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

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
    private AuthSessionManager authSessionManager;
    private SessionData sessionData;
    private SessionComponent daggerSessionComponent;


    private void initDatabase() {
        authSessionManager = daggerSessionComponent.getAuthSessionManager();
    }

    public void saveSessionData(String authToken, String phone, String password, String userId, String name) {
        authSessionManager.saveSessionData(authToken, phone, password, userId, name);
    }

    public ConcretGameModel(SessionComponent daggerSessionComponent) {
        this.daggerSessionComponent = daggerSessionComponent;
        initDatabase();
    }

    public SessionData getSessionData() {
        return authSessionManager.getSessionData();
    }

    public void attachUserToGame(AttachUserToGameCallback callback, String token, String gameId) {
        AttachUserTask attachUserTask = new AttachUserTask(callback, token, gameId);
        attachUserTask.execute();
    }

    public void unAttachUserFromGame(AttachUserToGameCallback callback, String token, String gameId) {
        UnAttachUserTask attachUserTask = new UnAttachUserTask(callback, token, gameId);
        attachUserTask.execute();
    }

    public void getGameParticipants(GameParticipantsCallback callback, String gameId) {
        GetGameParticipantsTask getGameParticipantsTask = new GetGameParticipantsTask(callback, gameId);
        getGameParticipantsTask.execute();
    }

    public void getUserPhone(UserPhoneCallback callback, String userId) {
        GetUserPhoneTask getUserPhoneTask = new GetUserPhoneTask(callback, userId);
        getUserPhoneTask.execute();
    }

    private Call<BaseServerAnswer<List<GamesParticipantData>>> callGetGameParticipantsApi(String gameId) {
        return AppBase.getBaseService().getGameParticipants(gameId);
    }

    private Call<BaseServerAnswer<String>> callAttachToGameApi(String token, String gameId) {
        return AppBase.getBaseService().attachToGame(token, gameId);
    }

    private Call<BaseServerAnswer<String>> callUnAttachToGameApi(String token, String gameId) {
        return AppBase.getBaseService().unAttachFromGame(token, gameId);
    }

    private Call<BaseServerAnswer<UserParticipantData>> callGetUserPhoneApi(String userId) {
        return AppBase.getBaseService().getUserPhone(userId);
    }

    private List<GamesParticipantData> fetchGameParticipantsResults(Response<BaseServerAnswer<List<GamesParticipantData>>> response) {
        BaseServerAnswer<List<GamesParticipantData>> baseGameParticipants = response.body();
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

    class AttachUserTask extends AsyncTask<Void, Void, Void> {

        private Boolean isSended = false;
        private AttachUserToGameCallback callback;
        private String token;
        private String gameId;

        AttachUserTask(AttachUserToGameCallback callback, String token, String gameId) {
            this.callback = callback;
            this.token = token;
            this.gameId = gameId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            callAttachToGameApi(token, gameId).enqueue(new Callback<BaseServerAnswer<String>>() {
                @Override
                public void onResponse(@NonNull Call<BaseServerAnswer<String>> call, @NonNull Response<BaseServerAnswer<String>> response) {
                    if (callback != null && response.body() != null) {
                        callback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess().toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BaseServerAnswer<String>> call, @NonNull Throwable t) {

                }
            });
            return null;
        }

    }

    class UnAttachUserTask extends AsyncTask<Void, Void, Void> {

        private Boolean isSended = false;
        private AttachUserToGameCallback callback;
        private String token;
        private String gameId;

        UnAttachUserTask(AttachUserToGameCallback callback, String token, String gameId) {
            this.callback = callback;
            this.token = token;
            this.gameId = gameId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            callUnAttachToGameApi(token, gameId).enqueue(new Callback<BaseServerAnswer<String>>() {
                @Override
                public void onResponse(@NonNull Call<BaseServerAnswer<String>> call, @NonNull Response<BaseServerAnswer<String>> response) {
                    if (callback != null && response.body() != null) {
                        callback.onSendResponse(Objects.requireNonNull(response.body()).getSuccess().toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BaseServerAnswer<String>> call, @NonNull Throwable t) {

                }
            });
            return null;
        }

    }

    class GetGameParticipantsTask extends AsyncTask<Void, Void, Void> {

        private String gameId;
        private GameParticipantsCallback callback;

        GetGameParticipantsTask(GameParticipantsCallback callback, String gameId) {
            this.callback = callback;
            this.gameId = gameId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            callGetGameParticipantsApi(gameId).enqueue(new Callback<BaseServerAnswer<List<GamesParticipantData>>>() {
                @Override
                public void onResponse(@NonNull Call<BaseServerAnswer<List<GamesParticipantData>>> call, @NonNull Response<BaseServerAnswer<List<GamesParticipantData>>> response) {
                    if (callback != null)
                        callback.onSendGamesParticipants(fetchGameParticipantsResults(response));
                }

                @Override
                public void onFailure(@NonNull Call<BaseServerAnswer<List<GamesParticipantData>>> call, @NonNull Throwable t) {

                }
            });
            return null;
        }
    }

    class GetUserPhoneTask extends AsyncTask<Void, Void, Void> {

        private String userId;
        private UserPhoneCallback callback;

        GetUserPhoneTask(UserPhoneCallback callback, String userId) {
            this.callback = callback;
            this.userId = userId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            callGetUserPhoneApi(userId).enqueue(new Callback<BaseServerAnswer<UserParticipantData>>() {
                @Override
                public void onResponse(@NonNull Call<BaseServerAnswer<UserParticipantData>> call, @NonNull Response<BaseServerAnswer<UserParticipantData>> response) {
                    if (callback != null)
                        callback.onSendUserPhone(Objects.requireNonNull(response.body()).getMessage());
                }

                @Override
                public void onFailure(@NonNull Call<BaseServerAnswer<UserParticipantData>> call, @NonNull Throwable t) {

                }
            });
            return null;
        }
    }

}
