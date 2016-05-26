package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Tenant {
	private Integer tenantId;
	private String tenantName;
	private Byte resType;
	private Byte tenantStatus;
	@JsonSerialize(using = DateSerializer.class)
	private Date inserviceDate;
	@JsonSerialize(using = DateSerializer.class)
	private Date expireDate;
	private int usedCpu;
	private int totalCpu;
	private int usedMem;
	private int totalMem;
	private String tenantDesc;
	private int parentId;
	@JsonSerialize(using = DateSerializer.class)
	private Date createTime;
	private int creator;

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public Byte getResType() {
		return resType;
	}

	public void setResType(Byte resType) {
		this.resType = resType;
	}

	public Byte getTenantStatus() {
		return tenantStatus;
	}

	public void setTenantStatus(Byte tenantStatus) {
		this.tenantStatus = tenantStatus;
	}

	public Date getInserviceDate() {
		return inserviceDate;
	}

	public void setInserviceDate(Date inserviceDate) {
		this.inserviceDate = inserviceDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public int getUsedCpu() {
		return usedCpu;
	}

	public void setUsedCpu(int usedCpu) {
		this.usedCpu = usedCpu;
	}

	public int getTotalCpu() {
		return totalCpu;
	}

	public void setTotalCpu(int totalCpu) {
		this.totalCpu = totalCpu;
	}

	public int getUsedMem() {
		return usedMem;
	}

	public void setUsedMem(int usedMem) {
		this.usedMem = usedMem;
	}

	public int getTotalMem() {
		return totalMem;
	}

	public void setTotalMem(int totalMem) {
		this.totalMem = totalMem;
	}

	public String getTenantDesc() {
		return tenantDesc;
	}

	public void setTenantDesc(String tenantDesc) {
		this.tenantDesc = tenantDesc;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getCreator() {
		return creator;
	}

	public void setCreator(int creator) {
		this.creator = creator;
	}

}
