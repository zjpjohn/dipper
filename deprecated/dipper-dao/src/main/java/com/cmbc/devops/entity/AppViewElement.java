package com.cmbc.devops.entity;

public class AppViewElement {
	private String label;
	private Float data;
	private String color;

	public AppViewElement(String label, Float data, String color) {
		this.label = label;
		this.data = data;
		this.color = color;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Float getData() {
		return data;
	}

	public void setData(Float data) {
		this.data = data;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
