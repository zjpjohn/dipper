package com.cmbc.devops.model;

/**
 * date：2015年8月20日 下午4:56:10 project name：cmbc-devops-common
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ContainerModel.java description：
 */
public class ContainerModel extends PageModel {

	private String appId;
	private Integer imageId;
	private String imageName;
	private Integer clusterId;
	private Integer hostId;
	private String createModel;
	private Integer lastConId;
	private Integer conNumber;
	private String conName;
	private String conPower;
	private int appStatus;
	private int monitorStatus;
	private String conDesc;
	private String createParams;
	private int userId;
	private String monitorHostId;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
		this.clusterId = clusterId;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public String getCreateModel() {
		return createModel;
	}

	public void setCreateModel(String createModel) {
		this.createModel = createModel;
	}

	public Integer getLastConId() {
		return lastConId;
	}

	public void setLastConId(Integer lastConId) {
		this.lastConId = lastConId;
	}

	public Integer getConNumber() {
		return conNumber;
	}

	public void setConNumber(Integer conNumber) {
		this.conNumber = conNumber;
	}

	public String getConName() {
		return conName;
	}

	public void setConName(String conName) {
		this.conName = conName;
	}

	public String getConPower() {
		return conPower;
	}

	public void setConPower(String conPower) {
		this.conPower = conPower;
	}

	public int getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}

	public int getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(int monitorStatus) {
		this.monitorStatus = monitorStatus;
	}

	public String getConDesc() {
		return conDesc;
	}

	public void setConDesc(String conDesc) {
		this.conDesc = conDesc;
	}

	public String getCreateParams() {
		return createParams;
	}

	public void setCreateParams(String createParams) {
		this.createParams = createParams;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getMonitorHostId() {
		return monitorHostId;
	}

	public void setMonitorHostId(String monitorHostId) {
		this.monitorHostId = monitorHostId;
	}

	@Override
	public String toString() {
		return "ContainerModel [appId=" + appId + ", imageId=" + imageId + ", imageName=" + imageName + ", clusterId="
				+ clusterId + ", hostId=" + hostId + ", createModel=" + createModel + ", lastConId=" + lastConId
				+ ", conNumber=" + conNumber + ", conName=" + conName + ", conPower=" + conPower + ", appStatus="
				+ appStatus + ", monitorStatus=" + monitorStatus + ", conDesc=" + conDesc + ", createParams="
				+ createParams + ", userId=" + userId + ", monitorHostId=" + monitorHostId + "]";
	}
	
}
