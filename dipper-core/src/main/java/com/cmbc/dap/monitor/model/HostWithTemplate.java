package com.cmbc.dap.monitor.model;

import java.util.List;

/**  
 * date：2015年11月17日 上午10:46:37  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：HostWithTemplate.java  
 * description：  
 */
public class HostWithTemplate {
	
	private HostInfo hostInfo;
	
	private List<TemplateInfo> templateInfos;

	public HostInfo getHostInfo() {
		return hostInfo;
	}

	public void setHostInfo(HostInfo hostInfo) {
		this.hostInfo = hostInfo;
	}

	public List<TemplateInfo> getTemplateInfos() {
		return templateInfos;
	}

	public void setTemplateInfos(List<TemplateInfo> templateInfos) {
		this.templateInfos = templateInfos;
	}

	public HostWithTemplate(HostInfo hostInfo, List<TemplateInfo> templateInfos) {
		super();
		this.hostInfo = hostInfo;
		this.templateInfos = templateInfos;
	}
	
	public HostWithTemplate() {
	}
	
}
