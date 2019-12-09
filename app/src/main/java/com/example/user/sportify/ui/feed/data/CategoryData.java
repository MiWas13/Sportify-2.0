package com.example.user.sportify.ui.feed.data;

public class CategoryData {
	
	private int id;
	private int icon;
	private String name;
	
	public int getIcon() {
		return icon;
	}
	
	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public CategoryData(int id, int icon, String name) {
		this.id = id;
		this.icon = icon;
		this.name = name;
	}
	
}
