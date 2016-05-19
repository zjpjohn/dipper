package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ApplicationWithBalanceName {

	private Integer appId;

	private String appName;

	private Byte appStatus;
	
	private Byte appType;

	private String appDesc;

	private Integer balanceId;

	private String balanceName;

	private String appUrl;
	
	private Integer appPort;

	@JsonSerialize(using = DateSerializer.class)
	private Date appCreatetime;

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName == null ? null : appName.trim();
	}

	public Byte getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(Byte appStatus) {
		this.appStatus = appStatus;
	}
	
	public Byte getAppType() {
		return appType;
	}

	public void setAppType(Byte appType) {
		this.appType = appType;
	}

	public String getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc == null ? null : appDesc.trim();
	}

	public Integer getBalanceId() {
		return balanceId;
	}

	public void setBalanceId(Integer balanceId) {
		this.balanceId = balanceId;
	}

	public String getBalanceName() {
		return balanceName;
	}

	public void setBalanceName(String balanceName) {
		this.balanceName = balanceName;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl == null ? null : appUrl.trim();
	}
	
	public Integer getAppPort() {
		return appPort;
	}

	public void setAppPort(Integer appPort) {
		this.appPort = appPort;
	}

	public Date getAppCreatetime() {
		return appCreatetime;
	}

	public void setAppCreatetime(Date appCreatetime) {
		this.appCreatetime = appCreatetime;
	}

}
