package com.example.user.sportify.ui.games;

import com.example.user.sportify.network.models.GameDataApi;

public interface UpdateGameButtonClickListener {
	
	void updateGameButtonOnClicked(int position, int gameId, GameDataApi game);
}
