package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class LoadBalance {
    private Integer lbId;

    private String lbName;

    private String lbDesc;

    private Integer lbMainHost;

    private String lbMainConf;

    private Integer lbBackupHost;

    private String lbBackupConf;
    @JsonSerialize(using = DateSerializer.class)
    private Date lbCreatetime;

    private Integer lbCreator;

    private Byte lbStatus;

    public Integer getLbId() {
        return lbId;
    }

    public void setLbId(Integer lbId) {
        this.lbId = lbId;
    }

    public String getLbName() {
        return lbName;
    }

    public void setLbName(String lbName) {
        this.lbName = lbName == null ? null : lbName.trim();
    }

    public String getLbDesc() {
        return lbDesc;
    }

    public void setLbDesc(String lbDesc) {
        this.lbDesc = lbDesc == null ? null : lbDesc.trim();
    }

    public Integer getLbMainHost() {
        return lbMainHost;
    }

    public void setLbMainHost(Integer lbMainHost) {
        this.lbMainHost = lbMainHost;
    }

    public String getLbMainConf() {
        return lbMainConf;
    }

    public void setLbMainConf(String lbMainConf) {
        this.lbMainConf = lbMainConf == null ? null : lbMainConf.trim();
    }

    public Integer getLbBackupHost() {
        return lbBackupHost;
    }

    public void setLbBackupHost(Integer lbBackupHost) {
        this.lbBackupHost = lbBackupHost;
    }

    public String getLbBackupConf() {
        return lbBackupConf;
    }

    public void setLbBackupConf(String lbBackupConf) {
        this.lbBackupConf = lbBackupConf == null ? null : lbBackupConf.trim();
    }

    public Date getLbCreatetime() {
        return lbCreatetime;
    }

    public void setLbCreatetime(Date lbCreatetime) {
        this.lbCreatetime = lbCreatetime;
    }

    public Integer getLbCreator() {
        return lbCreator;
    }

    public void setLbCreator(Integer lbCreator) {
        this.lbCreator = lbCreator;
    }

    public Byte getLbStatus() {
        return lbStatus;
    }

    public void setLbStatus(Byte lbStatus) {
        this.lbStatus = lbStatus;
    }
}