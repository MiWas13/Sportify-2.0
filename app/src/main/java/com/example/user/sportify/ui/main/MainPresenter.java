package com.example.user.sportify.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.user.sportify.R;
import com.example.user.sportify.ui.feed.FeedFragment;
import com.example.user.sportify.ui.map.MapFragment;
import com.example.user.sportify.ui.mygames.base.BaseMyGamesFragment;
import com.example.user.sportify.ui.profile.ProfileFragment;
import com.example.user.sportify.ui.registration.full.RegistrationFragment;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

class MainPresenter extends MvpBasePresenter<MainView> {

    private Context context;
    private MainModel mainModel;

    void onViewCreated() {
        ifViewAttached(MainView::initFirstTab);
    }

    void onTabSelected(int tabPosition) {

        Fragment fragment = null;
        switch (tabPosition) {
            case R.id.navigation_games:
                fragment = new FeedFragment();
                break;

            case R.id.navigation_map:
                fragment = new MapFragment();
                break;

            case R.id.navigation_my_games:
                fragment = new BaseMyGamesFragment(context);
                break;

            case R.id.navigation_profile:
                if (!checkAuth())
                    fragment = new RegistrationFragment();
                else
                    fragment = new ProfileFragment();
                break;
        }
        Fragment finalFragment = fragment;
        ifViewAttached(view -> view.changeCurrentPage(tabPosition, finalFragment));
    }

    MainPresenter(Context context, MainModel mainModel) {
        this.context = context;
        this.mainModel = mainModel;
    }

    private Boolean checkAuth() {
        return mainModel.getSessionData().authToken != null;
    }
}
