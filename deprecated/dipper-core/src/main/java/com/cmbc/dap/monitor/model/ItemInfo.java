package com.cmbc.dap.monitor.model;

import java.io.Serializable;

public class ItemInfo implements Serializable {

	/**
	 * 除了写明需要银行提供的内容，其他暂时不需要
	 */
	private static final long serialVersionUID = 1L;
	
	private String hostId;
	private String itemName;//需要银行提供
	private String itemKey;//需要银行提供
	private String interfaceId;//主机IP：port：10050
	private int type;//agent类型，默认为2 Zabbix trapper.
	private int valueType;//默认为0（数字，浮点型）
	private int delay;//自定义，延迟时间
	private int inventoryLink;

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getValueType() {
		return valueType;
	}

	public void setValueType(int valueType) {
		this.valueType = valueType;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getInventoryLink() {
		return inventoryLink;
	}

	public void setInventoryLink(int inventoryLink) {
		this.inventoryLink = inventoryLink;
	}

	public ItemInfo(String hostId, String itemName, String itemKey, String interfaceId, int type, int valueType,
			int delay, int inventoryLink) {
		super();
		this.hostId = hostId;
		this.itemName = itemName;
		this.itemKey = itemKey;
		this.interfaceId = interfaceId;
		this.type = type;
		this.valueType = valueType;
		this.delay = delay;
		this.inventoryLink = inventoryLink;
	}

	
	public ItemInfo(String hostId, String itemKey, String interfaceId, int delay) {
		super();
		this.hostId = hostId;
		this.itemName = itemKey;
		this.itemKey = itemKey;
		this.interfaceId = interfaceId;
		this.type = 2;
		this.valueType = 0;
		this.delay = delay;
	}

	public ItemInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "ItemIntf [hostId=" + hostId + ", itemName=" + itemName + ", itemKey=" + itemKey + ", interfaceId="
				+ interfaceId + ", type=" + type + ", valueType=" + valueType + ", delay=" + delay + ", inventoryLink="
				+ inventoryLink + "]";
	}

}
