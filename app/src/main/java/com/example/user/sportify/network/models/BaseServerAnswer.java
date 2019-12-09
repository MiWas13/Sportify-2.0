package com.example.user.sportify.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseServerAnswer<T> {
	
	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("message")
	@Expose
	private T message;
	
	public Boolean getSuccess() {
		return success;
	}
	
	public void setSuccess(final Boolean success) {
		this.success = success;
	}
	
	public T getMessage() {
		return message;
	}
	
	public void setMessage(final T message) {
		this.message = message;
	}
}
