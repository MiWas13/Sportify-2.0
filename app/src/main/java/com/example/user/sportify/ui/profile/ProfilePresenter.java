package com.example.user.sportify.ui.profile;

import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import static com.example.user.sportify.ui.utils.Constants.NAME_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.NEW_PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PHONE_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PROGRESS_DIALOG_FRAGMENT;

class ProfilePresenter extends MvpBasePresenter<ProfileView> {
	
	private final ProfileModel mProfileModel;
	private Boolean mPasswordIsValid = false;
	private Boolean mNewPasswordIsValid = false;
	private String mPassword = null;
	private Boolean mNameIsValid = true;
	private String mName = null;
	private Boolean mPhoneIsValid = true;
	private String mPhone = null;
	private Boolean mHasErrors = false;
	private Boolean mIsPhoneChecked = false;
	private ProgressDialog mProgressDialog;
	
	ProfilePresenter(final ProfileModel profileModel) {
		this.mProfileModel = profileModel;
	}
	
	public void onViewCreated() {
		ifViewAttached(view -> view.setFieldsContent(
			mProfileModel.getSessionData().name,
			mProfileModel.getSessionData().phone));
		mIsPhoneChecked = true;
		mNameIsValid = true;
		mPhone = mProfileModel.getSessionData().phone;
		mName = mProfileModel.getSessionData().name;
		mPhoneIsValid = true;
		mNameIsValid = true;
	}
	
	void fieldChanged(final String inputText, final int fieldType) {
		switch (fieldType) {
			case PASSWORD_EDIT_TEXT:
				mPasswordIsValid = inputText.length() >= 6;
				break;
			case NEW_PASSWORD_EDIT_TEXT:
				mNewPasswordIsValid = inputText.length() >= 6;
				mPassword = inputText;
				break;
			case NAME_EDIT_TEXT:
				mNameIsValid = inputText.length() > 0;
				mName = inputText;
				break;
			case PHONE_EDIT_TEXT:
				mIsPhoneChecked = false;
				mPhoneIsValid = inputText.length() >= 18;
				mPhone = inputText;
				break;
		}
		ifViewAttached(ProfileView::showConfirmButton);
	}
	
	void onConfirmButtonClicked() {
		if (!mPasswordIsValid) {
			mHasErrors = true;
			ifViewAttached(ProfileView::setPasswordError);
		}
		
		if (!mNewPasswordIsValid) {
			mHasErrors = true;
			ifViewAttached(ProfileView::setNewPasswordError);
		}
		
		if (!mNameIsValid) {
			mHasErrors = true;
			ifViewAttached(ProfileView::setNameError);
		}
		
		if (!mPhoneIsValid) {
			mHasErrors = true;
			ifViewAttached(ProfileView::setPhoneError);
		}
		
		if (!mHasErrors && checkPhone()) {
			ifViewAttached(view -> view.showProgressBar(
				mProgressDialog = new ProgressDialog(),
				PROGRESS_DIALOG_FRAGMENT));
			ProfileModel.updateProfile(response -> {
				ifViewAttached(view -> {
					view.hideProgressBar(mProgressDialog);
					view.clearPasswordFields();
				});
			}, mProfileModel.getSessionData().authToken, mName, mPhone, mPassword);
		}
	}
	
	private Boolean checkPhone() {
		if (!mIsPhoneChecked) {
			final StringBuilder sb = new StringBuilder(mPhone);
			sb.delete(0, 4);
			sb.delete(3, 5);
			
			sb.deleteCharAt(sb.lastIndexOf("-"));
			sb.deleteCharAt(sb.lastIndexOf("-"));
			
			if (!(sb.length() == 10)) {
				sb.deleteCharAt(10);
			}
			mPhone = sb.toString();
			mIsPhoneChecked = true;
		}
		return true;
	}
	
}
