package com.example.user.sportify.ui.feed.data;

public class CategoryData {
	
	private int mId;
	private int mIcon;
	private String mName;
	
	public int getIcon() {
		return mIcon;
	}
	
	public void setIcon(final int icon) {
		mIcon = icon;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(final String name) {
		mName = name;
	}
	
	public int getId() {
		return mId;
	}
	
	public void setId(final int id) {
		mId = id;
	}
	
	
	public CategoryData(final int id, final int icon, final String name) {
		mIcon = id;
		mIcon = icon;
		mName = name;
	}
	
}
