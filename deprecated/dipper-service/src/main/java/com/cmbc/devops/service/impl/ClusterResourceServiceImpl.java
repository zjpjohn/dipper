package com.cmbc.devops.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmbc.devops.dao.ClusterResourceMapper;
import com.cmbc.devops.entity.ClusterResource;
import com.cmbc.devops.service.ClusterResourceService;

/**  
 * date：2016年4月25日 上午11:14:42  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ClusterResourceServiceImpl.java  
 * description：  
 */
/**
 * @author cmbc
 *
 */

@Service
public class ClusterResourceServiceImpl implements ClusterResourceService {
	
	@Autowired
	private ClusterResourceMapper mapper;
	
	@Override
	public int insert(ClusterResource resource) throws Exception{
		return mapper.insert(resource);
	}

	@Override
	public int delete(Integer id) {
		return mapper.delete(id);
	}

	@Override
	public int update(ClusterResource resource) {
		return mapper.update(resource);
	}

	@Override
	public int updateConIdByHostIdAndCpuIds(Integer hostId, Integer[] cpuIds,
			Integer conId) {
		Map<String, Object> crMap = new HashMap<>();
		crMap.put("hostId", hostId);
		crMap.put("cpuIds", cpuIds);
		crMap.put("conId", conId);
		return mapper.updateConIdByHostIdAndCpuIds(crMap);
	}

	@Override
	public int updateByConId(Integer[] conIds) {
		return mapper.updateByConId(conIds);
	}

	@Override
	public ClusterResource select(Integer id) {
		return mapper.select(id);
	}

	@Override
	public List<ClusterResource> listAvaliableResourceByClusterId(
			Integer clusterId) {
		return mapper.listAvaliableResourceByClusterId(clusterId);
	}

	@Override
	public List<ClusterResource> findSuitableHostId(Integer clusterId, int cpuNum) {
		//获取空闲cpu最大的主机
		List<ClusterResource> clusterRes = listAvaliableResourceByClusterId(clusterId);
		List<ClusterResource> avalibleRes = new ArrayList<ClusterResource>();
		if(clusterRes.isEmpty()){
			return null;
		}
		//取第一个hostId
		int hostId = clusterRes.get(0).getHostId();
		//过滤重复主机
		for(ClusterResource res:clusterRes){
			if(res.getHostId() == hostId){
				avalibleRes.add(res);
			}
		}
		//如果最大主机剩余核数满足cpu限制时返回，不满足则返回null
		if (avalibleRes.size() >= cpuNum) {
			return avalibleRes;
		}
		return null;
	}

	@Override
	public int deleteByHostIdAndClusterId(Integer hostId, Integer clusterId)  throws Exception{
		Map<String,Integer> map=new HashMap<String,Integer>();
		map.put("clusterId", clusterId);
		map.put("hostId", hostId);
		return mapper.deleteByIds(map);
	}

	@Override
	public List<ClusterResource> findHostsByClusterId(Integer clusterId) throws Exception {
		return mapper.findHostsByClusterId(clusterId);
	}

	@Override
	public int collbackUpdate() throws Exception {
		return mapper.collbackUpdate();
	}

}
