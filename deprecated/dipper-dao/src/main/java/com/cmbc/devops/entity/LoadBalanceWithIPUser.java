package com.cmbc.devops.entity;


public class LoadBalanceWithIPUser extends LoadBalance {


	private String lbMainHostIP;

	private String lbBackupHostIP;

	private String lbCreatorName;

	public String getLbMainHostIP() {
		return lbMainHostIP;
	}

	public void setLbMainHostIP(String lbMainHostIP) {
		this.lbMainHostIP = lbMainHostIP;
	}

	public String getLbBackupHostIP() {
		return lbBackupHostIP;
	}

	public void setLbBackupHostIP(String lbBackupHostIP) {
		this.lbBackupHostIP = lbBackupHostIP;
	}

	public String getLbCreatorName() {
		return lbCreatorName;
	}

	public void setLbCreatorName(String lbCreatorName) {
		this.lbCreatorName = lbCreatorName;
	}

	@Override
	public String toString() {
		return "LoadBalanceWithIPUser [lbMainHostIP=" + lbMainHostIP + ", lbBackupHostIP=" + lbBackupHostIP
				+ ", lbCreatorName=" + lbCreatorName + "]";
	}
	

}
