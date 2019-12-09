package com.example.user.sportify.ui.feed;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.ui.games.GamesAdapter;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

interface FeedView extends MvpView {
    void initCategories(LinearLayoutManager categoriesLayoutManager, CategoriesAdapter categoriesAdapter);
    void initGames(LinearLayoutManager gamesLayoutManager, GamesAdapter gamesAdapter);
    void initGamesScrollListener();
    void loadFirstPage(List<GameDataApi> results);
    void loadNextPage(List<GameDataApi> results);
    void filterByCategory(List<GameDataApi> results, int position);
    void clearGames();
    void changeSelectedCategory(int position);
    void changeParticipantState(int position, int gameId, String currentPeopleQuantity);
    void cancelGame(int position, int gameId);
    void showProgressBar(ProgressDialog progressDialog, String tag);
    void hideProgressBar(ProgressDialog progressDialog);
    void showProgressBar();
    void hideProgressBar();
    void addLoading();
    void hideLoading();
    void startNewConcretGameActivity(Intent intent);
    void startNewCreateGameActivity(Intent intent);
    void showFirstRegDialog();
    void firstStepComplete();
    void secondStepComplete(Intent intent);
    void setPasswordError();
    void setNameError();
    void setPhoneError();
    void showMaxQuantitySnackBar();
    void showAuthError(String message);
}
