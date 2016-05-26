package com.cmbc.devops.entity;

import java.util.Date;

import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Parameter {
	private Integer paramId;

	private String paramName;

	/* 保存参数键信息 */
	private String paramValue;

	private Byte paramConnector;

	private Byte paramType;

	private Byte paramStatus;

	/* 参数是否可复用 */
	private Byte paramReusable;

	/* 参数间互斥 */
	private String paramMutex;

	/* 参数描述信息 */
	private String paramDesc;

	/* 用户备注信息 */
	private String paramComment;

	@JsonSerialize(using = DateSerializer.class)
	private Date paramCreatetime;

	private Integer paramCreator;
	
	private Integer tenantId;

	public Integer getParamId() {
		return paramId;
	}

	public void setParamId(Integer paramId) {
		this.paramId = paramId;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName == null ? null : paramName.trim();
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue == null ? null : paramValue.trim();
	}

	public Byte getParamConnector() {
		return paramConnector;
	}

	public void setParamConnector(Byte paramConnector) {
		this.paramConnector = paramConnector;
	}

	public Byte getParamType() {
		return paramType;
	}

	public void setParamType(Byte paramType) {
		this.paramType = paramType;
	}

	public Byte getParamStatus() {
		return paramStatus;
	}

	public void setParamStatus(Byte paramStatus) {
		this.paramStatus = paramStatus;
	}

	public String getParamDesc() {
		return paramDesc;
	}

	public void setParamDesc(String paramDesc) {
		this.paramDesc = paramDesc == null ? null : paramDesc.trim();
	}

	public String getParamComment() {
		return paramComment;
	}

	public void setParamComment(String paramComment) {
		this.paramComment = paramComment;
	}

	public Date getParamCreatetime() {
		return paramCreatetime;
	}

	public void setParamCreatetime(Date paramCreatetime) {
		this.paramCreatetime = paramCreatetime;
	}

	public Integer getParamCreator() {
		return paramCreator;
	}

	public void setParamCreator(Integer paramCreator) {
		this.paramCreator = paramCreator;
	}

	public Byte getParamReusable() {
		return paramReusable;
	}

	public void setParamReusable(Byte paramReusable) {
		this.paramReusable = paramReusable;
	}

	public String getParamMutex() {
		return paramMutex;
	}

	public void setParamMutex(String paramMutex) {
		this.paramMutex = paramMutex;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}
}