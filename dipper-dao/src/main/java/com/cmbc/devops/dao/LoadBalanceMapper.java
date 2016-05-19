package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.LoadBalance;
import com.cmbc.devops.exception.SqlException;

public interface LoadBalanceMapper {
	
	/**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年9月10日
     * insert loadbalance entity to DB
     */
    public int insertLoadBalance(LoadBalance record) throws SqlException;

    /**
     * @author langzi
     * @param lbId
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年9月10日
     * delete loadbalance entity from DB by lb ids(primary key)
     */
    public Integer deleteLoadBalance(String[] lbIds) throws SqlException;
    
    /**
     * @author langzi
     * @param record
     * @return
     * @throws SqlException
     * @version 1.0
     * 2015年9月10日
     * update loadbalance entity to DB
     */
    public int updateLoadBalance(LoadBalance record) throws SqlException;
	
	/**
	 * @author langzi
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2015年9月10日
	 * select all loadbalance entities from DB which meet lb conditions
	 */
	public List<LoadBalance> selectAll(LoadBalance lb) throws SqlException;
	
	/**
	 * @author langzi
	 * @param conIds
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2015年9月15日
	 * select all loadbalance entities from DB which meet container ids
	 */
	public List<LoadBalance> selectLoadBalanceByConId(String[] conIds) throws SqlException;
	
	/**
	 * @author langzi
	 * @param conIds
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2015年9月15日
	 * select all loadbalance entities from DB which meet host Id
	 */
	public List<LoadBalance> selectLoadBalanceByHostId(Integer hostId) throws SqlException;
	
	/**
	 * @author langzi
	 * @param lbId
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2015年9月10日
	 * select loadbalance from DB by lb Id(primary key)
	 */
	public LoadBalance selectLoadBalance(Integer lbId) throws SqlException;
	
	/**
	 * @author langzi
	 * @param lbName
	 * @return
	 * @throws SqlException
	 * @version 1.0
	 * 2015年10月21日
	 * select loadbalance from DB by lb name
	 */
	public LoadBalance selectLbByName(String lbName) throws SqlException;
    
}