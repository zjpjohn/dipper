package com.cmbc.devops.entity;

import org.springframework.beans.BeanUtils;

public class SoftwareWithCreator extends Software {
	private String swCreatorName;

	public SoftwareWithCreator() {

	}

	public SoftwareWithCreator(Software software) {
		super();
		BeanUtils.copyProperties(software, this);
	}

	public String getSwCreatorName() {
		return swCreatorName;
	}

	public void setSwCreatorName(String swCreatorName) {
		this.swCreatorName = swCreatorName;
	}

}
