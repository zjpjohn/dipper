package com.cmbc.devops.bean;

public class MessageResult extends Result {

	public MessageResult(boolean success, String message) {
		super(success, message);
	}

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public MessageResult(boolean success, String message, String title) {
		super(success, message);
		this.title = title;
	}

	@Override
	public String toString() {
		return "MessageResult [title=" + title + ", isSuccess()=" + isSuccess() + ", getMessage()=" + getMessage()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}


}
