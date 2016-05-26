package com.cmbc.devops.bean;

import java.util.List;

import com.cmbc.devops.bean.RemoteFile;

public class PathObject {
	
	private int retCode;
	private String retMessage;
	private List<RemoteFile> rfList;
	
	public PathObject(int retCode, String retMessage, List<RemoteFile> rfList) {
		super();
		this.retCode = retCode;
		this.retMessage = retMessage;
		this.rfList = rfList;
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

	public List<RemoteFile> getRfList() {
		return rfList;
	}

	public void setRfList(List<RemoteFile> rfList) {
		this.rfList = rfList;
	}

}
