package com.example.user.sportify.ui.map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.models.UserParticipantData;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.base.RecyclerViewBase;
import com.example.user.sportify.ui.concretgame.ConcretGameDescriptionAdapter;
import com.example.user.sportify.ui.concretgame.OrganizerConcretGameParticipantsAdapter;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;
import com.squareup.picasso.Picasso;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.runtime.ui_view.ViewProvider;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.sportify.ui.utils.Constants.BASE_UPLOADS_URL;

public class MapFragment extends MvpFragment<MapView, MapPresenter> implements MapView {
	
	@BindView(R.id.map_view)
	com.yandex.mapkit.mapview.MapView mapView;
	
	@BindView(R.id.little_desc)
	ConstraintLayout littleDesc;
	
	@BindView(R.id.bottom_sheet_address)
	TextView bottomSheetAddress;
	
	@BindView(R.id.bottom_sheet_quantity)
	TextView bottomSheetQuantity;
	
	@BindView(R.id.bottom_sheet_title)
	TextView bottomSheetTitle;
	
	@BindView(R.id.bottom_sheet_description)
	TextView bottomSheetDescription;
	
	@BindView(R.id.bottom_sheet_game_image)
	ImageView bottomSheetGameImage;
	
	@BindView(R.id.bottom_sheet_description_recycler)
	RecyclerView bottomSheetDescriptionRecycler;
	
	@BindView(R.id.participants_phones_recycler)
	RecyclerView participantsPhonesRecycler;
	
	@BindView(R.id.participant_map_concret_game)
	ConstraintLayout participantMapConcretGame;
	
	@BindView(R.id.organizer_map_concret_game)
	ConstraintLayout organizerMapConcretGame;
	
	@BindView(R.id.bottom_sheet_connect_button)
	Button connectToGameButton;
	
	@BindView(R.id.bottom_sheet_concret_connect_button)
	Button connectToConcretGameButton;
	
	@BindView(R.id.fab)
	FloatingActionButton fab;
	
	@BindView(R.id.map_progress_bar)
	ProgressBar mapProgressBar;
	
	private List<PlacemarkMapObject> mPlacemarkMapObjectList;
	
	private List<MapObjectTapListener> mObjectTapListenerList;
	
	private List<GameDataApi> mGamesList;
	
	
	private BottomSheetBehavior mBottomSheetBehavior;
	private ConcretGameDescriptionAdapter mConcretGameDescriptionAdapter;
	private OrganizerConcretGameParticipantsAdapter mOrganizerConcretGameParticipantsAdapter;
	
	@NonNull
	@Override
	public MapPresenter createPresenter() {
		return new MapPresenter(
			getActivity(),
			new MapModel(DaggerSessionComponent.builder().contextModule(new ContextModule(
				getActivity())).build()));
	}
	
	@Nullable
	@Override
	public View onCreateView(
		@NonNull final LayoutInflater inflater,
		@Nullable final ViewGroup container,
		@Nullable final Bundle savedInstanceState
	) {
		final View view = inflater.inflate(R.layout.base_map_fragment, null);
		mPlacemarkMapObjectList = new ArrayList<>();
		mObjectTapListenerList = new ArrayList<>();
		mGamesList = new ArrayList<>();
		ButterKnife.bind(this, view);
		MapKitFactory.initialize(Objects.requireNonNull(getActivity()));
		final CoordinatorLayout coordinatorLayout = view.findViewById(R.id.coordinator_layout);
		final View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
		mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		mBottomSheetBehavior.setBottomSheetCallback(new CustomBottomSheetCallback(fab, littleDesc));
		fab.setOnClickListener(new FabClickListener(presenter));
		mapView.getMap().move(
			new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
			new Animation(Animation.Type.SMOOTH, 0),
			null);
		
		return view;
	}
	
	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		presenter.onViewCreated();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mapView.onStart();
		MapKitFactory.getInstance().onStart();
	}
	
	@Override
	public void onStop() {
		mapView.onStop();
		MapKitFactory.getInstance().onStop();
		super.onStop();
	}
	
	@Override
	public void addPoints(
		final ArrayList<ArrayList<String>> coordinates,
		final List<GameDataApi> games,
		final ArrayList<Integer> icons
	) {
		
		//TODO: Большой костыль и нарушение структуры MVP, причина: garbage collector удалял taplistener, проблему решить не смог, поэтому решил хранить все слушатели, а так же объекты в массивах прямо во view
		
		final LayoutInflater ltInflater = getLayoutInflater();
		final View view = ltInflater.inflate(R.layout.map_icon_layout, null, false);
		final ImageView category = view.findViewById(R.id.icon);
		category.setColorFilter(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.colorMenu));
		
		for (int i = 0; i < coordinates.size(); i++) {

			category.setImageDrawable(getActivity().getDrawable(icons.get(i)));
			final ViewProvider viewProvider = new ViewProvider(view);
			final PlacemarkMapObject mark = mapView.getMap().getMapObjects().addPlacemark(
				new Point(
					Double.valueOf(coordinates.get(i).get(0)),
					Double.valueOf(coordinates.get(i).get(1))),
				viewProvider);
			mGamesList.add(games.get(i));
			final MapObjectTapListenerExtended listener = new MapObjectTapListenerExtended(
				presenter,
				games.get(i),
				mark);
			mPlacemarkMapObjectList.add(mark);
			mObjectTapListenerList.add(listener);
			mPlacemarkMapObjectList.get(mPlacemarkMapObjectList.indexOf(mark))
				.addTapListener(mObjectTapListenerList.get(mObjectTapListenerList.indexOf(listener)));
		
		}
	}
	
	@Override
	public void startNewActivity(final Intent intent) {
		startActivity(intent);
	}
	
	@Override
	public void startNewCreateGameActivity(final Intent intent) {
		startActivity(intent);
		Objects.requireNonNull(getActivity()).overridePendingTransition(
			R.anim.slide_up,
			R.anim.save_place);
	}
	
	@Override
	public void setActiveIcon(final PlacemarkMapObject mark, final int categoryIcon) {
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		final LayoutInflater ltInflater = getLayoutInflater();
		final View view1 = ltInflater.inflate(R.layout.map_icon_layout, null, false);
		final ImageView category1 = view1.findViewById(R.id.icon);
		category1.setColorFilter(Color.WHITE);
		category1.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.rounded_map_icon_active));
		category1.setImageDrawable(getActivity().getDrawable(categoryIcon));
		final ViewProvider viewProvider = new ViewProvider(view1);
		mPlacemarkMapObjectList.get(mPlacemarkMapObjectList.indexOf(mark)).setView(viewProvider);
	}
	
	@Override
	public void setInActiveIcon(final PlacemarkMapObject mark, final int categoryIcon) {
		final LayoutInflater ltInflater = getLayoutInflater();
		final View view1 = ltInflater.inflate(R.layout.map_icon_layout, null, false);
		final ImageView category1 = view1.findViewById(R.id.icon);
		category1.setColorFilter(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.colorMenu));
		category1.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.rounded_map_icon_inactive));
		category1.setImageDrawable(getActivity().getDrawable(categoryIcon));
		
		final ViewProvider viewProvider = new ViewProvider(view1);
		Log.e("mark", String.valueOf(mPlacemarkMapObjectList.indexOf(mark)));
		mPlacemarkMapObjectList.get(mPlacemarkMapObjectList.indexOf(mark)).setView(viewProvider);
		
	}
	
	@Override
	public void initGameDescription(
		final LinearLayoutManager concretGameDescriptionLayoutManager,
		final ConcretGameDescriptionAdapter concretGameDescriptionAdapter
	) {
		mConcretGameDescriptionAdapter = concretGameDescriptionAdapter;
		new RecyclerViewBase<ConcretGameDescriptionAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(
			bottomSheetDescriptionRecycler,
			concretGameDescriptionLayoutManager,
			mConcretGameDescriptionAdapter,
			true,
			null);
	}
	
	
	@Override
	public void setBaseInfo(final GameDataApi game, final String categoryName, final String date) {
		final String quantity = game.getCurrentPeopleQuantity() + "/" + game.getMaxPeopleQuantity();
		final String title = categoryName + " " + date;
		bottomSheetTitle.setText(title);
		bottomSheetQuantity.setText(quantity);
		bottomSheetAddress.setText(game.getLocation());
		getImageWithPicasso((BASE_UPLOADS_URL + game.getLocationPhotoUrl()), bottomSheetGameImage);
		bottomSheetDescription.setText(game.getDescription());
		connectToGameButton.setOnClickListener(new ConnectButtonOnClickListener(presenter, game));
		connectToConcretGameButton.setOnClickListener(new ConnectButtonOnClickListener(
			presenter,
			game));
	}
	
	@Override
	public void setGameInfo() {
	
	}
	
	@Override
	public void initOrganizerGameDescription(
		final LinearLayoutManager concretGameDescriptionLayoutManager,
		final ConcretGameDescriptionAdapter concretGameDescriptionAdapter
	) {
		mConcretGameDescriptionAdapter = concretGameDescriptionAdapter;
		new RecyclerViewBase<ConcretGameDescriptionAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(
			bottomSheetDescriptionRecycler,
			concretGameDescriptionLayoutManager,
			mConcretGameDescriptionAdapter,
			true,
			null);
	}
	
	@Override
	public void initParticipantsPhones(
		final LinearLayoutManager participantsPhonesLayoutManager,
		final OrganizerConcretGameParticipantsAdapter organizerConcretGameParticipantsAdapter
	) {
		mOrganizerConcretGameParticipantsAdapter = organizerConcretGameParticipantsAdapter;
		new RecyclerViewBase<OrganizerConcretGameParticipantsAdapter>(Objects.requireNonNull(
			getActivity())).initRecyclerView(
			participantsPhonesRecycler,
			participantsPhonesLayoutManager,
			mOrganizerConcretGameParticipantsAdapter,
			true,
			null);
	}
	
	@Override
	public void setInGameState() {
		connectToGameButton.setText("Вы в игре");
		connectToGameButton.setBackgroundColor(getResources().getColor(R.color.colorMapParticipantButton));
		connectToGameButton.setTextColor(getResources().getColor(R.color.colorMapParticipantText));
		connectToConcretGameButton.setText("Вы в игре");
		connectToConcretGameButton.setBackgroundColor(Color.parseColor("#FFB032"));
		connectToConcretGameButton.setTextColor(Color.parseColor("#ffffff"));
	}
	
	@Override
	public void setNotInGameState() {
		connectToGameButton.setText("Присоединиться");
		connectToGameButton.setTextColor(getResources().getColor(R.color.colorMapNotParticipantText));
		connectToGameButton.setBackgroundColor(getResources().getColor(R.color.colorMapNotParticipantButton));
		connectToConcretGameButton.setText("Присоединиться");
		connectToConcretGameButton.setBackgroundColor(Color.parseColor("#FF44ABFF"));
		connectToConcretGameButton.setTextColor(Color.parseColor("#ffffff"));
	}
	
	@Override
	public void hideParticipantButton() {
		connectToGameButton.setVisibility(View.GONE);
	}
	
	@Override
	public void showParticipantButton() {
		connectToGameButton.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void hideProgressBar() {
		mapProgressBar.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void showProgressBar() {
		mapProgressBar.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void showRegistrationSnackBar(final String message) {
		final CoordinatorLayout coordinatorLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.coordinator_layout);
		final TSnackbar snackbar = TSnackbar.make(coordinatorLayout, message, TSnackbar.LENGTH_LONG);
		final View snackbarView = snackbar.getView();
		final TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
		textView.setTextColor(getResources().getColor(R.color.colorPrimary));
		snackbar.show();
	}
	
	@Override
	public void showMapView() {
		mapView.setVisibility(View.VISIBLE);
	}
	
	
	@Override
	public void initOrganizerGame() {
		participantMapConcretGame.setVisibility(View.GONE);
		organizerMapConcretGame.setVisibility(View.VISIBLE);
		bottomSheetDescription = organizerMapConcretGame.findViewById(R.id.bottom_sheet_description);
		bottomSheetGameImage = organizerMapConcretGame.findViewById(R.id.bottom_sheet_game_image);
		bottomSheetDescriptionRecycler = organizerMapConcretGame.findViewById(R.id.bottom_sheet_description_recycler);
		participantsPhonesRecycler = organizerMapConcretGame.findViewById(R.id.participants_phones_recycler);
	}
	
	@Override
	public void showBottomSlider() {
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}
	
	@Override
	public void initGame() {
		organizerMapConcretGame.setVisibility(View.GONE);
		participantMapConcretGame.setVisibility(View.VISIBLE);
		bottomSheetDescription = participantMapConcretGame.findViewById(R.id.bottom_sheet_description);
		bottomSheetGameImage = participantMapConcretGame.findViewById(R.id.bottom_sheet_game_image);
		bottomSheetDescriptionRecycler = participantMapConcretGame.findViewById(R.id.bottom_sheet_description_recycler);
	}
	
	@Override
	public void addParticipant(final UserParticipantData user) {
		mOrganizerConcretGameParticipantsAdapter.addPosotion(user);
	}
	
	private void getImageWithPicasso(final String photoUrl, final ImageView imageView) {
		Picasso.with(getActivity())
			.load(photoUrl)
			.placeholder(R.drawable.image_placeholder)
			.fit()
			.centerCrop()
			.noFade()
			.into(imageView);
	}
	
}

class MapObjectTapListenerExtended implements MapObjectTapListener {
	
	private final GameDataApi mGame;
	private final MapPresenter mMapPresenter;
	private final PlacemarkMapObject mPlacemarkMapObject;
	
	
	MapObjectTapListenerExtended(
		final MapPresenter presenter,
		final GameDataApi game,
		final PlacemarkMapObject mark
	) {
		mGame = game;
		mMapPresenter = presenter;
		mPlacemarkMapObject = mark;
	}
	
	@Override
	public boolean onMapObjectTap(@NonNull final MapObject mapObject, @NonNull final Point point) {
		
		mMapPresenter.onMapObjectTap(mGame, mPlacemarkMapObject);
		return false;
	}
}

class FabClickListener implements View.OnClickListener {
	
	private final MapPresenter mMapPresenter;
	
	FabClickListener(final MapPresenter presenter) {
		mMapPresenter = presenter;
	}
	
	@Override
	public void onClick(final View view) {
		mMapPresenter.onFabClicked();
	}
}


class CustomBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {
	
	private final ConstraintLayout mLittleDesc;
	private final FloatingActionButton mFab;
	
	CustomBottomSheetCallback(final FloatingActionButton fab, final ConstraintLayout littleDesc) {
		mFab = fab;
		mLittleDesc = littleDesc;
	}
	
	@Override
	public void onStateChanged(@NonNull final View view, final int i) {
	
	}
	
	@Override
	public void onSlide(@NonNull final View view, final float v) {
		Log.e("", String.valueOf(v));
		if (v >= 0) {
			mFab.hide();
		} else {
			mFab.show();
		}
		
		if (v >= 0.8f) {
			mLittleDesc.setVisibility(View.GONE);
		} else {
			mLittleDesc.setVisibility(View.VISIBLE);
		}
	}
}


class ConnectButtonOnClickListener implements View.OnClickListener {
	
	private final MapPresenter mMapPresenter;
	private final GameDataApi mGame;
	
	ConnectButtonOnClickListener(final MapPresenter presenter, final GameDataApi game) {
		mMapPresenter = presenter;
		mGame = game;
	}
	
	@Override
	public void onClick(final View view) {
		mMapPresenter.onConnectButtonClicked(mGame);
	}
}