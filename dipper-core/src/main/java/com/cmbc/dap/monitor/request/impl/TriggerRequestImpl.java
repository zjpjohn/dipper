package com.cmbc.dap.monitor.request.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cmbc.dap.monitor.client.ZabbixMethod;
import com.cmbc.dap.monitor.model.TriggerInfo;
import com.cmbc.dap.monitor.request.Request;
import com.cmbc.dap.monitor.request.RequestBuilder;
import com.cmbc.dap.monitor.request.TriggerRequest;

/**  
 * date：2015年11月17日 上午11:48:16  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：TriggerMonitorImpl.java  
 * description：  
 */
@Component
public class TriggerRequestImpl implements TriggerRequest {
	
	private static final Logger LOGGER = Logger.getLogger(TriggerRequest.class);

	/* (non-Javadoc)
	 * @see com.cmbc.devops.core.zabbix.monitor.TriggerMonitor#addTriggerMonitorRequest(com.cmbc.devops.core.zabbix.entity.TriggerInfo)
	 */
	@Override
	public Request addTriggerMonitorRequest(TriggerInfo trigger) {
		Request request = RequestBuilder.newBuilder().method(ZabbixMethod.ADD_TRIGGER.getMethod())
				.paramEntry("description", trigger.getDescription())
				.paramEntry("expression", trigger.getExpression()).build();
		LOGGER.info(request);
		return request != null ? request : null;
	}

}
