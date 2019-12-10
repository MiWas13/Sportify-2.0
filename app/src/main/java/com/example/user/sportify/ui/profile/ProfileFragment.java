package com.example.user.sportify.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.sportify.R;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.sportify.ui.utils.Constants.NAME_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.NEW_PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PHONE_EDIT_TEXT;

public class ProfileFragment extends MvpFragment<ProfileView, ProfilePresenter> implements ProfileView {
	
	@BindView(R.id.profile_name_input)
	TextInputLayout profileNameInput;
	
	@BindView(R.id.profile_name)
	EditText profileName;
	
	@BindView(R.id.profile_phone_input)
	TextInputLayout profilePhoneInput;
	
	@BindView(R.id.profile_phone)
	EditText profilePhone;
	
	@BindView(R.id.profile_last_password_input)
	TextInputLayout profileLastPasswordInput;
	
	@BindView(R.id.profile_last_password)
	EditText profileLastPassword;
	
	@BindView(R.id.profile_new_password_input)
	TextInputLayout profileNewPasswordInput;
	
	@BindView(R.id.profile_new_password)
	EditText profileNewPassword;
	
	@BindView(R.id.confirm_change_profile_btn)
	Button changeProfileButton;
	
	@NonNull
	@Override
	public ProfilePresenter createPresenter() {
		return new ProfilePresenter(new ProfileModel(DaggerSessionComponent.builder().contextModule(
			new ContextModule(getActivity())).build()));
	}
	
	@Nullable
	@Override
	public View onCreateView(
		@NonNull final LayoutInflater inflater,
		@Nullable final ViewGroup container,
		@Nullable final Bundle savedInstanceState
	) {
		final View view = inflater.inflate(R.layout.profile_layout, null);
		ButterKnife.bind(this, view);
		profileName.addTextChangedListener(new CustomTextWatcher(presenter, NAME_EDIT_TEXT));
		profilePhone.addTextChangedListener(new CustomTextWatcher(presenter, PHONE_EDIT_TEXT));
		profileLastPassword.addTextChangedListener(new CustomTextWatcher(
			presenter,
			PASSWORD_EDIT_TEXT));
		profileNewPassword.addTextChangedListener(new CustomTextWatcher(
			presenter,
			NEW_PASSWORD_EDIT_TEXT));
		changeProfileButton.setOnClickListener(new ConfirmButtonListener(presenter));
		return view;
	}
	
	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getPresenter().onViewCreated();
	}
	
	@Override
	public void setFieldsContent(final String name, final String phone) {
		profileName.setText(name, TextView.BufferType.EDITABLE);
		profilePhone.setText(phone, TextView.BufferType.EDITABLE);
		changeProfileButton.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void setPasswordError() {
		profileLastPasswordInput.setErrorEnabled(true);
		profileLastPasswordInput.setError(getString(R.string.password_validation_error));
	}
	
	@Override
	public void setNewPasswordError() {
		profileNewPasswordInput.setErrorEnabled(true);
		profileNewPasswordInput.setError(getString(R.string.password_validation_error));
	}
	
	@Override
	public void setNameError() {
		profileNameInput.setErrorEnabled(true);
		profileNameInput.setError(getString(R.string.name_validation_error));
	}
	
	@Override
	public void setPhoneError() {
		profilePhoneInput.setErrorEnabled(true);
		profilePhoneInput.setError(getString(R.string.phone_validation_error));
	}
	
	@Override
	public void showConfirmButton() {
		changeProfileButton.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void clearPasswordFields() {
		profileLastPassword.setText("");
		profileNewPassword.setText("");
		profileLastPassword.clearFocus();
		profileNewPassword.clearFocus();
		profileName.clearFocus();
		profilePhone.clearFocus();
	}
	
	@Override
	public void showProgressBar(final ProgressDialog progressDialog, final String tag) {
		final FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
		final FragmentTransaction transaction = manager.beginTransaction();
		progressDialog.show(transaction, tag);
	}
	
	@Override
	public void hideProgressBar(final ProgressDialog progressDialog) {
		progressDialog.dismiss();
	}
}

class CustomTextWatcher implements TextWatcher {
	
	private final ProfilePresenter mProfilePresenter;
	private final int mEditTextType;
	
	
	CustomTextWatcher(final ProfilePresenter presenter, final int editTextType) {
		mProfilePresenter = presenter;
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
		mProfilePresenter.fieldChanged(editable.toString(), mEditTextType);
	}
	
}

class ConfirmButtonListener implements Button.OnClickListener {
	
	private final ProfilePresenter mProfilePresenter;
	
	@Override
	public void onClick(final View view) {
		mProfilePresenter.onConfirmButtonClicked();
	}
	
	ConfirmButtonListener(final ProfilePresenter presenter) {
		mProfilePresenter = presenter;
	}
}