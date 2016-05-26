package com.cmbc.devops.entity;

public class ClusterResource {
    private Integer id;

    private Integer hostId;

    private Integer cpuId;

    private Integer clusterId;

    private Integer conId;
    
    private Integer cpuNum;

    public ClusterResource() {
		super();
	}

	public ClusterResource(Integer id, Integer hostId, Integer cpuId, Integer clusterId, Integer conId) {
		super();
		this.id = id;
		this.hostId = hostId;
		this.cpuId = cpuId;
		this.clusterId = clusterId;
		this.conId = conId;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public Integer getCpuId() {
        return cpuId;
    }

    public void setCpuId(Integer cpuId) {
        this.cpuId = cpuId;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getConId() {
        return conId;
    }

    public void setConId(Integer conId) {
        this.conId = conId;
    }

	public Integer getCpuNum() {
		return cpuNum;
	}

	public void setCpuNum(Integer cpuNum) {
		this.cpuNum = cpuNum;
	}
}