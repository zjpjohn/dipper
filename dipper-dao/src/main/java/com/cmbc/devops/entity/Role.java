package com.cmbc.devops.entity;

public class Role {
    private Integer roleId;

    private String roleName;

    private String roleDesc;

    private Byte roleRemarks;

    private Byte roleStatus;
    
    private Integer roleCreator;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc == null ? null : roleDesc.trim();
    }

    public Byte getRoleRemarks() {
        return roleRemarks;
    }

    public void setRoleRemarks(Byte roleRemarks) {
        this.roleRemarks = roleRemarks;
    }

    public Byte getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(Byte roleStatus) {
        this.roleStatus = roleStatus;
    }

	public Integer getRoleCreator() {
		return roleCreator;
	}

	public void setRoleCreator(Integer roleCreator) {
		this.roleCreator = roleCreator;
	}
}