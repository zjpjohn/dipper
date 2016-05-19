package com.cmbc.devops.core.task;

import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.util.SSH;


/**  
 * date：2015年8月26日 下午5:01:26  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ContainerStartTask.java  
 * description：  
 */
public class SoftwareInstallTask implements Callable<JSONObject> {
	
	private SSH ssh;
	private String yumcall;
	private static final Logger LOGGER = Logger.getLogger(SoftwareInstallTask.class);
	
	public SoftwareInstallTask(SSH ssh, String yumcall) {
		super();
		this.ssh = ssh;
		this.yumcall = yumcall;
	}

	@Override
	public JSONObject call() {
		try {
			String result=ssh.executeWithResult("yum clean all;yum install -y "+yumcall);
			
			JSONObject jo=new JSONObject();
			if(StringUtils.contains(result, "already installed")){
				jo.put("result", true);
				jo.put("message", "软件已存在，不需要安装！");
			}else if(StringUtils.contains(result, "Complete!")){
				jo.put("result", true);
				jo.put("message", "软件已安装成功！");
			}else{
				jo.put("result", false);
				jo.put("message", "安装过程中出现未知错误！");
			}
			System.out.println(jo.toJSONString());
			return jo;
		} catch (Exception e) {
			LOGGER.error("Start container error", e);
			return null;
		}finally{
			ssh.close();
		}
	}
}
