package com.cmbc.dap.monitor.request.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cmbc.dap.monitor.client.ZabbixMethod;
import com.cmbc.dap.monitor.model.ItemInfo;
import com.cmbc.dap.monitor.request.ItemRequest;
import com.cmbc.dap.monitor.request.Request;
import com.cmbc.dap.monitor.request.RequestBuilder;

/**  
 * date：2015年11月17日 上午11:47:21  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ItemMonitorImpl.java  
 * description：  
 */
@Component
public class ItemRequestImpl implements ItemRequest {
	
	private static final Logger LOGGER = Logger.getLogger(ItemRequest.class);

	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.zabbix.monitor.ItemMonitor#addItemMonitorRequest(com.cmbc.devops.core.zabbix.entity.ItemInfo)
	 */
	@Override
	public Request addItemMonitorRequest(ItemInfo item) {
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.ADD_ITEM.getMethod())
				.paramEntry("name", item.getItemName()).paramEntry("key_", item.getItemKey())
				.paramEntry("hostid", item.getHostId()).paramEntry("type", item.getType())
				.paramEntry("value_type", item.getValueType()).paramEntry("interfaceid", item.getInterfaceId())
				.paramEntry("delay", item.getDelay()).paramEntry("inventory_link", item.getInventoryLink()).build();
		LOGGER.info(request);
		return request != null ? request : null;
		
	}

}
