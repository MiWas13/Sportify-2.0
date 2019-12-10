package com.example.user.sportify.ui.main;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.user.sportify.R;
import com.example.user.sportify.ui.feed.FeedFragment;
import com.example.user.sportify.ui.map.MapFragment;
import com.example.user.sportify.ui.mygames.base.BaseMyGamesFragment;
import com.example.user.sportify.ui.profile.ProfileFragment;
import com.example.user.sportify.ui.registration.full.RegistrationFragment;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

class MainPresenter extends MvpBasePresenter<MainView> {
	
	private final Context mContext;
	private final MainModel mMainModel;
	
	void onViewCreated() {
		ifViewAttached(MainView::initFirstTab);
	}
	
	void onTabSelected(final int tabPosition) {
		
		Fragment fragment = null;
		switch (tabPosition) {
			case R.id.navigation_games:
				fragment = new FeedFragment();
				break;
			
			case R.id.navigation_map:
				fragment = new MapFragment();
				break;
			
			case R.id.navigation_my_games:
				fragment = new BaseMyGamesFragment(mContext);
				break;
			
			case R.id.navigation_profile:
				if (!checkAuth()) {
					fragment = new RegistrationFragment();
				} else {
					fragment = new ProfileFragment();
				}
				break;
		}
		final Fragment finalFragment = fragment;
		ifViewAttached(view -> view.changeCurrentPage(tabPosition, finalFragment));
	}
	
	MainPresenter(final Context context, final MainModel mainModel) {
		mContext = context;
		mMainModel = mainModel;
	}
	
	private Boolean checkAuth() {
		return mMainModel.getSessionData().authToken != null;
	}
}
