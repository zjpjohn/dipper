package com.cmbc.devops.model;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

public class HostModel extends PageModel {

	private Integer hostId;
	private String hostUuid;
	private String hostName;
	private String hostUser;
	private String hostPwd;
	private Integer hostType;
	private String hostIp;
	private Integer hostCpu;
	private Integer hostMem;
	private Byte hostStatus;
	private String hostDesc;
	private String hostKernelVersion;
	private Integer clusterId;
	private Integer creator;
	private Date createTime;
	
	private JSONObject hostJo;

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public String getHostUuid() {
		return hostUuid;
	}

	public void setHostUuid(String hostUuid) {
		this.hostUuid = hostUuid;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostUser() {
		return hostUser;
	}

	public void setHostUser(String hostUser) {
		this.hostUser = hostUser;
	}

	public String getHostPwd() {
		return hostPwd;
	}

	public void setHostPwd(String hostPwd) {
		this.hostPwd = hostPwd;
	}

	public Integer getHostType() {
		return hostType;
	}

	public void setHostType(Integer hostType) {
		this.hostType = hostType;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public Integer getHostCpu() {
		return hostCpu;
	}

	public void setHostCpu(Integer hostCpu) {
		this.hostCpu = hostCpu;
	}

	public Integer getHostMem() {
		return hostMem;
	}

	public void setHostMem(Integer hostMem) {
		this.hostMem = hostMem;
	}

	public Byte getHostStatus() {
		return hostStatus;
	}

	public void setHostStatus(Byte hostStatus) {
		this.hostStatus = hostStatus;
	}

	public String getHostDesc() {
		return hostDesc;
	}

	public void setHostDesc(String hostDesc) {
		this.hostDesc = hostDesc;
	}

	public String getHostKernelVersion() {
		return hostKernelVersion;
	}

	public void setHostKernelVersion(String hostKernelVersion) {
		this.hostKernelVersion = hostKernelVersion;
	}

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
		this.clusterId = clusterId;
	}

	public Integer getCreator() {
		return creator;
	}

	public void setCreator(Integer creator) {
		this.creator = creator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public JSONObject getHostJo() {
		return hostJo;
	}

	public void setHostJo(JSONObject hostJo) {
		this.hostJo = hostJo;
	}

}