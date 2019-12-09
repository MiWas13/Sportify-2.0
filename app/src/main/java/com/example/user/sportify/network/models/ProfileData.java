package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileData {
	
	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("token")
	@Expose
	private String token;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("phone")
	@Expose
	private String phone;
	@SerializedName("password")
	@Expose
	private String password;
	@SerializedName("age")
	@Expose
	private String age;
	@SerializedName("showphone")
	@Expose
	private String showphone;
	@SerializedName("games_created")
	@Expose
	private String gamesCreated;
	@SerializedName("games_played")
	@Expose
	private String gamesPlayed;
	
	public String getId() {
		return id;
	}
	
	public void setId(final String id) {
		this.id = id;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(final String token) {
		this.token = token;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(final String phone) {
		this.phone = phone;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(final String password) {
		this.password = password;
	}
	
	public String getAge() {
		return age;
	}
	
	public void setAge(final String age) {
		this.age = age;
	}
	
	public String getShowphone() {
		return showphone;
	}
	
	public void setShowphone(final String showphone) {
		this.showphone = showphone;
	}
	
	public String getGamesCreated() {
		return gamesCreated;
	}
	
	public void setGamesCreated(final String gamesCreated) {
		this.gamesCreated = gamesCreated;
	}
	
	public String getGamesPlayed() {
		return gamesPlayed;
	}
	
	public void setGamesPlayed(final String gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}
	
}
