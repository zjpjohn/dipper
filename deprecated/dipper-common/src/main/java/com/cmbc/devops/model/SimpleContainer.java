package com.cmbc.devops.model;

import com.alibaba.fastjson.JSONObject;

/**  
 * date：2015年9月10日 上午9:56:35  
 * project name：cmbc-devops-dao  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：SimpleContainer.java  
 * description：  
 */
public class SimpleContainer {
	
	private Integer containerId;
	private String containerUuid;
	private String clusterIp;
	private String clusterPort;
	private byte monitorStatus;
	private String monitorHostId;
	
	private JSONObject conJo;
	
	public SimpleContainer() {
	}
	
	public SimpleContainer(String containerUuid, String clusterIp,
			String clusterPort, String monitorHostId) {
		super();
		this.containerUuid = containerUuid;
		this.clusterIp = clusterIp;
		this.clusterPort = clusterPort;
		this.monitorHostId = monitorHostId;
	}
	
	public Integer getContainerId() {
		return containerId;
	}

	public void setContainerId(Integer containerId) {
		this.containerId = containerId;
	}

	public String getContainerUuid() {
		return containerUuid;
	}
	public void setContainerUuid(String containerUuid) {
		this.containerUuid = containerUuid;
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
	
	public byte getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(byte monitorStatus) {
		this.monitorStatus = monitorStatus;
	}

	public String getMonitorHostId() {
		return monitorHostId;
	}

	public void setMonitorHostId(String monitorHostId) {
		this.monitorHostId = monitorHostId;
	}

	public JSONObject getConJo() {
		return conJo;
	}

	public void setConJo(JSONObject conJo) {
		this.conJo = conJo;
	}
	
}
