package com.example.user.sportify.ui.mygames;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.ui.games.GamesAdapter;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

interface MyGamesView extends MvpView {
    void initGamesRecyclerView(LinearLayoutManager gamesLayoutManager, GamesAdapter gamesAdapter);
    void setGamesData(List<GameDataApi> games);
    void clearList();
    void cancelGame(int position, int gameId);
    void showProgressBar(ProgressDialog progressDialog, String tag);
    void hideProgressBar(ProgressDialog progressDialog);
    void unParticipate(int position);
    void startNewActivity(Intent intent);
    void hideProgressBar();
    void setEmptyState();
}
