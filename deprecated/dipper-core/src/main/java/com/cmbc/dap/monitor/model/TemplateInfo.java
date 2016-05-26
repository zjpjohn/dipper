package com.cmbc.dap.monitor.model;
/**  
 * date：2015年11月17日 上午10:01:18  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：TemplateInfo.java  
 * description：  host monitor template infos
 */
public class TemplateInfo {
	
	private String templateid;
	
	public TemplateInfo() {
	}
	
	TemplateInfo(String templateId){
		this.templateid = templateId;
	}
	
	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}
	
}
