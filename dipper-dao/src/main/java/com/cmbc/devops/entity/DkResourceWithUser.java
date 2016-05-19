package com.cmbc.devops.entity;

import org.springframework.beans.BeanUtils;

public final class DkResourceWithUser extends DkResource {

	private String resUserName;

	public DkResourceWithUser(DkResource dkResource) {
		BeanUtils.copyProperties(dkResource, this);
	}

	public DkResourceWithUser(DkResource dkResource, User user) {
		BeanUtils.copyProperties(dkResource, this);
		this.resUserName = user.getUserName();
	}

	public String getResUserName() {
		return resUserName;
	}

	public void setResUserName(String resUserName) {
		this.resUserName = resUserName;
	}

}
