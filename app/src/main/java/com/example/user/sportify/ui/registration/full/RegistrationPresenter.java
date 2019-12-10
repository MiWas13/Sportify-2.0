package com.example.user.sportify.ui.registration.full;

import com.example.user.sportify.ui.profile.ProfileFragment;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import static com.example.user.sportify.ui.utils.Constants.AUTH_ERROR;
import static com.example.user.sportify.ui.utils.Constants.NAME_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PHONE_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PROGRESS_DIALOG_FRAGMENT;
import static com.example.user.sportify.ui.utils.Constants.REGISTRATION_ERROR;

public class RegistrationPresenter extends MvpBasePresenter<RegistrationView> {
	
	private RegistrationModel registrationModel;
	private Boolean passwordIsValid = false;
	private Boolean nameIsValid = false;
	private Boolean phoneIsValid = false;
	private Boolean hasErrors = false;
	private ProgressDialog progressDialog;
	private String name;
	private String phone;
	private String password;
	private Boolean isPhoneChecked = false;
	
	RegistrationPresenter(RegistrationModel registrationModel) {
		this.registrationModel = registrationModel;
	}
	
	void fieldChanged(String inputText, int fieldType) {
		switch (fieldType) {
			case PASSWORD_EDIT_TEXT:
				passwordIsValid = inputText.length() >= 6;
				password = inputText;
				break;
			case NAME_EDIT_TEXT:
				nameIsValid = inputText.length() > 0;
				name = inputText;
				break;
			case PHONE_EDIT_TEXT:
				phoneIsValid = inputText.length() >= 18;
				isPhoneChecked = false;
				phone = inputText;
				break;
		}
		ifViewAttached(RegistrationView::showConfirmButton);
	}
	
	
	void onConfirmButtonClicked() {
		hasErrors = false;
		if (!passwordIsValid) {
			hasErrors = true;
			ifViewAttached(RegistrationView::setPasswordError);
		}
		if (!nameIsValid) {
			hasErrors = true;
			ifViewAttached(RegistrationView::setNameError);
		}
		if (!phoneIsValid) {
			hasErrors = true;
			ifViewAttached(RegistrationView::setPhoneError);
		}
		
		if (!hasErrors && checkPhone()) {
			ifViewAttached(view -> view.showProgressBar(
				progressDialog = new ProgressDialog(),
				PROGRESS_DIALOG_FRAGMENT));
			new android.os.Handler().postDelayed(() -> RegistrationModel.signUp(token -> {
				if (token.equals(REGISTRATION_ERROR)) {
					ifViewAttached(view -> RegistrationModel.signIn(authToken -> {
						if (authToken.equals(AUTH_ERROR)) {
							view.hideProgressBar(progressDialog);
							view.showAuthError("Неверный пароль :(");
						} else {
							onSuccess(authToken);
						}
					}, phone, password));
				} else {
					onSuccess(token);
				}
			}, name, "1", phone, password), 500);
			
		}
	}
	
	private void onSuccess(String token) {
		RegistrationModel.getProfileInfo(profileData -> {
			registrationModel.saveSessionData(
				profileData.getToken(),
				profileData.getPhone(),
				profileData.getPassword(),
				profileData.getId(),
				profileData.getName());
			ifViewAttached(view -> view.hideProgressBar(progressDialog));
			ifViewAttached(view -> view.changeScreen(new ProfileFragment()));
		}, token);
	}
	
	private Boolean checkPhone() {
		if (isPhoneChecked) {
			return true;
		} else {
			StringBuilder sb = new StringBuilder(phone);
			sb.delete(0, 4);
			sb.delete(3, 5);
			sb.deleteCharAt(sb.lastIndexOf("-"));
			sb.deleteCharAt(sb.lastIndexOf("-"));
			if (!(sb.length() == 10)) {
				sb.deleteCharAt(10);
			}
			phone = sb.toString();
			isPhoneChecked = true;
			return true;
		}
	}
}
