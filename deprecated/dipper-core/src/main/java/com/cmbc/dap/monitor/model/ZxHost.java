package com.cmbc.dap.monitor.model;

import java.util.List;

public class ZxHost {
	private String hostId;
	private String hostName;
	private List<ZxItem> itemList;

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public List<ZxItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ZxItem> itemList) {
		this.itemList = itemList;
	}

}
