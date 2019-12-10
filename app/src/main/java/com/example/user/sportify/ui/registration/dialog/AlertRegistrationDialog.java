package com.example.user.sportify.ui.registration.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;

import com.google.android.material.textfield.TextInputLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.sportify.R;
import com.example.user.sportify.ui.feed.FeedPresenter;


import butterknife.ButterKnife;

import static com.example.user.sportify.ui.utils.Constants.NAME_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PHONE_EDIT_TEXT;

@SuppressLint("ValidFragment")
public class AlertRegistrationDialog {
	
	private final Context mContext;
	private AlertDialog mFirstDialog;
	private AlertDialog mSecondDialog;
	private final LayoutInflater mInflater;
	private final AlertDialog.Builder mBuilder;
	private TextInputLayout mPopRegPhoneInput;
	private TextInputLayout mPopRegPasswordInput;
	private TextInputLayout mPopRegNameInput;
	private final FeedPresenter mFeedPresenter;
	
	
	public AlertRegistrationDialog(final Context context, final FeedPresenter feedPresenter) {
		this.mContext = context;
		this.mFeedPresenter = feedPresenter;
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mBuilder = new AlertDialog.Builder(context);
	}
	
	public void showFirstDialog() {
		
		assert mInflater != null;
		final View view = mInflater.inflate(R.layout.authorization_first_layout, null);
		final Button firstReadyButton = view.findViewById(R.id.first_ready_button);
		
		final EditText phone = view.findViewById(R.id.pop_reg_phone);
		final EditText name = view.findViewById(R.id.pop_reg_name);
		phone.addTextChangedListener(new CustomDialogTextWatcher(mFeedPresenter, PHONE_EDIT_TEXT));
		name.addTextChangedListener(new CustomDialogTextWatcher(mFeedPresenter, NAME_EDIT_TEXT));
		
		mPopRegPhoneInput = view.findViewById(R.id.pop_reg_phone_input);
		mPopRegNameInput = view.findViewById(R.id.pop_reg_name_input);
		
		ButterKnife.bind(this, view);
		firstReadyButton.setOnClickListener(new FirstReadyButtonClickListener(mFeedPresenter));
		mBuilder.setView(view);
		mFirstDialog = mBuilder.create();
		mFirstDialog.show();
	}
	
	public void finishFirstDialog() {
		mFirstDialog.dismiss();
	}
	
	
	public void showSecondDialog() {
		assert mInflater != null;
		final View view = mInflater.inflate(R.layout.authorization_second_layout, null);
		mPopRegPasswordInput = view.findViewById(R.id.pop_reg_password_input);
		final EditText password = view.findViewById(R.id.pop_reg_password);
		password.addTextChangedListener(new CustomDialogTextWatcher(
			mFeedPresenter,
			PASSWORD_EDIT_TEXT));
		
		final Button secondReadyButton = view.findViewById(R.id.second_ready_button);
		secondReadyButton.setOnClickListener(new SecondReadyButtonClickListener(mFeedPresenter));
		mBuilder.setView(view);
		mSecondDialog = mBuilder.create();
		mSecondDialog.show();
	}
	
	public void finishSecondDialog() {
		mSecondDialog.dismiss();
	}
	
	
	public void setPasswordError() {
		mPopRegPasswordInput.setErrorEnabled(true);
		mPopRegPasswordInput.setError(mContext.getResources().getString(R.string.password_validation_error));
	}
	
	public void setNameError() {
		mPopRegNameInput.setErrorEnabled(true);
		mPopRegNameInput.setError(mContext.getResources().getString(R.string.name_validation_error));
		
	}
	
	public void setPhoneError() {
		mPopRegPhoneInput.setErrorEnabled(true);
		mPopRegPhoneInput.setError(mContext.getResources().getString(R.string.phone_validation_error));
	}
	
	
}

class FirstReadyButtonClickListener implements View.OnClickListener {
	
	private final FeedPresenter mFeedPresenter;
	
	FirstReadyButtonClickListener(final FeedPresenter presenter) {
		mFeedPresenter = presenter;
	}
	
	@Override
	public void onClick(final View view) {
		mFeedPresenter.onFirstReadyButtonClicked();
	}
}

class SecondReadyButtonClickListener implements View.OnClickListener {
	
	private final FeedPresenter mFeedPresenter;
	
	SecondReadyButtonClickListener(final FeedPresenter presenter) {
		mFeedPresenter = presenter;
	}
	
	@Override
	public void onClick(final View view) {
		mFeedPresenter.onSecondReadyButtonClicked();
	}
}

class CustomDialogTextWatcher implements TextWatcher {
	
	private final FeedPresenter mFeedPresenter;
	private final int mEditTextType;
	
	
	CustomDialogTextWatcher(final FeedPresenter presenter, final int editTextType) {
		mFeedPresenter = presenter;
		mEditTextType = editTextType;
	}
	
	@Override
	public void beforeTextChanged(
		final CharSequence charSequence,
		final int i,
		final int i1,
		final int i2
	) {
	
	}
	
	@Override
	public void onTextChanged(
		final CharSequence charSequence,
		final int i,
		final int i1,
		final int i2
	) {
	
	}
	
	
	@Override
	public void afterTextChanged(final Editable editable) {
		mFeedPresenter.fieldChanged(editable.toString(), mEditTextType);
	}
	
}
