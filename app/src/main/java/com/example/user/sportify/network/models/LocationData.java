package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationData {
    @SerializedName("location")
    @Expose
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
