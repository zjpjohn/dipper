package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class CanActDkResource {
	private Integer resId;
	private String resName;
	private Integer resCPU;
	private Integer resMEM;
	private Integer resBLKIO;
	private String resDesc;
	private String resComment;
	private Integer resCreator;
	@JsonSerialize(using = DateSerializer.class)
	private Date resCreatetime;

	/** 设置标志位，如果0为候选项目，1为激活项目 **/
	private Integer canActSign;

	public Integer getResId() {
		return resId;
	}

	public void setResId(Integer resId) {
		this.resId = resId;
	}

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public Integer getResCPU() {
		return resCPU;
	}

	public void setResCPU(Integer resCPU) {
		this.resCPU = resCPU;
	}

	public Integer getResMEM() {
		return resMEM;
	}

	public void setResMEM(Integer resMEM) {
		this.resMEM = resMEM;
	}

	public Integer getResBLKIO() {
		return resBLKIO;
	}

	public void setResBLKIO(Integer resBLKIO) {
		this.resBLKIO = resBLKIO;
	}

	public String getResDesc() {
		return resDesc;
	}

	public void setResDesc(String resDesc) {
		this.resDesc = resDesc;
	}

	public String getResComment() {
		return resComment;
	}

	public void setResComment(String resComment) {
		this.resComment = resComment;
	}

	public Integer getResCreator() {
		return resCreator;
	}

	public void setResCreator(Integer resCreator) {
		this.resCreator = resCreator;
	}

	public Date getResCreatetime() {
		return resCreatetime;
	}

	public void setResCreatetime(Date resCreatetime) {
		this.resCreatetime = resCreatetime;
	}

	public Integer getCanActSign() {
		return canActSign;
	}

	public void setCanActSign(Integer canActSign) {
		this.canActSign = canActSign;
	}

}
