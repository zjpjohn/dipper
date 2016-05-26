package com.cmbc.devops.model;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**  
 * date：2015年12月10日 上午11:08:32  
 * project name：cmbc-devops-common  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ApplicationDataModel.java  
 * description：  
 */
public class ApplicationDataModel {
	private int appId;
	private String appName;
	private Integer imageId;
	private String appVersion;
	private int appNum;
	private int runNum;
	private int maintenanceNum;
	@JsonSerialize(using = DateSerializer.class)
	private Date updateTime;
	private String appUrl;
	private String imageName;
	
	private Integer balanceId;
	private Integer appCpu;
	
	public ApplicationDataModel(int appId, String appName, int imageId, String appVersion, int appNum, int runNum,
			int maintenanceNum, Date updateTime, String appUrl, String imageName) {
		super();
		this.appId = appId;
		this.appName = appName;
		this.imageId = imageId;
		this.appVersion = appVersion;
		this.appNum = appNum;
		this.runNum = runNum;
		this.maintenanceNum = maintenanceNum;
		this.updateTime = updateTime;
		this.appUrl = appUrl;
		this.imageName = imageName;
	}

	public ApplicationDataModel() {
	}
	
	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	public int getAppNum() {
		return appNum;
	}

	public void setAppNum(int appNum) {
		this.appNum = appNum;
	}

	public int getRunNum() {
		return runNum;
	}

	public void setRunNum(int runNum) {
		this.runNum = runNum;
	}

	public int getMaintenanceNum() {
		return maintenanceNum;
	}

	public void setMaintenanceNum(int maintenanceNum) {
		this.maintenanceNum = maintenanceNum;
	}

	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getAppUrl() {
		return appUrl;
	}
	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Integer getBalanceId() {
		return balanceId;
	}

	public void setBalanceId(Integer balanceId) {
		this.balanceId = balanceId;
	}

	@Override
	public String toString() {
		return "ApplicationDataModel [appId=" + appId + ", appName=" + appName + ", imageId=" + imageId
				+ ", appVersion=" + appVersion + ", appNum=" + appNum + ", runNum=" + runNum + ", maintenanceNum="
				+ maintenanceNum + ", updateTime=" + updateTime + ", appUrl=" + appUrl + ", imageName=" + imageName
				+ "]";
	}

	public Integer getAppCpu() {
		return appCpu;
	}

	public void setAppCpu(Integer appCpu) {
		this.appCpu = appCpu;
	}

}
