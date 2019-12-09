package com.example.user.sportify.ui.creategame;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import com.example.user.sportify.network.api.AppBase;
import com.example.user.sportify.network.models.LocationData;
import com.example.user.sportify.network.session.AuthSessionManager;
import com.example.user.sportify.network.session.SessionComponent;
import com.example.user.sportify.network.session.SessionData;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CreateGameModel {

    private AuthSessionManager authSessionManager;
    private SessionComponent daggerSessionComponent;


    private void initDatabase() {
        authSessionManager = daggerSessionComponent.getAuthSessionManager();
    }

    CreateGameModel(SessionComponent daggerSessionComponent) {
        this.daggerSessionComponent = daggerSessionComponent;
        initDatabase();
    }

    public SessionData getSessionData() {
        return authSessionManager.getSessionData();
    }

    void createGame(CreateGameCallback callback, File file, String token, String categoryId, String description, String date, String time, String location, String coordinates, String maxPeopleQuantity, String currentPeopleQuantity) {
        MultipartBody.Part fileMulti = makeMultipartablePicture(file);
        RequestBody tokeMulti = makeMultipartable(token);
        RequestBody categoryIdMulti = makeMultipartable(categoryId);
        RequestBody descriptionMulti = makeMultipartable(description);
        RequestBody dateMulti = makeMultipartable(date);
        RequestBody timeMulti = makeMultipartable(time);
        RequestBody locationMulti = makeMultipartable(location);
        RequestBody coordinatesMulti = makeMultipartable(coordinates);
        RequestBody maxPeopleQuantityMulti = makeMultipartable(maxPeopleQuantity);
        RequestBody currentPeopleQuantityMulti = makeMultipartable(currentPeopleQuantity);
        CreateGameTask createGameTask = new CreateGameTask(callback, fileMulti, tokeMulti, categoryIdMulti, descriptionMulti, dateMulti, timeMulti, locationMulti, coordinatesMulti, maxPeopleQuantityMulti, currentPeopleQuantityMulti);
        createGameTask.execute();
    }

    void updateGame(CreateGameCallback callback, String categoryId, String description, String date, String time, String location, String coordinates, String maxPeopleQuantity, String gameId) {
        UpdateGameTask updateGameTask = new UpdateGameTask(callback, categoryId, description, date, time, location, coordinates, maxPeopleQuantity, gameId);
        updateGameTask.execute();
    }

    void getLocation(LocationCallback callback, String geocode) {
        GetLocationTask getLocationTask = new GetLocationTask(callback, geocode);
        getLocationTask.execute();
    }

    private Call<LocationData> callCancelGameApi(String geocode) {
        return AppBase.getBaseService().getLocation(geocode);
    }


    interface CreateGameCallback {
        void onGameCreated(String response);
    }

    interface LocationCallback {
        void onLocationReceived(String coordinates);
    }

    private RequestBody makeMultipartable(String content) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), content);
    }

    private MultipartBody.Part makeMultipartablePicture(File file) {
        if (file != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file.getAbsoluteFile());
            return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        } else return null;
    }

    class CreateGameTask extends AsyncTask<Void, Void, Void> {

        private CreateGameCallback callback;
        private MultipartBody.Part picture;
        private RequestBody token;
        private RequestBody categoryId;
        private RequestBody description;
        private RequestBody date;
        private RequestBody time;
        private RequestBody location;
        private RequestBody coordinates;
        private RequestBody maxPeopleQuantity;
        private RequestBody currentPeopleQuantity;

        CreateGameTask(CreateGameCallback callback, MultipartBody.Part picture, RequestBody token, RequestBody categoryId, RequestBody description, RequestBody date, RequestBody time, RequestBody location, RequestBody coordinates, RequestBody maxPeopleQuantity, RequestBody currentPeopleQuantity) {

            this.callback = callback;
            this.picture = picture;
            this.token = token;
            this.categoryId = categoryId;
            this.description = description;
            this.date = date;
            this.time = time;
            this.location = location;
            this.coordinates = coordinates;
            this.maxPeopleQuantity = maxPeopleQuantity;
            this.currentPeopleQuantity = currentPeopleQuantity;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Call<ResponseBody> call = AppBase.getBaseService().createGame(token, categoryId, description, date, time, location, picture, coordinates, maxPeopleQuantity, currentPeopleQuantity);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (callback != null) {
                        callback.onGameCreated(String.valueOf(response.message()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Log.e("ex", "ошибка");
                }
            });

            return null;
        }
    }

    class UpdateGameTask extends AsyncTask<Void, Void, Void> {

        private CreateGameCallback callback;
        private String categoryId;
        private String description;
        private String date;
        private String time;
        private String location;
        private String coordinates;
        private String maxPeopleQuantity;
        private String gameId;

        UpdateGameTask(CreateGameCallback callback, String categoryId, String description, String date, String time, String location, String coordinates, String maxPeopleQuantity, String gameId) {
            this.callback = callback;
            this.categoryId = categoryId;
            this.description = description;
            this.date = date;
            this.time = time;
            this.location = location;
            this.coordinates = coordinates;
            this.maxPeopleQuantity = maxPeopleQuantity;
            this.gameId = gameId;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Call<ResponseBody> call = AppBase.getBaseService().updateGame(categoryId, description, date, time, location, coordinates, maxPeopleQuantity, gameId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (callback != null) {
                        callback.onGameCreated(String.valueOf(response.message()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    Log.e("ex", "ошибка");
                }
            });

            return null;
        }
    }

    class GetLocationTask extends AsyncTask<Void, Void, Void> {

        private String geocode;
        private LocationCallback callback;

        GetLocationTask(LocationCallback callback, String geocode) {
            this.geocode = geocode;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            callCancelGameApi(geocode).enqueue(new Callback<LocationData>() {
                @Override
                public void onResponse(@NonNull Call<LocationData> call, @NonNull Response<LocationData> response) {
                    callback.onLocationReceived(Objects.requireNonNull(response.body()).getLocation());
//                    Log.e("loc", Objects.requireNonNull(response.body()).getLocation());
                }

                @Override
                public void onFailure(@NonNull Call<LocationData> call, @NonNull Throwable t) {

                }
            });
            return null;
        }
    }

}
