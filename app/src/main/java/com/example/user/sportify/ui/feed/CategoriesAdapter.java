package com.example.user.sportify.ui.feed;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.user.sportify.R;
import com.example.user.sportify.ui.feed.data.CategoryData;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {
	
	private final List<CategoryData> mCategories;
	private int selectedItem = 0;
	private final CategoriesRecyclerViewClickListener mCategoriesRecyclerViewClickListener;
	
	@NonNull
	@Override
	public CategoriesViewHolder onCreateViewHolder(
		@NonNull final ViewGroup viewGroup,
		final int i
	) {
		final Context context = viewGroup.getContext();
		final int layoutIdForListItem = R.layout.games_categories_item_layout;
		final LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
		
		return new CategoriesViewHolder(view, mCategoriesRecyclerViewClickListener);
	}
	
	@Override
	public void onBindViewHolder(
		@NonNull final CategoriesAdapter.CategoriesViewHolder categoriesViewHolder,
		final int position
	) {
		final CategoryData categoriesModel = mCategories.get(position);
		final TextView textView = categoriesViewHolder.mCategoryName;
		textView.setText(categoriesModel.getName());
		final ImageButton imageButton = categoriesViewHolder.mCategoryIcon;
		if (!(position == selectedItem)) {
			imageButton.setBackground(ResourcesCompat.getDrawable(
				categoriesViewHolder.itemView.getContext().getResources(),
				R.drawable.rounded_inactive_category_btn,
				null));
			imageButton.setColorFilter(Color.parseColor("#66BAFF"));
		} else {
			imageButton.setBackground(ResourcesCompat.getDrawable(
				categoriesViewHolder.itemView.getContext().getResources(),
				R.drawable.rounded_active_category,
				null));
			imageButton.setColorFilter(Color.WHITE);
		}
		
		ResourcesCompat.getDrawable(
			categoriesViewHolder.itemView.getContext().getResources(),
			categoriesModel.getIcon(),
			null);
		imageButton.setImageDrawable(ResourcesCompat.getDrawable(
			categoriesViewHolder.itemView.getContext().getResources(),
			categoriesModel.getIcon(),
			null));
	}
	
	void changeSelectedItem(final int position) {
		if (!(selectedItem == position)) {
			selectedItem = position;
			Log.e("pos", String.valueOf(position));
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getItemCount() {
		return mCategories.size();
	}
	
	
	static class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		
		private final CategoriesRecyclerViewClickListener mCategoriesRecyclerViewClickListener;
		
		private final TextView mCategoryName;
		private final ImageButton mCategoryIcon;
		
		
		private CategoriesViewHolder(
			final View holderItemView,
			final CategoriesRecyclerViewClickListener listener
		) {
			super(holderItemView);
			mCategoriesRecyclerViewClickListener = listener;
			holderItemView.setOnClickListener(this);
			mCategoryName = holderItemView.findViewById(R.id.category_name);
			mCategoryIcon = holderItemView.findViewById(R.id.category_icon);
			mCategoryIcon.setOnClickListener(this);
		}
		
		@Override
		public void onClick(final View view) {
			mCategoriesRecyclerViewClickListener.onClick(view, getAdapterPosition());
		}
	}
	
	CategoriesAdapter(
		final Context context,
		final List<CategoryData> categories,
		final CategoriesRecyclerViewClickListener listener
	) {
		this.mCategories = categories;
		this.mCategoriesRecyclerViewClickListener = listener;
	}
}
