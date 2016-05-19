package com.cmbc.devops.dao;

import java.util.List;

import com.cmbc.devops.entity.MonitorProxy;
import com.cmbc.devops.exception.SqlException;;

/**
 * date：2015年8月18日 下午11:53:13 project name：cmbc-devops-dao
 * 
 * @author yangqinglin
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationMapper.java description：
 */
public interface MntrProxyMapper {
	/**
	 * @author yangqinglin
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年11月18日
	 * insert monitory proxy entity to DB
	 */
	public int insertMntrProxy(MonitorProxy mntrProxy) throws SqlException;

	/**
	 * TODO
	 * 
	 * @author yangqinglin
	 * @return List<Application>
	 * @throws SqlException
	 * @version 1.0 2015年8月18日
	 * delete monitory proxy entitied from DB by monitor proxy id(primary key)
	 */
	public int deleteMntrProxy(List<Integer> mntridList) throws SqlException;

	/**
	 * TODO
	 * 
	 * @author yangqinglin
	 * @return int
	 * @throws SqlException
	 * @version 1.0 2015年8月18日
	 * update monitory porxy entities to DB
	 */
	public int updateMntrProxy(MonitorProxy mntrProxy) throws SqlException;
	/**
	 * TODO:
	 * 
	 * @author yangqinglin
	 * @return int
	 * @throws SqlException
	 * @version 1.0 2015年8月18日
	 * select monitor proxy from DB which meet monitoryProxy conditions
	 */
	public List<MonitorProxy> selectAll(MonitorProxy mntrProxy) throws SqlException;

	/**
	 * TODO
	 * 
	 * @author yangqinglin
	 * @return int
	 * @throws SqlException
	 * @version 1.0 2015年8月18日
	 * select monitorProxy entities from DB by monitorProxy Id(primary key)
	 */
	public MonitorProxy selectMntrProxyById(Integer mntrId) throws SqlException;

}