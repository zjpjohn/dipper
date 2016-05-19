package com.cmbc.devops.model;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TenantModel {

	private int tenantId;
	private String tenantName;
	private Byte resType;
	private Byte tenantStatus;
	private String beginDate;
	private String endDate;
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
	private String cluArray;
	private String managerName;
	private String eMail;
	private String phoneNumber;
	private String companyName;

	public int getTenantId() {
		return tenantId;
	}

	public void setTenantId(int tenantId) {
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

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getCluArray() {
		return cluArray;
	}

	public void setCluArray(String cluArray) {
		this.cluArray = cluArray;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
