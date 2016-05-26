package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.LoadBalance;

public interface LoadBalanceMapper {
	
	/**
     * @author langzi
     * @param record
     * @return
     * @throws Exception
     * @version 1.0
     * 2015年9月10日
     * insert loadbalance entity to DB
     */
    public int insertLoadBalance(LoadBalance record) throws Exception;

    /**
     * @author langzi
     * @param lbId
     * @return
     * @throws Exception
     * @version 1.0
     * 2015年9月10日
     * delete loadbalance entity from DB by lb ids(primary key)
     */
    public Integer deleteLoadBalance(String[] lbIds) throws Exception;
    
    /**
     * @author langzi
     * @param record
     * @return
     * @throws Exception
     * @version 1.0
     * 2015年9月10日
     * update loadbalance entity to DB
     */
    public int updateLoadBalance(LoadBalance record) throws Exception;
	
	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月10日
	 * select all loadbalance entities from DB which meet lb conditions
	 */
	public List<LoadBalance> selectAll(LoadBalance lb) throws Exception;
	
	/**
	 * @author langzi
	 * @param conIds
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 * select all loadbalance entities from DB which meet container ids
	 */
	public List<LoadBalance> selectLoadBalanceByConId(String[] conIds) throws Exception;
	
	/**
	 * @author langzi
	 * @param conIds
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 * select all loadbalance entities from DB which meet host Id
	 */
	public List<LoadBalance> selectLoadBalanceByHostId(Integer hostId) throws Exception;
	
	/**
	 * @author langzi
	 * @param lbId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月10日
	 * select loadbalance from DB by lb Id(primary key)
	 */
	public LoadBalance selectLoadBalance(Integer lbId) throws Exception;
	
	/**
	 * @author langzi
	 * @param lbName
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年10月21日
	 * select loadbalance from DB by lb name
	 */
	public LoadBalance selectLbByName(String lbName) throws Exception;
    
}