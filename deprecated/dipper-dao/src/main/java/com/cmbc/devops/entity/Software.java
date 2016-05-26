package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Software {
    private Integer swId;

    private Byte swType;

    private Byte swStatus;

    private String swName;

    private String swVersion;

    private String swYumcall;

    private String swDesc;

    @JsonSerialize(using = DateSerializer.class)
    private Date swCreatetime;

    private Integer swCreator;

    private Integer tenantId;

    public Integer getSwId() {
        return swId;
    }

    public void setSwId(Integer swId) {
        this.swId = swId;
    }

    public Byte getSwType() {
        return swType;
    }

    public void setSwType(Byte swType) {
        this.swType = swType;
    }

    public Byte getSwStatus() {
        return swStatus;
    }

    public void setSwStatus(Byte swStatus) {
        this.swStatus = swStatus;
    }

    public String getSwName() {
        return swName;
    }

    public void setSwName(String swName) {
        this.swName = swName == null ? null : swName.trim();
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion == null ? null : swVersion.trim();
    }

    public String getSwYumcall() {
        return swYumcall;
    }

    public void setSwYumcall(String swYumcall) {
        this.swYumcall = swYumcall == null ? null : swYumcall.trim();
    }

    public String getSwDesc() {
        return swDesc;
    }

    public void setSwDesc(String swDesc) {
        this.swDesc = swDesc == null ? null : swDesc.trim();
    }

    public Date getSwCreatetime() {
        return swCreatetime;
    }

    public void setSwCreatetime(Date swCreatetime) {
        this.swCreatetime = swCreatetime;
    }

    public Integer getSwCreator() {
        return swCreator;
    }

    public void setSwCreator(Integer swCreator) {
        this.swCreator = swCreator;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
}