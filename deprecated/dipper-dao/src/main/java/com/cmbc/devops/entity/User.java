package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class User {

	private Integer userId;

	private String userName;

	private String userPass;

	private String userMail;

	private String userPhone;

	private String userCompany;

	private Integer userLevel;

	private Byte userStatus;

	private String userLoginStatus;

	private Integer userRoleid;
	@JsonSerialize(using = DateSerializer.class)
	private Date userCreatedate;

	private Integer userCreator;

	private String createUserName;

	private String roleString;
	/* #多租户情况，添加租户的ID信息 */
	private Integer tenantId;

	public User() {

	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass == null ? null : userPass.trim();
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail == null ? null : userMail.trim();
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone == null ? null : userPhone.trim();
	}

	public String getUserCompany() {
		return userCompany;
	}

	public void setUserCompany(String userCompany) {
		this.userCompany = userCompany == null ? null : userCompany.trim();
	}

	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

	public Byte getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Byte userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserLoginStatus() {
		return userLoginStatus;
	}

	public void setUserLoginStatus(String userLoginStatus) {
		this.userLoginStatus = userLoginStatus;
	}

	public Integer getUserRoleid() {
		return userRoleid;
	}

	public void setUserRoleid(Integer userRoleid) {
		this.userRoleid = userRoleid;
	}

	public Date getUserCreatedate() {
		return userCreatedate;
	}

	public void setUserCreatedate(Date userCreatedate) {
		this.userCreatedate = userCreatedate;
	}

	public Integer getUserCreator() {
		return userCreator;
	}

	public void setUserCreator(Integer userCreator) {
		this.userCreator = userCreator;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getRoleString() {
		return roleString;
	}

	public void setRoleString(String roleString) {
		this.roleString = roleString;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}	
}