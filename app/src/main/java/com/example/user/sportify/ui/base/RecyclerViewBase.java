package com.example.user.sportify.ui.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewBase<T extends RecyclerView.Adapter> extends RecyclerView {
	
	public RecyclerViewBase(@NonNull final Context context) {
		super(context);
	}
	
	public RecyclerView initRecyclerView(
		final RecyclerView baseRecyclerView,
		final RecyclerView.LayoutManager layoutManager,
		final T adapter,
		final Boolean hasFixedSize,
		@Nullable final RecyclerView.ItemDecoration itemDecoration
	) {
		baseRecyclerView.setLayoutManager(layoutManager);
		baseRecyclerView.setAdapter(adapter);
		baseRecyclerView.setHasFixedSize(hasFixedSize);
		if (itemDecoration != null) {
			baseRecyclerView.addItemDecoration(itemDecoration);
		}
		return baseRecyclerView;
	}
	
}
