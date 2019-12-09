package com.example.user.sportify.ui.mygames.decoration;


import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

public class MarginGamesItemDecoration extends RecyclerView.ItemDecoration {
	
	@Override
	public void getItemOffsets(
		@NonNull Rect outRect,
		@NonNull View view,
		@NonNull RecyclerView parent,
		@NonNull RecyclerView.State state
	) {
		super.getItemOffsets(outRect, view, parent, state);
		int position = parent.getChildAdapterPosition(view);
		
		if (position == 0) {
			outRect.set(0, 16, 0, 0);
		} else {
			super.getItemOffsets(outRect, view, parent, state);
		}
	}
}