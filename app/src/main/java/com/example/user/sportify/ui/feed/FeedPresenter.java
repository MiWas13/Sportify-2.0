package com.example.user.sportify.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;

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
	
	private final Context mContext;
	private final FeedModel mFeedModel;
	private final RegistrationModel mRegistrationModel;
	private List<Integer> mGamesIdsParticipantArray;
	private ProgressDialog mProgressDialog;
	private int mCurrentCategoryId = 0;
	private int mPageCount = 1;
	private int mCurrentPage = 1;
	
	private Boolean mPasswordIsValid = false;
	private Boolean mNameIsValid = false;
	private Boolean mPhoneIsValid = false;
	private Boolean mHasErrors = false;
	private String mName;
	private String mPhone;
	private String mPassword;
	private Boolean mIsPhoneChecked = false;
	
	private boolean mIsLoading = false;
	private boolean mIsLastPage = false;
	
	void onViewCreated() {
		FeedModel.getGamesParticipant(gamesParticipantData -> {
			if (gamesParticipantData != null) {
				mGamesIdsParticipantArray = makeGamesIdsParticipantArray(gamesParticipantData);
			} else {
				mGamesIdsParticipantArray = makeGamesIdsParticipantArray(new ArrayList<>());
			}
			Log.e("пипжвьвжал", "я колбэкнулся");
			initViewElements();
		}, mFeedModel.getSessionData().authToken);
	}
	
	private void initViewElements() {
		
		ifViewAttached(view -> {
			view.initCategories(new LinearLayoutManager(
				mContext,
				LinearLayoutManager.HORIZONTAL,
				false), new CategoriesAdapter(mContext, initCategories(), (view12, position) -> {
				view.changeSelectedCategory(position);
				view.clearGames();
				mCurrentPage = 1;
				mCurrentCategoryId = position;
				view.showProgressBar();
				if (position == 0) {
					mIsLoading = false;
					mIsLastPage = false;
					onGamesInited();
				} else {
					mCurrentPage = 1;
					new Handler().postDelayed(() -> {
						FeedModel.getGamesPerPage((gameData, pagesQuantity) -> {
							mPageCount = pagesQuantity;
							if (gameData != null) {
								view.filterByCategory(gameData, position);
							} else {
								view.clearGames();
								view.hideProgressBar();
							}
							if (mCurrentPage <= pagesQuantity) {
								ifViewAttached(FeedView::addLoading);
							} else {
								mIsLastPage = true;
							}
						}, position, mCurrentPage);
						mCurrentPage++;
					}, 500);
					
				}
			}));
			
			
			view.initGames(new LinearLayoutManager(mContext), new GamesAdapter(
				mContext,
				(view1, position, game) -> {
					final Boolean isParticipant = mGamesIdsParticipantArray.contains(game.getId());
					final Boolean isOrganizer = game.getCreatorId() == Integer.valueOf(mFeedModel.getSessionData().userId);
					if (game.getIsCanceled() != 1) {
						view.startNewConcretGameActivity(new Intent(
							mContext,
							ConcretGameActivity.class).putExtra(EXTRA_GAME, game).putExtra(
							EXTRA_PARTICIPANT_TYPE,
							isParticipant).putExtra(EXTRA_ORGANIZER_TYPE, isOrganizer));
					}
				},
				mFeedModel.getSessionData().userId,
				mGamesIdsParticipantArray,
				//Click connect
				(game, position, gameId, isInGame) -> {
					
					if (mFeedModel.getSessionData().authToken == null) {
						ifViewAttached(FeedView::showFirstRegDialog);
					} else {
						
						if (Integer.valueOf(game.getCurrentPeopleQuantity()) < Integer.valueOf(game.getMaxPeopleQuantity())) {
							if (isInGame) {
								mFeedModel.unAttachUserFromGame(response -> {
									if (response) {
										view.changeParticipantState(
											position,
											gameId,
											String.valueOf(Integer.valueOf(game.getCurrentPeopleQuantity()) - 1));
									}
								}, mFeedModel.getSessionData().authToken, String.valueOf(gameId));
							} else {
								FeedModel.attachUserToGame(response -> {
									if (response) {
										view.changeParticipantState(
											position,
											gameId,
											String.valueOf(Integer.valueOf(game.getCurrentPeopleQuantity()) + 1));
									}
								}, mFeedModel.getSessionData().authToken, String.valueOf(gameId));
							}
						} else {
							view.showMaxQuantitySnackBar();
						}
						
					}
				},
				(position, gameId) -> {
					view.showProgressBar(
						mProgressDialog = new ProgressDialog(),
						PROGRESS_DIALOG_FRAGMENT);
					mFeedModel.cancelGame(response -> {
						view.cancelGame(position, gameId);
						view.hideProgressBar(mProgressDialog);
					}, mFeedModel.getSessionData().authToken, 1, String.valueOf(gameId));
				},
				(position, gameId, game) -> view.startNewCreateGameActivity(new Intent(
					mContext,
					CreateGame.class).putExtra(EXTRA_GAME, game))));
		});
		
	}
	
	FeedPresenter(final Context context, final FeedModel feedModel, final RegistrationModel registrationModel) {
		this.mContext = context;
		this.mFeedModel = feedModel;
		this.mRegistrationModel = registrationModel;
	}
	
	void onFabClicked() {
		if (mFeedModel.getSessionData().authToken == null) {
			ifViewAttached(FeedView::showFirstRegDialog);
		} else {
			ifViewAttached(view -> view.startNewCreateGameActivity(new Intent(
				mContext,
				CreateGame.class)));
		}
	}
	
	void onGamesInited() {
		
		new Handler().postDelayed(() -> {
			FeedModel.getGamesPerPage((gameData, pagesQuantity) -> {
				mPageCount = pagesQuantity;
				ifViewAttached(FeedView::initGamesScrollListener);
				ifViewAttached(view -> view.loadFirstPage(gameData));
				if (mCurrentPage <= pagesQuantity) {
					ifViewAttached(FeedView::addLoading);
				} else {
					mIsLastPage = true;
				}
			}, mCurrentCategoryId, mCurrentPage);
			mCurrentPage++;
		}, 500);
	}
	
	void onEndOfPageScrolled() {
		mIsLoading = true;
		new Handler().postDelayed(() -> {
			FeedModel.getGamesPerPage((gameData, pagesQuantity) -> {
				ifViewAttached(FeedView::hideLoading);
				mIsLoading = false;
				ifViewAttached(view -> view.loadNextPage(gameData));
				if (mCurrentPage != pagesQuantity) {
					ifViewAttached(FeedView::addLoading);
				} else {
					mIsLastPage = true;
				}
				mCurrentPage++;
			}, mCurrentCategoryId, mCurrentPage);
			
		}, 1500);
		
		
	}
	
	public int getTotalCount() {
		return mPageCount;
	}
	
	public boolean getIsLastPage() {
		return mIsLastPage;
	}
	
	public boolean getIsLoadind() {
		return mIsLoading;
	}
	
	private static ArrayList<CategoryData> initCategories() {
		final ArrayList<CategoryData> dataCategory = new ArrayList<>();
		dataCategory.add(new CategoryData(0, R.drawable.ic_all, "Все"));
		dataCategory.add(new CategoryData(1, R.drawable.ic_basketball, "Баскетбол"));
		dataCategory.add(new CategoryData(2, R.drawable.ic_football, "Футбол"));
		dataCategory.add(new CategoryData(3, R.drawable.ic_tennis, "Теннис"));
		dataCategory.add(new CategoryData(4, R.drawable.ic_chess, "Шахматы"));
		dataCategory.add(new CategoryData(5, R.drawable.ic_running, "Бег"));
		dataCategory.add(new CategoryData(6, R.drawable.ic_pingpong, "Пинг-Понг"));
		return dataCategory;
	}
	
	private static List<Integer> makeGamesIdsParticipantArray(final List<GamesParticipantData> gamesParticipantData) {
		
		final List<Integer> gamesIdsParticipantArray = new ArrayList<>();
		
		for (final GamesParticipantData game : gamesParticipantData) {
			gamesIdsParticipantArray.add(game.getGameId());
		}
		
		
		return gamesIdsParticipantArray;
	}
	
	public void fieldChanged(final String inputText, final int fieldType) {
		switch (fieldType) {
			case PASSWORD_EDIT_TEXT:
				mPasswordIsValid = inputText.length() >= 6;
				mPassword = inputText;
				break;
			case NAME_EDIT_TEXT:
				mNameIsValid = inputText.length() > 0;
				mName = inputText;
				break;
			case PHONE_EDIT_TEXT:
				mPhoneIsValid = inputText.length() >= 18;
				mIsPhoneChecked = false;
				mPhone = inputText;
				break;
		}
	}
	
	private void onSuccess(final String token) {
		RegistrationModel.getProfileInfo(profileData -> {
			mRegistrationModel.saveSessionData(
				profileData.getToken(),
				profileData.getPhone(),
				profileData.getPassword(),
				profileData.getId(),
				profileData.getName());
			ifViewAttached(view -> view.hideProgressBar(mProgressDialog));
			ifViewAttached(view -> view.secondStepComplete(new Intent(mContext, CreateGame.class)));
		}, token);
	}
	
	public void onFirstReadyButtonClicked() {
		mHasErrors = false;
		if (!mNameIsValid) {
			mHasErrors = true;
			ifViewAttached(FeedView::setNameError);
		}
		if (!mPhoneIsValid) {
			mHasErrors = true;
			ifViewAttached(FeedView::setPhoneError);
		}
		
		if (!mHasErrors) {
			ifViewAttached(FeedView::firstStepComplete);
		}
		
	}
	
	public void onSecondReadyButtonClicked() {
		
		if (!mPasswordIsValid) {
			mHasErrors = true;
			ifViewAttached(FeedView::setPasswordError);
		}
		
		if (!mHasErrors && checkPhone()) {
			RegistrationModel.signUp(token -> {
				ifViewAttached(view -> view.showProgressBar(
					mProgressDialog = new ProgressDialog(),
					PROGRESS_DIALOG_FRAGMENT));
				if (token.equals(REGISTRATION_ERROR)) {
					ifViewAttached(view -> RegistrationModel.signIn(authToken -> {
						if (authToken.equals(AUTH_ERROR)) {
							view.hideProgressBar(mProgressDialog);
							view.showAuthError("Неверный пароль :(");
						} else {
							onSuccess(authToken);
						}
					}, mPhone, mPassword));
				} else {
					onSuccess(token);
				}
			}, mName, "1", mPhone, mPassword);
		}
	}
	
	
	private Boolean checkPhone() {
		if (!mIsPhoneChecked) {
			final StringBuilder sb = new StringBuilder(mPhone);
			sb.delete(0, 4);
			sb.delete(3, 5);
			sb.deleteCharAt(sb.lastIndexOf("-"));
			sb.deleteCharAt(sb.lastIndexOf("-"));
			if (!(sb.length() == 10)) {
				sb.deleteCharAt(10);
			}
			mPhone = sb.toString();
			mIsPhoneChecked = true;
		}
		return true;
	}
}
