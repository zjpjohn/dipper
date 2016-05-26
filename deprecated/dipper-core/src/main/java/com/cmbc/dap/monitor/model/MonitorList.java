package com.cmbc.dap.monitor.model;

import java.io.Serializable;
import java.util.List;

/**  
 * date：2015年11月20日 上午11:33:39  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：MonitorList.java  
 * description：  
 */
public class MonitorList implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//监控项列表说明
	private List<ItemInfo> itemInfos;
	//告警规则说明
	private List<TriggerInfo> triggerInfos;
	
	public List<ItemInfo> getItemInfos() {
		return itemInfos;
	}
	public void setItemInfos(List<ItemInfo> itemInfos) {
		this.itemInfos = itemInfos;
	}
	public List<TriggerInfo> getTriggerInfos() {
		return triggerInfos;
	}
	public void setTriggerInfos(List<TriggerInfo> triggerInfos) {
		this.triggerInfos = triggerInfos;
	}
	
}
