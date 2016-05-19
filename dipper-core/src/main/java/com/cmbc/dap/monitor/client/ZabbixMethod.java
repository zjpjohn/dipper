package com.cmbc.dap.monitor.client;

/**
 * zabbix对外接口枚举
 * 
 * @author dmw
 *
 */
public enum ZabbixMethod {

	AUTH("user.login"), // 鉴权
	ADD_HOST("host.create"), // 添加主机
	GET_HOSTGROUP("hostgroup.get"),// 获取主机
	DEL_HOST("host.delete"),// 移除主机
	UPDATE_HOST("host.update"), //更新主机
	GET_TEMPLATE("template.get"),
	ADD_TEMPLATE("template.create"),//添加模板
	DEL_TEMPLATE("template.delete"),//删除模板
	ADD_ITEM("item.create"), // 添加监控项
	DEL_ITEM("item.delete"), // 移除监控项
	ADD_TRIGGER("trigger.create"),//添加监控指标
	DEL_TRIGGER("trigger.delete"),//删除监控指标
	GET_INTF("hostinterface.get"),//获取主机interfaceId
	GET_PROXY("proxy.get"),//获取代理
	/*以下内容为获取Zabbix监控数据新增*/
	GET_ALLHOST("host.get"),
	GET_ITEMS("item.get")
	;

	private String method;

	private ZabbixMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
