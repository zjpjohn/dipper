package com.cmbc.devops.model;

public class HostResourceModel {
	
	private Integer[] hostCpuCore;
	
	private String hostRealName;
	
	private Integer hostId;
	
	public HostResourceModel() {
		
	}
	
	public HostResourceModel(Integer[] hostCpuCore, String hostRealName, Integer hostId) {
		super();
		this.hostCpuCore = hostCpuCore;
		this.hostRealName = hostRealName;
		this.hostId = hostId;
	}
	
	public Integer[] getHostCpuCore() {
		return hostCpuCore;
	}

	public void setHostCpuCore(Integer[] hostCpuCore) {
		this.hostCpuCore = hostCpuCore;
	}

	public String gethostRealName() {
		return hostRealName;
	}

	public void sethostRealName(String hostRealName) {
		this.hostRealName = hostRealName;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}
	
	
}
