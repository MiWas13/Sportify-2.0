package com.example.user.sportify.ui.map;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.models.UserParticipantData;
import com.example.user.sportify.ui.concretgame.ConcretGameDescriptionAdapter;
import com.example.user.sportify.ui.concretgame.OrganizerConcretGameParticipantsAdapter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.yandex.mapkit.map.PlacemarkMapObject;

import java.util.ArrayList;
import java.util.List;

public interface MapView extends MvpView {
	
	void addPoints(
		ArrayList<ArrayList<String>> coordinates,
		List<GameDataApi> games,
		ArrayList<Integer> icons
	);
	
	void startNewActivity(Intent intent);
	
	void startNewCreateGameActivity(Intent intent);
	
	void setActiveIcon(PlacemarkMapObject mark, int categoryImage);
	
	void setInActiveIcon(PlacemarkMapObject mark, int categoryIcon);
	
	void initGameDescription(
		LinearLayoutManager gamesLayoutManager,
		ConcretGameDescriptionAdapter concretGameDescriptionAdapter
	);
	
	void setGameInfo();
	
	void showBottomSlider();
	
	void initGame();
	
	void setBaseInfo(GameDataApi game, String categoryName, String date);
	
	void initOrganizerGameDescription(
		LinearLayoutManager concretGameDescriptionLayoutManager,
		ConcretGameDescriptionAdapter concretGameDescriptionAdapter
	);
	
	void initOrganizerGame();
	
	void addParticipant(UserParticipantData user);
	
	void initParticipantsPhones(
		LinearLayoutManager participantsPhonesLayoutManager,
		OrganizerConcretGameParticipantsAdapter organizerConcretGameParticipantsAdapter
	);
	
	void setNotInGameState();
	
	void setInGameState();
	
	void hideParticipantButton();
	
	void showParticipantButton();
	
	void hideProgressBar();
	
	void showProgressBar();
	
	void showRegistrationSnackBar(String message);
	
	void showMapView();
}
