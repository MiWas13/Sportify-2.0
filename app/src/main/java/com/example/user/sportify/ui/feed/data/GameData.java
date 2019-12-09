package com.example.user.sportify.ui.feed.data;

public class GameData {
	
	public String getDate() {
		return mDate;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	private final int mImage;
	private final String mDate;
	private final String mDescription;
	private final String mAddress;
	private final String mPeopleQuantity;
	
	public int getImage() {
		return mImage;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public String getPeopleQuantity() {
		return mPeopleQuantity;
	}
	
	public Boolean getBtn() {
		return mBtn;
	}
	
	public Boolean getState() {
		return mState;
	}
	
	public Boolean isUserOrganizer() {
		return mUserIsOrganizer;
	}
	
	private final Boolean mBtn;
	private final Boolean mState;
	private final Boolean mUserIsOrganizer;
	
	public GameData(
		final int image,
		final String date,
		final String description,
		final String address,
		final String peopleQuantity,
		final Boolean btn,
		final Boolean state,
		final Boolean userIsOrganizer
	) {
		mImage = image;
		mDate = date;
		mDescription = description;
		mAddress = address;
		mPeopleQuantity = peopleQuantity;
		mBtn = btn;
		mState = state;
		mUserIsOrganizer = userIsOrganizer;
	}
}
