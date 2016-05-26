package com.cmbc.devops.model;

/**
 * @author langzi
 *
 */
public class ClusterModel extends PageModel {

	private Integer clusterId;
	private String clusterUuid;
	private String clusterName;
	private Byte clusterType;
	private Byte clusterStatus;
	private String clusterPort;
	private int clusterMode;
	private String managePath;
	private String clusterDesc;
	private Integer masteHostId;
	private String logFile;
	private Integer userId;
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
		this.clusterUuid = clusterUuid;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
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
		this.clusterPort = clusterPort;
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
		this.managePath = managePath;
	}

	public String getClusterDesc() {
		return clusterDesc;
	}

	public void setClusterDesc(String clusterDesc) {
		this.clusterDesc = clusterDesc;
	}

	public Integer getMasteHostId() {
		return masteHostId;
	}

	public void setMasteHostId(Integer masteHostId) {
		this.masteHostId = masteHostId;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public int getResType() {
		return resType;
	}

	public void setResType(int resType) {
		this.resType = resType;
	}
}