package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;

public class GameDataApi implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("category_id")
    @Expose
    private int categoryId;
    @SerializedName("creator_id")
    @Expose
    private int creatorId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("location_photo_url")
    @Expose
    private String locationPhotoUrl;
    @SerializedName("coordinates")
    @Expose
    private String coordinates;
    @SerializedName("max_people_quantity")
    @Expose
    private String maxPeopleQuantity;
    @SerializedName("is_canceled")
    @Expose
    private int isCanceled;

    public String getCoordinates() {
        return coordinates;
    }

    public void setCurrentPeopleQuantity(String currentPeopleQuantity) {
        this.currentPeopleQuantity = currentPeopleQuantity;
    }

    @SerializedName("current_people_quantity")
    @Expose
    private String currentPeopleQuantity;

    public String getCreatorPhone() {
        return creatorPhone;
    }

    @SerializedName("creator_phone")
    @Expose
    private String creatorPhone;

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public String getDescription() {
        return description;
    }


    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getLocationPhotoUrl() {
        return locationPhotoUrl;
    }

    public String getMaxPeopleQuantity() {
        return maxPeopleQuantity;
    }

    public String getCurrentPeopleQuantity() {
        return currentPeopleQuantity;
    }


    public int getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(int isCanceled) {
        this.isCanceled = isCanceled;
    }
}
