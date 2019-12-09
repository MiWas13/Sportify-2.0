package com.example.user.sportify.ui.mygames;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.example.user.sportify.network.session.ContextModule;
import com.example.user.sportify.network.session.DaggerSessionComponent;
import com.example.user.sportify.ui.base.RecyclerViewBase;
import com.example.user.sportify.ui.games.GamesAdapter;
import com.example.user.sportify.ui.mygames.decoration.MarginGamesItemDecoration;
import com.example.user.sportify.ui.registration.dialog.ProgressDialog;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.user.sportify.ui.utils.Constants.EXTRA_MY_GAMES_TYPE;

public class MyGamesFragment extends MvpFragment<MyGamesView, MyGamesPresenter> implements MyGamesView {

    @BindView(R.id.my_games_recycler)
    RecyclerView gamesRecyclerView;

    @BindView(R.id.progress_my_games)
    ProgressBar progressBar;

    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;

    private GamesAdapter gamesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.base_recycler_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            presenter.onViewCreated(bundle.getInt(EXTRA_MY_GAMES_TYPE, 0));
        }
    }

    @NonNull
    @Override
    public MyGamesPresenter createPresenter() {
        return new MyGamesPresenter(getActivity(), new MyGamesModel(DaggerSessionComponent.builder().contextModule(new ContextModule(getActivity())).build()));
    }

    @Override
    public void initGamesRecyclerView(LinearLayoutManager gamesLayoutManager, GamesAdapter gamesAdapter) {
        this.gamesAdapter = gamesAdapter;
        new RecyclerViewBase<GamesAdapter>(Objects.requireNonNull(getActivity())).initRecyclerView(gamesRecyclerView, gamesLayoutManager, gamesAdapter, true, new MarginGamesItemDecoration());
    }

    @Override
    public void setGamesData(List<GameDataApi> games) {
        gamesAdapter.addAll(games);
        gamesAdapter.notifyDataSetChanged();
    }


    @Override
    public void clearList() {
        gamesAdapter.clear();
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
    public void unParticipate(int position) {
        gamesAdapter.deleteItem(position);
    }

    @Override
    public void startNewActivity(Intent intent) {
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.zoom_in, R.anim.save_place);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setEmptyState() {
        progressBar.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }
}
