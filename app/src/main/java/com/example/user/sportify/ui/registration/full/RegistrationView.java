package com.example.user.sportify.ui.registration.full;

import androidx.fragment.app.Fragment;

import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface RegistrationView extends MvpView {
	
	void setPasswordError();
	
	void setNameError();
	
	void setPhoneError();
	
	void showConfirmButton();
	
	void showProgressBar(ProgressDialog progressDialog, String tag);
	
	void hideProgressBar(ProgressDialog progressDialog);
	
	void changeScreen(Fragment fragment);
	
	void showAuthError(String message);
}
