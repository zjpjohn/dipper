package com.cmbc.devops.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmbc.devops.dao.ClusterAppMapper;
import com.cmbc.devops.entity.ClusterApp;
import com.cmbc.devops.service.ClusterAppService;
import com.cmbc.devops.service.ClusterService;

/**  
 * date：2015年10月26日 上午10:54:28  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ClusterAppServiceImpl.java  
 * description：  
 */
@Component
public class ClusterAppServiceImpl implements ClusterAppService {
	
	@Autowired
	private ClusterAppMapper clusterAppMapper;
	private static final Logger logger = Logger.getLogger(ClusterService.class);
	
	@Override
	public int addClusterApp(ClusterApp record) throws Exception {
		logger.info("insert clusterapp entity to db");
		return clusterAppMapper.insert(record);
	}

	@Override
	public int removeClusterAppByClusterId(Integer[] clusterIds) throws Exception {
		logger.info("Remove cluster clusterapp by clusterId");
		return clusterAppMapper.deleteByClusterId(clusterIds);
	}

	@Override
	public int removeClusterAppByAppId(Integer[] appIds) throws Exception {
		logger.info("Remove cluster clusterapp by appId");
		return clusterAppMapper.deleteByAppId(appIds);
	}

	@Override
	public List<ClusterApp> listClusterAppsByClusterId(Integer[] clusterIds) throws Exception {
		//logger.info("list all cluster app entites from db by clusterIds");
		return clusterAppMapper.selectByClusterId(clusterIds);
	}

	@Override
	public List<ClusterApp> listClusterAppsByAppId(Integer[] appIds) throws Exception {
		//logger.info("list all cluster app entites from db by appIds");
		return clusterAppMapper.selectByAppId(appIds);
	}

}
