package com.example.user.sportify.ui.creategame;

import android.content.Context;
import android.util.AttributeSet;

public class AutoCompleteTextViewNoFilters extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    public AutoCompleteTextViewNoFilters(Context context) {
        super(context);
    }

    public AutoCompleteTextViewNoFilters(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public AutoCompleteTextViewNoFilters(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }
}