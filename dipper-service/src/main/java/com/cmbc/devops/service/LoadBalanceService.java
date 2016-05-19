package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.LoadBalance;


/**  
 * date：2015年9月10日 下午3:53:57  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：LoadBalanceService.java  
 * description：  
 */
public interface LoadBalanceService {
	
	/**
	 * @author langzi
	 * @return
	 * @version 1.0
	 * @throws Exception
	 * 2015年9月10日
	 */
	public List<LoadBalance> listAll(LoadBalance lb) throws Exception;
	
	/**
	 * @author langzi
	 * @param conId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 */
	public List<LoadBalance> listLoadBalanceByConId(String[] conIds) throws Exception;
	
	/**
	 * @author langzi
	 * @param conId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 */
	public List<LoadBalance> listLoadBalanceByHostId(Integer hostId) throws Exception;
	
	/**
	 * @author langzi
	 * @param userId
	 * @param pagenum
	 * @param pagesize
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月10日
	 */
	public GridBean listOnePageBalances(Integer userId, int pagenum,int pagesize, LoadBalance lb) throws Exception;
	
	/**
	 * @author langzi
	 * @param lbId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 */
	public LoadBalance getLoadBalance(Integer lbId) throws Exception;
	
	/**
	 * @author langzi
	 * @param lbName
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年10月21日
	 */
	public LoadBalance getLoadBalance(String lbName) throws Exception;
	
	/**
	 * @author langzi
	 * @param loadBalance
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月10日
	 */
	public Integer addLoadBalance(LoadBalance loadBalance) throws Exception;
	
	/**
	 * @author langzi
	 * @param loadBalance
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月10日
	 */
	public Integer updateBalance(LoadBalance loadBalance) throws Exception;
	
	/**
	 * @author langzi
	 * @param lbId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月10日
	 */
	public Integer removeBalance(String[] lbIds) throws Exception;

	/**
	 * @author yangqinglin
	 * @param lbId
	 * @return
	 * @version 1.0
	 * 2015年11月3日
	 * @throws Exception 
	 */
	public GridBean advancedSearchLoadbalance(Integer userId, int pagenum, int pagesize, JSONObject json_object) throws Exception;
	
	/**
	 * 根据负载id获取负载信息
	 * @param lbid
	 * @return
	 * @throws Exception
	 */
	public JSONObject getHostOfLBId(Integer lbId) throws Exception;
	
}
