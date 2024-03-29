package com.example.user.sportify.ui.feed.decoration;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.user.sportify.R;

import java.util.Objects;

public class MarginGamesItemDecoration extends RecyclerView.ItemDecoration {
	
	@Override
	public void getItemOffsets(
		@NonNull final Rect outRect,
		@NonNull final View view,
		@NonNull final RecyclerView parent,
		@NonNull final RecyclerView.State state
	) {
		super.getItemOffsets(outRect, view, parent, state);
		final int position = parent.getChildAdapterPosition(view);
		
		final int padding = view.getResources().getDimensionPixelOffset(R.dimen.last_games_recycler_padding);
		if (position == Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1) {
			outRect.set(0, 0, 0, padding);
		} else {
			super.getItemOffsets(outRect, view, parent, state);
		}
	}
}
