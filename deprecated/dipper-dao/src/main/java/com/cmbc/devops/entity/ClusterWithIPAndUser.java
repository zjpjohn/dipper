package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClusterWithIPAndUser {
	private Integer clusterId;

	private String clusterUuid;

	private String clusterName;

	private Byte clusterType;

	private Byte clusterStatus;

	private String clusterPort;

	private int clusterMode;

	private String managePath;

	private String clusterLogFile;

	private String clusterDesc;

	private Integer masteHostId;

	private String hostIP;

	private String creatorName;

	@JsonSerialize(using = DateSerializer.class)
	private Date clusterCreatetime;

	private Integer clusterCreator;

	private Integer tenantId;
	
	private Integer hostNum;
	private Integer CPU;
	private Integer MEM;
	
	private int resType;

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

	public int getClusterMode() {
		return clusterMode;
	}

	public void setClusterMode(int clusterMode) {
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

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getHostNum() {
		return hostNum;
	}

	public void setHostNum(Integer hostNum) {
		this.hostNum = hostNum;
	}

	public Integer getCPU() {
		return CPU;
	}

	public void setCPU(Integer cPU) {
		CPU = cPU;
	}

	public Integer getMEM() {
		return MEM;
	}

	public void setMEM(Integer mEM) {
		MEM = mEM;
	}

	public int getResType() {
		return resType;
	}

	public void setResType(int resType) {
		this.resType = resType;
	}
	
	
}