package com.cmbc.dap.monitor.model;

public class ZxNodeInfo {
	private String hostId;
	private String hostName;
	private Float itemCpuLoad;
	private Float itemMemLoad;
	private Float itemDiskLoad;

	public ZxNodeInfo(String hostId, String hostName, Float cpuLoad, Float memLoad, Float diskLoad) {
		this.hostId = hostId;
		this.hostName = hostName;
		this.itemCpuLoad = cpuLoad;
		this.itemMemLoad = memLoad;
		this.itemDiskLoad = diskLoad;
	}

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

	public Float getItemCpuLoad() {
		return itemCpuLoad;
	}

	public void setItemCpuLoad(Float itemCpuLoad) {
		this.itemCpuLoad = itemCpuLoad;
	}

	public Float getItemMemLoad() {
		return itemMemLoad;
	}

	public void setItemMemLoad(Float itemMemLoad) {
		this.itemMemLoad = itemMemLoad;
	}

	public Float getItemDiskLoad() {
		return itemDiskLoad;
	}

	public void setItemDiskLoad(Float itemDiskLoad) {
		this.itemDiskLoad = itemDiskLoad;
	}

}
