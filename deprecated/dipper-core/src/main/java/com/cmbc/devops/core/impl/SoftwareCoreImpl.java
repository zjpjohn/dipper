package com.cmbc.devops.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.core.SoftwareCore;
import com.cmbc.devops.core.task.SoftwareInstallTask;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.util.SSH;

@Component
public class SoftwareCoreImpl implements SoftwareCore {
	
	private static final Logger LOGGER = Logger.getLogger(SoftwareCore.class);

	@Override
	public List<HostModel> installSoftware(List<HostModel> hostlist, String yumcall) {
		//1.并发提交任务
		ExecutorService executor = Executors.newCachedThreadPool();  
        CompletionService<JSONObject> comp = new ExecutorCompletionService<>(executor); 
		for (HostModel hostModel : hostlist) {
			//获取连接
			SSH ssh = new SSH(hostModel.getHostIp(),hostModel.getHostUser(),hostModel.getHostPwd());
			try {
				if (!ssh.connect()) {
					break;
				}
			}catch (IOException e) {
				LOGGER.error("ssh["+hostModel.getHostIp()+"] connect error:", e);
			}
			comp.submit(new SoftwareInstallTask(ssh, yumcall));
		}
		executor.shutdown();
        //2.等待执行结果
        int index = 0;
        List<HostModel> resultHosts = new ArrayList<HostModel>();
        try {
			while(index < hostlist.size()){
				Future<JSONObject> future = comp.poll();
				if(future != null && future.get() != null) {  
					HostModel model = hostlist.get(index);
					model.setHostJo(future.get());
					resultHosts.add(model);
			    	index++;
			    }  
				TimeUnit.MILLISECONDS.sleep(1000);
			}
		} catch (Exception e) {
			LOGGER.error("install software error:operate server error", e);
			return null;
		}
		LOGGER.info("install software success:operate server success");
		return resultHosts;
	}
	
}
