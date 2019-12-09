package com.example.user.sportify.ui.profile;

import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface ProfileView extends MvpView {
	
	void setFieldsContent(String name, String phone);
	
	void setPasswordError();
	
	void setNewPasswordError();
	
	void setNameError();
	
	void setPhoneError();
	
	void showConfirmButton();
	
	void clearPasswordFields();
	
	void showProgressBar(ProgressDialog progressDialog, String tag);
	
	void hideProgressBar(ProgressDialog progressDialog);
}
