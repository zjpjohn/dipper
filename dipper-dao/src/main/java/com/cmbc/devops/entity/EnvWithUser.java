package com.cmbc.devops.entity;

import org.springframework.beans.BeanUtils;

public final class EnvWithUser extends Env {
	private String userName;

	public EnvWithUser(Env env) {
		BeanUtils.copyProperties(env, this);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}