package com.example.user.sportify.ui.concretgame;

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
import com.example.user.sportify.network.models.UserParticipantData;

import java.util.List;

public class OrganizerConcretGameParticipantsAdapter extends RecyclerView.Adapter<OrganizerConcretGameParticipantsAdapter.OrganizerConcretGameParticipantsAdapterViewHolder> {
	
	private final Context mContext;
	private final List<UserParticipantData> mUsersList;
	private final PhonePositionClickListener mPhonePositionClickListener;
	
	@NonNull
	@Override
	public OrganizerConcretGameParticipantsAdapterViewHolder onCreateViewHolder(
		@NonNull final ViewGroup viewGroup,
		final int position
	) {
		final Context context = viewGroup.getContext();
		final int layoutIdForListItem = R.layout.game_description_item;
		final LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
		
		return new OrganizerConcretGameParticipantsAdapterViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(
		@NonNull final OrganizerConcretGameParticipantsAdapterViewHolder holder,
		final int position
	) {
		holder.mGameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(
			holder.itemView.getContext().getResources(),
			R.drawable.ic_phone,
			null));
		holder.mGameDescFirstLine.setText(mUsersList.get(position).getPhone());
		holder.mGameDescSecondLine.setText(mUsersList.get(position).getName());
		holder.itemView.setOnClickListener(view -> mPhonePositionClickListener.onPhonePositionClicked(
			mUsersList.get(position).getPhone()));
	}
	
	@Override
	public int getItemCount() {
		if (mUsersList != null) {
			return mUsersList.size();
		} else {
			return 0;
		}
	}
	
	
	static class OrganizerConcretGameParticipantsAdapterViewHolder extends RecyclerView.ViewHolder {
		
		private final ImageView mGameDescIcon;
		private final TextView mGameDescFirstLine;
		private final TextView mGameDescSecondLine;
		
		private OrganizerConcretGameParticipantsAdapterViewHolder(final View holderItemView) {
			super(holderItemView);
			mGameDescIcon = itemView.findViewById(R.id.game_desc_icon);
			mGameDescFirstLine = itemView.findViewById(R.id.game_desc_first_line);
			mGameDescSecondLine = itemView.findViewById(R.id.game_desc_second_line);
		}
	}
	
	public OrganizerConcretGameParticipantsAdapter(
		final Context context,
		final List<UserParticipantData> users,
		final PhonePositionClickListener phonePositionClickListener
	) {
		mContext = context;
		mUsersList = users;
		mPhonePositionClickListener = phonePositionClickListener;
		
	}
	
	public void addPosition(final UserParticipantData user) {
		mUsersList.add(user);
		notifyDataSetChanged();
	}
}
