package com.cmbc.dap.monitor.model;

import java.util.List;

public class ZxHostGroup {
	private String groupId;
	private String groupName;
	private List<ZxHost> hostList;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<ZxHost> getHostList() {
		return hostList;
	}

	public void setHostList(List<ZxHost> hostList) {
		this.hostList = hostList;
	}

}
