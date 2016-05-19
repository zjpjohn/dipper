package com.cmbc.devops.entity;

import java.util.Date;
import java.util.List;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class AppAuxiliaryCluster {
	private Integer appId;

	private String envIds;

	private String clusterIds;

	private String clusterNames;

	private String appName;

	private Byte appStatus;

	private Integer appCpu;

	private Integer appMem;

	private Boolean appPortMap;

	private Integer appPubPort;

	private Integer appPriPort;

	private String appEnv;

	private String envNames;

	private Integer balanceId;

	private String appVolumn;

	private String appParams;

	private Byte appHealth;

	private Byte appMonitor;

	private String appCommand;

	private String appUrl;

	private String appProxy;

	private String appGrayPolicy;

	private String appDesc;

	private List<ClusterAuxiliaryImage> clusterList;

	@JsonSerialize(using = DateSerializer.class)
	private Date appCreatetime;

	private Integer appCreator;

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getEnvIds() {
		return envIds;
	}

	public void setEnvIds(String envIds) {
		this.envIds = envIds;
	}

	public String getClusterIds() {
		return clusterIds;
	}

	public void setClusterIds(String clusterIds) {
		this.clusterIds = clusterIds;
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

	public Integer getAppCpu() {
		return appCpu;
	}

	public void setAppCpu(Integer appCpu) {
		this.appCpu = appCpu;
	}

	public Integer getAppMem() {
		return appMem;
	}

	public void setAppMem(Integer appMem) {
		this.appMem = appMem;
	}

	public Boolean getAppPortMap() {
		return appPortMap;
	}

	public void setAppPortMap(Boolean appPortMap) {
		this.appPortMap = appPortMap;
	}

	public Integer getAppPubPort() {
		return appPubPort;
	}

	public void setAppPubPort(Integer appPubPort) {
		this.appPubPort = appPubPort;
	}

	public Integer getAppPriPort() {
		return appPriPort;
	}

	public void setAppPriPort(Integer appPriPort) {
		this.appPriPort = appPriPort;
	}

	public String getAppEnv() {
		return appEnv;
	}

	public void setAppEnv(String appEnv) {
		this.appEnv = appEnv == null ? null : appEnv.trim();
	}

	public Integer getBalanceId() {
		return balanceId;
	}

	public void setBalanceId(Integer balanceId) {
		this.balanceId = balanceId;
	}

	public String getAppVolumn() {
		return appVolumn;
	}

	public void setAppVolumn(String appVolumn) {
		this.appVolumn = appVolumn == null ? null : appVolumn.trim();
	}

	public String getAppCommand() {
		return appCommand;
	}

	public void setAppCommand(String appCommand) {
		this.appCommand = appCommand == null ? null : appCommand.trim();
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl == null ? null : appUrl.trim();
	}

	public String getAppProxy() {
		return appProxy;
	}

	public void setAppProxy(String appProxy) {
		this.appProxy = appProxy;
	}

	public String getAppGrayPolicy() {
		return appGrayPolicy;
	}

	public void setAppGrayPolicy(String appGrayPolicy) {
		this.appGrayPolicy = appGrayPolicy;
	}

	public String getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc == null ? null : appDesc.trim();
	}

	public String getAppParams() {
		return appParams;
	}

	public void setAppParams(String appParams) {
		this.appParams = appParams;
	}

	public Byte getAppHealth() {
		return appHealth;
	}

	public void setAppHealth(Byte appHealth) {
		this.appHealth = appHealth;
	}

	public Byte getAppMonitor() {
		return appMonitor;
	}

	public void setAppMonitor(Byte appMonitor) {
		this.appMonitor = appMonitor;
	}

	public Date getAppCreatetime() {
		return appCreatetime;
	}

	public void setAppCreatetime(Date appCreatetime) {
		this.appCreatetime = appCreatetime;
	}

	public Integer getAppCreator() {
		return appCreator;
	}

	public void setAppCreator(Integer appCreator) {
		this.appCreator = appCreator;
	}

	public String getClusterNames() {
		return clusterNames;
	}

	public void setClusterNames(String clusterNames) {
		this.clusterNames = clusterNames;
	}

	public String getEnvNames() {
		return envNames;
	}

	public void setEnvNames(String envNames) {
		this.envNames = envNames;
	}

	public List<ClusterAuxiliaryImage> getClusterList() {
		return clusterList;
	}

	public void setClusterList(List<ClusterAuxiliaryImage> clusterList) {
		this.clusterList = clusterList;
	}

	@Override
	public String toString() {
		return "App [appId=" + appId + ", envIds=" + envIds + ", clusterIds=" + clusterIds + ", clusterNames="
				+ clusterNames + ", appName=" + appName + ", appStatus=" + appStatus + ", appCpu=" + appCpu
				+ ", appMem=" + appMem + ", appPortMap=" + appPortMap + ", appPubPort=" + appPubPort + ", appPriPort="
				+ appPriPort + ", appEnv=" + appEnv + ", envNames=" + envNames + ", balanceId=" + balanceId
				+ ", appVolumn=" + appVolumn + ", appParams=" + appParams + ", appHealth=" + appHealth + ", appMonitor="
				+ appMonitor + ", appCommand=" + appCommand + ", appUrl=" + appUrl + ", appProxy=" + appProxy
				+ ", appGrayPolicy=" + appGrayPolicy + ", appDesc=" + appDesc + ", appCreatetime=" + appCreatetime
				+ ", appCreator=" + appCreator + "]";
	}

}