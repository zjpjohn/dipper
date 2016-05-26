package com.cmbc.devops.model;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * date：2015年12月10日 上午10:17:55 project name：cmbc-devops-common
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationReleaseModel.java description：
 * 
 *        /** date：2015年12月10日 上午10:17:55 project name：cmbc-devops-common
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationReleaseModel.java description：
 */
public class ApplicationReleaseModel {

	// 1.appid
	private int appId;
	// 实例名称
	private String conName;
	// 2.imageId
	private int imageId;
	// imageUrl
	private String imageUrl;
	// 3.clusterId
	private int clusterId;
	// 4.releaseMode
	private int releaseMode;
	
	private int cpu;
	private int mem;
	// 环境变量
	private String env;
	// 挂载点
	private String volume;
	// 6.参数list
	private String params;
	// 7.releaseNum
	private int releaseNum;
	// 8.startNum
	private int startNum;
	//监控开关  0：不添加    1：添加
	private int appMonitor;
	//健康检查开关 0:不做健康检查    1：做健康检查
	private int appHealth;
	// 用户信息
	private int userId;
	// 启动命令
	private String command;
	// 最后一个id
	private int lastConId;
	// 应用状态
	private int appStatus;
	// 监控状态
	private int monitorStatus;
	// 监控主机id
	private String monitorHostId;
	// 9.备注信息
	private String appDesc;
	//旧版本镜像id
	private int oldImageId;
	//发布环境id
	private int envId;
	
	private JSONObject conJo;

	/* 添加租户信息 */
	private Integer tenantId;
	
	private List<HostResourceModel> hrmList;

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getConName() {
		return conName;
	}

	public void setConName(String conName) {
		this.conName = conName;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public int getReleaseMode() {
		return releaseMode;
	}

	public void setReleaseMode(int releaseMode) {
		this.releaseMode = releaseMode;
	}

	public int getCpu() {
		return cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public int getMem() {
		return mem;
	}

	public void setMem(int mem) {
		this.mem = mem;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public int getReleaseNum() {
		return releaseNum;
	}

	public void setReleaseNum(int releaseNum) {
		this.releaseNum = releaseNum;
	}

	public int getStartNum() {
		return startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public int getAppMonitor() {
		return appMonitor;
	}

	public void setAppMonitor(int appMonitor) {
		this.appMonitor = appMonitor;
	}

	public int getAppHealth() {
		return appHealth;
	}

	public void setAppHealth(int appHealth) {
		this.appHealth = appHealth;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getLastConId() {
		return lastConId;
	}

	public void setLastConId(int lastConId) {
		this.lastConId = lastConId;
	}

	public int getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}

	public int getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(int monitorStatus) {
		this.monitorStatus = monitorStatus;
	}

	public String getMonitorHostId() {
		return monitorHostId;
	}

	public void setMonitorHostId(String monitorHostId) {
		this.monitorHostId = monitorHostId;
	}

	public String getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}

	public JSONObject getConJo() {
		return conJo;
	}

	public void setConJo(JSONObject conJo) {
		this.conJo = conJo;
	}

	public int getOldImageId() {
		return oldImageId;
	}
	public void setOldImageId(int oldImageId) {
		this.oldImageId = oldImageId;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public int getEnvId() {
		return envId;
	}

	public void setEnvId(int envId) {
		this.envId = envId;
	}

	public List<HostResourceModel> getHrmList() {
		return hrmList;
	}

	public void setHrmList(List<HostResourceModel> hrmList) {
		this.hrmList = hrmList;
	}

	@Override
	public String toString() {
		return "ApplicationReleaseModel [appId=" + appId + ", conName=" + conName + ", imageId=" + imageId
				+ ", imageUrl=" + imageUrl + ", clusterId=" + clusterId + ", releaseMode=" + releaseMode + ", cpu="
				+ cpu + ", mem=" + mem + ", env=" + env + ", volume=" + volume + ", params=" + params + ", releaseNum="
				+ releaseNum + ", startNum=" + startNum + ", appMonitor=" + appMonitor + ", appHealth=" + appHealth
				+ ", userId=" + userId + ", command=" + command + ", lastConId=" + lastConId + ", appStatus="
				+ appStatus + ", monitorStatus=" + monitorStatus + ", monitorHostId=" + monitorHostId + ", appDesc="
				+ appDesc + ", oldImageId=" + oldImageId + ", envId=" + envId + ", conJo=" + conJo + ", tenantId="
				+ tenantId + "]";
	}

	
}
