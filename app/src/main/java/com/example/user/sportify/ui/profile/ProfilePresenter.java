package com.example.user.sportify.ui.profile;

import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import static com.example.user.sportify.ui.utils.Constants.NAME_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.NEW_PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PHONE_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PROGRESS_DIALOG_FRAGMENT;

class ProfilePresenter extends MvpBasePresenter<ProfileView> {
	
	private ProfileModel profileModel;
	private Boolean passwordIsValid = false;
	private Boolean newPasswordIsValid = false;
	private String password = null;
	private Boolean nameIsValid = true;
	private String name = null;
	private Boolean phoneIsValid = true;
	private String phone = null;
	private Boolean hasErrors = false;
	private Boolean isPhoneChecked = false;
	private ProgressDialog progressDialog;
	
	ProfilePresenter(ProfileModel profileModel) {
		this.profileModel = profileModel;
	}
	
	public void onViewCreated() {
		ifViewAttached(view -> view.setFieldsContent(
			profileModel.getSessionData().name,
			profileModel.getSessionData().phone));
		isPhoneChecked = true;
		nameIsValid = true;
		phone = profileModel.getSessionData().phone;
		name = profileModel.getSessionData().name;
		phoneIsValid = true;
		nameIsValid = true;
	}
	
	void fieldChanged(String inputText, int fieldType) {
		switch (fieldType) {
			case PASSWORD_EDIT_TEXT:
				passwordIsValid = inputText.length() >= 6;
				break;
			case NEW_PASSWORD_EDIT_TEXT:
				newPasswordIsValid = inputText.length() >= 6;
				password = inputText;
				break;
			case NAME_EDIT_TEXT:
				nameIsValid = inputText.length() > 0;
				name = inputText;
				break;
			case PHONE_EDIT_TEXT:
				isPhoneChecked = false;
				phoneIsValid = inputText.length() >= 18;
				phone = inputText;
				break;
		}
		ifViewAttached(ProfileView::showConfirmButton);
	}
	
	void onConfirmButtonClicked() {
		if (!passwordIsValid) {
			hasErrors = true;
			ifViewAttached(ProfileView::setPasswordError);
		}
		
		if (!newPasswordIsValid) {
			hasErrors = true;
			ifViewAttached(ProfileView::setNewPasswordError);
		}
		
		if (!nameIsValid) {
			hasErrors = true;
			ifViewAttached(ProfileView::setNameError);
		}
		
		if (!phoneIsValid) {
			hasErrors = true;
			ifViewAttached(ProfileView::setPhoneError);
		}
		
		if (!hasErrors && checkPhone()) {
			ifViewAttached(view -> view.showProgressBar(
				progressDialog = new ProgressDialog(),
				PROGRESS_DIALOG_FRAGMENT));
			profileModel.updateProfile(response -> {
				ifViewAttached(view -> {
					view.hideProgressBar(progressDialog);
					view.clearPasswordFields();
				});
			}, profileModel.getSessionData().authToken, name, phone, password);
		}
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
