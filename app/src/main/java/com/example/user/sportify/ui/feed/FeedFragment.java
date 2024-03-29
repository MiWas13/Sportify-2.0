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
	
	private AlertRegistrationDialog mAlertRegistrationDialog;
	private CategoriesAdapter mCategoriesAdapter;
	private GamesAdapter mGamesAdapter;
	private List<GameDataApi> mGamesList;
	
	@BindView(R.id.games_recycler_view)
	RecyclerView gamesRecyclerView;
	
	@BindView(R.id.categories_recycler_view)
	RecyclerView categoriesRecyclerView;
	
	@BindView(R.id.fab)
	FloatingActionButton floatingActionButton;
	
	@BindView(R.id.progress_games)
	ProgressBar progressBar;
	
	private LinearLayoutManager mGamesLayoutManager;
	
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
		@NonNull final LayoutInflater inflater,
		@Nullable final ViewGroup container,
		@Nullable final Bundle savedInstanceState
	) {
		final View rootView = inflater.inflate(R.layout.games_fragment, null);
		ButterKnife.bind(this, rootView);
		floatingActionButton.setOnClickListener(new FabOnClickListener(presenter));
		return rootView;
	}
	
	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		presenter.onViewCreated();
	}
	
	@Override
	public void loadFirstPage(final List<GameDataApi> results) {
		mGamesList = results;
		if (results != null) {
			mGamesAdapter.addAll(results);
			gamesRecyclerView.setAdapter(mGamesAdapter);
			mGamesAdapter.notifyDataSetChanged();
		}
		
		progressBar.setVisibility(View.GONE);
	}
	
	@Override
	public void loadNextPage(final List<GameDataApi> results) {
		if (results != null) {
			mGamesList.addAll(results);
			mGamesAdapter.addAll(results);
			mGamesAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void initCategories(
		final LinearLayoutManager categoriesLayoutManager,
		final CategoriesAdapter categoriesAdapter
	) {
		this.mCategoriesAdapter = categoriesAdapter;
		new RecyclerViewBase<CategoriesAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(
			categoriesRecyclerView,
			categoriesLayoutManager,
			this.mCategoriesAdapter,
			true,
			new MarginItemDecoration());
	}
	
	@Override
	public void initGames(
		final LinearLayoutManager gamesLayoutManager,
		final GamesAdapter gamesAdapter
	) {
		mGamesAdapter = gamesAdapter;
		mGamesLayoutManager = gamesLayoutManager;
		new RecyclerViewBase<GamesAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(
			gamesRecyclerView,
			gamesLayoutManager,
			mGamesAdapter,
			true,
			new MarginGamesItemDecoration());
		presenter.onGamesInited();
	}
	
	
	@Override
	public void changeSelectedCategory(final int position) {
		mCategoriesAdapter.changeSelectedItem(position);
	}
	
	@Override
	public void changeParticipantState(
		final int position,
		final int gameId,
		final String currentPeopleQuantity
	) {
		mGamesAdapter.changeParticipateButtonState(position, gameId);
		mGamesAdapter.updatePeopleQuantity(position, currentPeopleQuantity);
	}
	
	@Override
	public void cancelGame(final int position, final int gameId) {
		mGamesAdapter.cancelGame(position);
	}
	
	@Override
	public void showProgressBar(final ProgressDialog progressDialog, final String tag) {
		final FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
		final FragmentTransaction transaction = manager.beginTransaction();
		progressDialog.show(transaction, tag);
	}
	
	@Override
	public void hideProgressBar(final ProgressDialog progressDialog) {
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
		mGamesAdapter.addLoading();
	}
	
	@Override
	public void hideLoading() {
		mGamesAdapter.hideLoading();
	}
	
	@Override
	public void startNewConcretGameActivity(final Intent intent) {
		startActivity(intent);
		Objects.requireNonNull(getActivity()).overridePendingTransition(
			R.anim.zoom_in,
			R.anim.save_place);
	}
	
	@Override
	public void startNewCreateGameActivity(final Intent intent) {
		startActivity(intent);
		Objects.requireNonNull(getActivity()).overridePendingTransition(
			R.anim.slide_up,
			R.anim.save_place);
	}
	
	@Override
	public void filterByCategory(final List<GameDataApi> results, final int position) {
		mGamesAdapter.addAll(results);
		gamesRecyclerView.setAdapter(mGamesAdapter);
		progressBar.setVisibility(View.GONE);
	}
	
	public void initGamesScrollListener() {
		gamesRecyclerView.addOnScrollListener(new FeedPaginationScrollListener(
			mGamesLayoutManager,
			floatingActionButton,
			presenter));
		
	}
	
	@Override
	public void clearGames() {
		mGamesAdapter.clear();
		mGamesAdapter.notifyDataSetChanged();
		gamesRecyclerView.setAdapter(mGamesAdapter);
	}
	
	
	@Override
	public void showFirstRegDialog() {
		mAlertRegistrationDialog = new AlertRegistrationDialog(
			Objects.requireNonNull(getActivity()),
			presenter);
		mAlertRegistrationDialog.showFirstDialog();
	}
	
	@Override
	public void firstStepComplete() {
		mAlertRegistrationDialog.finishFirstDialog();
		mAlertRegistrationDialog.showSecondDialog();
	}
	
	@Override
	public void secondStepComplete(final Intent intent) {
		mAlertRegistrationDialog.finishSecondDialog();
		startActivity(intent);
	}
	
	@Override
	public void setPasswordError() {
		mAlertRegistrationDialog.setPasswordError();
	}
	
	@Override
	public void setNameError() {
		mAlertRegistrationDialog.setNameError();
	}
	
	@Override
	public void setPhoneError() {
		mAlertRegistrationDialog.setPhoneError();
	}
	
	@Override
	public void showMaxQuantitySnackBar() {
		Snackbar.make(
			Objects.requireNonNull(getView()),
			"В этой игре уже слишком много человек :(",
			Snackbar.LENGTH_LONG).show();
	}
	
	@Override
	public void showAuthError(final String message) {
		final CoordinatorLayout coordinatorLayout = Objects.requireNonNull(getActivity()).findViewById(
			R.id.games_feed_layout);
		final TSnackbar snackbar = TSnackbar.make(
			coordinatorLayout,
			message,
			TSnackbar.LENGTH_LONG);
		final View snackbarView = snackbar.getView();
		final TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
		textView.setTextColor(getResources().getColor(R.color.colorPrimary));
		snackbar.show();
	}
}


class FabOnClickListener implements View.OnClickListener {
	
	private final FeedPresenter mFeedPresenter;
	
	@Override
	public void onClick(final View view) {
		mFeedPresenter.onFabClicked();
	}
	
	FabOnClickListener(final FeedPresenter presenter) {
		mFeedPresenter = presenter;
	}
}

class FeedPaginationScrollListener extends PaginationScrollListener {
	
	private final FloatingActionButton mFloatingActionButton;
	private final FeedPresenter mFeedPresenter;
	
	FeedPaginationScrollListener(
		final LinearLayoutManager layoutManager,
		final FloatingActionButton floatingActionButton,
		final FeedPresenter presenter
	) {
		super(layoutManager);
		mFloatingActionButton = floatingActionButton;
		mFeedPresenter = presenter;
	}
	
	@Override
	public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
		super.onScrolled(recyclerView, dx, dy);
		if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
			mFloatingActionButton.hide();
		} else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
			mFloatingActionButton.show();
		}
	}
	
	@Override
	protected void loadMoreItems() {
		mFeedPresenter.onEndOfPageScrolled();
	}
	
	@Override
	public int getTotalPageCount() {
		return mFeedPresenter.getTotalCount();
	}
	
	@Override
	public boolean isLastPage() {
		return mFeedPresenter.getIsLastPage();
	}
	
	@Override
	public boolean isLoading() {
		return mFeedPresenter.getIsLoadind();
	}
}

