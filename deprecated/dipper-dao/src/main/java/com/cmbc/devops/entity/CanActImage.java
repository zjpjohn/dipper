package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/** 专用于返回前台，判断是否为激活镜像使用 ***/
public class CanActImage {
	private Integer imageId;

	private String imageUuid;

	private int imageStatus;

	private String imageName;

	private String imageTag;

	private String imageSize;

	private String imageType;

	private String imageDesc;

	private Integer appId;

	/** 设置标志位，如果0为候选项目，1为激活项目 **/
	private Integer canActSign;

	private String imagePort;
	@JsonSerialize(using = DateSerializer.class)
	private Date imageCreatetime;

	private Integer imageCreator;

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
		this.imageUuid = imageUuid == null ? null : imageUuid.trim();
	}

	public int getImageStatus() {
		return imageStatus;
	}

	public void setImageStatus(int imageStatus) {
		this.imageStatus = imageStatus;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName == null ? null : imageName.trim();
	}

	public String getImageTag() {
		return imageTag;
	}

	public void setImageTag(String imageTag) {
		this.imageTag = imageTag == null ? null : imageTag.trim();
	}

	public String getImageSize() {
		return imageSize;
	}

	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}

	public String getImageDesc() {
		return imageDesc;
	}

	public void setImageDesc(String imageDesc) {
		this.imageDesc = imageDesc == null ? null : imageDesc.trim();
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getImagePort() {
		return imagePort;
	}

	public void setImagePort(String imagePort) {
		this.imagePort = imagePort == null ? null : imagePort.trim();
	}

	public Date getImageCreatetime() {
		return imageCreatetime;
	}

	public void setImageCreatetime(Date imageCreatetime) {
		this.imageCreatetime = imageCreatetime;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public Integer getImageCreator() {
		return imageCreator;
	}

	public void setImageCreator(Integer imageCreator) {
		this.imageCreator = imageCreator;
	}

	public Integer getCanActSign() {
		return canActSign;
	}

	public void setCanActSign(Integer canActSign) {
		this.canActSign = canActSign;
	}

}