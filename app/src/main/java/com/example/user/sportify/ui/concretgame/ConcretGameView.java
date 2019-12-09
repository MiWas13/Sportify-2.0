package com.example.user.sportify.ui.concretgame;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.user.sportify.network.models.UserParticipantData;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpView;

interface ConcretGameView extends MvpView {
	
	void initToolbar(String category);
	
	void initGameDescription(
		LinearLayoutManager gamesLayoutManager,
		ConcretGameDescriptionAdapter concretGameDescriptionAdapter
	);
	
	void initParticipantsPhones(
		LinearLayoutManager participantsPhonesLayoutManager,
		OrganizerConcretGameParticipantsAdapter organizerConcretGameParticipantsAdapter
	);
	
	void setPhoto(String photoUrl);
	
	void setDescription(String description);
	
	void startNewActivity(Intent intent);
	
	void hideConnectButton();
	
	void setInGameState();
	
	void setNotInGameState();
	
	void showProgressBar(ProgressDialog progressDialog, String tag);
	
	void hideProgressBar(ProgressDialog progressDialog);
	
	void addParticipant(UserParticipantData user);
}
