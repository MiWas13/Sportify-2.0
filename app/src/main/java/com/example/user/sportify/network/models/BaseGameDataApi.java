package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseGameDataApi {
	
	@SerializedName("info")
	@Expose
	private GamesPagesInfoData info;
	@SerializedName("posts")
	@Expose
	private List<GameDataApi> posts = null;
	
	public GamesPagesInfoData getInfo() {
		return info;
	}
	
	public void setInfo(final GamesPagesInfoData info) {
		this.info = info;
	}
	
	public List<GameDataApi> getPosts() {
		return posts;
	}
	
	public void setPosts(final List<GameDataApi> posts) {
		this.posts = posts;
	}
	
}

