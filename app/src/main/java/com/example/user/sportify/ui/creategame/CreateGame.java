package com.example.user.sportify.ui.creategame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.squareup.picasso.Picasso;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.search.SearchFactory;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.sportify.ui.utils.Constants.BASE_UPLOADS_URL;
import static com.example.user.sportify.ui.utils.Constants.EXTRA_GAME;
import static com.example.user.sportify.ui.utils.Constants.GALLERY_REQUEST;
import static com.example.user.sportify.ui.utils.Constants.NEW_CATEGORY_ID;
import static com.example.user.sportify.ui.utils.Constants.NEW_DESCRIPTION;
import static com.example.user.sportify.ui.utils.Constants.NEW_LOCATION;
import static com.example.user.sportify.ui.utils.Constants.NEW_MAX_PEOPLE_QUANTITY;
import static com.example.user.sportify.ui.utils.Constants.PERMISSION_REQUEST_CODE;

public class CreateGame extends MvpActivity<CreateGameView, CreateGamePresenter> implements CreateGameView {
	
	@BindView(R.id.new_game_toolbar)
	Toolbar toolbar;
	
	@BindView(R.id.new_game_address_input)
	TextInputLayout newGameAddressInput;
	
	@BindView(R.id.new_game_address)
	AutoCompleteTextViewNoFilters newGameAddress;
	
	@BindView(R.id.new_game_date)
	Button dateBtn;
	
	@BindView(R.id.location_image_button)
	Button locationImageButton;
	
	@BindView(R.id.new_location_image)
	ImageView newLocationImage;
	
	@BindView(R.id.new_game_category_spinner)
	Spinner categorySpinner;
	
	@BindView(R.id.shadow)
	View shadow;
	
	@BindView(R.id.new_game_show_phone_switch)
	Switch showPhone;
	
	@BindView(R.id.new_game_confirm_button)
	Button newConfirmButton;
	
	@BindView(R.id.new_game_description_input)
	TextInputLayout newDescriptionInput;
	
	@BindView(R.id.new_game_description)
	EditText newDescription;
	
	@BindView(R.id.new_game_people_quantity_input)
	TextInputLayout newPeopleQuantityInput;
	
	@BindView(R.id.new_game_people_quantity)
	EditText newPeopleQuantity;
	
	private ArrayAdapter<String> mAddressAdapter;
	
	@NonNull
	@Override
	public CreateGamePresenter createPresenter() {
		return new CreateGamePresenter(new CreateGameModel(DaggerSessionComponent.builder().contextModule(
			new ContextModule(this)).build()), this);
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		MapKitFactory.initialize(this);
		SearchFactory.initialize(this);
		setContentView(R.layout.new_game_layout);
		super.onCreate(savedInstanceState);
		
		ButterKnife.bind(this);
		dateBtn.setOnClickListener(new NewGameDataButtonListener(presenter));
		
		locationImageButton.setOnClickListener(new NewGameLocationImage(presenter));
		
		newLocationImage.setOnClickListener(new NewGameLocationImage(presenter));
		
		showPhone.setOnCheckedChangeListener(new NewGameSwitcherCheckListener(presenter));
		
		newConfirmButton.setOnClickListener(new NewConfirmButtonListener(presenter));
		if (getIntent().getSerializableExtra(EXTRA_GAME) != null) {
			presenter.onViewCreated((GameDataApi) getIntent().getSerializableExtra(EXTRA_GAME));
		} else {
			presenter.onViewCreated(null);
		}
		
	}
	
	@Override
	public void setChangeableData(final GameDataApi game) {
		newGameAddress.setText(game.getLocation());
		dateBtn.setText(getCurrentDate(game.getDate(), game.getTime()));
		showPhone.setChecked(true);
		newDescription.setText(game.getDescription());
		newPeopleQuantity.setText(game.getMaxPeopleQuantity());
		categorySpinner.setSelection(game.getCategoryId());
		locationImageButton.setClickable(false);
		newLocationImage.setClickable(false);
		if (game.getLocationPhotoUrl() != null) {
			getImageWithPicasso((BASE_UPLOADS_URL + game.getLocationPhotoUrl()), newLocationImage);
			locationImageButton.setVisibility(View.GONE);
			shadow.setVisibility(View.GONE);
		}
	}
	
	private void getImageWithPicasso(final String photoUrl, final ImageView imageView) {
		Picasso.with(this)
			.load(photoUrl)
			.placeholder(R.drawable.image_placeholder)
			.fit()
			.centerCrop()
			.noFade()
			.into(imageView);
	}
	
	
	@SuppressLint("SimpleDateFormat")
	private static String getCurrentDate(final String serverDate, final String time) {
		final Locale russian = new Locale("ru");
		final String[] newMonths = {
			"января", "февраля", "марта", "апреля", "мая", "июня",
			"июля", "августа", "сентября", "октября", "ноября", "декабря" };
		final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(russian);
		dateFormatSymbols.setMonths(newMonths);
		final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, russian);
		final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
		simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
		
		final StringBuilder stringBuilder = new StringBuilder(time);
		
		stringBuilder.delete(stringBuilder.lastIndexOf(":"), 8);
		
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(serverDate);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return simpleDateFormat.format(date) + " в " + stringBuilder.toString();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		MapKitFactory.getInstance().onStart();
	}
	
	@Override
	protected void onStop() {
		MapKitFactory.getInstance().onStop();
		super.onStop();
	}
	
	
	@Override
	public void initAddressField(final ArrayAdapter<String> addressAdapter) {
		mAddressAdapter = addressAdapter;
		newGameAddress.setAdapter(mAddressAdapter);
	}
	
	@Override
	public void initCategorySpinner(final ArrayAdapter<String> categoriesAdapter) {
		categorySpinner.setAdapter(categoriesAdapter);
		categorySpinner.setOnItemSelectedListener(new OnCategoryItemClicked(presenter));
	}
	
	@Override
	public void updateAddressAdapter(final List<String> suggestResult) {
		mAddressAdapter.clear();
		mAddressAdapter.addAll(suggestResult);
		mAddressAdapter.notifyDataSetChanged();
	}
	
	
	@Override
	public void initToolbar() {
		toolbar.setNavigationIcon(R.drawable.ic_close);
		toolbar.setNavigationOnClickListener(view -> finish());
		newGameAddress.addTextChangedListener(new NewGameTextWatcher(
			presenter,
			newGameAddress,
			NEW_LOCATION));
		newDescription.addTextChangedListener(new NewGameTextWatcher(
			presenter,
			null,
			NEW_DESCRIPTION));
		newPeopleQuantity.addTextChangedListener(new NewGameTextWatcher(
			presenter,
			null,
			NEW_MAX_PEOPLE_QUANTITY));
	}
	
	@Override
	public void showDataPickerDialog(final DatePickerDialog datePickerDialog) {
		datePickerDialog.show();
	}
	
	@Override
	public void showTimePickerDialog(final TimePickerDialog timePickerDialog) {
		timePickerDialog.show();
	}
	
	@Override
	public void setDateFieldValue(final String date) {
		dateBtn.setText(date);
	}
	
	@Override
	public void setLocationImage(final Bitmap bitmap) {
		newLocationImage.setImageBitmap(bitmap);
		locationImageButton.setVisibility(View.GONE);
		shadow.setVisibility(View.GONE);
	}
	
	@Override
	public void hideLocationPhotoPlaceholder() {
		locationImageButton.setVisibility(View.GONE);
		newLocationImage.setVisibility(View.GONE);
	}
	
	@Override
	public void checkGalleryPermission() {
		if (ContextCompat.checkSelfPermission(
			this,
			Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(
				this,
				Manifest.permission.READ_EXTERNAL_STORAGE)) {
				presenter.dontHavePermission();
			} else {
				ActivityCompat.requestPermissions(
					this,
					new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
					PERMISSION_REQUEST_CODE);
			}
		} else {
			presenter.permissionIsOk();
		}
	}
	
	@Override
	public void getImageFromGallery(final Intent photoPickerIntent) {
		startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
	}
	
	@Override
	public void showProgressDialog(final ProgressDialog progressDialog, final String tag) {
		final FragmentManager manager = getSupportFragmentManager();
		final FragmentTransaction transaction = manager.beginTransaction();
		progressDialog.show(transaction, tag);
	}
	
	@Override
	public void hideProgressDialog(final ProgressDialog progressDialog) {
		progressDialog.dismiss();
	}
	
	@Override
	public void finishActivity() {
		finish();
	}
	
	@Override
	public void setDescriptionError() {
		newDescriptionInput.setErrorEnabled(true);
		newDescriptionInput.setError(getResources().getString(R.string.require_field));
	}
	
	@Override
	public void setLocationError() {
		newGameAddressInput.setErrorEnabled(true);
		newGameAddressInput.setError(getResources().getString(R.string.require_field));
	}
	
	@Override
	public void setCategoryError() {
		((TextView) categorySpinner.getChildAt(0)).setError(getResources().getString(R.string.require_field));
	}
	
	@Override
	public void setPeopleQuantityError() {
		newPeopleQuantityInput.setErrorEnabled(true);
		newPeopleQuantityInput.setError(getResources().getString(R.string.people_quantity_error));
	}
	
	@Override
	public void setDateError() {
		//TODO
//        newDescriptionInput.setErrorEnabled(true);
//        newDescriptionInput.setError(getResources().getString(R.string.require_field));
	}
	
	@Override
	public void showPermissionSnack(final String message) {
		Snackbar.make(
			getWindow().getDecorView().getRootView(),
			message,
			Snackbar.LENGTH_LONG).show();
	}
	
	@Override
	protected void onActivityResult(
		final int requestCode,
		final int resultCode,
		@Nullable final Intent imageReturnedIntent
	) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		if (imageReturnedIntent != null) {
			presenter.onLoadImage(requestCode, resultCode, imageReturnedIntent);
		}
	}
	
	
	@Override
	public void onRequestPermissionsResult(
		final int requestCode,
		@NonNull final String[] permissions,
		@NonNull final int[] grantResults
	) {
		presenter.permissionChecked(requestCode, permissions, grantResults);
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.slide_down);
	}
}

class NewGameTextWatcher implements TextWatcher {
	
	private final CreateGamePresenter mCreateGamePresenter;
	private final AutoCompleteTextView mAutoCompleteTextView;
	private final int mEditTextType;
	
	NewGameTextWatcher(
		final CreateGamePresenter presenter,
		final AutoCompleteTextView autoCompleteTextView,
		final int editTextType
	) {
		mCreateGamePresenter = presenter;
		mAutoCompleteTextView = autoCompleteTextView;
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
		if (mEditTextType == NEW_LOCATION) {
			mCreateGamePresenter.requestSuggest(editable.toString());
		}
		mCreateGamePresenter.fieldChanged(editable.toString(), mEditTextType);
	}
}

class NewGameDatePickerListener implements DatePickerDialog.OnDateSetListener {
	
	private final CreateGamePresenter mCreateGamePresenter;
	
	NewGameDatePickerListener(final CreateGamePresenter presenter) {
		mCreateGamePresenter = presenter;
	}
	
	
	@Override
	public void onDateSet(
		final DatePicker datePicker,
		final int year,
		final int month,
		final int day
	) {
		mCreateGamePresenter.onDataSet(year, month, day);
	}
}

class NewGameTimePickerListener implements TimePickerDialog.OnTimeSetListener {
	
	private final CreateGamePresenter mCreateGamePresenter;
	private final int mYear;
	private final int mMonth;
	private final int mDay;
	
	NewGameTimePickerListener(
		final CreateGamePresenter presenter,
		final int year,
		final int month,
		final int day
	) {
		mCreateGamePresenter = presenter;
		mYear = year;
		mMonth = month;
		mDay = day;
	}
	
	@Override
	public void onTimeSet(final TimePicker timePicker, final int hours, final int minutes) {
		mCreateGamePresenter.onTimeSet(mYear, mMonth, mDay, hours, minutes);
	}
}

class NewGameDataButtonListener implements SearchView.OnClickListener {
	
	private final CreateGamePresenter mCreateGamePresenter;
	
	NewGameDataButtonListener(final CreateGamePresenter presenter) {
		mCreateGamePresenter = presenter;
	}
	
	@Override
	public void onClick(final View view) {
		mCreateGamePresenter.onDataButtonClicked();
	}
}

class NewGameLocationImage implements View.OnClickListener {
	
	private final CreateGamePresenter mCreateGamePresenter;
	
	NewGameLocationImage(final CreateGamePresenter presenter) {
		mCreateGamePresenter = presenter;
	}
	
	@Override
	public void onClick(final View view) {
		mCreateGamePresenter.onLocationImageClicked();
	}
}

class NewGameSwitcherCheckListener implements CompoundButton.OnCheckedChangeListener {
	
	private final CreateGamePresenter mCreateGamePresenter;
	
	NewGameSwitcherCheckListener(final CreateGamePresenter presenter) {
		mCreateGamePresenter = presenter;
	}
	
	@Override
	public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
		mCreateGamePresenter.showPhoneChanged(b);
	}
}

class NewConfirmButtonListener implements View.OnClickListener {
	
	private final CreateGamePresenter mCreateGamePresenter;
	
	NewConfirmButtonListener(final CreateGamePresenter presenter) {
		mCreateGamePresenter = presenter;
	}
	
	@Override
	public void onClick(final View view) {
		mCreateGamePresenter.onConfirmClicked();
	}
}

class OnCategoryItemClicked implements AdapterView.OnItemSelectedListener {
	
	private final CreateGamePresenter mCreateGamePresenter;
	
	OnCategoryItemClicked(final CreateGamePresenter presenter) {
		mCreateGamePresenter = presenter;
	}
	
	@Override
	public void onItemSelected(
		final AdapterView<?> adapterView,
		final View view,
		final int selectedItemPosition,
		final long selectedId
	) {
		mCreateGamePresenter.fieldChanged(String.valueOf(selectedId), NEW_CATEGORY_ID);
	}
	
	@Override
	public void onNothingSelected(final AdapterView<?> adapterView) {
	
	}
}