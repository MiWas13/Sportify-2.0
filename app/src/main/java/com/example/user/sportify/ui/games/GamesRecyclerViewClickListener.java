package com.example.user.sportify.ui.games;

import android.view.View;

import com.example.user.sportify.network.models.GameDataApi;

public interface GamesRecyclerViewClickListener {
	
	void onClick(View view, int position, GameDataApi game);
}