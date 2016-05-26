package com.cmbc.devops.entity;

import java.util.Date;
import java.util.List;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClusterAuxiliaryImage {
	private Integer clusterId;

	private String clusterUuid;

	private String clusterName;

	private Byte clusterType;

	private Byte clusterStatus;

	private String clusterPort;

	private Byte clusterMode;

	private String managePath;

	private String clusterLogFile;

	private String clusterDesc;

	private Integer masteHostId;
	@JsonSerialize(using = DateSerializer.class)
	private Date clusterCreatetime;

	private Integer clusterCreator;

	private List<ImageAuxiliaryContainer> imageList;

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
		this.clusterId = clusterId;
	}

	public String getClusterUuid() {
		return clusterUuid;
	}

	public void setClusterUuid(String clusterUuid) {
		this.clusterUuid = clusterUuid == null ? null : clusterUuid.trim();
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName == null ? null : clusterName.trim();
	}

	public Byte getClusterType() {
		return clusterType;
	}

	public void setClusterType(Byte clusterType) {
		this.clusterType = clusterType;
	}

	public Byte getClusterStatus() {
		return clusterStatus;
	}

	public void setClusterStatus(Byte clusterStatus) {
		this.clusterStatus = clusterStatus;
	}

	public String getClusterPort() {
		return clusterPort;
	}

	public void setClusterPort(String clusterPort) {
		this.clusterPort = clusterPort == null ? null : clusterPort.trim();
	}

	public Byte getClusterMode() {
		return clusterMode;
	}

	public void setClusterMode(Byte clusterMode) {
		this.clusterMode = clusterMode;
	}

	public String getManagePath() {
		return managePath;
	}

	public void setManagePath(String managePath) {
		this.managePath = managePath == null ? null : managePath.trim();
	}

	public String getClusterLogFile() {
		return clusterLogFile;
	}

	public void setClusterLogFile(String clusterLogFile) {
		this.clusterLogFile = clusterLogFile;
	}

	public String getClusterDesc() {
		return clusterDesc;
	}

	public void setClusterDesc(String clusterDesc) {
		this.clusterDesc = clusterDesc == null ? null : clusterDesc.trim();
	}

	public Integer getMasteHostId() {
		return masteHostId;
	}

	public void setMasteHostId(Integer masteHostId) {
		this.masteHostId = masteHostId;
	}

	public Date getClusterCreatetime() {
		return clusterCreatetime;
	}

	public void setClusterCreatetime(Date clusterCreatetime) {
		this.clusterCreatetime = clusterCreatetime;
	}

	public Integer getClusterCreator() {
		return clusterCreator;
	}

	public void setClusterCreator(Integer clusterCreator) {
		this.clusterCreator = clusterCreator;
	}

	public List<ImageAuxiliaryContainer> getImageList() {
		return imageList;
	}

	public void setImageList(List<ImageAuxiliaryContainer> imageList) {
		this.imageList = imageList;
	}
}