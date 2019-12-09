package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserParticipantData {
    @SerializedName("phone")
    @Expose
    private String phone;

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    @SerializedName("name")
    @Expose
    private String name;

}
