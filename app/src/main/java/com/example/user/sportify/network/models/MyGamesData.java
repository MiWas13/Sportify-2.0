package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.security.acl.Owner;
import java.util.List;

public class MyGamesData {


    @SerializedName("owner")
    @Expose
    private List<GameDataApi> owner = null;

    @SerializedName("attached")
    @Expose
    private List<GamesParticipantData> attached = null;

    public List<GameDataApi> getOwner() {
        return owner;
    }

    public void setOwner(List<GameDataApi> owner) {
        this.owner = owner;
    }

    public List<GamesParticipantData> getAttached() {
        return attached;
    }

    public void setAttached(List<GamesParticipantData> attached) {
        this.attached = attached;
    }
}
