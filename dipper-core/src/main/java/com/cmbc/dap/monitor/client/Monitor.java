package com.cmbc.dap.monitor.client;

import java.util.List;

import com.cmbc.dap.monitor.model.HostWithTemplate;
import com.cmbc.dap.monitor.model.ItemInfo;
import com.cmbc.dap.monitor.model.TriggerInfo;
import com.cmbc.devops.bean.Result;

/**
 * 容器监控服务接口
 * 
 * @author dmw
 *
 */
public interface Monitor {
	
	public abstract void init();

	/**
	 * 添加主机
	 * 
	 * @param host
	 * @return
	 */
	public abstract Result addHostMonitor(int groupId, HostWithTemplate host);
	
	/**
	 * 批量添加容器
	 * 
	 * @param hosts
	 * @return
	 */
	public abstract List<Result> addHostMonitor(int groupId, List<HostWithTemplate> hosts);
	
	/**
	 * @author langzi
	 * @param hostId
	 * @return
	 * @version 1.0
	 * 2015年11月19日
	 */
	public abstract Result disableHostMonitor(String hostId);
	
	/**
	 * @author langzi
	 * @param hostId
	 * @return
	 * @version 1.0
	 * 2015年11月19日
	 */
	public abstract Result enableHostMonitor(String hostId);
	
	/**
	 * 取消主机监控
	 * 
	 * @param hostId
	 * @return
	 */
	public abstract Result cancelHostMonitor(String hostId);
	
	/**
	 * 批量取消主机监控
	 * 
	 * @param hostIds
	 * @return
	 */
	public Result cancelHostMonitor(String[] hostIds);

	/**
	 * 添加监控项
	 * 
	 * @param item
	 * @return
	 */
	public abstract Result addMoniItem(ItemInfo item);
	
	/**
	 * 批量添加监控项
	 * 
	 * @param items
	 * @return
	 */
	public abstract List<Result> addMoniItems(List<ItemInfo> items);
	
	/**
	 * 添加告警规则
	 * 
	 * @param item
	 * @return
	 */
	public abstract Result addTriggerMonitor(TriggerInfo trigger);

	/**
	 * 批量添加告警规则
	 * 
	 * @param items
	 * @return
	 */
	public abstract List<Result> addTriggerMonitor(List<TriggerInfo> trigger);

	/**
	 * @author langzi
	 * @param groupId
	 * @return
	 * @version 1.0
	 * 2015年11月17日
	 */
	public Result getTemplates();
	
	/**
	 * @author langzi
	 * @param groupId
	 * @return
	 * @version 1.0
	 * 2015年11月17日
	 */
	public Result getGroup();
	
	
}
