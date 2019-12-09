package com.example.user.sportify.ui.feed.data;

public class GameData {
	
	public int getImage() {
		return image;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getAdress() {
		return adress;
	}
	
	public String getPeopleQuantity() {
		return peopleQuantity;
	}
	
	public Boolean getBtn() {
		return btn;
	}
	
	public Boolean getState() {
		return state;
	}
	
	public Boolean getUserIsOrganizer() {
		return userIsOrganizer;
	}
	
	
	private int image;
	private String date;
	private String description;
	private String adress;
	private String peopleQuantity;
	private Boolean btn;
	private Boolean state;
	
	private Boolean userIsOrganizer;
	
	public GameData(
		int image,
		String date,
		String description,
		String adress,
		String peopleQuantity,
		Boolean btn,
		Boolean state,
		Boolean userIsOrganizer
	) {
		this.image = image;
		this.date = date;
		this.description = description;
		this.adress = adress;
		this.peopleQuantity = peopleQuantity;
		this.btn = btn;
		this.state = state;
		this.userIsOrganizer = userIsOrganizer;
	}
}
