package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Env {
    private Integer envId;

    private String envName;

    private Byte envStatus;

    private String envDesc;
    
    private String configCenter;
    
    private String envParam;

    @JsonSerialize(using = DateSerializer.class)
    private Date envCreatetime;

    private Integer envCreator;

    public Integer getEnvId() {
        return envId;
    }

    public void setEnvId(Integer envId) {
        this.envId = envId;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName == null ? null : envName.trim();
    }

    public Byte getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(Byte envStatus) {
        this.envStatus = envStatus;
    }

    public String getEnvDesc() {
        return envDesc;
    }

    public void setEnvDesc(String envDesc) {
        this.envDesc = envDesc == null ? null : envDesc.trim();
    }

    public Date getEnvCreatetime() {
        return envCreatetime;
    }

    public void setEnvCreatetime(Date envCreatetime) {
        this.envCreatetime = envCreatetime;
    }

    public Integer getEnvCreator() {
        return envCreator;
    }

    public void setEnvCreator(Integer envCreator) {
        this.envCreator = envCreator;
    }

	public String getConfigCenter() {
		return configCenter;
	}

	public void setConfigCenter(String configCenter) {
		this.configCenter = configCenter;
	}

	public String getEnvParam() {
		return envParam;
	}

	public void setEnvParam(String envParam) {
		this.envParam = envParam;
	}
}