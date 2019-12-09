package com.example.user.sportify.ui.concretgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.sportify.R;
import com.example.user.sportify.network.models.GameDataApi;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConcretGameDescriptionAdapter extends RecyclerView.Adapter<ConcretGameDescriptionAdapter.ConcretGameDescriptionAdapterViewHolder> {

    private ConcretGameItemClickListener listener;
    private PhonePositionClickListener phonePositionClickListener;
    private MapPositionClickListener mapPositionClickListener;
    private GameDataApi game;
    private Context context;
    private Boolean isOrganizer;

    @NonNull
    @Override
    public ConcretGameDescriptionAdapter.ConcretGameDescriptionAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.game_description_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ConcretGameDescriptionAdapter.ConcretGameDescriptionAdapterViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcretGameDescriptionAdapter.ConcretGameDescriptionAdapterViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(), R.drawable.ic_people, null));
                String peopleQuantity = game.getCurrentPeopleQuantity() + " из " + game.getMaxPeopleQuantity();
                holder.gameDescFirstLine.setText(peopleQuantity);
                holder.gameDescSecondLine.setText(holder.itemView.getContext().getResources().getString(R.string.people_quantity));
                break;
            case 1:
                holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(), R.drawable.ic_date, null));
                holder.gameDescFirstLine.setText(getCurrentDate(game.getDate(), game.getTime()));
                holder.gameDescSecondLine.setText(holder.itemView.getContext().getResources().getString(R.string.game_start_desc));
                break;
            case 2:
                holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(), R.drawable.ic_location, null));
                holder.gameDescFirstLine.setText(game.getLocation());
                holder.gameDescSecondLine.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorMenu));
                holder.gameDescSecondLine.setText(holder.itemView.getContext().getResources().getString(R.string.show_navigation_desc));
                holder.itemView.setOnClickListener(view -> mapPositionClickListener.onPhonePositionClicked(game.getCoordinates()));
                break;
            case 3:
                holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(), R.drawable.ic_phone, null));
                holder.gameDescFirstLine.setText(game.getCreatorPhone());
                holder.gameDescSecondLine.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorMenu));
                holder.gameDescSecondLine.setText(holder.itemView.getContext().getResources().getString(R.string.call_organizer_desc));
                holder.itemView.setOnClickListener(view -> phonePositionClickListener.onPhonePositionClicked(game.getCreatorPhone()));
                break;
        }
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

    @Override
    public int getItemCount() {
        if (isOrganizer)
            return 3;
        else
            return 4;
    }


    class ConcretGameDescriptionAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ConcretGameItemClickListener listener;

        ImageView gameDescIcon;
        TextView gameDescFirstLine;
        TextView gameDescSecondLine;


        ConcretGameDescriptionAdapterViewHolder(View itemView, ConcretGameItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
            gameDescIcon = itemView.findViewById(R.id.game_desc_icon);
            gameDescFirstLine = itemView.findViewById(R.id.game_desc_first_line);
            gameDescSecondLine = itemView.findViewById(R.id.game_desc_second_line);
            gameDescIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    public ConcretGameDescriptionAdapter(Context context, GameDataApi game, ConcretGameItemClickListener listener, PhonePositionClickListener phonePositionClickListener, MapPositionClickListener mapPositionClickListener, Boolean isOrganizer) {
        this.context = context;
        this.game = game;
        this.listener = listener;
        this.phonePositionClickListener = phonePositionClickListener;
        this.mapPositionClickListener = mapPositionClickListener;
        this.isOrganizer = isOrganizer;
    }
}
