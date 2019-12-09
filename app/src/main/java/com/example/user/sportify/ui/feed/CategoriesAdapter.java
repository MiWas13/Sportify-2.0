package com.example.user.sportify.ui.feed;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.user.sportify.R;
import com.example.user.sportify.ui.feed.data.CategoryData;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    private List<CategoryData> categories;
    private int selectedItem = 0;
    private CategoriesRecyclerViewClickListener listener;


    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.games_categories_item_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new CategoriesViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.CategoriesViewHolder categoriesViewHolder, int position) {
        CategoryData categoriesModel = categories.get(position);
        TextView textView = categoriesViewHolder.categoryName;
        textView.setText(categoriesModel.getName());
        ImageButton imageButton = categoriesViewHolder.categoryIcon;
        if (!(position == selectedItem)) {
            imageButton.setBackground(ResourcesCompat.getDrawable(categoriesViewHolder.itemView.getContext().getResources(), R.drawable.rounded_inactive_category_btn, null));
            imageButton.setColorFilter(Color.parseColor("#66BAFF"));
        } else {
            imageButton.setBackground(ResourcesCompat.getDrawable(categoriesViewHolder.itemView.getContext().getResources(), R.drawable.rounded_active_category, null));
            imageButton.setColorFilter(Color.WHITE);
        }

        ResourcesCompat.getDrawable(categoriesViewHolder.itemView.getContext().getResources(), categoriesModel.getIcon(), null);
        imageButton.setImageDrawable(ResourcesCompat.getDrawable(categoriesViewHolder.itemView.getContext().getResources(), categoriesModel.getIcon(), null));
    }

    void changeSelectedItem(int position) {
        if (!(selectedItem == position)) {
            selectedItem = position;
            Log.e("pos", String.valueOf(position));
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CategoriesRecyclerViewClickListener listener;

        TextView categoryName;
        ImageButton categoryIcon;


        CategoriesViewHolder(View itemView, CategoriesRecyclerViewClickListener listener) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    CategoriesAdapter(Context context, ArrayList<CategoryData> categories, CategoriesRecyclerViewClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }
}
