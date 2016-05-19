package com.cmbc.devops.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("systemConfig")
public class SystemConfig {
	@Value("${zabbix.server}")
	private String zabbixServer;// zabbix服务器地址
	@Value("${zabbix.manager}")
	private String zabbixManager;// zabbix 管理员名
	@Value("${zabbix.password}")
	private String zabbixPassword;// zabbix 管理密码
	@Value("${zabbix.templateName}")
	private String zabbixTemplateNames;
	@Value("${zabbix.hostName}")
	private String zabbixHostName;
	@Value("${zabbix.proxyid}")
	private String zabbixProxyId;
	@Value("${delay.time}")
	private long delayTime;
	@Value("${zk.server}")
	private String zkServer;
	@Value("${mq.server}")
	private String mqServer;
	@Value("${docker.port}")
	private String dockerPort;

	public String getZabbixServer() {
		return zabbixServer;
	}

	public void setZabbixServer(String zabbixServer) {
		this.zabbixServer = zabbixServer;
	}

	public String getZabbixManager() {
		return zabbixManager;
	}

	public void setZabbixManager(String zabbixManager) {
		this.zabbixManager = zabbixManager;
	}

	public String getZabbixPassword() {
		return zabbixPassword;
	}

	public void setZabbixPassword(String zabbixPassword) {
		this.zabbixPassword = zabbixPassword;
	}

	public String getZabbixTemplateNames() {
		return zabbixTemplateNames;
	}

	public void setZabbixTemplateNames(String zabbixTemplateNames) {
		this.zabbixTemplateNames = zabbixTemplateNames;
	}

	public String getZabbixHostName() {
		return zabbixHostName;
	}

	public void setZabbixHostName(String zabbixHostName) {
		this.zabbixHostName = zabbixHostName;
	}

	public String getZabbixProxyId() {
		return zabbixProxyId;
	}

	public void setZabbixProxyId(String zabbixProxyId) {
		this.zabbixProxyId = zabbixProxyId;
	}

	public long getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	public String getZkServer() {
		return zkServer;
	}

	public void setZkServer(String zkServer) {
		this.zkServer = zkServer;
	}
	
	public String getMqServer() {
		return mqServer;
	}

	public void setMqServer(String mqServer) {
		this.mqServer = mqServer;
	}

	public String getDockerPort() {
		return dockerPort;
	}

	public void setDockerPort(String dockerPort) {
		this.dockerPort = dockerPort;
	}
	
}
