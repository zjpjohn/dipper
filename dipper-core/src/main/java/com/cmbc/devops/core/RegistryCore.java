package com.cmbc.devops.core;

import com.cmbc.devops.bean.Result;

/**
 * date：2015年8月25日 上午11:29:47 project name：cmbc-devops-core
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ContainerCore.java description：
 */
public interface RegistryCore {

	/**
	 * @author yangqinglin
	 * @description 在目标主机上创建仓库
	 * @return
	 * @version 1.0 2015年9月18日
	 */
	Result createRegistry(String ip, String name, String password, String fileName, String imageName, String imageTag);
	
}
