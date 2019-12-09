package com.example.user.sportify.ui.feed.decoration;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.user.sportify.R;

import java.util.Objects;

public class MarginGamesItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);

        int padding = view.getResources().getDimensionPixelOffset(R.dimen.last_games_recycler_padding);
        if (position == Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1) {
            outRect.set(0, 0, 0, padding);
        } else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }
}
