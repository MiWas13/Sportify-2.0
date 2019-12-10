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
	
	private AuthSessionManager mAuthSessionManager;
	private final SessionComponent mDaggerSessionComponent;
	
	
	private void initDatabase() {
		mAuthSessionManager = mDaggerSessionComponent.getAuthSessionManager();
	}
	
	CreateGameModel(final SessionComponent daggerSessionComponent) {
		mDaggerSessionComponent = daggerSessionComponent;
		initDatabase();
	}
	
	public SessionData getSessionData() {
		return mAuthSessionManager.getSessionData();
	}
	
	static void createGame(
		final CreateGameCallback callback,
		final File file,
		final String token,
		final String categoryId,
		final String description,
		final String date,
		final String time,
		final String location,
		final String coordinates,
		final String maxPeopleQuantity,
		final String currentPeopleQuantity
	) {
		final MultipartBody.Part fileMulti = makeMultipartablePicture(file);
		final RequestBody tokeMulti = makeMultipartable(token);
		final RequestBody categoryIdMulti = makeMultipartable(categoryId);
		final RequestBody descriptionMulti = makeMultipartable(description);
		final RequestBody dateMulti = makeMultipartable(date);
		final RequestBody timeMulti = makeMultipartable(time);
		final RequestBody locationMulti = makeMultipartable(location);
		final RequestBody coordinatesMulti = makeMultipartable(coordinates);
		final RequestBody maxPeopleQuantityMulti = makeMultipartable(maxPeopleQuantity);
		final RequestBody currentPeopleQuantityMulti = makeMultipartable(currentPeopleQuantity);
		final CreateGameTask createGameTask = new CreateGameTask(
			callback,
			fileMulti,
			tokeMulti,
			categoryIdMulti,
			descriptionMulti,
			dateMulti,
			timeMulti,
			locationMulti,
			coordinatesMulti,
			maxPeopleQuantityMulti,
			currentPeopleQuantityMulti);
		createGameTask.execute();
	}
	
	static void updateGame(
		final CreateGameCallback callback,
		final String categoryId,
		final String description,
		final String date,
		final String time,
		final String location,
		final String coordinates,
		final String maxPeopleQuantity,
		final String gameId
	) {
		final UpdateGameTask updateGameTask = new UpdateGameTask(
			callback,
			categoryId,
			description,
			date,
			time,
			location,
			coordinates,
			maxPeopleQuantity,
			gameId);
		updateGameTask.execute();
	}
	
	static void getLocation(final LocationCallback callback, final String geocode) {
		final GetLocationTask getLocationTask = new GetLocationTask(callback, geocode);
		getLocationTask.execute();
	}
	
	private static Call<LocationData> callCancelGameApi(final String geocode) {
		return AppBase.getBaseService().getLocation(geocode);
	}
	
	
	interface CreateGameCallback {
		
		void onGameCreated(String response);
	}
	
	interface LocationCallback {
		
		void onLocationReceived(String coordinates);
	}
	
	private static RequestBody makeMultipartable(final String content) {
		return RequestBody.create(MediaType.parse("multipart/form-data"), content);
	}
	
	private static MultipartBody.Part makeMultipartablePicture(final File file) {
		if (file != null) {
			final RequestBody requestFile = RequestBody.create(
				MediaType.parse("multipart/form-data"),
				file.getAbsoluteFile());
			return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
		} else {
			return null;
		}
	}
	
	static class CreateGameTask extends AsyncTask<Void, Void, Void> {
		
		private final CreateGameCallback mCreateGameCallback;
		private final MultipartBody.Part mPicture;
		private final RequestBody mToken;
		private final RequestBody mCategoryId;
		private final RequestBody mDescription;
		private final RequestBody mDate;
		private final RequestBody mTime;
		private final RequestBody mLocation;
		private final RequestBody mCoordinates;
		private final RequestBody mMaxPeopleQuantity;
		private final RequestBody mCurrentPeopleQuantity;
		
		private CreateGameTask(
			final CreateGameCallback callback,
			final MultipartBody.Part picture,
			final RequestBody token,
			final RequestBody categoryId,
			final RequestBody description,
			final RequestBody date,
			final RequestBody time,
			final RequestBody location,
			final RequestBody coordinates,
			final RequestBody maxPeopleQuantity,
			final RequestBody currentPeopleQuantity
		) {
			
			mCreateGameCallback = callback;
			mPicture = picture;
			mToken = token;
			mCategoryId = categoryId;
			mDescription = description;
			mDate = date;
			mTime = time;
			mLocation = location;
			mCoordinates = coordinates;
			mMaxPeopleQuantity = maxPeopleQuantity;
			mCurrentPeopleQuantity = currentPeopleQuantity;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			
			final Call<ResponseBody> call = AppBase.getBaseService().createGame(
				mToken,
				mCategoryId,
				mDescription,
				mDate,
				mTime,
				mLocation,
				mPicture,
				mCoordinates,
				mMaxPeopleQuantity,
				mCurrentPeopleQuantity);
			call.enqueue(new Callback<ResponseBody>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<ResponseBody> call,
					@NonNull final Response<ResponseBody> response
				) {
					if (mCreateGameCallback != null) {
						mCreateGameCallback.onGameCreated(String.valueOf(response.message()));
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<ResponseBody> call,
					@NonNull final Throwable t
				) {
					t.printStackTrace();
					Log.e("ex", "ошибка");
				}
			});
			
			return null;
		}
	}
	
	static class UpdateGameTask extends AsyncTask<Void, Void, Void> {
		
		private final CreateGameCallback mCreateGameCallback;
		private final String mCategoryId;
		private final String mDescription;
		private final String mDate;
		private final String mTime;
		private final String mLocation;
		private final String mCoordinates;
		private final String mMaxPeopleQuantity;
		private final String mGameId;
		
		private UpdateGameTask(
			final CreateGameCallback callback,
			final String categoryId,
			final String description,
			final String date,
			final String time,
			final String location,
			final String coordinates,
			final String maxPeopleQuantity,
			final String gameId
		) {
			mCreateGameCallback = callback;
			mCategoryId = categoryId;
			mDescription = description;
			mDate = date;
			mTime = time;
			mLocation = location;
			mCoordinates = coordinates;
			mMaxPeopleQuantity = maxPeopleQuantity;
			mGameId = gameId;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			
			final Call<ResponseBody> call = AppBase.getBaseService().updateGame(
				mCategoryId,
				mDescription,
				mDate,
				mTime,
				mLocation,
				mCoordinates,
				mMaxPeopleQuantity,
				mGameId);
			call.enqueue(new Callback<ResponseBody>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<ResponseBody> call,
					@NonNull final Response<ResponseBody> response
				) {
					if (mCreateGameCallback != null) {
						mCreateGameCallback.onGameCreated(String.valueOf(response.message()));
					}
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<ResponseBody> call,
					@NonNull final Throwable t
				) {
					t.printStackTrace();
					Log.e("ex", "ошибка");
				}
			});
			
			return null;
		}
	}
	
	static class GetLocationTask extends AsyncTask<Void, Void, Void> {
		
		private final String mGeocode;
		private final LocationCallback mLocationCallback;
		
		private GetLocationTask(final LocationCallback callback, final String geocode) {
			mGeocode = geocode;
			mLocationCallback = callback;
		}
		
		@Override
		protected Void doInBackground(final Void... voids) {
			callCancelGameApi(mGeocode).enqueue(new Callback<LocationData>() {
				
				@Override
				public void onResponse(
					@NonNull final Call<LocationData> call,
					@NonNull final Response<LocationData> response
				) {
					mLocationCallback.onLocationReceived(Objects.requireNonNull(response.body()).getLocation());
				}
				
				@Override
				public void onFailure(
					@NonNull final Call<LocationData> call,
					@NonNull final Throwable t
				) {
				
				}
			});
			return null;
		}
	}
	
}
