package com.example.user.sportify.ui.feed.decoration;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.user.sportify.R;

import java.util.Objects;

public class MarginItemDecoration extends RecyclerView.ItemDecoration {
	
	@Override
	public void getItemOffsets(
		@NonNull final Rect outRect,
		@NonNull final View view,
		@NonNull final RecyclerView parent,
		@NonNull final RecyclerView.State state
	) {
		super.getItemOffsets(outRect, view, parent, state);
		final int position = parent.getChildAdapterPosition(view);
		
		if (position == Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1) {
			outRect.set(0, 0, 0, 0);
		} else {
			super.getItemOffsets(outRect, view, parent, state);
		}
		
		final int padding = view.getResources().getDimensionPixelOffset(R.dimen.first_categories_recycler_padding);
		
		if (position == 0) {
			//TODO
			outRect.set(padding, 0, 0, 0);
		} else {
			super.getItemOffsets(outRect, view, parent, state);
		}
	}
}
