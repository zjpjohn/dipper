package com.cmbc.devops.model;
/**  
 * date：2015年9月15日 上午9:10:54  
 * project name：cmbc-devops-common  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：LoadBalanceTemplate.java  
 * description：  Saving nginx template info to replace nginx template's params
 */
public class LoadBalanceTemplate {
	
	//替换内容
	private String upStream;
	private String location;
	//本地文件路径+文件名
	private String localFile;
	//本地模板文件路径+文件名
	private String localTemplate;
	//负载均衡信息
	private String serverConfPath;
	//服务器信息
	private HostModel hostModel;
	
	public String getUpStream() {
		return upStream;
	}
	public void setUpStream(String upStream) {
		this.upStream = upStream;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocalFile() {
		return localFile;
	}
	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}
	public String getLocalTemplate() {
		return localTemplate;
	}
	public void setLocalTemplate(String localTemplate) {
		this.localTemplate = localTemplate;
	}
	public String getServerConfPath() {
		return serverConfPath;
	}
	public void setServerConfPath(String serverConfPath) {
		this.serverConfPath = serverConfPath;
	}
	public HostModel getHostModel() {
		return hostModel;
	}
	public void setHostModel(HostModel hostModel) {
		this.hostModel = hostModel;
	}
	
}
