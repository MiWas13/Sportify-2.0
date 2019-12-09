package com.example.user.sportify.ui.concretgame;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.models.GamesParticipantData;
import com.example.user.sportify.network.models.UserParticipantData;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.sportify.ui.utils.Constants.BASE_UPLOADS_URL;
import static com.example.user.sportify.ui.utils.Constants.PROGRESS_DIALOG_FRAGMENT;

class ConcretGamePresenter extends MvpBasePresenter<ConcretGameView> {

    private Context context;
    private ConcretGameModel concretGameModel;
    private Boolean isParticipant;
    private int gameId;
    private ProgressDialog progressDialog;
    private List<UserParticipantData> users;

    void onViewCreated(GameDataApi game, Boolean isParticipant, Boolean isOrganizer) {
        this.isParticipant = isParticipant;
        this.gameId = game.getId();
        ifViewAttached(view -> view.setDescription(game.getDescription()));
        if (isOrganizer) {
            users = new ArrayList<>();
            ifViewAttached(view -> view.initParticipantsPhones(new LinearLayoutManager(context), new OrganizerConcretGameParticipantsAdapter(context, users, phone -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+7" + phone));
                view.startNewActivity(intent);
            })));
            addParticipants(String.valueOf(gameId));
        } else {
            if (isParticipant) {
                ifViewAttached(ConcretGameView::setInGameState);
            } else {
                ifViewAttached(ConcretGameView::setNotInGameState);
            }
        }


        ifViewAttached(view -> view.initToolbar(getCategoryName(game.getCategoryId())));
        ifViewAttached(view -> view.initGameDescription(new LinearLayoutManager(context), new ConcretGameDescriptionAdapter(context, game, (view1, position) -> Log.e("regregre", "меня нажали"),
                phone -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:+7" + phone));
                    view.startNewActivity(intent);
                },

                coordinates -> {
                    Intent mapIntent;
                    try {
                        mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("yandexmaps://maps.yandex.ru/?pt=" + modifyCoordinates(coordinates) + "&z=12&l=map"));
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

                }, isOrganizer)));
        ifViewAttached(view -> view.setPhoto(BASE_UPLOADS_URL + game.getLocationPhotoUrl()));
    }

    void onConnectButtonClicked() {
        ifViewAttached(view -> view.showProgressBar(progressDialog = new ProgressDialog(), PROGRESS_DIALOG_FRAGMENT));
        if (isParticipant) {
            concretGameModel.unAttachUserFromGame(response -> {
                ifViewAttached(ConcretGameView::setNotInGameState);
                ifViewAttached(view -> view.hideProgressBar(progressDialog));
            }, concretGameModel.getSessionData().authToken, String.valueOf(gameId));
            isParticipant = false;
        } else {
            concretGameModel.attachUserToGame(response -> {
                ifViewAttached(ConcretGameView::setInGameState);
                ifViewAttached(view -> view.hideProgressBar(progressDialog));
            }, concretGameModel.getSessionData().authToken, String.valueOf(gameId));
            isParticipant = true;
        }
    }

    @NonNull
    private String modifyCoordinates(String coordinates) {
        StringBuilder sb = new StringBuilder(coordinates);
        sb.insert(sb.lastIndexOf(" "), ",");
        sb.deleteCharAt(sb.lastIndexOf(" "));
        return sb.toString().trim();
    }

    ConcretGamePresenter(Context context, ConcretGameModel concretGameModel) {
        this.context = context;
        this.concretGameModel = concretGameModel;
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

    private void addParticipants(String gameId) {
        concretGameModel.getGameParticipants(gamesParticipantData -> {
            for (GamesParticipantData gamesParticipant : gamesParticipantData) {
                concretGameModel.getUserPhone(user -> ifViewAttached(view -> view.addParticipant(user)), gamesParticipant.getUserId());
            }
        }, gameId);
    }

}
