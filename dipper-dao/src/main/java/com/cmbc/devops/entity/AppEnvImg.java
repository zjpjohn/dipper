package com.cmbc.devops.entity;

public class AppEnvImg {
	private Integer id;
	private Integer appId;
	private Integer envId;
	private Integer imgId;

	private String envName;


	public AppEnvImg() {

	}

	public AppEnvImg(Integer appId, Integer envId, Integer imgId) {
		this.appId = appId;
		this.envId = envId;
		this.imgId = imgId;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public Integer getEnvId() {
		return envId;
	}

	public void setEnvId(Integer envId) {
		this.envId = envId;
	}

	public Integer getImgId() {
		return imgId;
	}

	public void setImgId(Integer imgId) {
		this.imgId = imgId;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}
}
