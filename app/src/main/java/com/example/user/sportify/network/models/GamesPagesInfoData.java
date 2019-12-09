package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GamesPagesInfoData {
	
	@SerializedName("pages")
	@Expose
	private Integer pages;
	@SerializedName("count")
	@Expose
	private Integer count;
	
	public Integer getPages() {
		return pages;
	}
	
	public void setPages(final Integer pages) {
		this.pages = pages;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(final Integer count) {
		this.count = count;
	}
}
