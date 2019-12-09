package com.example.user.sportify.ui.creategame;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ArrayAdapter;

import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

interface CreateGameView extends MvpView {
	
	void initAddressField(ArrayAdapter<String> addressAdapter);
	
	void initCategorySpinner(ArrayAdapter<String> categoriesAdapter);
	
	void updateAddressAdapter(List<String> suggestResult);
	
	void initToolbar();
	
	void showDataPickerDialog(DatePickerDialog datePickerDialog);
	
	void showTimePickerDialog(TimePickerDialog timePickerDialog);
	
	void setDateFieldValue(String date);
	
	void checkGalleryPermission();
	
	void setLocationImage(Bitmap bitmap);
	
	void hideLocationPhotoPlaceholder();
	
	void getImageFromGallery(Intent photoPickerIntent);
	
	void showProgressDialog(ProgressDialog progressDialog, String tag);
	
	void hideProgressDialog(ProgressDialog progressDialog);
	
	void setChangeableData(GameDataApi game);
	
	void finishActivity();
	
	void setDescriptionError();
	
	void setLocationError();
	
	void setCategoryError();
	
	void setPeopleQuantityError();
	
	void setDateError();
	
	void showPermissionSnack(String message);
}
