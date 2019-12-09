package com.example.user.sportify.ui.concretgame;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

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
	
	private final ConcretGameItemClickListener listener;
	private final PhonePositionClickListener phonePositionClickListener;
	private final MapPositionClickListener mapPositionClickListener;
	private final GameDataApi game;
	private final Context context;
	private final Boolean isOrganizer;
	
	@NonNull
	@Override
	public ConcretGameDescriptionAdapter.ConcretGameDescriptionAdapterViewHolder onCreateViewHolder(
		@NonNull final ViewGroup viewGroup,
		final int i
	) {
		final Context context = viewGroup.getContext();
		final int layoutIdForListItem = R.layout.game_description_item;
		final LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
		
		return new ConcretGameDescriptionAdapter.ConcretGameDescriptionAdapterViewHolder(
			view,
			listener);
	}
	
	@Override
	public void onBindViewHolder(
		@NonNull ConcretGameDescriptionAdapter.ConcretGameDescriptionAdapterViewHolder holder,
		int position
	) {
		switch (position) {
			case 0:
				holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(
					holder.itemView.getContext().getResources(),
					R.drawable.ic_people,
					null));
				final String peopleQuantity = game.getCurrentPeopleQuantity() + " из " + game.getMaxPeopleQuantity();
				holder.gameDescFirstLine.setText(peopleQuantity);
				holder.gameDescSecondLine.setText(holder.itemView.getContext().getResources().getString(
					R.string.people_quantity));
				break;
			case 1:
				holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(
					holder.itemView.getContext().getResources(),
					R.drawable.ic_date,
					null));
				holder.gameDescFirstLine.setText(getCurrentDate(game.getDate(), game.getTime()));
				holder.gameDescSecondLine.setText(holder.itemView.getContext().getResources().getString(
					R.string.game_start_desc));
				break;
			case 2:
				holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(
					holder.itemView.getContext().getResources(),
					R.drawable.ic_location,
					null));
				holder.gameDescFirstLine.setText(game.getLocation());
				holder.gameDescSecondLine.setTextColor(holder.itemView.getContext().getResources().getColor(
					R.color.colorMenu));
				holder.gameDescSecondLine.setText(holder.itemView.getContext().getResources().getString(
					R.string.show_navigation_desc));
				holder.itemView.setOnClickListener(view -> mapPositionClickListener.onPhonePositionClicked(
					game.getCoordinates()));
				break;
			case 3:
				holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(
					holder.itemView.getContext().getResources(),
					R.drawable.ic_phone,
					null));
				holder.gameDescFirstLine.setText(game.getCreatorPhone());
				holder.gameDescSecondLine.setTextColor(holder.itemView.getContext().getResources().getColor(
					R.color.colorMenu));
				holder.gameDescSecondLine.setText(holder.itemView.getContext().getResources().getString(
					R.string.call_organizer_desc));
				holder.itemView.setOnClickListener(view -> phonePositionClickListener.onPhonePositionClicked(
					game.getCreatorPhone()));
				break;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getCurrentDate(final String serverDate, final String time) {
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
		} catch (ParseException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder(simpleDateFormat.format(date));
		
		if (sb.lastIndexOf("2018 г.") != -1) {
			sb.delete(sb.lastIndexOf("2018 г."), simpleDateFormat.format(date).length());
		} else {
			sb.delete(sb.lastIndexOf("2019 г."), simpleDateFormat.format(date).length());
		}
		return sb.toString().trim() + " в " + stringBuilder.toString();
	}
	
	@Override
	public int getItemCount() {
		if (isOrganizer) {
			return 3;
		} else {
			return 4;
		}
	}
	
	
	class ConcretGameDescriptionAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		
		private final ConcretGameItemClickListener listener;
		
		private final ImageView gameDescIcon;
		private final TextView gameDescFirstLine;
		private final TextView gameDescSecondLine;
		
		
		private ConcretGameDescriptionAdapterViewHolder(
			final View itemView,
			final ConcretGameItemClickListener listener
		) {
			super(itemView);
			this.listener = listener;
			itemView.setOnClickListener(this);
			gameDescIcon = itemView.findViewById(R.id.game_desc_icon);
			gameDescFirstLine = itemView.findViewById(R.id.game_desc_first_line);
			gameDescSecondLine = itemView.findViewById(R.id.game_desc_second_line);
			gameDescIcon.setOnClickListener(this);
		}
		
		@Override
		public void onClick(final View view) {
			listener.onClick(view, getAdapterPosition());
		}
	}
	
	public ConcretGameDescriptionAdapter(
		final Context context,
		final GameDataApi game,
		final ConcretGameItemClickListener listener,
		final PhonePositionClickListener phonePositionClickListener,
		final MapPositionClickListener mapPositionClickListener,
		final Boolean isOrganizer
	) {
		this.context = context;
		this.game = game;
		this.listener = listener;
		this.phonePositionClickListener = phonePositionClickListener;
		this.mapPositionClickListener = mapPositionClickListener;
		this.isOrganizer = isOrganizer;
	}
}
