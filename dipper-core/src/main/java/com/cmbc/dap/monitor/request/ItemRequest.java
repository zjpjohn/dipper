package com.cmbc.dap.monitor.request;

import com.cmbc.dap.monitor.model.ItemInfo;

/**  
 * date：2015年11月17日 上午11:44:00  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ItemMonitor.java  
 * description：  
 */
public interface ItemRequest {
	
	/**
	 * @author langzi
	 * @param zabbix
	 * @param item
	 * @return
	 * @version 1.0
	 * 2015年11月17日
	 */
	public abstract Request addItemMonitorRequest(ItemInfo item);
	
	
}
