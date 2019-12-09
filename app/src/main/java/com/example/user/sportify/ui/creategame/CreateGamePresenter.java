package com.example.user.sportify.ui.creategame;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.example.user.sportify.ui.utils.FileUtils;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchType;
import com.yandex.mapkit.search.SuggestItem;
import com.yandex.runtime.Error;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Handler;

import static android.app.Activity.RESULT_OK;
import static com.example.user.sportify.ui.utils.Constants.GALLERY_REQUEST;
import static com.example.user.sportify.ui.utils.Constants.NEW_CATEGORY_ID;
import static com.example.user.sportify.ui.utils.Constants.NEW_DESCRIPTION;
import static com.example.user.sportify.ui.utils.Constants.NEW_LOCATION;
import static com.example.user.sportify.ui.utils.Constants.NEW_MAX_PEOPLE_QUANTITY;
import static com.example.user.sportify.ui.utils.Constants.PERMISSION_REQUEST_CODE;
import static com.example.user.sportify.ui.utils.Constants.PROGRESS_DIALOG_FRAGMENT;

class CreateGamePresenter extends MvpBasePresenter<CreateGameView> {

    private CreateGameModel createGameModel;
    private Context context;
    private ProgressDialog progressDialog;
    private SearchManager searchManager;
    private List<String> suggestResult;
    private ArrayAdapter<String> addressAdapter;
    private Boolean isChanging = false;
    private String gameId;
    private Boolean isCategorySet = false;
    private Boolean isDateSet = false;
    private Boolean isLocationSet = false;
    private Boolean isMaxQuantityValid = false;
    private Boolean isDescriptionValid = false;
    private File file;
    private String categoryId;
    private String date;
    private String time;
    private String location;
    private String maxPeopleQuantity;
    private String description;
    private Boolean showPhone = false;
    private Boolean hasErrors = false;
    private final Point CENTER = new Point(55.75, 37.62);
    private final double BOX_SIZE = 0.2;
    private final BoundingBox BOUNDING_BOX = new BoundingBox(
            new Point(CENTER.getLatitude() - BOX_SIZE, CENTER.getLongitude() - BOX_SIZE),
            new Point(CENTER.getLatitude() + BOX_SIZE, CENTER.getLongitude() + BOX_SIZE));
    private final SearchOptions SEARCH_OPTIONS = new SearchOptions().setSearchTypes(
            SearchType.GEO.value |
                    SearchType.BIZ.value |
                    SearchType.TRANSIT.value);


    CreateGamePresenter(CreateGameModel createGameModel, Context context) {
        this.createGameModel = createGameModel;
        this.context = context;
    }

    void onViewCreated(GameDataApi game) {

        String[] categories = new String[]{
                "Во что хотите сыграть?",
                "Баскетбол",
                "Футбол",
                "Теннис",
                "Шахматы",
                "Бег",
                "Пинг-Понг"
        };

        List<String> categoriesList = new ArrayList<>(Arrays.asList(categories));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, categoriesList);
        ifViewAttached(view -> view.initCategorySpinner(spinnerArrayAdapter));

        if (game != null) {
            isChanging = true;
            ifViewAttached(view -> view.setChangeableData(game));
            isCategorySet = true;
            isDateSet = true;
            isLocationSet = true;
            isMaxQuantityValid = true;
            isDescriptionValid = true;
            categoryId = String.valueOf(game.getCategoryId());
            date = game.getDate();
            time = game.getTime();
            location = game.getLocation();
            maxPeopleQuantity = game.getMaxPeopleQuantity();
            description = game.getDescription();
            gameId = String.valueOf(game.getId());
        }

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        suggestResult = new ArrayList<>();
        addressAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, android.R.id.text1, suggestResult);
        ifViewAttached(CreateGameView::initToolbar);
        ifViewAttached(view -> view.initAddressField(addressAdapter));

    }

    void onDataButtonClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new NewGameDatePickerListener(this), now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Готово", datePickerDialog);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отменить", datePickerDialog);
        ifViewAttached(view -> view.showDataPickerDialog(datePickerDialog));
    }

    void onDataSet(int year, int month, int day) {

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new NewGameTimePickerListener(this, year, month, day), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Готово", timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отменить", timePickerDialog);
        ifViewAttached(view -> view.showTimePickerDialog(timePickerDialog));
    }

    void onTimeSet(int year, int month, int day, int hours, int minutes) {

        ifViewAttached(view -> view.setDateFieldValue(getCurrentDate(year, month + 1, day, hours, minutes)));
    }

    void onLoadImage(int requestCode, int resultCode, Intent imageReturnedIntent) {
        Bitmap bitmap = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {

                    ifViewAttached(view -> {
                        view.setLocationImage(compressBitmap(getRealPathFromURI(context, imageReturnedIntent.getData()), 360, 162));
                    });

                }
        }

        this.file = FileUtils.getFile(context, Uri.fromFile(fromBitmapToFile(compressBitmap(getRealPathFromURI(context, imageReturnedIntent.getData()), 360, 162))));

    }

    private File fromBitmapToFile(Bitmap bitmap) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, 1 + ".jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    private Bitmap compressBitmap(String path, int reqWidth, int reqHeight) {


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    void requestSuggest(String query) {

        searchManager.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, new SearchManager.SuggestListener() {
            @Override
            public void onSuggestResponse(@NonNull List<SuggestItem> suggest) {
                suggestResult.clear();
                int RESULT_NUMBER_LIMIT = 5;
                for (int i = 0; i < Math.min(RESULT_NUMBER_LIMIT, suggest.size()); i++) {
                    if (Objects.requireNonNull(suggest.get(i).getDisplayText()).contains("Россия"))
                        suggestResult.add(suggest.get(i).getDisplayText());
                    ifViewAttached(view -> view.updateAddressAdapter(suggestResult));
                }
            }

            @Override
            public void onSuggestError(@NonNull Error error) {

            }
        });
    }

    void showPhoneChanged(Boolean showPhone) {
        this.showPhone = showPhone;
    }

    void onLocationImageClicked() {
        ifViewAttached(CreateGameView::checkGalleryPermission);
    }

    void permissionChecked(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionIsOk();
                } else {
                    ifViewAttached(CreateGameView::hideLocationPhotoPlaceholder);
                }
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate(int year, int month, int day, int hours, int minutes) {
        Locale russian = new Locale("ru");
        String[] newMonths = {
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(russian);
        dateFormatSymbols.setMonths(newMonths);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, russian);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.date = year + "-" + month + "-" + day;
        Log.e("date", this.date);
        this.time = hours + ":" + minutes + ":" + "00";
        Log.e("date", this.time);
        isDateSet = true;
        return simpleDateFormat.format(date) + " в " + hours + ":" + minutes;
    }

    void permissionIsOk() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        ifViewAttached(view -> view.getImageFromGallery(photoPickerIntent));
    }

    void dontHavePermission() {
        ifViewAttached(view -> view.showPermissionSnack("У нас нет разрешения, дайте его в настройках, пожалуйста"));
    }

    void fieldChanged(String inputText, int fieldType) {
        switch (fieldType) {
            case NEW_MAX_PEOPLE_QUANTITY:
                try {
                    isMaxQuantityValid = Integer.valueOf(inputText) >= 2 && Integer.valueOf(inputText) < 30;
                } catch (Exception e) {
                    isMaxQuantityValid = false;
                }
                maxPeopleQuantity = inputText;
                break;
            case NEW_DESCRIPTION:
                isDescriptionValid = inputText.length() > 0;
                description = inputText;
                break;
            case NEW_CATEGORY_ID:
                isCategorySet = Integer.valueOf(inputText) > 0;
                categoryId = inputText;
                break;

            case NEW_LOCATION:
                isLocationSet = inputText.length() > 0;
                location = inputText;
                break;
        }
    }


    void onConfirmClicked() {
        hasErrors = false;

        if (!isCategorySet) {
            hasErrors = true;
            ifViewAttached(CreateGameView::setCategoryError);
        }

        if (!isDateSet) {
            hasErrors = true;
            ifViewAttached(CreateGameView::setDateError);

        }

        if (!isLocationSet) {
            hasErrors = true;
            ifViewAttached(CreateGameView::setLocationError);

        }

        if (!isMaxQuantityValid) {
            hasErrors = true;
            ifViewAttached(CreateGameView::setPeopleQuantityError);
        }

        if (!isDescriptionValid) {
            hasErrors = true;
            ifViewAttached(CreateGameView::setDescriptionError);
        }


        if (!hasErrors) {
            createGameModel.getLocation(coordinates -> {
                location = trimLocation();
                ifViewAttached(view -> view.showProgressDialog(progressDialog = new ProgressDialog(), PROGRESS_DIALOG_FRAGMENT));
                new android.os.Handler().postDelayed(() -> {
                    if (!isChanging) {
                        createGameModel.createGame(response -> ifViewAttached(view -> {
                            view.hideProgressDialog(progressDialog);
                            view.finishActivity();
                        }), file, createGameModel.getSessionData().authToken, categoryId, description, date, time, location, coordinates, maxPeopleQuantity, "1");
                    } else {
                        createGameModel.updateGame(response -> ifViewAttached(view -> {
                            view.hideProgressDialog(progressDialog);
                            view.finishActivity();
                        }), categoryId, description, date, time, location, coordinates, maxPeopleQuantity, gameId);
                    }
                }, 500);
            }, location);
        }

    }

    private String trimLocation() {
        location = location.trim();
        StringBuilder trimmedLocation = new StringBuilder(location);
        trimmedLocation.delete(0, 7);
        return trimmedLocation.toString().trim();
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
