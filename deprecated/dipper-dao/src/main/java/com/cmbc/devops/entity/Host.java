package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Host {
	private Integer hostId;

	private String hostUuid;

	private String hostName;

	private String hostUser;

	private String hostPwd;

	private Byte hostType;

	private String hostIp;

	private Integer hostCpu;

	private Integer hostMem;

	private Byte hostStatus;

	private String hostDesc;
	
	private String hostRealName;

	private String hostKernelVersion;

	private Integer clusterId;

	@JsonSerialize(using = DateSerializer.class)
	private Date hostCreatetime;

	private Integer hostCreator;

	private Integer tenantId;
	
	//所在集群名称
	private String clusterName;

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
		this.hostUuid = hostUuid == null ? null : hostUuid.trim();
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName == null ? null : hostName.trim();
	}

	public String getHostUser() {
		return hostUser;
	}

	public void setHostUser(String hostUser) {
		this.hostUser = hostUser == null ? null : hostUser.trim();
	}

	public String getHostPwd() {
		return hostPwd;
	}

	public void setHostPwd(String hostPwd) {
		this.hostPwd = hostPwd == null ? null : hostPwd.trim();
	}

	public Byte getHostType() {
		return hostType;
	}

	public void setHostType(Byte hostType) {
		this.hostType = hostType;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp == null ? null : hostIp.trim();
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
		this.hostDesc = hostDesc == null ? null : hostDesc.trim();
	}
	
	public String getHostRealName() {
		return hostRealName;
	}

	public void setHostRealName(String hostRealName) {
		this.hostRealName = hostRealName;
	}

	public String getHostKernelVersion() {
		return hostKernelVersion;
	}

	public void setHostKernelVersion(String hostKernelVersion) {
		this.hostKernelVersion = hostKernelVersion == null ? null : hostKernelVersion.trim();
	}

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
		this.clusterId = clusterId;
	}

	public Date getHostCreatetime() {
		return hostCreatetime;
	}

	public void setHostCreatetime(Date hostCreatetime) {
		this.hostCreatetime = hostCreatetime;
	}

	public Integer getHostCreator() {
		return hostCreator;
	}

	public void setHostCreator(Integer hostCreator) {
		this.hostCreator = hostCreator;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

}