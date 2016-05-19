package com.cmbc.devops.entity;

public class RegIdImageType {

	private Integer registryId;

	private Byte imageStatus;
	
	private Integer tenantId;

	public Integer getRegistryId() {
		return registryId;
	}

	public void setRegistryId(Integer registryId) {
		this.registryId = registryId;
	}

	public Byte getImageStatus() {
		return imageStatus;
	}

	public void setImageStatus(Byte imageStatus) {
		this.imageStatus = imageStatus;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}
	
	
}
