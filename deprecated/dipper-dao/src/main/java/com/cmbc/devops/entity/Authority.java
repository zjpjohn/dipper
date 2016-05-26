package com.cmbc.devops.entity;

public class Authority {

	private Integer actionId;
	private String actionName;
	private String actionDesc;
	private String actionRelativeUrl;
	private Byte actionType;
	private String actionRemarks;
	private Integer actionParentId;
	
	private String parentActionName;

	public Authority() {

	}

	public Authority(Integer actionId, String actionName, String actionDesc,
			String actionRelativeUrl, Byte actionType, String actionRemarks,
			Integer actionParentId) {
		super();
		this.actionId = actionId;
		this.actionName = actionName;
		this.actionDesc = actionDesc;
		this.actionRelativeUrl = actionRelativeUrl;
		this.actionType = actionType;
		this.actionRemarks = actionRemarks;
		this.actionParentId = actionParentId;
	}

	public Integer getActionId() {
		return actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName == null ? null : actionName.trim();
	}

	public String getActionDesc() {
		return actionDesc;
	}

	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc == null ? null : actionDesc.trim();
	}

	public String getActionRelativeUrl() {
		return actionRelativeUrl;
	}

	public void setActionRelativeUrl(String actionRelativeUrl) {
		this.actionRelativeUrl = actionRelativeUrl == null ? null
				: actionRelativeUrl.trim();
	}

	public Byte getActionType() {
		return actionType;
	}

	public void setActionType(Byte actionType) {
		this.actionType = actionType;
	}

	public String getActionRemarks() {
		return actionRemarks;
	}

	public void setActionRemarks(String actionRemarks) {
		this.actionRemarks = actionRemarks == null ? null : actionRemarks
				.trim();
	}

	public Integer getActionParentId() {
		return actionParentId;
	}

	public void setActionParentId(Integer actionParentId) {
		this.actionParentId = actionParentId;
	}

	public String getParentActionName() {
		return parentActionName;
	}

	public void setParentActionName(String parentActionName) {
		this.parentActionName = parentActionName;
	}
	
}