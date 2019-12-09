package com.example.user.sportify.ui.registration.full;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
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

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.user.sportify.R;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.sportify.ui.utils.Constants.NAME_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PHONE_EDIT_TEXT;

public class RegistrationFragment extends MvpFragment<RegistrationView, RegistrationPresenter> implements RegistrationView {
	
	@BindView(R.id.reg_name)
	EditText profileName;
	
	@BindView(R.id.reg_name_input)
	TextInputLayout profileNameInput;
	
	@BindView(R.id.reg_phone)
	EditText profilePhone;
	
	@BindView(R.id.reg_phone_input)
	TextInputLayout profilePhoneInput;
	
	@BindView(R.id.reg_password)
	EditText profilePassword;
	
	@BindView(R.id.reg_password_input)
	TextInputLayout profilePasswordInput;
	
	@BindView(R.id.confirm_reg_profile_btn)
	Button profileButton;
	
	@NonNull
	@Override
	public RegistrationPresenter createPresenter() {
		return new RegistrationPresenter(new RegistrationModel(DaggerSessionComponent.builder().contextModule(
			new ContextModule(getActivity())).build()));
	}
	
	@Nullable
	@Override
	public View onCreateView(
		@NonNull LayoutInflater inflater,
		@Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState
	) {
		View view = inflater.inflate(R.layout.registration_layout, null);
		ButterKnife.bind(this, view);
		profileName.addTextChangedListener(new CustomTextWatcher(presenter, NAME_EDIT_TEXT));
		profilePassword.addTextChangedListener(new CustomTextWatcher(
			presenter,
			PASSWORD_EDIT_TEXT));
		profilePhone.addTextChangedListener(new CustomTextWatcher(presenter, PHONE_EDIT_TEXT));
		profileButton.setOnClickListener(new ConfirmButtonListener(presenter));
		profileButton.setVisibility(View.INVISIBLE);
		return view;
	}
	
	@Override
	public void setPasswordError() {
		profilePasswordInput.setErrorEnabled(true);
		profilePasswordInput.setError(getString(R.string.password_validation_error));
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
		profileButton.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void showProgressBar(ProgressDialog progressDialog, String tag) {
		FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		progressDialog.show(transaction, tag);
	}
	
	@Override
	public void hideProgressBar(ProgressDialog progressDialog) {
		progressDialog.dismiss();
	}
	
	@Override
	public void changeScreen(Fragment fragment) {
		Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().setCustomAnimations(
			R.animator.slide_in_left,
			R.animator.slide_in_right).replace(R.id.main_frame_layout, fragment).commit();
	}
	
	@Override
	public void showAuthError(String message) {
		ConstraintLayout constraintLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.reg_layout);
		TSnackbar snackbar = TSnackbar.make(constraintLayout, message, Snackbar.LENGTH_LONG);
		View snackbarView = snackbar.getView();
		TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
		textView.setTextColor(getResources().getColor(R.color.colorPrimary));
		snackbar.show();
	}
}

class CustomTextWatcher implements TextWatcher {
	
	private RegistrationPresenter presenter;
	private int editTextType;
	
	
	CustomTextWatcher(RegistrationPresenter presenter, int editTextType) {
		this.presenter = presenter;
		this.editTextType = editTextType;
	}
	
	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
	
	}
	
	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
	
	}
	
	
	@Override
	public void afterTextChanged(Editable editable) {
		presenter.fieldChanged(editable.toString(), editTextType);
	}
	
}


class ConfirmButtonListener implements Button.OnClickListener {
	
	private RegistrationPresenter presenter;
	
	@Override
	public void onClick(View view) {
		presenter.onConfirmButtonClicked();
	}
	
	ConfirmButtonListener(RegistrationPresenter presenter) {
		this.presenter = presenter;
	}
}


