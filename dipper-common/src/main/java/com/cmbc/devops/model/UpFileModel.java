package com.cmbc.devops.model;

public class UpFileModel {

	private String fileStartName;
	private String fileEndName;
	private String hostList;
	private String fileSrc;
	private Integer hostId;
	private Integer appId;
	private String uuid;
	private String localUuid;

	public String getHostList() {
		return hostList;
	}

	public void setHostList(String hostList) {
		this.hostList = hostList;
	}

	public String getFileStartName() {
		return fileStartName;
	}

	public void setFileStartName(String fileStartName) {
		this.fileStartName = fileStartName;
	}

	public String getFileEndName() {
		return fileEndName;
	}

	public void setFileEndName(String fileEndName) {
		this.fileEndName = fileEndName;
	}

	public String getFileSrc() {
		return fileSrc;
	}

	public void setFileSrc(String fileSrc) {
		this.fileSrc = fileSrc;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLocalUuid() {
		return localUuid;
	}

	public void setLocalUuid(String localUuid) {
		this.localUuid = localUuid;
	}
}
