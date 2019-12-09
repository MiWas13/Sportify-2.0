package com.example.user.sportify.ui.mygames.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.user.sportify.R;
import com.example.user.sportify.ui.mygames.MyGamesFragment;
import static com.example.user.sportify.ui.utils.Constants.EXTRA_MY_GAMES_TYPE;
import static com.example.user.sportify.ui.utils.Constants.GAME_ORGANIZER_TYPE;
import static com.example.user.sportify.ui.utils.Constants.GAME_PARTICIPANT_TYPE;

public class BaseGamesPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    BaseGamesPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {

        Bundle bundle = new Bundle();
        Fragment fragment;
        if (i == 0) {
            bundle.putInt(EXTRA_MY_GAMES_TYPE, GAME_PARTICIPANT_TYPE);
        } else {
            bundle.putInt(EXTRA_MY_GAMES_TYPE, GAME_ORGANIZER_TYPE);
        }
        fragment = new MyGamesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return context.getString(R.string.game_participant_tab);
            case 1:
                return context.getString(R.string.game_organizer_tab);
            default:
                return null;
        }
    }
}
