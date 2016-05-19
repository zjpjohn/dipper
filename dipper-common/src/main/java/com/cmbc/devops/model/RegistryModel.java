/**
 * 
 */
package com.cmbc.devops.model;

/**  
 * date：2015年8月24日 上午12:22:54
 * project name：cmbc-devops-common
 * @author  mayh
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：RegistryModel.java
 * description：  
 */
public class RegistryModel extends PageModel {
	
	private int registryId;
	private String registryName;
	private int registryPort;
	private Integer hostId;
   private String registryDesc;

	public int getRegistryId() {
		return registryId;
	}

	public void setRegistryId(int registryId) {
		this.registryId = registryId;
	}

	public String getRegistryName() {
		return registryName;
	}

	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

	public int getRegistryPort() {
		return registryPort;
	}

	public void setRegistryPort(int registryPort) {
		this.registryPort = registryPort;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public String getRegistryDesc() {
		return registryDesc;
	}

	public void setRegistryDesc(String registryDesc) {
		this.registryDesc = registryDesc;
	}

}
