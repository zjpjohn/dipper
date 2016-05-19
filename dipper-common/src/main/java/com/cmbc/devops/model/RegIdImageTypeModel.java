package com.cmbc.devops.model;

public class RegIdImageTypeModel extends PageModel {
	private Integer registryId;
	private Byte imageStatus;

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

}
