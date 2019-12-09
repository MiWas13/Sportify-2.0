package com.example.user.sportify.ui.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.user.sportify.ui.games.ConnectButtonClickListener;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;
import com.squareup.picasso.Picasso;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.Callback;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.ColoredPolylineMapObject;
import com.yandex.mapkit.map.ConflictResolvingMode;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectCollectionListener;
import com.yandex.mapkit.map.MapObjectDragListener;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.MapObjectVisitor;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PlacemarksStyler;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.runtime.image.AnimatedImageProvider;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.ui_view.ViewProvider;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.LAYER_TYPE_SOFTWARE;
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

    private List<PlacemarkMapObject> placemarkMapObjectList;

    private List<MapObjectTapListener> objectTapListenerList;

    private List<GameDataApi> games;


    private BottomSheetBehavior bottomSheetBehavior;
    private ConcretGameDescriptionAdapter concretGameDescriptionAdapter;
    private OrganizerConcretGameParticipantsAdapter organizerConcretGameParticipantsAdapter;

    @NonNull
    @Override
    public MapPresenter createPresenter() {
        return new MapPresenter(getActivity(), new MapModel(DaggerSessionComponent.builder().contextModule(new ContextModule(getActivity())).build()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_map_fragment, null);
        placemarkMapObjectList = new ArrayList<>();
        objectTapListenerList = new ArrayList<>();
        games = new ArrayList<>();
        ButterKnife.bind(this, view);
        MapKitFactory.initialize(Objects.requireNonNull(getActivity()));
        CoordinatorLayout coordinatorLayout = view.findViewById(R.id.coordinator_layout);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new CustomBottomSheetCallback(fab, littleDesc));
        fab.setOnClickListener(new FabClickListener(presenter));
        mapView.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
    public void addPoints(ArrayList<ArrayList<String>> coordinates, List<GameDataApi> games, ArrayList<Integer> icons) {

        //TODO: Большой костыль и нарушение структуры MVP, причина: garbage collector удалял taplistener, проблему решить не смог, поэтому решил хранить все слушатели, а так же объекты в массивах прямо во view

        LayoutInflater ltInflater = getLayoutInflater();
        View view = ltInflater.inflate(R.layout.map_icon_layout, null, false);
        ImageView category = view.findViewById(R.id.icon);
        category.setColorFilter(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.colorMenu));

        for (int i = 0; i < coordinates.size(); i++) {

//            if (!this.games.contains(games.get(i))) {
            category.setImageDrawable(getActivity().getDrawable(icons.get(i)));
            ViewProvider viewProvider = new ViewProvider(view);
            PlacemarkMapObject mark = mapView.getMap().getMapObjects().addPlacemark(new Point(Double.valueOf(coordinates.get(i).get(0)), Double.valueOf(coordinates.get(i).get(1))), viewProvider);
            this.games.add(games.get(i));
            MapObjectTapListenerExtended listener = new MapObjectTapListenerExtended(presenter, games.get(i), mark);
            placemarkMapObjectList.add(mark);
            objectTapListenerList.add(listener);
            placemarkMapObjectList.get(placemarkMapObjectList.indexOf(mark)).addTapListener(objectTapListenerList.get(objectTapListenerList.indexOf(listener)));
//            }

        }
    }

    @Override
    public void startNewActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void startNewCreateGameActivity(Intent intent) {
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_up, R.anim.save_place);
    }

    @Override
    public void setActiveIcon(PlacemarkMapObject mark, int categoryIcon) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        LayoutInflater ltInflater = getLayoutInflater();
        View view1 = ltInflater.inflate(R.layout.map_icon_layout, null, false);
        ImageView category1 = view1.findViewById(R.id.icon);
        category1.setColorFilter(Color.WHITE);
        category1.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.rounded_map_icon_active));
        category1.setImageDrawable(getActivity().getDrawable(categoryIcon));
        ViewProvider viewProvider = new ViewProvider(view1);
        placemarkMapObjectList.get(placemarkMapObjectList.indexOf(mark)).setView(viewProvider);
    }

    @Override
    public void setInActiveIcon(PlacemarkMapObject mark, int categoryIcon) {
        LayoutInflater ltInflater = getLayoutInflater();
        View view1 = ltInflater.inflate(R.layout.map_icon_layout, null, false);
        ImageView category1 = view1.findViewById(R.id.icon);
        category1.setColorFilter(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.colorMenu));
        category1.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.rounded_map_icon_inactive));
        category1.setImageDrawable(getActivity().getDrawable(categoryIcon));

        ViewProvider viewProvider = new ViewProvider(view1);
        Log.e("mark", String.valueOf(placemarkMapObjectList.indexOf(mark)));
        placemarkMapObjectList.get(placemarkMapObjectList.indexOf(mark)).setView(viewProvider);

    }

    @Override
    public void initGameDescription(LinearLayoutManager concretGameDescriptionLayoutManager, ConcretGameDescriptionAdapter concretGameDescriptionAdapter) {
        this.concretGameDescriptionAdapter = concretGameDescriptionAdapter;
        new RecyclerViewBase<ConcretGameDescriptionAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(bottomSheetDescriptionRecycler, concretGameDescriptionLayoutManager, this.concretGameDescriptionAdapter, true, null);
    }


    @Override
    public void setBaseInfo(GameDataApi game, String categoryName, String date) {
        String quantity = game.getCurrentPeopleQuantity() + "/" + game.getMaxPeopleQuantity();
        String title = categoryName + " " + date;
        bottomSheetTitle.setText(title);
        bottomSheetQuantity.setText(quantity);
        bottomSheetAddress.setText(game.getLocation());
        getImageWithPicasso((BASE_UPLOADS_URL + game.getLocationPhotoUrl()), bottomSheetGameImage);
        bottomSheetDescription.setText(game.getDescription());
        connectToGameButton.setOnClickListener(new ConnectButtonOnClickListener(presenter, game));
        connectToConcretGameButton.setOnClickListener(new ConnectButtonOnClickListener(presenter, game));
    }

    @Override
    public void setGameInfo() {

    }

    @Override
    public void initOrganizerGameDescription(LinearLayoutManager concretGameDescriptionLayoutManager, ConcretGameDescriptionAdapter concretGameDescriptionAdapter) {
        this.concretGameDescriptionAdapter = concretGameDescriptionAdapter;
        new RecyclerViewBase<ConcretGameDescriptionAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(bottomSheetDescriptionRecycler, concretGameDescriptionLayoutManager, this.concretGameDescriptionAdapter, true, null);
    }

    @Override
    public void initParticipantsPhones(LinearLayoutManager participantsPhonesLayoutManager, OrganizerConcretGameParticipantsAdapter organizerConcretGameParticipantsAdapter) {
        this.organizerConcretGameParticipantsAdapter = organizerConcretGameParticipantsAdapter;
        new RecyclerViewBase<OrganizerConcretGameParticipantsAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(participantsPhonesRecycler, participantsPhonesLayoutManager, this.organizerConcretGameParticipantsAdapter, true, null);
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
    public void showRegistrationSnackBar(String message) {
        CoordinatorLayout coordinatorLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.coordinator_layout);
        TSnackbar snackbar = TSnackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
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
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
    public void addParticipant(UserParticipantData user) {
        organizerConcretGameParticipantsAdapter.addPosotion(user);
    }

    private void getImageWithPicasso(String photoUrl, ImageView imageView) {
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

    private GameDataApi game;
    private MapPresenter presenter;
    private PlacemarkMapObject mark;


    MapObjectTapListenerExtended(MapPresenter presenter, GameDataApi game, PlacemarkMapObject mark) {
        this.game = game;
        this.presenter = presenter;
        this.mark = mark;
    }

    @Override
    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {

        presenter.onMapObjectTap(game, mark);
        return false;
    }
}

class FabClickListener implements View.OnClickListener {

    private MapPresenter presenter;

    FabClickListener(MapPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        presenter.onFabClicked();
    }
}


class CustomBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

    private ConstraintLayout littleDesc;
    private FloatingActionButton fab;

    CustomBottomSheetCallback(FloatingActionButton fab, ConstraintLayout littleDesc) {
        this.fab = fab;
        this.littleDesc = littleDesc;
    }

    @Override
    public void onStateChanged(@NonNull View view, int i) {

    }

    @Override
    public void onSlide(@NonNull View view, float v) {
        Log.e("", String.valueOf(v));
        if (v >= 0)
            fab.hide();
        else fab.show();

        if (v >= 0.8f)
            littleDesc.setVisibility(View.GONE);
        else littleDesc.setVisibility(View.VISIBLE);
    }
}


class ConnectButtonOnClickListener implements View.OnClickListener {

    private MapPresenter presenter;
    private GameDataApi game;

    ConnectButtonOnClickListener(MapPresenter presenter, GameDataApi game) {
        this.presenter = presenter;
        this.game = game;
    }

    @Override
    public void onClick(View view) {
        presenter.onConnectButtonClicked(game);
    }
}