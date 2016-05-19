package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.HostWithUser;
import com.cmbc.devops.exception.SqlException;

/**
 * @author luogan 2015年8月29日 下午4:03:44
 */
public interface HostMapper {

	/**
	 * @author luogan
	 * @param host
	 * @return create host
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 insert host entity to DB
	 */
	public int insertHost(Host record) throws SqlException;

	/**
	 * @author luogan
	 * @param host
	 * @return delete host
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 delete host entity from DB by host Id(primary
	 *          key)
	 */
	public int deleteHost(Integer hostId) throws SqlException;

	/**
	 * @author luogan
	 * @param host
	 * @return delete host
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 delete host entity from DB by host Ids(primary
	 *          key)
	 */
	public int deleteHosts(List<Integer> hostIds) throws SqlException;

	/**
	 * @author luogan
	 * @param host
	 * @return update host
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 update host entity to DB
	 */
	public int updateHost(Host record) throws SqlException;

	/**
	 * @author luogan
	 * @param host
	 * @return select host
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 select host entity from DB by host Id(primary
	 *          key)
	 */
	public Host selectHost(Integer hostId) throws SqlException;

	/**
	 * @author langzi
	 * @param hostIp
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年9月11日 select host entity from DB by host Ip
	 */
	public Host selectHostByIp(Host host) throws SqlException;

	/**
	 * @author langzi
	 * @param hostName
	 * @return
	 * @throws SqlException
	 * @version 1.0 2015年10月21日 select host entity from DB by host name
	 */
	public Host selectHostByName(String hostName) throws SqlException;

	/**
	 * @author luogan
	 * @param host
	 * @throws SqlException
	 * @return select hostList
	 * @version 1.0 2015年8月28日 select host entities from DB which meet host
	 *          conditions
	 */
	public List<Host> selectAllHost(Host host) throws SqlException;

	/**
	 * @author luogan
	 * @param host
	 * @throws SqlException
	 * @return create host
	 * @version 1.0 2015年8月28日 select host entities from DB which meet type
	 */
	public List<Host> selectHostList(int type) throws SqlException;

	/**
	 * @author luogan
	 * @param host
	 * @return create host
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 select host entities from DB which meet cluster
	 *          conditions
	 */
	public List<Host> selectHostListByCluster(int type) throws SqlException;

	/**
	 * @author luogan
	 * @param host
	 * @return create host
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 select host entities from DB which meet cluster
	 *          Id
	 */
	public List<Host> selectHostListByClusterId(int clusterId) throws SqlException;

	/**
	 * @author yangqinglin
	 * @param host
	 * @return query all host via host id list
	 * @throws SqlException
	 * @version 1.0 2015年10月10日 14:56 select host entities from DB which meet
	 *          host Id
	 */
	public List<Host> selectHostListByHostIds(List<Integer> hostIds) throws SqlException;

	/**
	 * @author yangqinglin
	 * @param host
	 * @return query all host via hostwithuser
	 * @throws SqlException
	 * @version 1.0 2015年10月30日
	 */
	public List<Host> selectHostByHostUser(HostWithUser hostuser) throws SqlException;

	/**
	 * @author zll
	 * @param lbid
	 * @return query all host by lbid
	 * @throws SqlException
	 * @version 1.0 2015年10月10日 14:56 select host entities from DB which meet
	 *          loadbalance Id
	 */
	public List<Host> selectAllHostByLBId(Integer lbid) throws SqlException;

	public List<Host> getListBySoftId(Integer softId) throws SqlException;

	/** 批量更新，将符合条件的主机节点划入集群资源中 */
	public Integer updateHostInCluster(Map<String, Object> insMap) throws Exception;
}