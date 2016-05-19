package com.cmbc.dap.monitor.request;

import com.cmbc.dap.monitor.model.HostInfo;
import com.cmbc.dap.monitor.model.HostWithTemplate;

/**  
 * date：2015年11月17日 上午11:44:15  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：HostMonitor.java  
 * description：  
 */
public interface HostRequest {
	
	/**
	 * @author langzi
	 * @param groupId
	 * @param host
	 * @return
	 * @version 1.0
	 * 2015年11月17日
	 */
	public abstract Request addHostMonitorWithTemplateRequest(int groupId, HostWithTemplate host);
	
	/**
	 * @author langzi
	 * @param groupId
	 * @param host
	 * @return
	 * @version 1.0
	 * 2015年11月17日
	 */
	public abstract Request addHostMonitorRequest(int groupId, HostInfo host);
	
	/**
	 * @author langzi
	 * @param hostId
	 * @return
	 * @version 1.0
	 * 2015年11月19日
	 */
	public abstract Request enableHostMonitorRequest(String hostId);
	
	/**
	 * @author langzi
	 * @param hostId
	 * @return
	 * @version 1.0
	 * 2015年11月19日
	 */
	public abstract Request disableHostMonitorRequest(String hostId);
	
	/**
	 * @author langzi
	 * @param hostIds
	 * @return
	 * @version 1.0
	 * 2015年11月17日
	 */
	public abstract ObjectRequest cancelHostMonitorRequest(String[] hostIds);
	
	/**
	 * @author langzi
	 * @param hostName
	 * @return
	 * @version 1.0
	 * 2015年11月27日
	 */
	public abstract Request getHostGroupRequest(String groupName);
	
}
