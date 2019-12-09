package com.example.user.sportify.ui.concretgame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.models.UserParticipantData;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.base.RecyclerViewBase;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.sportify.ui.utils.Constants.EXTRA_GAME;
import static com.example.user.sportify.ui.utils.Constants.EXTRA_ORGANIZER_TYPE;
import static com.example.user.sportify.ui.utils.Constants.EXTRA_PARTICIPANT_TYPE;

public class ConcretGameActivity extends MvpActivity<ConcretGameView, ConcretGamePresenter> implements ConcretGameView {

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.main_concret_game_image)
    ImageView gamePhoto;

    @BindView(R.id.concret_game_description)
    TextView gameDescription;

    @BindView(R.id.game_description_recycler)
    RecyclerView gameDescriptionRecycler;

    @BindView(R.id.concret_game_toolbar_title)
    TextView categoryName;

    private RecyclerView participantsPhonesRecycler;
    private Button connectButton;

    private ConcretGameDescriptionAdapter concretGameDescriptionAdapter;
    private OrganizerConcretGameParticipantsAdapter organizerConcretGameParticipantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getIntent().getBooleanExtra(EXTRA_ORGANIZER_TYPE, false)) {
            setContentView(R.layout.concret_game);
            connectButton = findViewById(R.id.concret_game_connect_button);
            connectButton.setOnClickListener(new OnConnectButtonClickListener(presenter));
        } else {
            setContentView(R.layout.organizer_concret_game);
            participantsPhonesRecycler = findViewById(R.id.participants_phones_recycler);
        }
        ButterKnife.bind(this);

        presenter.onViewCreated((GameDataApi) getIntent().getSerializableExtra(EXTRA_GAME), getIntent().getBooleanExtra(EXTRA_PARTICIPANT_TYPE, false), getIntent().getBooleanExtra(EXTRA_ORGANIZER_TYPE, false));
    }

    @NonNull
    @Override
    public ConcretGamePresenter createPresenter() {
        return new ConcretGamePresenter(this, new ConcretGameModel(DaggerSessionComponent.builder().contextModule(new ContextModule(this)).build()));
    }

    @Override
    public void initToolbar(String category) {
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        categoryName.setText(category);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public void initGameDescription(LinearLayoutManager concretGameDescriptionLayoutManager, ConcretGameDescriptionAdapter concretGameDescriptionAdapter) {
        this.concretGameDescriptionAdapter = concretGameDescriptionAdapter;
        new RecyclerViewBase<ConcretGameDescriptionAdapter>(this).initRecyclerView(gameDescriptionRecycler, concretGameDescriptionLayoutManager, this.concretGameDescriptionAdapter, true, null);
    }

    @Override
    public void initParticipantsPhones(LinearLayoutManager participantsPhonesLayoutManager, OrganizerConcretGameParticipantsAdapter organizerConcretGameParticipantsAdapter) {
        this.organizerConcretGameParticipantsAdapter = organizerConcretGameParticipantsAdapter;
        new RecyclerViewBase<OrganizerConcretGameParticipantsAdapter>(this).initRecyclerView(participantsPhonesRecycler, participantsPhonesLayoutManager, this.organizerConcretGameParticipantsAdapter, true, null);
    }

    @Override
    public void setPhoto(String photoUrl) {
        Picasso.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
                .noFade()
                .into(gamePhoto);
    }

    @Override
    public void setDescription(String description) {
        gameDescription.setText(description);
    }

    @Override
    public void startNewActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void hideConnectButton() {
        connectButton.setVisibility(View.GONE);
    }

    @Override
    public void setInGameState() {
        connectButton.setText("Вы в игре");
        connectButton.setBackgroundColor(Color.parseColor("#FFB032"));
    }

    @Override
    public void setNotInGameState() {
        connectButton.setText("Присоединиться");
        connectButton.setBackgroundColor(Color.parseColor("#FF44ABFF"));
    }

    @Override
    public void showProgressBar(ProgressDialog progressDialog, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        progressDialog.show(transaction, tag);
    }

    @Override
    public void hideProgressBar(ProgressDialog progressDialog) {
        progressDialog.dismiss();
    }

    @Override
    public void addParticipant(UserParticipantData user) {
        organizerConcretGameParticipantsAdapter.addPosotion(user);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.zoom_out);
    }
}

class OnConnectButtonClickListener implements View.OnClickListener {

    private ConcretGamePresenter presenter;

    OnConnectButtonClickListener(ConcretGamePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        presenter.onConnectButtonClicked();
    }
}
