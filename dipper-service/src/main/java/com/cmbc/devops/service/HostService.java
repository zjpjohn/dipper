package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.model.HostModel;

/**
 * @author luogan 2015年8月17日 上午9:09:59
 */
public interface HostService {

	/**
	 * @author luogan
	 * @param host
	 * @return create host
	 * @version 1.0 2015年8月28日
	 */
	public abstract int createHost(JSONObject jo);

	/**
	 * @author langzi
	 * @param host
	 * @return
	 * @version 1.0 2015年10月23日
	 * @throws Exception
	 */
	public abstract int createHost(Host host) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return delete host
	 * @version 1.0 2015年8月28日
	 */
	public abstract int deleteHost(Integer hostId) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return delete host
	 * @version 1.0 2015年8月28日
	 */
	public abstract int deleteHosts(List<Integer> hostIds);

	/**
	 * @author luogan
	 * @param host
	 * @return update host
	 * @version 1.0 2015年8月28日
	 */
	public abstract int update(Host host);

	/**
	 * @author luogan
	 * @param host
	 * @return select hostList
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract GridBean getOnePageHostList(int userId, int page, int rows, HostModel hostModel) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return select hostList
	 * @version 1.0 2015年8月28日
	 */
	public abstract int countAllClusterList(HostModel hostModel);

	/**
	 * @author luogan
	 * @param host
	 * @return select host
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract Host getHost(Host host) throws Exception;

	/**
	 * 按照主键获取主机信息
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public abstract Host loadHost(Integer id) throws Exception;

	/**
	 * @author langzi
	 * @param hostIp
	 * @return
	 * @version 1.0 2015年9月11日
	 * @throws Exception
	 */
	public abstract Host getHostByIp(Host host) throws Exception;

	/**
	 * @author langzi
	 * @param hostName
	 * @return
	 * @version 1.0 2015年10月21日
	 * @throws Exception
	 */
	public abstract Host getHostByName(String hostName) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return select host
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract JSONObject getJsonObjectOfHost(Host host) throws Exception;

	/**
	 * @author langzi
	 * @param type
	 * @return
	 * @version 1.0 2015年9月11日
	 * @throws Exception
	 */
	public abstract List<Host> listHostByType(int type) throws Exception;

	/**
	 * @author langzi
	 * @param type
	 * @return
	 * @version 1.0 2015年9月11日
	 * @throws Exception
	 */
	public abstract List<Host> listHostByTypeAndCluster(int type) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return select ip
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract List<Host> listHostByClusterId(int clusterId) throws Exception;

	/**
	 * @author yangqinglin
	 * @param host
	 * @return 查询包含host id列表的所有主机
	 * @version 1.0 2015年10月10日
	 * @throws Exception
	 */
	public abstract List<Host> listHostByHostIds(List<Integer> host_ids) throws Exception;

	/**
	 * @author yangqinglin
	 * @param host
	 * @return GridBean
	 * @version 1.0 2015年10月8日
	 * @throws Exception
	 */
	public abstract GridBean searchAllHosts(int userId, int pageNum, int pageSize, HostModel hostModel)
			throws Exception;

	/**
	 * 加载所有的主机信息s
	 * 
	 * @throws Exception
	 */
	public abstract List<Host> loadAllHost(Host host) throws Exception;

	/**
	 * @author yangqinglin
	 * @param host
	 * @return GridBean
	 * @version 1.0 2015年10月30日
	 * @throws Exception
	 */
	public abstract GridBean advancedSearchHost(Integer userId, int pagenumber, int pagesize, HostModel hostModel,
			JSONObject json_object) throws Exception;

	/**
	 * 根据负载id获取它的主机和备份主机列表
	 * 
	 * @author zll
	 * @param lbid(负载id)
	 * @return List<Host>
	 * @throws Exception
	 */
	public abstract List<Host> listAllHostByLBId(int lbid) throws Exception;

	/**
	 * 根据主机id获取详细信息
	 * 
	 * @param hostids
	 * @return
	 * @throws Exception
	 */
	public abstract List<HostModel> getHostModelByIds(String[] hostids) throws Exception;

	/**
	 * 根据softwareid获取安装该软件的主机列表
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public abstract List<Host> getListBySoftId(Integer id) throws Exception;

	/** 批量更新，将主机节点划入到集群中 */
	public Integer updateHostInCluster(Integer clusterId, List<Integer> hostIds) throws Exception;
}
