package com.cmbc.devops.manager.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.manager.ClusterManager;
import com.cmbc.devops.service.ClusterService;

@Component("checkHostAndClusterStatus")
public class CheckHostAndClusterStatus {
	private static final Logger LOGGER = Logger.getLogger(CheckHostAndClusterStatus.class);
	
	@Resource
	private ClusterManager clusterManager; 
	@Resource
	private ClusterService clusterService;
	
	//五分钟执行一次
	@Scheduled(cron="0 0/5 * * * ? ")
	private void work() {
		clusterHealthCheack();
	}

	/**
	 * 集群健康检查
	 */
	private void clusterHealthCheack() {
		//获取集群列表
		List<Cluster> clulist=new ArrayList<Cluster>();
		try {
			clulist=clusterService.listAllCluster();
		} catch (Exception e) {
			LOGGER.error("list All Cluster error!");
		}
		if(!clulist.isEmpty()){
			//遍历集群
			for (Cluster cluster : clulist) {
				//健康检查
				Result re=clusterManager.clusterHealthCheck(cluster.getClusterId());
				//根据返回结果更改数据库集群状态
				if(re.isSuccess()){
					if(cluster.getClusterStatus()==Status.CLUSTER.ABNORMAL.ordinal()){
						cluster.setClusterStatus((byte) Status.CLUSTER.NORMAL.ordinal());
					}
				}else{
					if(cluster.getClusterStatus()==Status.CLUSTER.NORMAL.ordinal()){
						cluster.setClusterStatus((byte) Status.CLUSTER.ABNORMAL.ordinal());
					}
				}
				clusterService.update(cluster);
			}
		}
	}
}
