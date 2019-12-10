package com.example.user.sportify.ui.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
	
	private final LinearLayoutManager mLayoutManager;
	
	protected PaginationScrollListener(final LinearLayoutManager layoutManager) {
		mLayoutManager = layoutManager;
	}
	
	@Override
	public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
		super.onScrolled(recyclerView, dx, dy);
		
		final int visibleItemCount = mLayoutManager.getChildCount();
		final int totalItemCount = mLayoutManager.getItemCount();
		final int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
		
		if (dy > 0) {
			if (!isLoading() && !isLastPage()) {
				if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
					&& firstVisibleItemPosition >= 0) {
					loadMoreItems();
				}
			}
		}
	}
	
	protected abstract void loadMoreItems();
	
	public abstract int getTotalPageCount();
	
	public abstract boolean isLastPage();
	
	public abstract boolean isLoading();
}