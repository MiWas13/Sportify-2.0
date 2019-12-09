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
import com.example.user.sportify.ui.feed.data.GameData;
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

    private List<GameDataApi> games;
    private GamesRecyclerViewClickListener mListener;
    private ConnectToGameListener connectToGameListener;
    private String userId;
    private ConnectButtonClickListener connectButtonClickListener;
    private boolean isLoadingAdded = false;
    private Context context;
    private List<Integer> gamesIdsParticipantArray;
    private DeleteGameButtonClickListener deleteGameButtonClickListener;
    private UpdateGameButtonClickListener updateGameButtonClickListener;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem;
        LayoutInflater inflater;
        View view;
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

    public void updateData(List<GameDataApi> updatedGames) {
        games.clear();
        games.addAll(updatedGames);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

//        if (!isLoadingAdded) {
        if (position == games.size() - 1 && isLoadingAdded) {
            return GAME_LOADING_VIEWHOLDER;
        } else {
            if (String.valueOf(games.get(position).getCreatorId()).equals(userId)) {
                return GAME_ORGANIZER_VIEWHOLDER;
            } else {
                return GAME_BASE_VIEWHOLDER;
            }
        }

//        } else {
//            return GAME_LOADING_VIEWHOLDER;
//        }
//        if (position == games.size() - 1 && isLoadingAdded) {
//            return 3;
//        } else {
////            if (games.get(position).getUserIsOrganizer()) {
////                return 0;
////            } else {
//            return 1;
////            }
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder gamesViewHolderVariative, int position) {

        GameDataApi gameData = games.get(position);

        switch (gamesViewHolderVariative.getItemViewType()) {
            case GAME_BASE_VIEWHOLDER:
                GamesViewHolder gamesViewHolder = (GamesViewHolder) gamesViewHolderVariative;
                Log.e("loc", gameData.getLocation());
                Log.e("iscancel", String.valueOf(gameData.getIsCanceled()));
                Log.e("", "");
                if (gameData.getIsCanceled() == 1) {
                    cancelGame(gamesViewHolder);
                } else {
                    notCancelGame(gamesViewHolder);
                    if (gamesIdsParticipantArray.contains(gameData.getId())) {
                        gamesViewHolder.button.setText("Вы в игре");
                        gamesViewHolder.button.setBackground(ResourcesCompat.getDrawable(gamesViewHolder.itemView.getContext().getResources(), R.drawable.rounded_connect_button_inactive, null));
                        gamesViewHolder.button.setOnClickListener(view -> connectToGameListener.connectButtonOnClicked(gameData, position, gameData.getId(), true));
                    } else {
                        gamesViewHolder.button.setText("Присоединиться");
                        gamesViewHolder.button.setBackground(ResourcesCompat.getDrawable(gamesViewHolder.itemView.getContext().getResources(), R.drawable.rounded_connect_button_active, null));
                        gamesViewHolder.button.setOnClickListener(view -> connectToGameListener.connectButtonOnClicked(gameData, gamesViewHolder.getAdapterPosition(), gameData.getId(), false));
                    }
                }

                gamesViewHolder.gameChip.setText(getCategoryName(gameData.getCategoryId()));
                gamesViewHolder.date.setText(getCurrentDate(gameData.getDate(), gameData.getTime()));
                gamesViewHolder.description.setText(shortifyString(gameData.getDescription()));
                gamesViewHolder.address.setText(gameData.getLocation());
                String peopleQuantityParticipant = gameData.getCurrentPeopleQuantity() + " / " + gameData.getMaxPeopleQuantity();
                gamesViewHolder.peopleQuantity.setText(peopleQuantityParticipant);
                if (!gameData.getLocationPhotoUrl().equals("") && gameData.getLocationPhotoUrl() != null)
                    getImageWithPicasso((BASE_UPLOADS_URL + gameData.getLocationPhotoUrl()), gamesViewHolder.image);
                else gameWithoutPhoto(gamesViewHolder);
                break;

            case GAME_ORGANIZER_VIEWHOLDER:

                GamesOrganizerViewHolder gamesOrganizerViewHolder = (GamesOrganizerViewHolder) gamesViewHolderVariative;

                if (gameData.getIsCanceled() == 1) {
                    cancelOrganizerGame(gamesOrganizerViewHolder);
                } else {
                    notCancelOrganizerGame(gamesOrganizerViewHolder);
                    gamesOrganizerViewHolder.deleteButton.setOnClickListener(view -> deleteGameButtonClickListener.deleteGameButtonOnClicked(position, gameData.getId()));
                    gamesOrganizerViewHolder.changeButton.setOnClickListener(view -> updateGameButtonClickListener.updateGameButtonOnClicked(position, gameData.getId(), gameData));
                }
                gamesOrganizerViewHolder.date.setText(getCurrentDate(gameData.getDate(), gameData.getTime()));
                gamesOrganizerViewHolder.description.setText(shortifyString(gameData.getDescription()));
                gamesOrganizerViewHolder.address.setText(gameData.getLocation());
                String peopleQuantityOrganizer = gameData.getCurrentPeopleQuantity() + " / " + gameData.getMaxPeopleQuantity();
                gamesOrganizerViewHolder.gameChip.setText(peopleQuantityOrganizer);

                if (!gameData.getLocationPhotoUrl().equals("") && gameData.getLocationPhotoUrl() != null)
                    getImageWithPicasso((BASE_UPLOADS_URL + gameData.getLocationPhotoUrl()), gamesOrganizerViewHolder.image);
                else organizerGameWithoutPhoto(gamesOrganizerViewHolder);

                break;

            case GAME_LOADING_VIEWHOLDER:
                break;


        }
    }

    private void gameWithoutPhoto(GamesViewHolder gamesViewHolder) {
        gamesViewHolder.image.setVisibility(View.GONE);
    }

    private void organizerGameWithoutPhoto(GamesOrganizerViewHolder gamesOrganizerViewHolder) {
        gamesOrganizerViewHolder.image.setVisibility(View.GONE);
    }


    private void cancelGame(GamesViewHolder gamesViewHolder) {
        gamesViewHolder.canceledLayout.setVisibility(View.VISIBLE);
        gamesViewHolder.button.setVisibility(View.INVISIBLE);
        gamesViewHolder.button.setEnabled(false);
        gamesViewHolder.gameChip.setVisibility(View.GONE);
    }

    private void notCancelGame(GamesViewHolder gamesViewHolder) {
        gamesViewHolder.canceledLayout.setVisibility(View.INVISIBLE);
        gamesViewHolder.button.setVisibility(View.VISIBLE);
        gamesViewHolder.button.setEnabled(true);
        gamesViewHolder.gameChip.setVisibility(View.VISIBLE);
    }

    private void cancelOrganizerGame(GamesOrganizerViewHolder gamesOrganizerViewHolder) {
        gamesOrganizerViewHolder.canceledLayout.setVisibility(View.VISIBLE);
        gamesOrganizerViewHolder.gameChip.setVisibility(View.GONE);
        gamesOrganizerViewHolder.changeButton.setEnabled(false);
        gamesOrganizerViewHolder.changeButton.setVisibility(View.INVISIBLE);
        gamesOrganizerViewHolder.deleteButton.setEnabled(false);
        gamesOrganizerViewHolder.deleteButton.setVisibility(View.INVISIBLE);
    }

    private void notCancelOrganizerGame(GamesOrganizerViewHolder gamesOrganizerViewHolder) {
        gamesOrganizerViewHolder.canceledLayout.setVisibility(View.INVISIBLE);
        gamesOrganizerViewHolder.gameChip.setVisibility(View.VISIBLE);
        gamesOrganizerViewHolder.changeButton.setEnabled(true);
        gamesOrganizerViewHolder.changeButton.setVisibility(View.VISIBLE);
        gamesOrganizerViewHolder.deleteButton.setEnabled(true);
        gamesOrganizerViewHolder.deleteButton.setVisibility(View.VISIBLE);
    }

    private void getImageWithPicasso(String photoUrl, ImageView imageView) {
        Picasso.with(context)
                .load(photoUrl)
                .placeholder(R.drawable.image_placeholder)
                .fit()
                .centerCrop()
                .noFade()
                .into(imageView);
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate(String serverDate, String time) {
        Locale russian = new Locale("ru");
        String[] newMonths = {
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(russian);
        dateFormatSymbols.setMonths(newMonths);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, russian);
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);

        StringBuilder stringBuilder = new StringBuilder(time);

        stringBuilder.delete(stringBuilder.lastIndexOf(":"), 8);

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder(simpleDateFormat.format(date));

        if (sb.lastIndexOf("2018 г.") != -1)
            sb.delete(sb.lastIndexOf("2018 г."), simpleDateFormat.format(date).length());
        else sb.delete(sb.lastIndexOf("2019 г."), simpleDateFormat.format(date).length());
        return sb.toString().trim() + " в " + stringBuilder.toString();
    }

    private String shortifyString(String description) {

        if (description.length() > 120) {
            StringBuilder stringBuilder = new StringBuilder(description);
            stringBuilder.delete(120, description.length());
            description = stringBuilder.toString() + "...";
        }

        return description;
    }

    @Override
    public int getItemCount() {
        return games == null ? 0 : games.size();
//        return games.size();
    }


    public class GamesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private GamesRecyclerViewClickListener mListener;

        // Create a TextView variable called listItemNumberView
        ImageView image;
        LinearLayout canceledLayout;
        TextView date;
        TextView description;
        TextView address;
        TextView peopleQuantity;
        TextView gameChip;
        Button button;


        // Create a constructor for NewsViewHolder that accepts a View called itemView as a parameter

        GamesViewHolder(View itemView, GamesRecyclerViewClickListener mListener) {
            super(itemView);
            this.mListener = mListener;
            itemView.setOnClickListener(this);
            image = itemView.findViewById(R.id.game_image);
            canceledLayout = itemView.findViewById(R.id.cancelled_layout);
            date = itemView.findViewById(R.id.game_date);
            description = itemView.findViewById(R.id.game_description);
            address = itemView.findViewById(R.id.game_address);
            peopleQuantity = itemView.findViewById(R.id.game_people_quantity);
            gameChip = itemView.findViewById(R.id.game_chip_category_name);
            button = itemView.findViewById(R.id.game_button);

        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition(), getItem(getAdapterPosition()));
        }
    }


    public class GamesOrganizerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private GamesRecyclerViewClickListener mListener;

        // Create a TextView variable called listItemNumberView
        ImageView image;
        LinearLayout canceledLayout;
        TextView date;
        TextView description;
        TextView address;
        TextView gameChip;
        Button deleteButton;
        Button changeButton;


        // Create a constructor for NewsViewHolder that accepts a View called itemView as a parameter

        GamesOrganizerViewHolder(View itemView, GamesRecyclerViewClickListener mListener) {
            super(itemView);
            this.mListener = mListener;
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
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition(), getItem(getAdapterPosition()));
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }


    public GamesAdapter(Context context, GamesRecyclerViewClickListener mListener, String userId, List<Integer> gamesIdsParticipantArray, ConnectToGameListener connectToGameListener,
                        DeleteGameButtonClickListener deleteGameButtonClickListener, UpdateGameButtonClickListener updateGameButtonClickListener) {
        this.mListener = mListener;
        this.context = context;
        this.userId = userId;
        this.gamesIdsParticipantArray = gamesIdsParticipantArray;
        this.connectToGameListener = connectToGameListener;
        this.deleteGameButtonClickListener = deleteGameButtonClickListener;
        this.updateGameButtonClickListener = updateGameButtonClickListener;

        games = new ArrayList<>();
    }

    public void add(GameData gameData) {
        games.add(null);
        notifyItemInserted(games.size() - 1);
    }

    public void updatePeopleQuantity(int position, String currentPeopleQuantity) {
//        games.get(position).setCurrentPeopleQuantity(String.valueOf(Integer.valueOf(games.get(position).getCurrentPeopleQuantity()) + 1));
        games.get(position).setCurrentPeopleQuantity(currentPeopleQuantity);
    }

//    public void addAll(ArrayList<GameDataApi> gameDataList) {
//        games.addAll(gameDataList);
//        notifyDataSetChanged();
//    }

//    public void remove(GameData id) {
//        int position = games.indexOf(id);
//        if (position > -1) {
//            games.remove(position);
//            notifyItemRemoved(position);
//        }
//    }


    private GameDataApi getItem(int position) {
        return games.get(position);
    }

    public List<GameDataApi> getGames() {
        return games;
    }

    public void setGames(List<GameDataApi> movieResults) {
        this.games = movieResults;
    }

    public void add(GameDataApi game) {
        games.add(game);
        notifyItemInserted(games.size() - 1);
    }

    public void addAll(List<GameDataApi> results) {
        for (GameDataApi result : results) {
            add(result);
        }
    }

    public void addLoading() {
        isLoadingAdded = true;
        add(new GameDataApi());
        notifyDataSetChanged();
    }

    public void hideLoading() {
        isLoadingAdded = false;

        int position = games.size() - 1;
        GameDataApi game = getItem(position);

        if (game != null) {
            games.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void remove(GameDataApi r) {
        int position = games.indexOf(r);
        games.clear();
        if (position > -1) {
            games.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        games.clear();
//        while (getItemCount() > 0) {
//            remove(getItem(0));
//        }
    }

    private String getCategoryName(int categoryId) {
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

    public void changeParticipateButtonState(int position, int gameId) {

        if (gamesIdsParticipantArray.contains(gameId)) {
            gamesIdsParticipantArray.remove(gamesIdsParticipantArray.indexOf(gameId));
        } else {
            gamesIdsParticipantArray.add(gameId);
        }

        notifyItemChanged(position);
    }

    public void cancelGame(int position) {
        games.get(position).setIsCanceled(1);
        notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        games.remove(games.get(position));
        notifyItemChanged(position);
    }

}
