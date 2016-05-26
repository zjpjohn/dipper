package com.cmbc.dap.monitor.request;

import com.cmbc.dap.monitor.model.TriggerInfo;

/**  
 * date：2015年11月17日 上午11:44:57  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：TriggerMonitor.java  
 * description：  
 */
public interface TriggerRequest {
	
	/**
	 * @author langzi
	 * @param zabbix
	 * @param trigger
	 * @return
	 * @version 1.0
	 * 2015年11月17日
	 */
	public abstract Request addTriggerMonitorRequest(TriggerInfo trigger);
	
}
