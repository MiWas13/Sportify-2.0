package com.example.user.sportify.ui.mygames;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.models.GamesParticipantData;
import com.example.user.sportify.ui.concretgame.ConcretGameActivity;
import com.example.user.sportify.ui.creategame.CreateGame;
import com.example.user.sportify.ui.games.GamesAdapter;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import static com.example.user.sportify.ui.utils.Constants.EXTRA_GAME;
import static com.example.user.sportify.ui.utils.Constants.EXTRA_PARTICIPANT_TYPE;
import static com.example.user.sportify.ui.utils.Constants.GAME_ORGANIZER_TYPE;
import static com.example.user.sportify.ui.utils.Constants.GAME_PARTICIPANT_TYPE;
import static com.example.user.sportify.ui.utils.Constants.PROGRESS_DIALOG_FRAGMENT;


class MyGamesPresenter extends MvpBasePresenter<MyGamesView> {
    private Context context;
    private MyGamesModel myGamesModel;
    private int currentPage = 1;
    private int lastPage = 0;
    private ProgressDialog progressDialog;

    private List<GamesParticipantData> gamesParticipantDataPres;
    private List<GameDataApi> participantGames;
    private List<Integer> gamesIdsParticipantArray;

    void onViewCreated(int myGamesType) {

        participantGames = new ArrayList<>();
        gamesIdsParticipantArray = new ArrayList<>();
        if (myGamesType == GAME_PARTICIPANT_TYPE) {
            participantType();
        } else if (myGamesType == GAME_ORGANIZER_TYPE) {
            organizerType();
        }

    }

    private void participantType() {
        new android.os.Handler().postDelayed(() -> {
            myGamesModel.getGamesParticipant(gamesParticipantData -> {
                if (gamesParticipantData != null) {
                    gamesParticipantDataPres = gamesParticipantData;

                    for (GamesParticipantData gamesParticipantDataList : gamesParticipantDataPres) {
                        gamesIdsParticipantArray.add(gamesParticipantDataList.getGameId());
                    }

                    ifViewAttached(view -> view.initGamesRecyclerView(new LinearLayoutManager(context), new GamesAdapter(context, (view1, position, game) -> {
                        if (game.getIsCanceled() != 1)
                            view.startNewActivity(new Intent(context, ConcretGameActivity.class).putExtra(EXTRA_GAME, game).putExtra(EXTRA_PARTICIPANT_TYPE, true));
                    }, myGamesModel.getSessionData().userId, gamesIdsParticipantArray,
                            //Connect game button clicked
                            (game, position, gameId, isInGame) -> {
                                view.showProgressBar(progressDialog = new ProgressDialog(), PROGRESS_DIALOG_FRAGMENT);
                                myGamesModel.unAttachUserFromGame(response -> {
                                    view.unParticipate(position);
                                    view.hideProgressBar(progressDialog);
                                }, myGamesModel.getSessionData().authToken, String.valueOf(gameId));
                            },
                            null, null)));

                    checkFirstPage();
                } else {
                    ifViewAttached(MyGamesView::setEmptyState);
                    ifViewAttached(MyGamesView::hideProgressBar);
                }
            }, myGamesModel.getSessionData().authToken);
        }, 500);
    }


    private void organizerType() {
        new android.os.Handler().postDelayed(() -> {
            ifViewAttached(view -> view.initGamesRecyclerView(new LinearLayoutManager(context), new GamesAdapter(context, (view1, position, game) -> {
                if (game.getIsCanceled() != 1)
                    view.startNewActivity(new Intent(context, ConcretGameActivity.class).putExtra(EXTRA_GAME, game).putExtra(EXTRA_PARTICIPANT_TYPE, false));
            }, myGamesModel.getSessionData().userId, gamesIdsParticipantArray,
                    null,
                    //Cancel game button clicked
                    (position, gameId) -> {
                        view.showProgressBar(progressDialog = new ProgressDialog(), PROGRESS_DIALOG_FRAGMENT);
                        myGamesModel.cancelGame(response -> {
                            view.cancelGame(position, gameId);
                            view.hideProgressBar(progressDialog);
                        }, myGamesModel.getSessionData().authToken, 1, String.valueOf(gameId));
                    },
                    //Change button clicked
                    (position, gameId, game) -> {
                        view.startNewActivity(new Intent(context, CreateGame.class).putExtra(EXTRA_GAME, game));
                    })));

            myGamesModel.getGamesOrganizer((gameData, pagesQuantity) -> ifViewAttached(view -> {
                if (gameData != null)
                    view.setGamesData(gameData);
                else view.setEmptyState();
                view.hideProgressBar();
            }), myGamesModel.getSessionData().authToken);
        }, 500);
    }

    private void checkFirstPage() {
        myGamesModel.getGamesPerPage((gameData, pagesQuantity) -> {
            lastPage = pagesQuantity;
            while (currentPage <= lastPage) {
                checkArray(currentPage);
                currentPage++;
            }
        }, 0, 1);
    }


    private void checkArray(int page) {
        myGamesModel.getGamesPerPage((gameData, pagesQuantity) -> {
            for (GameDataApi gameDataApi : gameData) {
                for (GamesParticipantData gamesParticipantData : gamesParticipantDataPres) {
                    if (gameDataApi.getId() == gamesParticipantData.getGameId()) {
                        participantGames.add(gameDataApi);
                    }
                }
            }
            ifViewAttached(MyGamesView::hideProgressBar);
            if (participantGames != null && participantGames.size() > 0) {
                ifViewAttached(view -> view.setGamesData(participantGames));
                participantGames.clear();
            }
        }, 0, page);

    }

    MyGamesPresenter(Context context, MyGamesModel myGamesModel) {
        this.context = context;
        this.myGamesModel = myGamesModel;
    }
}
