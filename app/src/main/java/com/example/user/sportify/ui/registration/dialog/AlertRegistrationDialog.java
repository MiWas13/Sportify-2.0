package com.example.user.sportify.ui.registration.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.sportify.R;
import com.example.user.sportify.ui.feed.FeedPresenter;


import butterknife.ButterKnife;

import static com.example.user.sportify.ui.utils.Constants.NAME_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PASSWORD_EDIT_TEXT;
import static com.example.user.sportify.ui.utils.Constants.PHONE_EDIT_TEXT;

@SuppressLint("ValidFragment")
public class AlertRegistrationDialog {

    private Context context;
    private AlertDialog firstDialog;
    private AlertDialog secondDialog;
    private LayoutInflater inflater;
    private AlertDialog.Builder mBuilder;
    private TextInputLayout popRegPhoneInput;
    private TextInputLayout popRegPasswordInput;
    private TextInputLayout popRegNameInput;
    private FeedPresenter presenter;


    public AlertRegistrationDialog(Context context, FeedPresenter presenter) {
        this.context = context;
        this.presenter = presenter;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBuilder = new AlertDialog.Builder(context);
    }

    public void showFirstDialog() {

        assert inflater != null;
        View view = inflater.inflate(R.layout.authorization_first_layout, null);
        Button firstReadyButton = view.findViewById(R.id.first_ready_button);

        EditText phone = view.findViewById(R.id.pop_reg_phone);
        EditText name = view.findViewById(R.id.pop_reg_name);
        phone.addTextChangedListener(new CustomDialogTextWatcher(presenter, PHONE_EDIT_TEXT));
        name.addTextChangedListener(new CustomDialogTextWatcher(presenter, NAME_EDIT_TEXT));

        popRegPhoneInput = view.findViewById(R.id.pop_reg_phone_input);
        popRegNameInput = view.findViewById(R.id.pop_reg_name_input);

        ButterKnife.bind(this, view);
        firstReadyButton.setOnClickListener(new FirstReadyButtonClickListener(presenter));
        mBuilder.setView(view);
        firstDialog = mBuilder.create();
        firstDialog.show();
    }

    public void finishFirstDialog() {
        firstDialog.dismiss();
    }


    public void showSecondDialog() {
        assert inflater != null;
        View view = inflater.inflate(R.layout.authorization_second_layout, null);
        popRegPasswordInput = view.findViewById(R.id.pop_reg_password_input);
        EditText password = view.findViewById(R.id.pop_reg_password);
        password.addTextChangedListener(new CustomDialogTextWatcher(presenter, PASSWORD_EDIT_TEXT));

        Button secondReadyButton = view.findViewById(R.id.second_ready_button);
        secondReadyButton.setOnClickListener(new SecondReadyButtonClickListener(presenter));
        mBuilder.setView(view);
        secondDialog = mBuilder.create();
        secondDialog.show();
    }

    public void finishSecondDialog() {
        secondDialog.dismiss();
    }


    public void setPasswordError() {
        popRegPasswordInput.setErrorEnabled(true);
        popRegPasswordInput.setError(context.getResources().getString(R.string.password_validation_error));
    }

    public void setNameError() {
        popRegNameInput.setErrorEnabled(true);
        popRegNameInput.setError(context.getResources().getString(R.string.name_validation_error));

    }

    public void setPhoneError() {
        popRegPhoneInput.setErrorEnabled(true);
        popRegPhoneInput.setError(context.getResources().getString(R.string.phone_validation_error));
    }


}

class FirstReadyButtonClickListener implements View.OnClickListener {

    private FeedPresenter presenter;

    FirstReadyButtonClickListener(FeedPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        presenter.onFirstReadyButtonClicked();
    }
}

class SecondReadyButtonClickListener implements View.OnClickListener {

    private FeedPresenter presenter;

    SecondReadyButtonClickListener(FeedPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        presenter.onSecondReadyButtonClicked();
    }
}

class CustomDialogTextWatcher implements TextWatcher {
    private FeedPresenter presenter;
    private int editTextType;


    CustomDialogTextWatcher(FeedPresenter presenter, int editTextType) {
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
