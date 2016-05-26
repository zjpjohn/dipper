package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class CanActTemplate {

	private String tplId;
	private String tplName;
	private String tplVersion;
	private Byte tplType;
	private Byte tplStatus;
	private String tplDesc;
	private String tplComment;
	@JsonSerialize(using = DateSerializer.class)
	private Date tplCreatetime;
	private Integer tplCreator;

	/** 设置标志位，如果0为候选项目，1为激活项目 **/
	private Integer canActSign;

	public String getTplId() {
		return tplId;
	}

	public void setTplId(String tplId) {
		this.tplId = tplId;
	}

	public String getTplName() {
		return tplName;
	}

	public void setTplName(String tplName) {
		this.tplName = tplName;
	}

	public String getTplVersion() {
		return tplVersion;
	}

	public void setTplVersion(String tplVersion) {
		this.tplVersion = tplVersion;
	}

	public Byte getTplType() {
		return tplType;
	}

	public void setTplType(Byte tplType) {
		this.tplType = tplType;
	}

	public Byte getTplStatus() {
		return tplStatus;
	}

	public void setTplStatus(Byte tplStatus) {
		this.tplStatus = tplStatus;
	}

	public String getTplDesc() {
		return tplDesc;
	}

	public void setTplDesc(String tplDesc) {
		this.tplDesc = tplDesc;
	}

	public String getTplComment() {
		return tplComment;
	}

	public void setTplComment(String tplComment) {
		this.tplComment = tplComment;
	}

	public Date getTplCreatetime() {
		return tplCreatetime;
	}

	public void setTplCreatetime(Date tplCreatetime) {
		this.tplCreatetime = tplCreatetime;
	}

	public Integer getTplCreator() {
		return tplCreator;
	}

	public void setTplCreator(Integer tplCreator) {
		this.tplCreator = tplCreator;
	}

	public Integer getCanActSign() {
		return canActSign;
	}

	public void setCanActSign(Integer canActSign) {
		this.canActSign = canActSign;
	}

}
