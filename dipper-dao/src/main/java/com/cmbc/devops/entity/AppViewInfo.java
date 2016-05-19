package com.cmbc.devops.entity;

import java.util.List;

public class AppViewInfo {
	private Integer appId;
	private String appName;
	private List<AppViewElement> cpuInfoList;
	private List<AppViewElement> memInfoList;

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
		this.appName = appName;
	}

	public List<AppViewElement> getCpuInfoList() {
		return cpuInfoList;
	}

	public void setCpuinfoList(List<AppViewElement> cpuInfoList) {
		this.cpuInfoList = cpuInfoList;
	}

	public List<AppViewElement> getMemInfoList() {
		return memInfoList;
	}

	public void setMeminfoList(List<AppViewElement> memInfoList) {
		this.memInfoList = memInfoList;
	}

}
