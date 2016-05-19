package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class MonitorProxy {
	private Integer mpId;
	private String mpName;
	private Byte mpStatus;
	private String mpIP;
	private Integer mpPort;
	private String mpDesc;
	private String mpComment;
	private Integer mpCreator;
	@JsonSerialize(using = DateSerializer.class)
	private Date mpCreatetime;

	public Integer getMpId() {
		return mpId;
	}

	public void setMpId(Integer mpId) {
		this.mpId = mpId;
	}

	public String getMpName() {
		return mpName;
	}

	public void setMpName(String mpName) {
		this.mpName = mpName;
	}

	public Byte getMpStatus() {
		return mpStatus;
	}

	public void setMpStatus(Byte mpStatus) {
		this.mpStatus = mpStatus;
	}

	public String getMpIP() {
		return mpIP;
	}

	public void setMpIP(String mpIP) {
		this.mpIP = mpIP;
	}

	public Integer getMpPort() {
		return mpPort;
	}

	public void setMpPort(Integer mpPort) {
		this.mpPort = mpPort;
	}

	public String getMpDesc() {
		return mpDesc;
	}

	public void setMpDesc(String mpDesc) {
		this.mpDesc = mpDesc;
	}

	public String getMpComment() {
		return mpComment;
	}

	public void setMpComment(String mpComment) {
		this.mpComment = mpComment;
	}

	public Integer getMpCreator() {
		return mpCreator;
	}

	public void setMpCreator(Integer mpCreator) {
		this.mpCreator = mpCreator;
	}

	public Date getMpCreatetime() {
		return mpCreatetime;
	}

	public void setMpCreatetime(Date mpCreatetime) {
		this.mpCreatetime = mpCreatetime;
	}

}
