package com.example.user.sportify.ui.main;

import android.support.v4.app.Fragment;

import com.hannesdorfmann.mosby3.mvp.MvpView;

interface MainView extends MvpView {
    void initFirstTab();

    void changeCurrentPage(int tabPosition, Fragment fragment);
//    boolean loadFragment(Fragment fragment);
}
