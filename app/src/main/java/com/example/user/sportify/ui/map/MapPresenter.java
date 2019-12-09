package com.example.user.sportify.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;

import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.models.GamesParticipantData;
import com.example.user.sportify.network.models.UserParticipantData;
import com.example.user.sportify.ui.concretgame.ConcretGameDescriptionAdapter;
import com.example.user.sportify.ui.concretgame.OrganizerConcretGameParticipantsAdapter;
import com.example.user.sportify.ui.creategame.CreateGame;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.yandex.mapkit.map.PlacemarkMapObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


class MapPresenter extends MvpBasePresenter<MapView> {
	
	private MapModel mapModel;
	private int currentPage = 1;
	private int lastPage = 0;
	private Context context;
	private PlacemarkMapObject lastClickedMark;
	private GameDataApi lastClickedGame;
	private List<UserParticipantData> users;
	private List<Integer> participantDataArray;
	private ArrayList<ArrayList<String>> locationArray;
	private List<GameDataApi> gamesArray;
	private ArrayList<Integer> iconsArray;
	
	MapPresenter(Context context, MapModel mapModel) {
		this.mapModel = mapModel;
		this.context = context;
	}
	
	public void onViewCreated() {
		getFirstPage();
		getParticipantsData();
		locationArray = new ArrayList<>();
		gamesArray = new ArrayList<>();
		iconsArray = new ArrayList<>();
		
		
	}
	
	private void getFirstPage() {
		new android.os.Handler().postDelayed(() -> {
			mapModel.getGamesPerPage((gameData, pagesQuantity) -> {
				ifViewAttached(view -> {
					view.showMapView();
					view.hideProgressBar();
				});
				lastPage = pagesQuantity;
				locationArray.addAll(makeLocationArray(gameData));
				gamesArray.addAll(gameData);
				iconsArray.addAll(makeIconsArray(gameData));
				
				while (currentPage <= lastPage) {
					getNextPage(currentPage);
					currentPage++;
				}
				
			}, 0, 1);
		}, 800);
		
	}
	
	private void getParticipantsData() {
		mapModel.getGamesParticipant(
			this::makeParticipantDataArray,
			mapModel.getSessionData().authToken);
	}
	
	
	private void getNextPage(int page) {
		mapModel.getGamesPerPage((gameData, pagesQuantity) -> {
			locationArray.addAll(makeLocationArray(gameData));
			gamesArray.addAll(gameData);
			iconsArray.addAll(makeIconsArray(gameData));
			if (page == lastPage) {
				ifViewAttached(view -> view.addPoints(locationArray, gamesArray, iconsArray));
			}
		}, 0, page);
		
	}
	
	private ArrayList<ArrayList<String>> makeLocationArray(List<GameDataApi> games) {
		ArrayList<String> coordinatesArray = new ArrayList<>();
		ArrayList<ArrayList<String>> locationsList = new ArrayList<>();
		for (GameDataApi gameDataApi : games) {
			coordinatesArray.add(getLatitude(gameDataApi.getCoordinates()));
			coordinatesArray.add(getLongitude(gameDataApi.getCoordinates()));
			locationsList.add(coordinatesArray);
			//TODO: надо экономить память
			coordinatesArray = new ArrayList<>();
		}
		
		return locationsList;
	}
	
	void onMapObjectTap(GameDataApi game, PlacemarkMapObject mark) {
		
		ifViewAttached(view -> view.setActiveIcon(mark, getCategoryIcon(game.getCategoryId())));

//        if (lastClickedGame != null && !lastClickedGame.getLocation().equals(game.getLocation())) {
//            Log.e("Loc", game.getLocation());
//            Log.e("lastLoc", lastClickedGame.getLocation());
//            ifViewAttached(view -> view.setInActiveIcon(lastClickedMark, lastClickedView, lastClickedCategory, getCategoryIcon(lastClickedGame.getCategoryId())));
//        }
		
		Boolean isOrganizer = String.valueOf(game.getCreatorId()).equals(mapModel.getSessionData().userId);
		if (isOrganizer) {
			ifViewAttached(view -> {
				view.initOrganizerGame();
				view.hideParticipantButton();
			});
			
			ifViewAttached(view -> view.initOrganizerGameDescription(
				new LinearLayoutManager(context),
				new ConcretGameDescriptionAdapter(context, game,
					(view1, position) -> {
					
					}, phone -> {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:+7" + phone));
					view.startNewActivity(intent);
					
				}, coordinates -> {
					Intent mapIntent;
					try {
						mapIntent = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("yandexmaps://maps.yandex.ru/?pt=" + modifyCoordinates(
								coordinates) + "&z=12&l=map"));
						view.startNewActivity(mapIntent);
					} catch (Exception yandexNotFound) {
						Log.e("", "Нет яндекс карт");
						try {
							Uri gmmIntentUri = Uri.parse("google.navigation:q=" + game.getLocation());
							mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
							mapIntent.setPackage("com.google.android.apps.maps");
							view.startNewActivity(mapIntent);
						} catch (Exception googleNotFound) {
							Log.e("", "нет приложений для карт");
						}
					}
					
				}, true)));
			
			addParticipants(String.valueOf(game.getId()));
		} else {
			
			ifViewAttached(view -> {
				view.showParticipantButton();
				view.initGame();
				view.setGameInfo();
			});
			
			ifViewAttached(MapView::showParticipantButton);
			if (participantDataArray.contains(game.getId())) {
				ifViewAttached(MapView::setInGameState);
			} else {
				ifViewAttached(MapView::setNotInGameState);
			}
			
			ifViewAttached(view -> view.initGameDescription(
				new LinearLayoutManager(context),
				new ConcretGameDescriptionAdapter(context, game,
					(view1, position) -> {
					
					}, phone -> {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:+7" + phone));
					view.startNewActivity(intent);
				}, coordinates -> {
					Intent mapIntent;
					try {
						mapIntent = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("yandexmaps://maps.yandex.ru/?pt=" + modifyCoordinates(
								coordinates) + "&z=12&l=map"));
						view.startNewActivity(mapIntent);
					} catch (Exception yandexNotFound) {
						Log.e("", "Нет яндекс карт");
						try {
//                                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=" + modifyCoordinates(coordinates));
							Uri gmmIntentUri = Uri.parse("google.navigation:q=" + game.getLocation());
							mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
							mapIntent.setPackage("com.google.android.apps.maps");
							view.startNewActivity(mapIntent);
						} catch (Exception googleNotFound) {
							Log.e("", "нет приложений для карт");
						}
					}
					
				}, false)));
		}
		
		ifViewAttached(view -> view.setBaseInfo(
			game,
			getCategoryName(game.getCategoryId()),
			getCurrentDate(game.getDate(), game.getTime())));
		
		users = new ArrayList<>();
		ifViewAttached(view -> view.initParticipantsPhones(
			new LinearLayoutManager(context),
			new OrganizerConcretGameParticipantsAdapter(context, users, phone -> {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:+7" + phone));
				view.startNewActivity(intent);
			})));
		
		ifViewAttached(MapView::showBottomSlider);
		
		lastClickedMark = mark;
		lastClickedGame = game;
	}
	
	@NonNull
	private String getLatitude(String coordinates) {
		
		StringBuilder sb = new StringBuilder(coordinates);
		sb.delete(0, sb.lastIndexOf(" "));
		
		return sb.toString().trim();
	}
	
	
	@NonNull
	private String getLongitude(String coordinates) {
		coordinates = coordinates.trim();
		
		StringBuilder sb = new StringBuilder(coordinates);
		sb.delete(sb.lastIndexOf(" "), coordinates.length());
		return sb.toString().trim();
	}
	
	private ArrayList<Integer> makeIconsArray(List<GameDataApi> games) {
		ArrayList<Integer> iconsArray = new ArrayList<>();
		for (GameDataApi game : games) {
			iconsArray.add(getCategoryIcon(game.getCategoryId()));
		}
		return iconsArray;
	}
	
	private int getCategoryIcon(int categoryId) {
		
		switch (categoryId) {
			case 1:
				return R.drawable.ic_basketball;
			
			case 2:
				return R.drawable.ic_football;
			
			case 3:
				return R.drawable.ic_tennis;
			
			case 4:
				return R.drawable.ic_chess;
			
			case 5:
				return R.drawable.ic_running;
			
			case 6:
				return R.drawable.ic_pingpong;
		}
		
		return 0;
	}
	
	private String getCategoryName(int categoryId) {
		String categoryName = "";
		switch (categoryId) {
			case 1:
				categoryName = "Баскетбол";
				break;
			case 2:
				categoryName = "Футбол";
				break;
			case 3:
				categoryName = "Теннис";
				break;
			case 4:
				categoryName = "Шахматы";
				break;
			case 5:
				categoryName = "Бег";
				break;
			case 6:
				categoryName = "Пинг-Понг";
				break;
		}
		return categoryName;
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getCurrentDate(String serverDate, String time) {
		Locale russian = new Locale("ru");
		String[] newMonths = {
			"января", "февраля", "марта", "апреля", "мая", "июня",
			"июля", "августа", "сентября", "октября", "ноября", "декабря" };
		DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(russian);
		dateFormatSymbols.setMonths(newMonths);
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, russian);
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
		simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
		
		StringBuilder stringBuilder = new StringBuilder(time);
		
		stringBuilder.delete(stringBuilder.lastIndexOf(":"), 8);
		
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(serverDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder(simpleDateFormat.format(date));
		
		if (sb.lastIndexOf("2018 г.") != -1) {
			sb.delete(sb.lastIndexOf("2018 г."), simpleDateFormat.format(date).length());
		} else {
			sb.delete(sb.lastIndexOf("2019 г."), simpleDateFormat.format(date).length());
		}
		return sb.toString().trim() + " в " + stringBuilder.toString();
	}
	
	@NonNull
	private String modifyCoordinates(String coordinates) {
		StringBuilder sb = new StringBuilder(coordinates);
		sb.insert(sb.lastIndexOf(" "), ",");
		sb.deleteCharAt(sb.lastIndexOf(" "));
		return sb.toString().trim();
	}
	
	private void addParticipants(String gameId) {
		mapModel.getGameParticipants(gamesParticipantData -> {
			for (GamesParticipantData gamesParticipant : gamesParticipantData) {
				mapModel.getUserPhone(
					user -> ifViewAttached(view -> view.addParticipant(user)),
					gamesParticipant.getUserId());
			}
		}, gameId);
	}
	
	public void onFabClicked() {
		if (mapModel.getSessionData().authToken != null) {
			ifViewAttached(view -> view.startNewCreateGameActivity(new Intent(
				context,
				CreateGame.class)));
		} else {
			ifViewAttached(view -> view.showRegistrationSnackBar(
				"Пожалуйста, зарегистрируйтесь в разделе «профиль» :)"));
		}
	}
	
	public void onConnectButtonClicked(GameDataApi game) {
		if (participantDataArray.contains(game.getId())) {
			mapModel.unAttachUserFromGame(response -> {
				ifViewAttached(MapView::setNotInGameState);
				participantDataArray.remove(participantDataArray.indexOf(game.getId()));
			}, mapModel.getSessionData().authToken, String.valueOf(game.getId()));
		} else {
			mapModel.attachUserToGame(response -> {
				ifViewAttached(MapView::setInGameState);
				participantDataArray.add(game.getId());
			}, mapModel.getSessionData().authToken, String.valueOf(game.getId()));
		}
	}
	
	private void makeParticipantDataArray(List<GamesParticipantData> gamesParticipantData) {
		participantDataArray = new ArrayList<>();
		if (gamesParticipantData != null) {
			for (GamesParticipantData game : gamesParticipantData) {
				participantDataArray.add(game.getGameId());
			}
		}
	}
	
}
