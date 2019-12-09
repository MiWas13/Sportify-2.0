package com.example.user.sportify.ui.games;

import com.example.user.sportify.network.models.GameDataApi;

public interface ConnectToGameListener {
	
	void connectButtonOnClicked(GameDataApi game, int position, int gameId, Boolean isInGame);
}
