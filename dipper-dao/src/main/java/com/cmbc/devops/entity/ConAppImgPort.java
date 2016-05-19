package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ConAppImgPort {
	private Integer conId;

	private String conUuid;

	private Integer conImgid;

	private String imageTag;

	private Integer conCreator;

	private String conName;

	private Byte conPower;

	private Byte appStatus;

	private Byte monitorStatus;

	private Byte conStatus;

	private String conStartCommand;

	private String conStartParam;

	private Integer conCpu;

	private Integer conMem;

	private String conDesc;

	private Integer appId;

	private String appName;

	private String conPortInfo;

	private String monitorHostId;

	private String clusterIp;

	private String clusterPort;

	private Integer hostId;
	
	private String conIp;

	@JsonSerialize(using = DateSerializer.class)
	private Date conCreatetime;

	public Integer getConId() {
		return conId;
	}

	public void setConId(Integer conId) {
		this.conId = conId;
	}

	public String getConUuid() {
		return conUuid;
	}

	public void setConUuid(String conUuid) {
		this.conUuid = conUuid == null ? null : conUuid.trim();
	}

	public Integer getConImgid() {
		return conImgid;
	}

	public void setConImgid(Integer conImgid) {
		this.conImgid = conImgid;
	}

	public Integer getConCreator() {
		return conCreator;
	}

	public void setConCreator(Integer conCreator) {
		this.conCreator = conCreator;
	}

	public String getConName() {
		return conName;
	}

	public void setConName(String conName) {
		this.conName = conName == null ? null : conName.trim();
	}

	public Byte getConPower() {
		return conPower;
	}

	public void setConPower(Byte conPower) {
		this.conPower = conPower;
	}

	public Byte getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(Byte appStatus) {
		this.appStatus = appStatus;
	}

	public Byte getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(Byte monitorStatus) {
		this.monitorStatus = monitorStatus;
	}

	public Byte getConStatus() {
		return conStatus;
	}

	public void setConStatus(Byte conStatus) {
		this.conStatus = conStatus;
	}

	public String getConStartCommand() {
		return conStartCommand;
	}

	public void setConStartCommand(String conStartCommand) {
		this.conStartCommand = conStartCommand == null ? null : conStartCommand.trim();
	}

	public String getConStartParam() {
		return conStartParam;
	}

	public void setConStartParam(String conStartParam) {
		this.conStartParam = conStartParam == null ? null : conStartParam.trim();
	}

	public Integer getConCpu() {
		return conCpu;
	}

	public void setConCpu(Integer conCpu) {
		this.conCpu = conCpu;
	}

	public Integer getConMem() {
		return conMem;
	}

	public void setConMem(Integer conMem) {
		this.conMem = conMem;
	}

	public String getConDesc() {
		return conDesc;
	}

	public void setConDesc(String conDesc) {
		this.conDesc = conDesc == null ? null : conDesc.trim();
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public String getClusterIp() {
		return clusterIp;
	}

	public void setClusterIp(String clusterIp) {
		this.clusterIp = clusterIp;
	}

	public String getClusterPort() {
		return clusterPort;
	}

	public void setClusterPort(String clusterPort) {
		this.clusterPort = clusterPort;
	}

	public Date getConCreatetime() {
		return conCreatetime;
	}

	public void setConCreatetime(Date conCreatetime) {
		this.conCreatetime = conCreatetime;
	}

	public String getMonitorHostId() {
		return monitorHostId;
	}

	public void setMonitorHostId(String monitorHostId) {
		this.monitorHostId = monitorHostId;
	}

	public String getImageTag() {
		return imageTag;
	}

	public void setImageTag(String imageTag) {
		this.imageTag = imageTag;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getConPortInfo() {
		return conPortInfo;
	}

	public void setConPortInfo(String conPortInfo) {
		this.conPortInfo = conPortInfo;
	}

	public String getConIp() {
		return conIp;
	}

	public void setConIp(String conIp) {
		this.conIp = conIp;
	}

}