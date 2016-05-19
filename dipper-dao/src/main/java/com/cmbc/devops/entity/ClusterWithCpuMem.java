package com.cmbc.devops.entity;

import org.springframework.beans.BeanUtils;

public class ClusterWithCpuMem extends Cluster {

	private Integer totalCpu;
	private Integer totalMem;

	public ClusterWithCpuMem(Cluster cluster, int totalCpu, int totalMem) {
		BeanUtils.copyProperties(cluster, this);
		this.totalCpu = totalCpu;
		this.totalMem = totalMem;
	}

	public Integer getTotalCpu() {
		return totalCpu;
	}

	public void setTotalCpu(Integer totalCpu) {
		this.totalCpu = totalCpu;
	}

	public Integer getTotalMem() {
		return totalMem;
	}

	public void setTotalMem(Integer totalMem) {
		this.totalMem = totalMem;
	}

}
