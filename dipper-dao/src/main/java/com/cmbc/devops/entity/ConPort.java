package com.cmbc.devops.entity;

public class ConPort {
    private Integer id;

    private Integer containerId;
    
    private String conIp;

    private String pubPort;

    private String priPort;
    
    

    public ConPort(Integer containerId, String conIp,
			String pubPort, String priPort) {
		this.containerId = containerId;
		this.conIp = conIp;
		this.pubPort = pubPort;
		this.priPort = priPort;
	}

	public ConPort() {
		super();
	}



	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }
    
    public String getConIp() {
		return conIp;
	}

	public void setConIp(String conIp) {
		this.conIp = conIp;
	}

	public String getPubPort() {
        return pubPort;
    }

    public void setPubPort(String pubPort) {
        this.pubPort = pubPort == null ? null : pubPort.trim();
    }

    public String getPriPort() {
        return priPort;
    }

    public void setPriPort(String priPort) {
        this.priPort = priPort == null ? null : priPort.trim();
    }
}