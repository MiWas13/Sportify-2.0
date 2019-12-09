package com.example.user.sportify.ui.main;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.user.sportify.R;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.feed.FeedFragment;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.main_frame_layout)
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(new MainActivityTabSelectionListener(getPresenter(), bottomNavigationView));
        getPresenter().onViewCreated();
    }


    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(this, new MainModel(DaggerSessionComponent.builder().contextModule(new ContextModule(this)).build()));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void initFirstTab() {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new FeedFragment()).commit();
    }

    @Override
    public void changeCurrentPage(int tabPosition, Fragment fragment) {
        assert fragment != null;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right).replace(R.id.main_frame_layout, fragment).commit();
        bottomNavigationView.getMenu().findItem(tabPosition).setChecked(true);
    }

}

class MainActivityTabSelectionListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    private MainPresenter mainPresenter;
    private BottomNavigationView bottomNavigationView;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mainPresenter.onTabSelected(bottomNavigationView.getMenu().findItem(menuItem.getItemId()).getItemId());
        return false;
    }

    MainActivityTabSelectionListener(MainPresenter mainPresenter, BottomNavigationView bottomNavigationView) {
        this.mainPresenter = mainPresenter;
        this.bottomNavigationView = bottomNavigationView;
    }

}


