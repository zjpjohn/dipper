package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import com.cmbc.devops.entity.ClusterResource;

/**
 * @author langzi
 *
 */
public interface ClusterResourceMapper {
    
    /**
     * @author langzi
     * @param record
     * @return
     * @version 1.0
     * 2016年4月25日
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
    public int updateConIdByHostIdAndCpuIds(Map<String, Object> crMap);
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
     * @param map
     * @return
     */
	public int deleteByIds(Map<String, Integer> map)  throws Exception;
	
	public List<ClusterResource> findHostsByClusterId(Integer clusterId)  throws Exception;
	
	public int collbackUpdate() throws Exception;
    
}