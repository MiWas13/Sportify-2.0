package com.example.user.sportify.ui.auth;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

public class AuthActivity extends MvpActivity<AuthView, AuthPresenter>{
    @NonNull
    @Override
    public AuthPresenter createPresenter() {
        return null;
    }
}
