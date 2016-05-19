package com.cmbc.devops.entity;

import org.springframework.beans.BeanUtils;

/** 主机中包含主机、集群和创建人信息 */
public class HostWithUser extends Host {

	private String creatorName;
	private Integer clusterId;
	private String clusterName;

	public HostWithUser() {
		super();
	}

	public HostWithUser(Host host) {
		BeanUtils.copyProperties(host, this);
	}

	public HostWithUser(Host host, Cluster cluster, User user) {
		BeanUtils.copyProperties(host, this);
		this.clusterId = cluster.getClusterId();
		this.clusterName = cluster.getClusterName();
		this.creatorName = user.getUserName();
	}

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
		this.clusterId = clusterId;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

}