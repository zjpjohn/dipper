package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterWithIPAndUser;

/**
 * @author luogan 2015年8月29日 下午4:03:22
 */
public interface ClusterMapper {

	/**
	 * @author luogan
	 * @param cluster
	 * @return create cluster
	 * @throws Exception
	 * @version 1.0 2015年8月28日 insert cluster entity to DB
	 */
	public int insertCluster(Cluster record) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return delete cluster
	 * @throws Exception
	 * @version 1.0 2015年8月28日 delete cluster entity from DB by
	 *          clusterId(primary key)
	 */
	public int deleteCluster(Integer clusterId) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return update cluster
	 * @throws Exception
	 * @version 1.0 2015年8月28日 update cluster entity to DB
	 */
	public int updateCluster(Cluster record) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return select cluster
	 * @throws Exception
	 * @version 1.0 2015年8月28日 select cluster entity from DB by
	 *          clusterId(primary key)
	 */
	public Cluster selectCluster(Integer clusterId) throws Exception;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年10月26日 select cluster entities from DB which meet app
	 *          conditions
	 */
	public List<Cluster> selectClusterInApp(Integer appId) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return create clusterList
	 * @throws Exception
	 * @version 1.0 2015年8月28日 select cluster entites from DB which meet cluster
	 *          conditions
	 */
	public List<ClusterWithIPAndUser> selectAllClusterWithIPAndUser(ClusterWithIPAndUser record) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return create clusterList
	 * @throws Exception
	 * @version 1.0 2015年8月28日 select all cluster entites from DB
	 */
	public List<Cluster> selectAllCluster() throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return create cluster
	 * @throws Exception
	 * @version 1.0 2015年8月28日 select cluster entites from DB by slaveHostId
	 *          which in cluster
	 */
	public Cluster selectClusterBySlaveHostId(Integer hostId) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return create clusterList
	 * @throws Exception
	 * @version 1.0 2015年8月28日 select cluster entites from DB by masterHostId
	 */
	public List<Cluster> selectClustersByMasterhostId(Integer hostId) throws Exception;

	/**
	 * @author langzi
	 * @param clusterName
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年10月19日 select cluster entity from DB by cluster name
	 */
	public Cluster selectClusterByName(String clusterName) throws Exception;

	/**
	 * @author langzi
	 * @param managePath
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年10月19日 select cluster entity from DB by cluster config
	 */
	public Cluster selectClusterByConf(String managePath) throws Exception;

	/** 获取全部没有划入租户的孤儿集群列表 */
	public List<Cluster> getOrphanClus() throws Exception;

	/** 批量更新，将符合条件的集群划入租户资源中 */
	public Integer updateClusterInTenant(Map<String, Object> insMap) throws Exception;

}