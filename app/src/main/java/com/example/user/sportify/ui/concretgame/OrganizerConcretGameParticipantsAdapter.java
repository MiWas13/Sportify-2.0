package com.example.user.sportify.ui.concretgame;

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
import com.example.user.sportify.network.models.UserParticipantData;

import java.util.List;

public class OrganizerConcretGameParticipantsAdapter extends RecyclerView.Adapter<OrganizerConcretGameParticipantsAdapter.OrganizerConcretGameParticipantsAdapterViewHolder> {

    private Context context;
    private List<UserParticipantData> users;
    private PhonePositionClickListener phonePositionClickListener;

    @NonNull
    @Override
    public OrganizerConcretGameParticipantsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.game_description_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new OrganizerConcretGameParticipantsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizerConcretGameParticipantsAdapterViewHolder holder, int position) {
        holder.gameDescIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(), R.drawable.ic_phone, null));
        holder.gameDescFirstLine.setText(users.get(position).getPhone());
        holder.gameDescSecondLine.setText(users.get(position).getName());
        holder.itemView.setOnClickListener(view -> phonePositionClickListener.onPhonePositionClicked(users.get(position).getPhone()));
    }

    @Override
    public int getItemCount() {
        if (users != null)
            return users.size();
        else return 0;
    }


    class OrganizerConcretGameParticipantsAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView gameDescIcon;
        TextView gameDescFirstLine;
        TextView gameDescSecondLine;


        OrganizerConcretGameParticipantsAdapterViewHolder(View itemView) {
            super(itemView);
            gameDescIcon = itemView.findViewById(R.id.game_desc_icon);
            gameDescFirstLine = itemView.findViewById(R.id.game_desc_first_line);
            gameDescSecondLine = itemView.findViewById(R.id.game_desc_second_line);
        }
    }

    public OrganizerConcretGameParticipantsAdapter(Context context, List<UserParticipantData> users, PhonePositionClickListener phonePositionClickListener) {
        this.context = context;
        this.users = users;
        this.phonePositionClickListener = phonePositionClickListener;

    }

    public void addPosotion(UserParticipantData user) {
        users.add(user);
        notifyDataSetChanged();
    }
}
