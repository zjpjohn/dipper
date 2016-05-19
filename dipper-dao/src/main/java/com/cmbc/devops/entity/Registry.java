package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Registry {
	private Integer registryId;

	private String registryName;

	private Integer registryPort;

	private Byte registryStatus;

	private Integer hostId;

	private String registryDesc;

	@JsonSerialize(using = DateSerializer.class)
	private Date registryCreatetime;

	private Integer registryCreator;

	private Integer tenantId;

	public Integer getRegistryId() {
		return registryId;
	}

	public void setRegistryId(Integer registryId) {
		this.registryId = registryId;
	}

	public String getRegistryName() {
		return registryName;
	}

	public void setRegistryName(String registryName) {
		this.registryName = registryName == null ? null : registryName.trim();
	}

	public Integer getRegistryPort() {
		return registryPort;
	}

	public void setRegistryPort(Integer registryPort) {
		this.registryPort = registryPort;
	}

	public Byte getRegistryStatus() {
		return registryStatus;
	}

	public void setRegistryStatus(Byte registryStatus) {
		this.registryStatus = registryStatus;
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
		this.registryDesc = registryDesc == null ? null : registryDesc.trim();
	}

	public Date getRegistryCreatetime() {
		return registryCreatetime;
	}

	public void setRegistryCreatetime(Date registryCreatetime) {
		this.registryCreatetime = registryCreatetime;
	}

	public Integer getRegistryCreator() {
		return registryCreator;
	}

	public void setRegistryCreator(Integer registryCreator) {
		this.registryCreator = registryCreator;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}
}