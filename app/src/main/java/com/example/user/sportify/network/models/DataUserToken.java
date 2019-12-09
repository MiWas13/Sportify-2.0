package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataUserToken {
	
	@SerializedName("token")
	@Expose
	private String token;
	
	public String getToken() {
		return token;
	}
	
	public void setToken(final String token) {
		this.token = token;
	}
}
