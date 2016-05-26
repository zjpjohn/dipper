package com.cmbc.devops.entity;

public class ParamElement {
	private String paramName;
	private String paramKey;
	private String paramConnector;
	private String paramValue;
	private int paramRepeat;
	private String paramRemark;

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamConnector() {
		return paramConnector;
	}

	public void setParamConnector(String paramConnector) {
		this.paramConnector = paramConnector;
	}

	public int getParamRepeat() {
		return paramRepeat;
	}

	public void setParamRepeat(int paramRepeat) {
		this.paramRepeat = paramRepeat;
	}

	public String getParamRemark() {
		return paramRemark;
	}

	public void setParamRemark(String paramRemark) {
		this.paramRemark = paramRemark;
	}
	
}
