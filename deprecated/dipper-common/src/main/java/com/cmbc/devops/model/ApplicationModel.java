/**
 * 
 */
package com.cmbc.devops.model;

/**  
 * date：2015年8月19日 上午11:22:32
 * project name：cmbc-devops-common
 * @author  mayh
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ApplicationModel.java
 * description：  
 */
public class ApplicationModel extends PageModel {
	
	private Integer appId;
	
	private String appName;
	
    private Integer appType;

    private String appDesc;
    
    private Integer balanceId;

    private String appUrl;
    
    private Integer appPort;
    
    private String appVersion;
    
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public String getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
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
		this.appUrl = appUrl;
	}

	public Integer getAppPort() {
		return appPort;
	}

	public void setAppPort(Integer appPort) {
		this.appPort = appPort;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

}
