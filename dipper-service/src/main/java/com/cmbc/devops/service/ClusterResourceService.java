package com.cmbc.devops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmbc.devops.entity.ClusterResource;

/**  
 * date：2016年4月25日 上午11:14:20  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ClusterResourceService.java  
 * description：  
 */

public interface ClusterResourceService {
	/**
     * @author langzi
     * @param record
     * @return
     * @version 1.0
     * 2016年4月25日
	 * @throws Exception 
     */
    public int insert(ClusterResource record) throws Exception;
    /**
     * @author langzi
     * @param id
     * @return
     * @version 1.0
     * 2016年4月25日
     */
    public int delete(Integer id);
    /**
     * @author langzi
     * @param record
     * @return
     * @version 1.0
     * 2016年4月25日
     */
    public int update(ClusterResource record);
    /**
     * @author langzi
     * @param hostId
     * @param cpuIds
     * @param conId
     * @return
     * @version 1.0
     * 2016年4月25日
     */
    public int updateConIdByHostIdAndCpuIds(Integer hostId, Integer[] cpuIds, Integer conId);
    /**
     * @author langzi
     * @param conIds
     * @return
     * @version 1.0
     * 2016年4月25日
     */
    public int updateByConId(Integer[] conIds);
    /**
     * @author langzi
     * @param id
     * @return
     * @version 1.0
     * 2016年4月25日
     */
    public ClusterResource select(Integer id);
    /**
     * @author langzi
     * @param clusterId
     * @return
     * @version 1.0
     * 2016年4月25日
     */
    public List<ClusterResource> listAvaliableResourceByClusterId(Integer clusterId);
    /**
     * @author langzi
     * @param clusterId
     * @return
     * @version 1.0
     * 2016年4月25日
     */
    public List<ClusterResource> findSuitableHostId(Integer clusterId, int cpuNum);
    
    /**
     * delete by clusterId And hostId
     * @param hostId
     * @param clusterId
     * @throws Exception 
     */
	public int deleteByHostIdAndClusterId(Integer hostId, Integer clusterId) throws Exception; 
	
	 /**
     * @author zll
     * @param clusterId
     * @return
     * @version 1.0
     * 2016年4月25日
     */
    public List<ClusterResource> findHostsByClusterId(Integer clusterId) throws Exception;
    
	public abstract int collbackUpdate() throws Exception;
}
