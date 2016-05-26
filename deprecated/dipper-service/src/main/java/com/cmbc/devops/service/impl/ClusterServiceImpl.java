package com.cmbc.devops.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.dao.ClusterMapper;
import com.cmbc.devops.dao.HostMapper;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterWithCpuMem;
import com.cmbc.devops.entity.ClusterWithIPAndUser;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.model.ClusterModel;
import com.cmbc.devops.service.ClusterService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * @author luogan 2015年8月17日 上午9:10:18
 */
@Component
public class ClusterServiceImpl implements ClusterService {

	private static Logger logger = Logger.getLogger(ClusterServiceImpl.class);
	@Resource
	private ClusterMapper clusterMapper;
	@Resource
	private HostMapper hostMapper;

	@Override
	public int createCluster(ClusterModel model) {
		int result = 1;
		Cluster cluster = new Cluster();
		cluster.setClusterName(model.getClusterName());
		cluster.setClusterPort(model.getClusterPort());
		String cluUuid = UUID.randomUUID().toString();
		cluster.setClusterUuid(cluUuid);
		cluster.setMasteHostId(model.getMasteHostId());
		cluster.setClusterDesc(model.getClusterDesc());
		cluster.setClusterCreator(model.getUserId());
		cluster.setClusterType((byte) Type.CLUSTER.DOCKER.ordinal());
		cluster.setClusterMode((byte) model.getClusterMode());
		cluster.setManagePath(model.getManagePath());
		cluster.setClusterLogFile(model.getLogFile());
		cluster.setClusterStatus((byte) Status.CLUSTER.NORMAL.ordinal());
		cluster.setClusterCreatetime(new Date());
		cluster.setTenantId(model.getTenantId());
		cluster.setResType((byte) model.getResType());
		try {
			clusterMapper.insertCluster(cluster);
		} catch (Exception e) {
			logger.error("create cluster error", e);
			result = 0;
		}
		return result;
	}

	@Override
	public int deleteCluster(int clusterId) {
		try {
			return clusterMapper.deleteCluster(clusterId);
		} catch (Exception e) {
			logger.error("Remove cluster error", e);
			return 0;
		}
	}

	@Override
	public int update(Cluster cluster) {
		try {
			return clusterMapper.updateCluster(cluster);
		} catch (Exception e) {
			logger.error("update cluster fail" + e);
			return 0;
		}
	}

	@Override
	public int updateCluster(JSONObject jo) {
		int result = 1;
		try {
			Cluster cluster = JSONObject.toJavaObject(jo, Cluster.class);
			clusterMapper.updateCluster(cluster);
		} catch (Exception e) {
			logger.error("update cluster fail" + e);
			result = 0;
		}
		return result;
	}

	@Override
	public GridBean getOnePageClusterList(int userId, int tenantId, int page, int rows,
			ClusterWithIPAndUser clusterWithIPAndUser) throws Exception {
		PageHelper.startPage(page, rows);
		ClusterWithIPAndUser cluster = new ClusterWithIPAndUser();
		cluster.setClusterName(clusterWithIPAndUser.getClusterName());
		cluster.setTenantId(tenantId);
		List<ClusterWithIPAndUser> clusters = clusterMapper.selectAllClusterWithIPAndUser(cluster);
		int totalpage = ((Page<?>) clusters).getPages();
		Long totalNum = ((Page<?>) clusters).getTotal();
		GridBean gridBean = new GridBean(page, totalpage, totalNum.intValue(), clusters);
		return gridBean;
	}

	@Override
	public JSONArray getAllClusterList(int userId, int tenantId, ClusterWithIPAndUser clusterWithIPAndUser)
			throws Exception {
		ClusterWithIPAndUser cluster = new ClusterWithIPAndUser();
		cluster.setClusterStatus(clusterWithIPAndUser.getClusterStatus());
		cluster.setMasteHostId(clusterWithIPAndUser.getMasteHostId());
		cluster.setTenantId(tenantId);
		List<ClusterWithIPAndUser> clusters = clusterMapper.selectAllClusterWithIPAndUser(cluster);
		JSONArray ja = (JSONArray) JSONArray.toJSON(clusters);
		return ja;
	}

	@Override
	public JSONArray getOrphanClus() throws Exception {
		List<Cluster> clusters = clusterMapper.getOrphanClus();
		List<ClusterWithCpuMem> clusterCpuMems = new ArrayList<ClusterWithCpuMem>();

		for (Cluster sinCluster : clusters) {

			Integer clusterId = sinCluster.getClusterId();
			List<Host> hostList = hostMapper.selectHostListByClusterId(clusterId);
			int totalCpu = 0;
			int totalMem = 0;
			/** 遍历集群内的主机节点，获取CPU和内存的总量 **/
			if (!hostList.isEmpty()) {
				for (Host sinHost : hostList) {
					totalCpu += sinHost.getHostCpu();
					totalMem += sinHost.getHostMem();
				}
			}
			/** 生成带有CPU和内存总量元素，并加入到链表中 */
			ClusterWithCpuMem clusterCpuMem = new ClusterWithCpuMem(sinCluster, totalCpu, totalMem);
			clusterCpuMems.add(clusterCpuMem);
		}
		JSONArray ja = (JSONArray) JSONArray.toJSON(clusterCpuMems);
		return ja;
	}

	@Override
	public JSONArray getOnePageClusterMasterList(int tenantId) throws Exception {
		JSONArray ja = new JSONArray();
		Host sel_host = new Host();
		sel_host.setHostType((byte) Type.HOST.SWARM.ordinal());
		/** @todo tenantId */
		// List<Host> hostList = hostMapper.selectHostList(0);
		List<Host> hostList = hostMapper.selectAllHost(sel_host);
		if (hostList != null) {
			for (Host host : hostList) {
				JSONObject jo = new JSONObject();
				jo.put("hostip", host.getHostIp());
				jo.put("hostuuid", host.getHostUuid());
				jo.put("hostid", host.getHostId());
				ja.add(jo);
			}
		}
		return ja;
	}

	@Override
	public int countAllClusterList(ClusterModel clusterModel) {
		return 0;
	}

	@Override
	public Cluster getCluster(int clusterId) throws Exception {
		return clusterMapper.selectCluster(clusterId);
	}

	@Override
	public Cluster getClusterByHost(Host host) throws Exception {
		return clusterMapper.selectClusterBySlaveHostId(host.getHostId());
	}

	@Override
	public JSONObject getJsonObjectOfCluster(int clusterId) throws Exception {
		return (JSONObject) JSONObject.toJSON(getCluster(clusterId));
	}

	@Override
	public List<Cluster> listClustersByhostId(int hostId) throws Exception {
		return clusterMapper.selectClustersByMasterhostId(hostId);
	}

	@Override
	public JSONArray listClustersByappId(int appId) throws Exception {
		List<Cluster> clusters = clusterMapper.selectClusterInApp(appId);
		JSONArray ja = new JSONArray();
		for (Cluster cluster : clusters) {
			JSONObject jo = (JSONObject) JSONObject.toJSON(cluster);
			ja.add(jo);
		}
		return ja;
	}

	@Override
	public List<Cluster> listAllCluster() throws Exception {
		return clusterMapper.selectAllCluster();
	}

	@Override
	public Cluster getClusterByName(String clusterName) throws Exception {
		Cluster cluster = clusterMapper.selectClusterByName(clusterName);
		return cluster != null ? cluster : null;
	}

	@Override
	public Cluster getClusterBymanagePath(String managePath) throws Exception {
		Cluster cluster = clusterMapper.selectClusterByConf(managePath);
		return cluster != null ? cluster : null;
	}

	@Override
	public Integer updateClusterInTenant(Integer tenantId, List<Integer> clusterIds) throws Exception {
		Map<String, Object> updateCluMap = new HashMap<String, Object>();
		updateCluMap.put("tenantId", tenantId);
		updateCluMap.put("clusterIdList", clusterIds);
		return clusterMapper.updateClusterInTenant(updateCluMap);
	}

	@Override
	public GridBean advancedSearchCluster(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			ClusterModel clusterModel, JSONObject json_object) throws Exception {

		PageHelper.startPage(pagenumber, pagesize);

		/* 组装应用查询数据的条件 */
		ClusterWithIPAndUser clusteripuser = new ClusterWithIPAndUser();
		clusteripuser.setClusterStatus((byte) Status.CLUSTER.NORMAL.ordinal());

		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int i = 0; i < params.length; i++) {
			switch (params[i].trim()) {
			/* 填充集群名称 */
			case "1":
				clusteripuser.setClusterName(values[i].trim());
				break;
			/* 集群地址 */
			case "2":
				clusteripuser.setHostIP(values[i].trim());
				break;
			/* 管理端口号 */
			case "3":
				clusteripuser.setClusterPort(values[i].trim());
				break;
			/* 管理用户名称 */
			case "4":
				clusteripuser.setCreatorName(values[i].trim());
				break;
			default:
				break;
			}
		}

		/* 添加租户维度查询 */
		clusteripuser.setTenantId(tenantId);

		List<ClusterWithIPAndUser> clusters = clusterMapper.selectAllClusterWithIPAndUser(clusteripuser);

		int totalpage = ((Page<?>) clusters).getPages();
		Long totalNum = ((Page<?>) clusters).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), clusters);
		return gridBean;
	}

	@Override
	public GridBean searchAllClusters(int userId, int tenantId, int pageNum, int pageSize, ClusterModel clusterModel)
			throws Exception {
		PageHelper.startPage(pageNum, pageSize);
		ClusterWithIPAndUser cluster = new ClusterWithIPAndUser();
		cluster.setClusterName(clusterModel.getSearch());
		cluster.setTenantId(tenantId);
		List<ClusterWithIPAndUser> clusters = clusterMapper.selectAllClusterWithIPAndUser(cluster);

		int totalpage = ((Page<?>) clusters).getPages();
		Long totalNum = ((Page<?>) clusters).getTotal();
		GridBean gridBean = new GridBean(pageNum, totalpage, totalNum.intValue(), clusters);
		return gridBean;
	}

	@Override
	public List<Cluster> getClustersByAppId(Integer appId) throws Exception {
		return clusterMapper.selectClusterInApp(appId);
	}

}
