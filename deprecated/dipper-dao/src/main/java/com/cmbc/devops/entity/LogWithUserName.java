package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class LogWithUserName {

	private Integer logId;

	private String logObject;

	private String logAction;

	private String logResult;

	private Integer userId;

	private String userName;

	private String userIp;
	@JsonSerialize(using = DateSerializer.class)
	private Date logCreatetime;

	@JsonSerialize(using = DateSerializer.class)
	private Date beginTime;
	@JsonSerialize(using = DateSerializer.class)
	private Date endTime;
	private String logDetail;

	public LogWithUserName() {

	}

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public String getLogObject() {
		return logObject;
	}

	public void setLogObject(String logObject) {
		this.logObject = logObject;
	}

	public String getLogAction() {
		return logAction;
	}

	public void setLogAction(String logAction) {
		this.logAction = logAction;
	}

	public String getLogResult() {
		return logResult;
	}

	public void setLogResult(String logResult) {
		this.logResult = logResult;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public Date getLogCreatetime() {
		return logCreatetime;
	}

	public void setLogCreatetime(Date logCreatetime) {
		this.logCreatetime = logCreatetime;
	}

	public String getLogDetail() {
		return logDetail;
	}

	public void setLogDetail(String logDetail) {
		this.logDetail = logDetail;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "Log [logId=" + logId + ", logObject=" + logObject + ", logAction=" + logAction + ", logResult="
				+ logResult + ", userId=" + userId + ", userIp=" + userIp + ", logCreatetime=" + logCreatetime
				+ ", logDetail=" + logDetail + "]";
	}

	public LogWithUserName(Integer logId, String logObject, String logAction, String logResult, Integer userId,
			String userIp, Date logCreatetime, String logDetail) {
		super();
		this.logId = logId;
		this.logObject = logObject;
		this.logAction = logAction;
		this.logResult = logResult;
		this.userId = userId;
		this.userIp = userIp;
		this.logCreatetime = logCreatetime;
		this.logDetail = logDetail;
	}

}