package com.example.user.sportify.ui.creategame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.squareup.picasso.Picasso;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.search.SearchFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    private ArrayAdapter<String> addressAdapter;

    @NonNull
    @Override
    public CreateGamePresenter createPresenter() {
        return new CreateGamePresenter(new CreateGameModel(DaggerSessionComponent.builder().contextModule(new ContextModule(this)).build()), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    public void setChangeableData(GameDataApi game) {
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

    private void getImageWithPicasso(String photoUrl, ImageView imageView) {
        Picasso.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
                .noFade()
                .into(imageView);
    }


    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate(String serverDate, String time) {
        Locale russian = new Locale("ru");
        String[] newMonths = {
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(russian);
        dateFormatSymbols.setMonths(newMonths);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, russian);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);

        StringBuilder stringBuilder = new StringBuilder(time);

        stringBuilder.delete(stringBuilder.lastIndexOf(":"), 8);

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(serverDate);
        } catch (ParseException e) {
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
    public void initAddressField(ArrayAdapter<String> addressAdapter) {
        this.addressAdapter = addressAdapter;
        newGameAddress.setAdapter(this.addressAdapter);
    }

    @Override
    public void initCategorySpinner(ArrayAdapter<String> categoriesAdapter) {
        categorySpinner.setAdapter(categoriesAdapter);
        categorySpinner.setOnItemSelectedListener(new OnCategoryItemClicked(presenter));
    }

    @Override
    public void updateAddressAdapter(List<String> suggestResult) {
        addressAdapter.clear();
        addressAdapter.addAll(suggestResult);
        addressAdapter.notifyDataSetChanged();
    }


    @Override
    public void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(view -> finish());
        newGameAddress.addTextChangedListener(new NewGameTextWatcher(presenter, newGameAddress, NEW_LOCATION));
        newDescription.addTextChangedListener(new NewGameTextWatcher(presenter, null, NEW_DESCRIPTION));
        newPeopleQuantity.addTextChangedListener(new NewGameTextWatcher(presenter, null, NEW_MAX_PEOPLE_QUANTITY));
    }

    @Override
    public void showDataPickerDialog(DatePickerDialog datePickerDialog) {
        datePickerDialog.show();
    }

    @Override
    public void showTimePickerDialog(TimePickerDialog timePickerDialog) {
        timePickerDialog.show();
    }

    @Override
    public void setDateFieldValue(String date) {
        dateBtn.setText(date);
    }

    @Override
    public void setLocationImage(Bitmap bitmap) {
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                presenter.dontHavePermission();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            presenter.permissionIsOk();
        }
    }

    @Override
    public void getImageFromGallery(Intent photoPickerIntent) {
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    public void showProgressDialog(ProgressDialog progressDialog, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        progressDialog.show(transaction, tag);
    }

    @Override
    public void hideProgressDialog(ProgressDialog progressDialog) {
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
    public void showPermissionSnack(String message) {
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (imageReturnedIntent != null)
            presenter.onLoadImage(requestCode, resultCode, imageReturnedIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        presenter.permissionChecked(requestCode, permissions, grantResults);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down);
    }
}

class NewGameTextWatcher implements TextWatcher {

    private CreateGamePresenter presenter;
    private AutoCompleteTextView autoCompleteTextView;
    private int editTextType;

    NewGameTextWatcher(CreateGamePresenter presenter, AutoCompleteTextView autoCompleteTextView, int editTextType) {
        this.presenter = presenter;
        this.autoCompleteTextView = autoCompleteTextView;
        this.editTextType = editTextType;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        if (editTextType == NEW_LOCATION) {
//            autoCompleteTextView.showDropDown();
//        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editTextType == NEW_LOCATION) {
            presenter.requestSuggest(editable.toString());
        }
        presenter.fieldChanged(editable.toString(), editTextType);
    }
}

class NewGameDatePickerListener implements DatePickerDialog.OnDateSetListener {

    private CreateGamePresenter presenter;

    NewGameDatePickerListener(CreateGamePresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        presenter.onDataSet(year, month, day);
    }
}

class NewGameTimePickerListener implements TimePickerDialog.OnTimeSetListener {
    private CreateGamePresenter presenter;
    private int year;
    private int month;
    private int day;

    NewGameTimePickerListener(CreateGamePresenter presenter, int year, int month, int day) {
        this.presenter = presenter;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
        presenter.onTimeSet(year, month, day, hours, minutes);
    }
}

class NewGameDataButtonListener implements SearchView.OnClickListener {

    private CreateGamePresenter presenter;

    NewGameDataButtonListener(CreateGamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        presenter.onDataButtonClicked();
    }
}

class NewGameLocationImage implements View.OnClickListener {

    private CreateGamePresenter presenter;

    NewGameLocationImage(CreateGamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        presenter.onLocationImageClicked();
    }
}

class NewGameSwitcherCheckListener implements CompoundButton.OnCheckedChangeListener {

    private CreateGamePresenter presenter;

    NewGameSwitcherCheckListener(CreateGamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        presenter.showPhoneChanged(b);
    }
}

class NewConfirmButtonListener implements View.OnClickListener {

    private CreateGamePresenter presenter;

    NewConfirmButtonListener(CreateGamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        presenter.onConfirmClicked();
    }
}

class OnCategoryItemClicked implements AdapterView.OnItemSelectedListener {

    private CreateGamePresenter presenter;

    OnCategoryItemClicked(CreateGamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int selectedItemPosition, long selectedId) {
        presenter.fieldChanged(String.valueOf(selectedId), NEW_CATEGORY_ID);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}