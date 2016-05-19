package com.cmbc.devops.entity;

public class DashboardShow {
	
	private int dockerHostNumber;
	private int swarmHostNumber;
	private int nginxHostNumber;
	private int registryHostNumber;
	private int containerTotal;
	private int containerRunning;
	private int containerStop;
	private int clusterNumber;
	private int applicationNumber;
	private int loadBalanceNumer;
	private int registryNumber;
	
	public int getDockerHostNumber() {
		return dockerHostNumber;
	}
	public void setDockerHostNumber(int dockerHostNumber) {
		this.dockerHostNumber = dockerHostNumber;
	}
	public int getSwarmHostNumber() {
		return swarmHostNumber;
	}
	public void setSwarmHostNumber(int swarmHostNumber) {
		this.swarmHostNumber = swarmHostNumber;
	}
	public int getNginxHostNumber() {
		return nginxHostNumber;
	}
	public void setNginxHostNumber(int nginxHostNumber) {
		this.nginxHostNumber = nginxHostNumber;
	}
	public int getRegistryHostNumber() {
		return registryHostNumber;
	}
	public void setRegistryHostNumber(int registryHostNumber) {
		this.registryHostNumber = registryHostNumber;
	}
	public int getContainerTotal() {
		return containerTotal;
	}
	public void setContainerTotal(int containerTotal) {
		this.containerTotal = containerTotal;
	}
	public int getContainerRunning() {
		return containerRunning;
	}
	public void setContainerRunning(int containerRunning) {
		this.containerRunning = containerRunning;
	}
	public int getContainerStop() {
		return containerStop;
	}
	public void setContainerStop(int containerStop) {
		this.containerStop = containerStop;
	}
	public int getClusterNumber() {
		return clusterNumber;
	}
	public void setClusterNumber(int clusterNumber) {
		this.clusterNumber = clusterNumber;
	}
	public int getApplicationNumber() {
		return applicationNumber;
	}
	public void setApplicationNumber(int applicationNumber) {
		this.applicationNumber = applicationNumber;
	}
	public int getLoadBalanceNumer() {
		return loadBalanceNumer;
	}
	public void setLoadBalanceNumer(int loadBalanceNumer) {
		this.loadBalanceNumer = loadBalanceNumer;
	}
	public int getRegistryNumber() {
		return registryNumber;
	}
	public void setRegistryNumber(int registryNumber) {
		this.registryNumber = registryNumber;
	}
	
}
