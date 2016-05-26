package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClusterWithHostContainerNum {
	private Integer clusterId;

	private String clusterUuid;

	private String clusterName;

	private Byte clusterType;

	private Byte clusterStatus;

	private String clusterPort;

	private String managePath;

	private String clusterDesc;

	private Integer hostQuantity;

	private Integer containerQuantity;

	private Integer masteHostId;

	private String masteHostIPaddr;

	@JsonSerialize(using = DateSerializer.class)
	private Date clusterCreatetime;

	private Integer clusterCreator;

	private String createUsername;

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

	public String getManagePath() {
		return managePath;
	}

	public void setManagePath(String managePath) {
		this.managePath = managePath == null ? null : managePath.trim();
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

	public Integer getHostQuantity() {
		return hostQuantity;
	}

	public void setHostQuantity(Integer hostQuantity) {
		this.hostQuantity = hostQuantity;
	}

	public Integer getContainerQuantity() {
		return containerQuantity;
	}

	public void setContainerQuantity(Integer containerQuantity) {
		this.containerQuantity = containerQuantity;
	}

	public String getMasteHostIPaddr() {
		return masteHostIPaddr;
	}

	public void setMasteHostIPaddr(String masteHostIPaddr) {
		this.masteHostIPaddr = masteHostIPaddr;
	}

	public String getCreateUsername() {
		return createUsername;
	}

	public void setCreateUsername(String createUsername) {
		this.createUsername = createUsername;
	}
}