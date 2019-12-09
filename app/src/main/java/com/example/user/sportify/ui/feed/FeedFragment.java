package com.example.user.sportify.ui.feed;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.base.PaginationScrollListener;
import com.example.user.sportify.ui.base.RecyclerViewBase;
import com.example.user.sportify.ui.feed.decoration.MarginGamesItemDecoration;
import com.example.user.sportify.ui.feed.decoration.MarginItemDecoration;
import com.example.user.sportify.ui.games.GamesAdapter;
import com.example.user.sportify.ui.registration.dialog.AlertRegistrationDialog;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.example.user.sportify.ui.registration.full.RegistrationModel;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedFragment extends MvpFragment<FeedView, FeedPresenter> implements FeedView {
	
	private AlertRegistrationDialog alertRegistrationDialog;
	private CategoriesAdapter categoriesAdapter;
	private GamesAdapter gamesAdapter;
	private List<GameDataApi> games;
	
	
	@BindView(R.id.games_recycler_view)
	RecyclerView gamesRecyclerView;
	
	@BindView(R.id.categories_recycler_view)
	RecyclerView categoriesRecyclerView;
	
	@BindView(R.id.fab)
	FloatingActionButton floatingActionButton;
	
	@BindView(R.id.progress_games)
	ProgressBar progressBar;
	
	private LinearLayoutManager gamesLayoutManager;
	
	
	@NonNull
	@Override
	public FeedPresenter createPresenter() {
		return new FeedPresenter(
			getActivity(),
			new FeedModel(DaggerSessionComponent.builder().contextModule(new ContextModule(
				getActivity())).build()),
			new RegistrationModel(DaggerSessionComponent.builder().contextModule(new ContextModule(
				getActivity())).build()));
	}
	
	@Nullable
	@Override
	public View onCreateView(
		@NonNull LayoutInflater inflater,
		@Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState
	) {
		View rootView = inflater.inflate(R.layout.games_fragment, null);
		ButterKnife.bind(this, rootView);
		floatingActionButton.setOnClickListener(new FabOnClickListener(presenter));
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		presenter.onViewCreated();
	}
	
	@Override
	public void loadFirstPage(List<GameDataApi> results) {
		games = results;
		if (results != null) {
			gamesAdapter.addAll(results);
			gamesRecyclerView.setAdapter(gamesAdapter);
			gamesAdapter.notifyDataSetChanged();
		}
		
		progressBar.setVisibility(View.GONE);
	}
	
	@Override
	public void loadNextPage(List<GameDataApi> results) {
		if (results != null) {
			games.addAll(results);
			gamesAdapter.addAll(results);
			gamesAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void initCategories(
		LinearLayoutManager categoriesLayoutManager,
		CategoriesAdapter categoriesAdapter
	) {
		this.categoriesAdapter = categoriesAdapter;
		new RecyclerViewBase<CategoriesAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(
			categoriesRecyclerView,
			categoriesLayoutManager,
			this.categoriesAdapter,
			true,
			new MarginItemDecoration());
	}
	
	@Override
	public void initGames(LinearLayoutManager gamesLayoutManager, GamesAdapter gamesAdapter) {
		this.gamesAdapter = gamesAdapter;
		this.gamesLayoutManager = gamesLayoutManager;
		new RecyclerViewBase<GamesAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(
			gamesRecyclerView,
			gamesLayoutManager,
			this.gamesAdapter,
			true,
			new MarginGamesItemDecoration());
		presenter.onGamesInited();
	}
	
	
	@Override
	public void changeSelectedCategory(int position) {
		categoriesAdapter.changeSelectedItem(position);
	}
	
	@Override
	public void changeParticipantState(int position, int gameId, String currentPeopleQuantity) {
		gamesAdapter.changeParticipateButtonState(position, gameId);
		gamesAdapter.updatePeopleQuantity(position, currentPeopleQuantity);
	}
	
	@Override
	public void cancelGame(int position, int gameId) {
		gamesAdapter.cancelGame(position);
	}
	
	@Override
	public void showProgressBar(ProgressDialog progressDialog, String tag) {
		FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		progressDialog.show(transaction, tag);
	}
	
	@Override
	public void hideProgressBar(ProgressDialog progressDialog) {
		progressDialog.dismiss();
	}
	
	@Override
	public void showProgressBar() {
		progressBar.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void hideProgressBar() {
		progressBar.setVisibility(View.GONE);
	}
	
	@Override
	public void addLoading() {
		gamesAdapter.addLoading();
	}
	
	@Override
	public void hideLoading() {
		gamesAdapter.hideLoading();
	}
	
	@Override
	public void startNewConcretGameActivity(Intent intent) {
		startActivity(intent);
		Objects.requireNonNull(getActivity()).overridePendingTransition(
			R.anim.zoom_in,
			R.anim.save_place);
	}
	
	@Override
	public void startNewCreateGameActivity(Intent intent) {
		startActivity(intent);
		Objects.requireNonNull(getActivity()).overridePendingTransition(
			R.anim.slide_up,
			R.anim.save_place);
	}
	
	@Override
	public void filterByCategory(List<GameDataApi> results, int position) {
		gamesAdapter.addAll(results);
		gamesRecyclerView.setAdapter(gamesAdapter);
		progressBar.setVisibility(View.GONE);
	}
	
	public void initGamesScrollListener() {
		gamesRecyclerView.addOnScrollListener(new FeedPaginationScrollListener(
			gamesLayoutManager,
			floatingActionButton,
			presenter));
		
	}
	
	@Override
	public void clearGames() {
		gamesAdapter.clear();
		gamesAdapter.notifyDataSetChanged();
		gamesRecyclerView.setAdapter(gamesAdapter);
	}
	
	
	@Override
	public void showFirstRegDialog() {
		alertRegistrationDialog = new AlertRegistrationDialog(
			Objects.requireNonNull(getActivity()),
			presenter);
		alertRegistrationDialog.showFirstDialog();
	}
	
	@Override
	public void firstStepComplete() {
		alertRegistrationDialog.finishFirstDialog();
		alertRegistrationDialog.showSecondDialog();
	}
	
	@Override
	public void secondStepComplete(Intent intent) {
		alertRegistrationDialog.finishSecondDialog();
		startActivity(intent);
	}
	
	@Override
	public void setPasswordError() {
		alertRegistrationDialog.setPasswordError();
	}
	
	@Override
	public void setNameError() {
		alertRegistrationDialog.setNameError();
	}
	
	@Override
	public void setPhoneError() {
		alertRegistrationDialog.setPhoneError();
	}
	
	@Override
	public void showMaxQuantitySnackBar() {
		Snackbar.make(
			Objects.requireNonNull(getView()),
			"В этой игре уже слишком много человек :(",
			Snackbar.LENGTH_LONG).show();
	}
	
	@Override
	public void showAuthError(String message) {
		CoordinatorLayout coordinatorLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.games_feed_layout);
		TSnackbar snackbar = TSnackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
		View snackbarView = snackbar.getView();
		TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
		textView.setTextColor(getResources().getColor(R.color.colorPrimary));
		snackbar.show();
	}
}


class FabOnClickListener implements View.OnClickListener {
	
	private FeedPresenter presenter;
	
	@Override
	public void onClick(View view) {
		presenter.onFabClicked();
	}
	
	FabOnClickListener(FeedPresenter presenter) {
		this.presenter = presenter;
		
	}
}

class FeedPaginationScrollListener extends PaginationScrollListener {
	
	private FloatingActionButton floatingActionButton;
	private FeedPresenter presenter;
	
	FeedPaginationScrollListener(
		LinearLayoutManager layoutManager,
		FloatingActionButton floatingActionButton,
		FeedPresenter presenter
	) {
		super(layoutManager);
		this.floatingActionButton = floatingActionButton;
		this.presenter = presenter;
	}
	
	@Override
	public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
		super.onScrolled(recyclerView, dx, dy);
		if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
			floatingActionButton.hide();
		} else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
			floatingActionButton.show();
		}
	}
	
	@Override
	protected void loadMoreItems() {
		presenter.onEndOfPageScrolled();
	}
	
	@Override
	public int getTotalPageCount() {
		return presenter.getTotalCount();
	}
	
	@Override
	public boolean isLastPage() {
		return presenter.getIsLastPage();
	}
	
	@Override
	public boolean isLoading() {
		return presenter.getIsLoadind();
	}
}

