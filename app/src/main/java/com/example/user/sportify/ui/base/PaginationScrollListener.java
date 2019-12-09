package com.example.user.sportify.ui.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
	
	private LinearLayoutManager layoutManager;
	
	protected PaginationScrollListener(final LinearLayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}
	
	@Override
	public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
		super.onScrolled(recyclerView, dx, dy);
		
		final int visibleItemCount = layoutManager.getChildCount();
		final int totalItemCount = layoutManager.getItemCount();
		final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
		
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