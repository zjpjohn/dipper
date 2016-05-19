package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterWithIPAndUser;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.model.ClusterModel;

/**
 * @author luogan 2015年8月17日 上午9:09:53
 */
public interface ClusterService {

	/**
	 * @author luogan
	 * @param cluster
	 * @return create cluster
	 * @version 1.0 2015年8月28日
	 */
	public abstract int createCluster(ClusterModel model);

	/**
	 * @author luogan
	 * @param cluster
	 * @return delete cluster
	 * @version 1.0 2015年8月28日
	 */
	public abstract int deleteCluster(int clusterId);

	/**
	 * @author luogan
	 * @param cluster
	 * @return update cluster
	 * @version 1.0 2015年8月28日
	 */
	public abstract int update(Cluster cluster);

	/**
	 * @author luogan
	 * @param cluster
	 * @return update cluster
	 * @version 1.0 2015年8月28日
	 */
	public abstract int updateCluster(JSONObject jo);

	/**
	 * @author luogan
	 * @param cluster
	 * @return select clusterList
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract GridBean getOnePageClusterList(int userId, int tenantId, int page, int rows,
			ClusterWithIPAndUser clusterWithIPAndUser) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return select clusterList
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract JSONArray getAllClusterList(int userId, int tenantId, ClusterWithIPAndUser clusterWithIPAndUser)
			throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return select clusterList
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract List<Cluster> listClustersByhostId(int hostId) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return select clusterList
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract JSONArray listClustersByappId(int appId) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return select clusterList
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract JSONArray getOnePageClusterMasterList(int tenantId) throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年10月10日
	 * @throws Exception
	 */
	public abstract List<Cluster> listAllCluster() throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return select clusterList
	 * @version 1.0 2015年8月28日
	 */
	public abstract int countAllClusterList(ClusterModel clusterModel);

	/**
	 * @author luogan
	 * @param cluster
	 * @return select cluster
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public Cluster getCluster(int clusterId) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return select cluster
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public JSONObject getJsonObjectOfCluster(int clusterId) throws Exception;

	/**
	 * @author luogan
	 * @param cluster
	 * @return select clusterByHost
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public Cluster getClusterByHost(Host host) throws Exception;

	/**
	 * @author langzi
	 * @param name
	 * @return
	 * @version 1.0 2015年10月19日
	 * @throws Exception
	 */
	public Cluster getClusterByName(String clusterName) throws Exception;

	/**
	 * @author langzi
	 * @param managePath
	 * @return
	 * @version 1.0 2015年10月19日
	 * @throws Exception
	 */
	public Cluster getClusterBymanagePath(String managePath) throws Exception;

	/**
	 * @author yangqinglin
	 * @param managePath
	 * @return
	 * @version 1.0 2015年10月30日
	 * @throws Exception
	 */
	public abstract GridBean advancedSearchCluster(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			ClusterModel clusterModel, JSONObject json_object) throws Exception;

	/**
	 * 实现根据集群名称模糊查询
	 * 
	 * @throws Exception
	 */
	public abstract GridBean searchAllClusters(int userId, int tenantId, int pageNum, int pageSize,
			ClusterModel clusterModel) throws Exception;

	/**
	 * 通过appid获取约束集群列表
	 * 
	 * @throws Exception
	 */
	public List<Cluster> getClustersByAppId(Integer appid) throws Exception;

	/** 获取全部没有划归租户的孤儿集群列表 */
	public abstract JSONArray getOrphanClus() throws Exception;

	/** 将集群批量划入租户资源中 */
	public Integer updateClusterInTenant(Integer tenantId, List<Integer> clusterIds) throws Exception;

}
