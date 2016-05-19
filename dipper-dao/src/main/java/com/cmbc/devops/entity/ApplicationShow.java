package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ApplicationShow {
	private Integer appId;
	private String appName;
	private Byte appStatus;
	private Byte appType;
	private String appDesc;
	private Integer balanceId;
	private String appUrl;
	private Integer appPort;
	/** 新增加关于应用发布显示的内容 *****/
	// 关于镜像部分的内容
	private Integer imageId;
	private String imageUuid;
	private String imageName;
	private String imageTag;
	// 容器部分的内容
	private Integer instTotal;
	private Integer instRun;
	private Integer instStop;
	private Integer instHealthy;
	private Integer instException;
	private Integer instInView;
	private Integer instOutView;
	@JsonSerialize(using = DateSerializer.class)
	private Date conUpdatetime;

	@JsonSerialize(using = DateSerializer.class)
	private Date appCreatetime;

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName == null ? null : appName.trim();
	}

	public Byte getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(Byte appStatus) {
		this.appStatus = appStatus;
	}

	public Byte getAppType() {
		return appType;
	}

	public void setAppType(Byte appType) {
		this.appType = appType;
	}

	public String getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc == null ? null : appDesc.trim();
	}

	public Integer getBalanceId() {
		return balanceId;
	}

	public void setBalanceId(Integer balanceId) {
		this.balanceId = balanceId;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl == null ? null : appUrl.trim();
	}

	public Integer getAppPort() {
		return appPort;
	}

	public void setAppPort(Integer appPort) {
		this.appPort = appPort;
	}

	public Date getAppCreatetime() {
		return appCreatetime;
	}

	public void setAppCreatetime(Date appCreatetime) {
		this.appCreatetime = appCreatetime;
	}

	public Integer getImageId() {
		return imageId;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public String getImageUuid() {
		return imageUuid;
	}

	public void setImageUuid(String imageUuid) {
		this.imageUuid = imageUuid;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageTag() {
		return imageTag;
	}

	public void setImageTag(String imageTag) {
		this.imageTag = imageTag;
	}

	public Integer getInstTotal() {
		return instTotal;
	}

	public void setInstTotal(Integer instTotal) {
		this.instTotal = instTotal;
	}

	public Integer getInstRun() {
		return instRun;
	}

	public void setInstRun(Integer instRun) {
		this.instRun = instRun;
	}

	public Integer getInstStop() {
		return instStop;
	}

	public void setInstStop(Integer instStop) {
		this.instStop = instStop;
	}

	public Integer getInstHealthy() {
		return instHealthy;
	}

	public void setInstHealthy(Integer instHealthy) {
		this.instHealthy = instHealthy;
	}

	public Integer getInstException() {
		return instException;
	}

	public void setInstException(Integer instException) {
		this.instException = instException;
	}

	public Integer getInstInView() {
		return instInView;
	}

	public void setInstInView(Integer instInView) {
		this.instInView = instInView;
	}

	public Integer getInstOutView() {
		return instOutView;
	}

	public void setInstOutView(Integer instOutView) {
		this.instOutView = instOutView;
	}

	public Date getConUpdatetime() {
		return conUpdatetime;
	}

	public void setConUpdatetime(Date conUpdatetime) {
		this.conUpdatetime = conUpdatetime;
	}
}