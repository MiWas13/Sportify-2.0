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
	
	private RecyclerView mParticipantsPhonesRecycler;
	private Button mConnectButton;
	
	private OrganizerConcretGameParticipantsAdapter mOrganizerConcretGameParticipantsAdapter;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!getIntent().getBooleanExtra(EXTRA_ORGANIZER_TYPE, false)) {
			setContentView(R.layout.concret_game);
			mConnectButton = findViewById(R.id.concret_game_connect_button);
			mConnectButton.setOnClickListener(new OnConnectButtonClickListener(presenter));
		} else {
			setContentView(R.layout.organizer_concret_game);
			mParticipantsPhonesRecycler = findViewById(R.id.participants_phones_recycler);
		}
		ButterKnife.bind(this);
		
		presenter.onViewCreated(
			(GameDataApi) getIntent().getSerializableExtra(EXTRA_GAME),
			getIntent().getBooleanExtra(EXTRA_PARTICIPANT_TYPE, false),
			getIntent().getBooleanExtra(EXTRA_ORGANIZER_TYPE, false));
	}
	
	@NonNull
	@Override
	public ConcretGamePresenter createPresenter() {
		return new ConcretGamePresenter(
			this,
			new ConcretGameModel(DaggerSessionComponent.builder().contextModule(new ContextModule(
				this)).build()));
	}
	
	@Override
	public void initToolbar(final String category) {
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
	public void initGameDescription(
		final LinearLayoutManager concretGameDescriptionLayoutManager,
		final ConcretGameDescriptionAdapter concretGameDescriptionAdapter
	) {
		new RecyclerViewBase<ConcretGameDescriptionAdapter>(this).initRecyclerView(
			gameDescriptionRecycler,
			concretGameDescriptionLayoutManager,
			concretGameDescriptionAdapter,
			true,
			null);
	}
	
	@Override
	public void initParticipantsPhones(
		final LinearLayoutManager participantsPhonesLayoutManager,
		final OrganizerConcretGameParticipantsAdapter organizerConcretGameParticipantsAdapter
	) {
		mOrganizerConcretGameParticipantsAdapter = organizerConcretGameParticipantsAdapter;
		new RecyclerViewBase<OrganizerConcretGameParticipantsAdapter>(this).initRecyclerView(
			mParticipantsPhonesRecycler,
			participantsPhonesLayoutManager,
			mOrganizerConcretGameParticipantsAdapter,
			true,
			null);
	}
	
	@Override
	public void setPhoto(final String photoUrl) {
		Picasso.with(this)
			.load(photoUrl)
			.placeholder(R.drawable.image_placeholder)
			.fit()
			.centerCrop()
			.noFade()
			.into(gamePhoto);
	}
	
	@Override
	public void setDescription(final String description) {
		gameDescription.setText(description);
	}
	
	@Override
	public void startNewActivity(final Intent intent) {
		startActivity(intent);
	}
	
	@Override
	public void hideConnectButton() {
		mConnectButton.setVisibility(View.GONE);
	}
	
	@Override
	public void setInGameState() {
		mConnectButton.setText("Вы в игре");
		mConnectButton.setBackgroundColor(Color.parseColor("#FFB032"));
	}
	
	@Override
	public void setNotInGameState() {
		mConnectButton.setText("Присоединиться");
		mConnectButton.setBackgroundColor(Color.parseColor("#FF44ABFF"));
	}
	
	@Override
	public void showProgressBar(final ProgressDialog progressDialog, final String tag) {
		final FragmentManager manager = getSupportFragmentManager();
		final FragmentTransaction transaction = manager.beginTransaction();
		progressDialog.show(transaction, tag);
	}
	
	@Override
	public void hideProgressBar(final ProgressDialog progressDialog) {
		progressDialog.dismiss();
	}
	
	@Override
	public void addParticipant(final UserParticipantData user) {
		mOrganizerConcretGameParticipantsAdapter.addPosition(user);
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.zoom_out);
	}
}

class OnConnectButtonClickListener implements View.OnClickListener {
	
	private final ConcretGamePresenter mConcretGamePresenter;
	
	OnConnectButtonClickListener(final ConcretGamePresenter presenter) {
		mConcretGamePresenter = presenter;
	}
	
	@Override
	public void onClick(final View view) {
		mConcretGamePresenter.onConnectButtonClicked();
	}
}
