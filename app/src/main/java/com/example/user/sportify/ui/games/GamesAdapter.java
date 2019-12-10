package com.example.user.sportify.ui.games;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.user.sportify.ui.utils.Constants.BASE_UPLOADS_URL;
import static com.example.user.sportify.ui.utils.Constants.GAME_BASE_VIEWHOLDER;
import static com.example.user.sportify.ui.utils.Constants.GAME_LOADING_VIEWHOLDER;
import static com.example.user.sportify.ui.utils.Constants.GAME_ORGANIZER_VIEWHOLDER;

public class GamesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	
	private List<GameDataApi> mGames;
	private final GamesRecyclerViewClickListener mListener;
	private final ConnectToGameListener mConnectToGameListener;
	private final String mUserId;
	private boolean mIsLoadingAdded = false;
	private final Context mContext;
	private final List<Integer> mGamesIdsParticipantArray;
	private final DeleteGameButtonClickListener mDeleteGameButtonClickListener;
	private final UpdateGameButtonClickListener mUpdateGameButtonClickListener;
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(
		@NonNull final ViewGroup viewGroup,
		final int viewType
	) {
		final Context context = viewGroup.getContext();
		final int layoutIdForListItem;
		final LayoutInflater inflater;
		final View view;
		if (viewType == GAME_ORGANIZER_VIEWHOLDER) {
			layoutIdForListItem = R.layout.my_games_organizer_item_layout;
			inflater = LayoutInflater.from(context);
			view = inflater.inflate(layoutIdForListItem, viewGroup, false);
			return new GamesOrganizerViewHolder(view, mListener);
		} else if (viewType == GAME_BASE_VIEWHOLDER) {
			layoutIdForListItem = R.layout.games_item_layout;
			inflater = LayoutInflater.from(context);
			view = inflater.inflate(layoutIdForListItem, viewGroup, false);
			return new GamesViewHolder(view, mListener);
		} else {
			layoutIdForListItem = R.layout.game_pagination_progress_bar;
			inflater = LayoutInflater.from(context);
			view = inflater.inflate(layoutIdForListItem, viewGroup, false);
			return new LoadingVH(view);
		}
	}
	
	@Override
	public int getItemViewType(final int position) {
		
		if (position == mGames.size() - 1 && mIsLoadingAdded) {
			return GAME_LOADING_VIEWHOLDER;
		} else {
			if (String.valueOf(mGames.get(position).getCreatorId()).equals(mUserId)) {
				return GAME_ORGANIZER_VIEWHOLDER;
			} else {
				return GAME_BASE_VIEWHOLDER;
			}
		}
	}
	
	@Override
	public void onBindViewHolder(
		@NonNull final RecyclerView.ViewHolder gamesViewHolderVariative,
		final int position
	) {
		
		final GameDataApi gameData = mGames.get(position);
		
		switch (gamesViewHolderVariative.getItemViewType()) {
			case GAME_BASE_VIEWHOLDER:
				final GamesViewHolder gamesViewHolder = (GamesViewHolder) gamesViewHolderVariative;
				Log.e("loc", gameData.getLocation());
				Log.e("iscancel", String.valueOf(gameData.getIsCanceled()));
				Log.e("", "");
				if (gameData.getIsCanceled() == 1) {
					cancelGame(gamesViewHolder);
				} else {
					notCancelGame(gamesViewHolder);
					if (mGamesIdsParticipantArray.contains(gameData.getId())) {
						gamesViewHolder.mButton.setText("Вы в игре");
						gamesViewHolder.mButton.setBackground(ResourcesCompat.getDrawable(
							gamesViewHolder.itemView.getContext().getResources(),
							R.drawable.rounded_connect_button_inactive,
							null));
						gamesViewHolder.mButton.setOnClickListener(view -> mConnectToGameListener.connectButtonOnClicked(
							gameData,
							position,
							gameData.getId(),
							true));
					} else {
						gamesViewHolder.mButton.setText("Присоединиться");
						gamesViewHolder.mButton.setBackground(ResourcesCompat.getDrawable(
							gamesViewHolder.itemView.getContext().getResources(),
							R.drawable.rounded_connect_button_active,
							null));
						gamesViewHolder.mButton.setOnClickListener(view -> mConnectToGameListener.connectButtonOnClicked(
							gameData,
							gamesViewHolder.getAdapterPosition(),
							gameData.getId(),
							false));
					}
				}
				
				gamesViewHolder.mGameChip.setText(getCategoryName(gameData.getCategoryId()));
				gamesViewHolder.mDate.setText(getCurrentDate(
					gameData.getDate(),
					gameData.getTime()));
				gamesViewHolder.mDescription.setText(shortifyString(gameData.getDescription()));
				gamesViewHolder.mAddress.setText(gameData.getLocation());
				final String peopleQuantityParticipant = gameData.getCurrentPeopleQuantity() + " / " + gameData.getMaxPeopleQuantity();
				gamesViewHolder.mPeopleQuantity.setText(peopleQuantityParticipant);
				if (!gameData.getLocationPhotoUrl().isEmpty() && gameData.getLocationPhotoUrl() != null) {
					getImageWithPicasso(
						(BASE_UPLOADS_URL + gameData.getLocationPhotoUrl()),
						gamesViewHolder.mImage);
				} else {
					gameWithoutPhoto(gamesViewHolder);
				}
				break;
			
			case GAME_ORGANIZER_VIEWHOLDER:
				
				final GamesOrganizerViewHolder gamesOrganizerViewHolder = (GamesOrganizerViewHolder) gamesViewHolderVariative;
				
				if (gameData.getIsCanceled() == 1) {
					cancelOrganizerGame(gamesOrganizerViewHolder);
				} else {
					notCancelOrganizerGame(gamesOrganizerViewHolder);
					gamesOrganizerViewHolder.deleteButton.setOnClickListener(view -> mDeleteGameButtonClickListener.deleteGameButtonOnClicked(
						position,
						gameData.getId()));
					gamesOrganizerViewHolder.changeButton.setOnClickListener(view -> mUpdateGameButtonClickListener.updateGameButtonOnClicked(
						position,
						gameData.getId(),
						gameData));
				}
				gamesOrganizerViewHolder.date.setText(getCurrentDate(
					gameData.getDate(),
					gameData.getTime()));
				gamesOrganizerViewHolder.description.setText(shortifyString(gameData.getDescription()));
				gamesOrganizerViewHolder.address.setText(gameData.getLocation());
				final String peopleQuantityOrganizer = gameData.getCurrentPeopleQuantity() + " / " + gameData.getMaxPeopleQuantity();
				gamesOrganizerViewHolder.gameChip.setText(peopleQuantityOrganizer);
				
				if (!gameData.getLocationPhotoUrl().isEmpty() && gameData.getLocationPhotoUrl() != null) {
					getImageWithPicasso(
						(BASE_UPLOADS_URL + gameData.getLocationPhotoUrl()),
						gamesOrganizerViewHolder.image);
				} else {
					organizerGameWithoutPhoto(gamesOrganizerViewHolder);
				}
				
				break;
			
			case GAME_LOADING_VIEWHOLDER:
				break;
			
			
		}
	}
	
	private void gameWithoutPhoto(final GamesViewHolder gamesViewHolder) {
		gamesViewHolder.mImage.setVisibility(View.GONE);
	}
	
	private void organizerGameWithoutPhoto(final GamesOrganizerViewHolder gamesOrganizerViewHolder) {
		gamesOrganizerViewHolder.image.setVisibility(View.GONE);
	}
	
	
	private void cancelGame(final GamesViewHolder gamesViewHolder) {
		gamesViewHolder.mCanceledLayout.setVisibility(View.VISIBLE);
		gamesViewHolder.mButton.setVisibility(View.INVISIBLE);
		gamesViewHolder.mButton.setEnabled(false);
		gamesViewHolder.mGameChip.setVisibility(View.GONE);
	}
	
	private void notCancelGame(final GamesViewHolder gamesViewHolder) {
		gamesViewHolder.mCanceledLayout.setVisibility(View.INVISIBLE);
		gamesViewHolder.mButton.setVisibility(View.VISIBLE);
		gamesViewHolder.mButton.setEnabled(true);
		gamesViewHolder.mGameChip.setVisibility(View.VISIBLE);
	}
	
	private void cancelOrganizerGame(final GamesOrganizerViewHolder gamesOrganizerViewHolder) {
		gamesOrganizerViewHolder.canceledLayout.setVisibility(View.VISIBLE);
		gamesOrganizerViewHolder.gameChip.setVisibility(View.GONE);
		gamesOrganizerViewHolder.changeButton.setEnabled(false);
		gamesOrganizerViewHolder.changeButton.setVisibility(View.INVISIBLE);
		gamesOrganizerViewHolder.deleteButton.setEnabled(false);
		gamesOrganizerViewHolder.deleteButton.setVisibility(View.INVISIBLE);
	}
	
	private void notCancelOrganizerGame(final GamesOrganizerViewHolder gamesOrganizerViewHolder) {
		gamesOrganizerViewHolder.canceledLayout.setVisibility(View.INVISIBLE);
		gamesOrganizerViewHolder.gameChip.setVisibility(View.VISIBLE);
		gamesOrganizerViewHolder.changeButton.setEnabled(true);
		gamesOrganizerViewHolder.changeButton.setVisibility(View.VISIBLE);
		gamesOrganizerViewHolder.deleteButton.setEnabled(true);
		gamesOrganizerViewHolder.deleteButton.setVisibility(View.VISIBLE);
	}
	
	private void getImageWithPicasso(final String photoUrl, final ImageView imageView) {
		Picasso.with(mContext)
			.load(photoUrl)
			.placeholder(R.drawable.image_placeholder)
			.fit()
			.centerCrop()
			.noFade()
			.into(imageView);
	}
	
	@SuppressLint("SimpleDateFormat")
	private static String getCurrentDate(final String serverDate, final String time) {
		final Locale russian = new Locale("ru");
		final String[] newMonths = {
			"января", "февраля", "марта", "апреля", "мая", "июня",
			"июля", "августа", "сентября", "октября", "ноября", "декабря" };
		final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(russian);
		dateFormatSymbols.setMonths(newMonths);
		final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, russian);
		final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
		simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
		
		final StringBuilder stringBuilder = new StringBuilder(time);
		
		stringBuilder.delete(stringBuilder.lastIndexOf(":"), 8);
		
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(serverDate);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		final StringBuilder sb = new StringBuilder(simpleDateFormat.format(date));
		
		if (sb.lastIndexOf("2018 г.") != -1) {
			sb.delete(sb.lastIndexOf("2018 г."), simpleDateFormat.format(date).length());
		} else {
			sb.delete(sb.lastIndexOf("2019 г."), simpleDateFormat.format(date).length());
		}
		return sb.toString().trim() + " в " + stringBuilder.toString();
	}
	
	private static String shortifyString(String description) {
		
		if (description.length() > 120) {
			final StringBuilder stringBuilder = new StringBuilder(description);
			stringBuilder.delete(120, description.length());
			description = stringBuilder.toString() + "...";
		}
		
		return description;
	}
	
	@Override
	public int getItemCount() {
		return mGames == null ? 0 : mGames.size();
	}
	
	
	public class GamesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		
		private final GamesRecyclerViewClickListener mGamesRecyclerViewClickListener;
		
		private final ImageView mImage;
		private final LinearLayout mCanceledLayout;
		private final TextView mDate;
		private final TextView mDescription;
		private final TextView mAddress;
		private final TextView mPeopleQuantity;
		private final TextView mGameChip;
		private final Button mButton;
		
		private GamesViewHolder(
			final View holderItemView,
			final GamesRecyclerViewClickListener listener
		) {
			super(holderItemView);
			mGamesRecyclerViewClickListener = listener;
			itemView.setOnClickListener(this);
			mImage = itemView.findViewById(R.id.game_image);
			mCanceledLayout = itemView.findViewById(R.id.cancelled_layout);
			mDate = itemView.findViewById(R.id.game_date);
			mDescription = itemView.findViewById(R.id.game_description);
			mAddress = itemView.findViewById(R.id.game_address);
			mPeopleQuantity = itemView.findViewById(R.id.game_people_quantity);
			mGameChip = itemView.findViewById(R.id.game_chip_category_name);
			mButton = itemView.findViewById(R.id.game_button);
			
		}
		
		@Override
		public void onClick(final View view) {
			mListener.onClick(view, getAdapterPosition(), getItem(getAdapterPosition()));
		}
	}
	
	
	public class GamesOrganizerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		
		private final GamesRecyclerViewClickListener mGamesRecyclerViewClickListener;
		
		private final ImageView image;
		private final LinearLayout canceledLayout;
		private final TextView date;
		private final TextView description;
		private final TextView address;
		private final TextView gameChip;
		private final Button deleteButton;
		private final Button changeButton;
		
		
		private GamesOrganizerViewHolder(
			final View holderItemView,
			final GamesRecyclerViewClickListener listener
		) {
			super(holderItemView);
			mGamesRecyclerViewClickListener = listener;
			itemView.setOnClickListener(this);
			image = itemView.findViewById(R.id.game_image);
			canceledLayout = itemView.findViewById(R.id.cancelled_layout);
			date = itemView.findViewById(R.id.game_date);
			description = itemView.findViewById(R.id.game_description);
			address = itemView.findViewById(R.id.game_address);
			gameChip = itemView.findViewById(R.id.game_chip_category_name);
			deleteButton = itemView.findViewById(R.id.delete_btn);
			changeButton = itemView.findViewById(R.id.change_btn);
		}
		
		@Override
		public void onClick(final View view) {
			mListener.onClick(view, getAdapterPosition(), getItem(getAdapterPosition()));
		}
	}
	
	private static class LoadingVH extends RecyclerView.ViewHolder {
		
		private LoadingVH(final View holderItemView) {
			super(holderItemView);
		}
	}
	
	
	public GamesAdapter(
		final Context context,
		final GamesRecyclerViewClickListener listener,
		final String userId,
		final List<Integer> gamesIdsParticipantArray,
		final ConnectToGameListener connectToGameListener,
		final DeleteGameButtonClickListener deleteGameButtonClickListener,
		final UpdateGameButtonClickListener updateGameButtonClickListener
	) {
		this.mListener = listener;
		this.mContext = context;
		this.mUserId = userId;
		this.mGamesIdsParticipantArray = gamesIdsParticipantArray;
		this.mConnectToGameListener = connectToGameListener;
		this.mDeleteGameButtonClickListener = deleteGameButtonClickListener;
		this.mUpdateGameButtonClickListener = updateGameButtonClickListener;
		
		mGames = new ArrayList<>();
	}
	
	public void updatePeopleQuantity(final int position, final String currentPeopleQuantity) {
		mGames.get(position).setCurrentPeopleQuantity(currentPeopleQuantity);
	}
	
	private GameDataApi getItem(final int position) {
		return mGames.get(position);
	}
	
	public List<GameDataApi> getGames() {
		return mGames;
	}
	
	public void setGames(final List<GameDataApi> movieResults) {
		mGames = movieResults;
	}
	
	private void add(final GameDataApi game) {
		mGames.add(game);
		notifyItemInserted(mGames.size() - 1);
	}
	
	public void addAll(final List<GameDataApi> results) {
		for (final GameDataApi result : results) {
			add(result);
		}
	}
	
	public void addLoading() {
		mIsLoadingAdded = true;
		add(new GameDataApi());
		notifyDataSetChanged();
	}
	
	public void hideLoading() {
		mIsLoadingAdded = false;
		
		final int position = mGames.size() - 1;
		final GameDataApi game = getItem(position);
		
		if (game != null) {
			mGames.remove(position);
			notifyItemRemoved(position);
		}
	}
	
	public void clear() {
		mIsLoadingAdded = false;
		mGames.clear();
	}
	
	private static String getCategoryName(final int categoryId) {
		String categoryName = "";
		switch (categoryId) {
			case 1:
				categoryName = "Баскетбол";
				break;
			case 2:
				categoryName = "Футбол";
				break;
			case 3:
				categoryName = "Теннис";
				break;
			case 4:
				categoryName = "Шахматы";
				break;
			case 5:
				categoryName = "Бег";
				break;
			case 6:
				categoryName = "Пинг-Понг";
				break;
		}
		return categoryName;
	}
	
	public void changeParticipateButtonState(final int position, final int gameId) {
		
		if (mGamesIdsParticipantArray.contains(gameId)) {
			mGamesIdsParticipantArray.remove((Integer) gameId);
		} else {
			mGamesIdsParticipantArray.add(gameId);
		}
		
		notifyItemChanged(position);
	}
	
	public void cancelGame(final int position) {
		mGames.get(position).setIsCanceled(1);
		notifyItemChanged(position);
	}
	
	public void deleteItem(final int position) {
		mGames.remove(mGames.get(position));
		notifyItemChanged(position);
	}
	
}
