package com.cmbc.dap.monitor.model;

import java.util.List;

public class ZxSet {
	private String serverIp;
	private String zxUserName;
	private String zxPassword;
	private String setId;
	private String setName;
	private String setKey;
	private List<ZxHostGroup> groupList;

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getZxUserName() {
		return zxUserName;
	}

	public void setZxUserName(String zxUserName) {
		this.zxUserName = zxUserName;
	}

	public String getZxPassword() {
		return zxPassword;
	}

	public void setZxPassword(String zxPassword) {
		this.zxPassword = zxPassword;
	}

	public String getSetId() {
		return setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public String getSetKey() {
		return setKey;
	}

	public void setSetKey(String setKey) {
		this.setKey = setKey;
	}

	public List<ZxHostGroup> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<ZxHostGroup> groupList) {
		this.groupList = groupList;
	}

}
