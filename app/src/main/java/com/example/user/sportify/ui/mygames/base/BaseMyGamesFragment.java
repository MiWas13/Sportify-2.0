package com.example.user.sportify.ui.mygames.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.sportify.R;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class BaseMyGamesFragment extends MvpFragment<BaseMyGamesView, BaseMyGamesPresenter> implements BaseMyGamesView {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_games_layout, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onViewCreated();
    }

    @SuppressLint("ValidFragment")
    public BaseMyGamesFragment(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BaseMyGamesPresenter createPresenter() {
        return new BaseMyGamesPresenter();
    }

    @Override
    public void initToolbar() {
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void initViewPager() {
        BaseGamesPagerAdapter adapter = new BaseGamesPagerAdapter(context, getChildFragmentManager());
        viewPager.setAdapter(adapter);
    }
}
