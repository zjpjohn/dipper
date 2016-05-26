package com.cmbc.devops.bean;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.util.DateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author cmbc
 *  ActiveMQ message packet object
 */
public class MessagePacket implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int userId;
	private int tenantId;
	@JsonSerialize(using = DateSerializer.class)
	private Date time;
	private String AuthorityId;
	private int status;
	private String object;
	private String action;
	private JSONObject params;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getTenantId() {
		return tenantId;
	}
	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getAuthorityId() {
		return AuthorityId;
	}
	public void setAuthorityId(String authorityId) {
		AuthorityId = authorityId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public JSONObject getParams() {
		return params;
	}
	public void setParams(JSONObject params) {
		this.params = params;
	}
	public MessagePacket() {
	}
	public MessagePacket(int userId, int tenantId, Date time, String authorityId, int status, String object,
			String action, JSONObject params) {
		super();
		this.userId = userId;
		this.tenantId = tenantId;
		this.time = time;
		AuthorityId = authorityId;
		this.status = status;
		this.object = object;
		this.action = action;
		this.params = params;
	}
	@Override
	public String toString() {
		return "MessagePacket [userId=" + userId + ", tenantId=" + tenantId + ", time=" + time + ", AuthorityId="
				+ AuthorityId + ", status=" + status + ", object=" + object + ", action=" + action + ", params=" + params + "]";
	}
	
}
