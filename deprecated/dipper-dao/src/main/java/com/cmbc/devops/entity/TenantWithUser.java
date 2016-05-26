package com.cmbc.devops.entity;

import org.springframework.beans.BeanUtils;

public class TenantWithUser extends Tenant {
	private String userName;

	public TenantWithUser(Tenant tenant, User user) {
		BeanUtils.copyProperties(tenant, this);
		this.userName = user.getUserName();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
