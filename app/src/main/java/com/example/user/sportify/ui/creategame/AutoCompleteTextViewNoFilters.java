package com.example.user.sportify.ui.creategame;

import android.content.Context;
import android.util.AttributeSet;

public class AutoCompleteTextViewNoFilters extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
	
	public AutoCompleteTextViewNoFilters(final Context context) {
		super(context);
	}
	
	public AutoCompleteTextViewNoFilters(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public AutoCompleteTextViewNoFilters(
		final Context context,
		final AttributeSet attrs,
		final int defStyle
	) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void performFiltering(final CharSequence text, final int keyCode) {
		final String filterText = "";
		super.performFiltering(filterText, keyCode);
	}
}