package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.HostWithUser;

/**
 * @author luogan 2015年8月29日 下午4:03:44
 */
public interface HostMapper {

	/**
	 * @author luogan
	 * @param host
	 * @return create host
	 * @throws Exception
	 * @version 1.0 2015年8月28日 insert host entity to DB
	 */
	public int insertHost(Host record) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return delete host
	 * @throws Exception
	 * @version 1.0 2015年8月28日 delete host entity from DB by host Id(primary
	 *          key)
	 */
	public int deleteHost(Integer hostId) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return delete host
	 * @throws Exception
	 * @version 1.0 2015年8月28日 delete host entity from DB by host Ids(primary
	 *          key)
	 */
	public int deleteHosts(List<Integer> hostIds) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return update host
	 * @throws Exception
	 * @version 1.0 2015年8月28日 update host entity to DB
	 */
	public int updateHost(Host record) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return select host
	 * @throws Exception
	 * @version 1.0 2015年8月28日 select host entity from DB by host Id(primary
	 *          key)
	 */
	public Host selectHost(Integer hostId) throws Exception;

	/**
	 * @author langzi
	 * @param hostIp
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年9月11日 select host entity from DB by host Ip
	 */
	public Host selectHostByIp(Host host) throws Exception;

	/**
	 * @author langzi
	 * @param hostName
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年10月21日 select host entity from DB by host name
	 */
	public Host selectHostByName(String hostName) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @throws Exception
	 * @return select hostList
	 * @version 1.0 2015年8月28日 select host entities from DB which meet host
	 *          conditions
	 */
	public List<Host> selectAllHost(Host host) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @throws Exception
	 * @return create host
	 * @version 1.0 2015年8月28日 select host entities from DB which meet type
	 */
	public List<Host> selectHostList(int type) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return create host
	 * @throws Exception
	 * @version 1.0 2015年8月28日 select host entities from DB which meet cluster
	 *          conditions
	 */
	public List<Host> selectHostListByCluster(int type) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return create host
	 * @throws Exception
	 * @version 1.0 2015年8月28日 select host entities from DB which meet cluster
	 *          Id
	 */
	public List<Host> selectHostListByClusterId(int clusterId) throws Exception;

	/**
	 * @author yangqinglin
	 * @param host
	 * @return query all host via host id list
	 * @throws Exception
	 * @version 1.0 2015年10月10日 14:56 select host entities from DB which meet
	 *          host Id
	 */
	public List<Host> selectHostListByHostIds(List<Integer> hostIds) throws Exception;

	/**
	 * @author yangqinglin
	 * @param host
	 * @return query all host via hostwithuser
	 * @throws Exception
	 * @version 1.0 2015年10月30日
	 */
	public List<Host> selectHostByHostUser(HostWithUser hostuser) throws Exception;

	/**
	 * @author zll
	 * @param lbid
	 * @return query all host by lbid
	 * @throws Exception
	 * @version 1.0 2015年10月10日 14:56 select host entities from DB which meet
	 *          loadbalance Id
	 */
	public List<Host> selectAllHostByLBId(Integer lbid) throws Exception;

	public List<Host> getListBySoftId(Integer softId) throws Exception;

	/** 批量更新，将符合条件的主机节点划入集群资源中 */
	public Integer updateHostInCluster(Map<String, Object> insMap) throws Exception;
}