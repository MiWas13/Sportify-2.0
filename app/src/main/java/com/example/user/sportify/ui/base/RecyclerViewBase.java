package com.example.user.sportify.ui.base;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewBase<T extends RecyclerView.Adapter> extends RecyclerView {

    public RecyclerViewBase(@NonNull Context context) {
        super(context);
    }

    public RecyclerView initRecyclerView(RecyclerView baseRecyclerView, RecyclerView.LayoutManager layoutManager, T adapter, Boolean hasFixedSize, @Nullable RecyclerView.ItemDecoration itemDecoration) {
        baseRecyclerView.setLayoutManager(layoutManager);
        baseRecyclerView.setAdapter(adapter);
        baseRecyclerView.setHasFixedSize(hasFixedSize);
        if (itemDecoration != null)
            baseRecyclerView.addItemDecoration(itemDecoration);
        return baseRecyclerView;
    }

}
