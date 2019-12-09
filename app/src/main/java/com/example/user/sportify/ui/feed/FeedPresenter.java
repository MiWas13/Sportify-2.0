package com.example.user.sportify.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GamesParticipantData;
import com.example.user.sportify.ui.concretgame.ConcretGameActivity;
import com.example.user.sportify.ui.creategame.CreateGame;
import com.example.user.sportify.ui.feed.data.CategoryData;
import com.example.user.sportify.ui.games.GamesAdapter;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.example.user.sportify.ui.registration.full.RegistrationModel;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.List;

import static com.example.user.sportify.ui.utils.Constants.AUTH_ERROR;
import static com.example.user.sportify.ui.utils.Constants.EXTRA_GAME;
import static com.example.user.sportify.ui.utils.Constants.EXTRA_ORGANIZER_TYPE;
import static com.example.user.sportify.ui.utils.Constants.EXTRA_PARTICIPANT_TYPE;
import static com.example.user.sportify.ui.utils.Constants.NAME_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PHONE_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PROGRESS_DIALOG_FRAGMENT;
import static com.example.user.sportify.ui.utils.Constants.REGISTRATION_ERROR;

public class FeedPresenter extends MvpBasePresenter<FeedView> {
    private Context context;
    private FeedModel feedModel;
    private RegistrationModel registrationModel;
    private List<Integer> gamesIdsParticipantArray;
    private ProgressDialog progressDialog;
    private int currentCategoryId = 0;
    private int pageCount = 1;
    private int currentPage = 1;

    private Boolean passwordIsValid = false;
    private Boolean nameIsValid = false;
    private Boolean phoneIsValid = false;
    private Boolean hasErrors = false;
    private String name;
    private String phone;
    private String password;
    private Boolean isPhoneChecked = false;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    void onViewCreated() {
        feedModel.getGamesParticipant(gamesParticipantData -> {
            if (gamesParticipantData != null) {
                gamesIdsParticipantArray = makeGamesIdsParticipantArray(gamesParticipantData);
            } else {
                gamesIdsParticipantArray = makeGamesIdsParticipantArray(new ArrayList<>());
            }
            Log.e("пипжвьвжал", "я колбэкнулся");
            initViewElements();
        }, feedModel.getSessionData().authToken);
    }

    private void initViewElements() {

        ifViewAttached(view -> {
            view.initCategories(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false), new CategoriesAdapter(context, initCategories(), (view12, position) -> {
                view.changeSelectedCategory(position);
                view.clearGames();
                currentPage = 1;
                currentCategoryId = position;
                view.showProgressBar();
                if (position == 0) {
                    isLoading = false;
                    isLastPage = false;
                    onGamesInited();
                } else {
                    currentPage = 1;
                    new Handler().postDelayed(() -> {
                        feedModel.getGamesPerPage((gameData, pagesQuantity) -> {
                            pageCount = pagesQuantity;
                            if (gameData != null) {
                                view.filterByCategory(gameData, position);
                            } else {
                                view.clearGames();
                                view.hideProgressBar();
                            }
                            if (currentPage <= pagesQuantity)
                                ifViewAttached(FeedView::addLoading);
                            else isLastPage = true;
                        }, position, currentPage);
                        currentPage++;
                    }, 500);

                }
            }));


            view.initGames(new LinearLayoutManager(context), new GamesAdapter(context, (view1, position, game) -> {
                Boolean isParticipant = gamesIdsParticipantArray.contains(game.getId());
                Boolean isOrganizer;
                isOrganizer = game.getCreatorId() == Integer.valueOf(feedModel.getSessionData().userId);
                if (game.getIsCanceled() != 1)
                    view.startNewConcretGameActivity(new Intent(context, ConcretGameActivity.class).putExtra(EXTRA_GAME, game).putExtra(EXTRA_PARTICIPANT_TYPE, isParticipant).putExtra(EXTRA_ORGANIZER_TYPE, isOrganizer));
            }, feedModel.getSessionData().userId, gamesIdsParticipantArray,
                    //Click connect
                    (game, position, gameId, isInGame) -> {

                        if (feedModel.getSessionData().authToken == null) {
                            ifViewAttached(FeedView::showFirstRegDialog);
                        } else {

                            if (Integer.valueOf(game.getCurrentPeopleQuantity()) < Integer.valueOf(game.getMaxPeopleQuantity())) {
                                if (isInGame) {
                                    feedModel.unAttachUserFromGame(response -> {
                                        if (response)
                                            view.changeParticipantState(position, gameId, String.valueOf(Integer.valueOf(game.getCurrentPeopleQuantity()) - 1));
                                    }, feedModel.getSessionData().authToken, String.valueOf(gameId));
                                } else {
                                    feedModel.attachUserToGame(response -> {
                                        if (response)
                                            view.changeParticipantState(position, gameId, String.valueOf(Integer.valueOf(game.getCurrentPeopleQuantity()) + 1));
                                    }, feedModel.getSessionData().authToken, String.valueOf(gameId));
                                }
                            } else {
                                view.showMaxQuantitySnackBar();
                            }

                        }
                    }, (position, gameId) -> {
                view.showProgressBar(progressDialog = new ProgressDialog(), PROGRESS_DIALOG_FRAGMENT);
                feedModel.cancelGame(response -> {
                    view.cancelGame(position, gameId);
                    view.hideProgressBar(progressDialog);
                }, feedModel.getSessionData().authToken, 1, String.valueOf(gameId));
            }, (position, gameId, game) -> view.startNewCreateGameActivity(new Intent(context, CreateGame.class).putExtra(EXTRA_GAME, game))));
        });

    }

    FeedPresenter(Context context, FeedModel feedModel, RegistrationModel registrationModel) {
        this.context = context;
        this.feedModel = feedModel;
        this.registrationModel = registrationModel;
    }

    void onFabClicked() {
        if (feedModel.getSessionData().authToken == null) {
            ifViewAttached(FeedView::showFirstRegDialog);
        } else {
            ifViewAttached(view -> view.startNewCreateGameActivity(new Intent(context, CreateGame.class)));
        }
    }

    void onGamesInited() {

        new Handler().postDelayed(() -> {
            feedModel.getGamesPerPage((gameData, pagesQuantity) -> {
                pageCount = pagesQuantity;
                ifViewAttached(FeedView::initGamesScrollListener);
                ifViewAttached(view -> view.loadFirstPage(gameData));
                if (currentPage <= pagesQuantity) ifViewAttached(FeedView::addLoading);
                else isLastPage = true;
            }, currentCategoryId, currentPage);
            currentPage++;
        }, 500);
    }

    void onEndOfPageScrolled() {
        isLoading = true;
        new Handler().postDelayed(() -> {
            feedModel.getGamesPerPage((gameData, pagesQuantity) -> {
                ifViewAttached(FeedView::hideLoading);
                isLoading = false;
                ifViewAttached(view -> view.loadNextPage(gameData));
                if (currentPage != pagesQuantity) ifViewAttached(FeedView::addLoading);
                else isLastPage = true;
                currentPage++;
            }, currentCategoryId, currentPage);

        }, 1500);


    }

    public int getTotalCount() {
        return pageCount;
    }

    public boolean getIsLastPage() {
        return isLastPage;
    }

    public boolean getIsLoadind() {
        return isLoading;
    }

    private ArrayList<CategoryData> initCategories() {
        ArrayList<CategoryData> dataCategory = new ArrayList<>();
        dataCategory.add(new CategoryData(0, R.drawable.ic_all, "Все"));
        dataCategory.add(new CategoryData(1, R.drawable.ic_basketball, "Баскетбол"));
        dataCategory.add(new CategoryData(2, R.drawable.ic_football, "Футбол"));
        dataCategory.add(new CategoryData(3, R.drawable.ic_tennis, "Теннис"));
        dataCategory.add(new CategoryData(4, R.drawable.ic_chess, "Шахматы"));
        dataCategory.add(new CategoryData(5, R.drawable.ic_running, "Бег"));
        dataCategory.add(new CategoryData(6, R.drawable.ic_pingpong, "Пинг-Понг"));
        return dataCategory;
    }

    private List<Integer> makeGamesIdsParticipantArray(List<GamesParticipantData> gamesParticipantData) {

        List<Integer> gamesIdsParticipantArray = new ArrayList<>();

        for (GamesParticipantData game : gamesParticipantData) {
            gamesIdsParticipantArray.add(game.getGameId());
        }


        return gamesIdsParticipantArray;
    }

    public void fieldChanged(String inputText, int fieldType) {
        switch (fieldType) {
            case PASSWORD_EDIT_TEXT:
                passwordIsValid = inputText.length() >= 6;
                password = inputText;
                break;
            case NAME_EDIT_TEXT:
                nameIsValid = inputText.length() > 0;
                name = inputText;
                break;
            case PHONE_EDIT_TEXT:
                phoneIsValid = inputText.length() >= 18;
                isPhoneChecked = false;
                phone = inputText;
                break;
        }
    }

    private void onSuccess(String token) {
        registrationModel.getProfileInfo(profileData -> {
            registrationModel.saveSessionData(profileData.getToken(), profileData.getPhone(), profileData.getPassword(), profileData.getId(), profileData.getName());
            ifViewAttached(view -> view.hideProgressBar(progressDialog));
            ifViewAttached(view -> view.secondStepComplete(new Intent(context, CreateGame.class)));
        }, token);
    }

    public void onFirstReadyButtonClicked() {
        hasErrors = false;
        if (!nameIsValid) {
            hasErrors = true;
            ifViewAttached(FeedView::setNameError);
        }
        if (!phoneIsValid) {
            hasErrors = true;
            ifViewAttached(FeedView::setPhoneError);
        }

        if (!hasErrors) {
            ifViewAttached(FeedView::firstStepComplete);
        }

    }

    public void onSecondReadyButtonClicked() {

        if (!passwordIsValid) {
            hasErrors = true;
            ifViewAttached(FeedView::setPasswordError);
        }

        if (!hasErrors && checkPhone()) {
            registrationModel.signUp(token -> {
                ifViewAttached(view -> view.showProgressBar(progressDialog = new ProgressDialog(), PROGRESS_DIALOG_FRAGMENT));
                if (token.equals(REGISTRATION_ERROR)) {
                    ifViewAttached(view -> registrationModel.signIn(authToken -> {
                        if (authToken.equals(AUTH_ERROR)) {
                            view.hideProgressBar(progressDialog);
                            view.showAuthError("Неверный пароль :(");
                        } else {
                            onSuccess(authToken);
                        }
                    }, phone, password));
                } else {
                    onSuccess(token);
                }
            }, name, "1", phone, password);
        }
    }


    private Boolean checkPhone() {
        if (isPhoneChecked) {
            return true;
        } else {
            StringBuilder sb = new StringBuilder(phone);
            sb.delete(0, 4);
            sb.delete(3, 5);
            sb.deleteCharAt(sb.lastIndexOf("-"));
            sb.deleteCharAt(sb.lastIndexOf("-"));
            if (!(sb.length() == 10))
                sb.deleteCharAt(10);
            phone = sb.toString();
            isPhoneChecked = true;
            return true;
        }
    }
}
