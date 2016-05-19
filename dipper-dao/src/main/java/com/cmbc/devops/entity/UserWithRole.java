package com.cmbc.devops.entity;

import org.springframework.beans.BeanUtils;

public class UserWithRole extends User {

	private String roleName;

	public UserWithRole(User user) {
		BeanUtils.copyProperties(user, this);
	}

	public UserWithRole(User user, Role role) {
		BeanUtils.copyProperties(user, this);
		this.roleName = role.getRoleName();
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}