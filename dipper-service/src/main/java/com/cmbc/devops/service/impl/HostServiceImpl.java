package com.cmbc.devops.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.dao.ClusterMapper;
import com.cmbc.devops.dao.HostMapper;
import com.cmbc.devops.dao.UserMapper;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.HostWithUser;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.exception.SqlException;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.service.HostService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * @author luogan 2015年8月17日 上午9:10:22
 */
@Component
public class HostServiceImpl implements HostService {

	private static Logger logger = Logger.getLogger(HostServiceImpl.class);
	@Resource
	private HostMapper hostMapper;
	@Resource
	private UserMapper userMapper;
	@Resource
	private ClusterMapper clusterMapper;

	@Override
	public int createHost(JSONObject jo) {
		int result = 1;
		Host record = new Host();
		record.setHostName(jo.getString("hostName"));
		record.setHostUuid(jo.getString("hostUuid"));
		record.setHostCpu(jo.getInteger("hostCpu"));
		record.setHostMem(jo.getInteger("hostMem"));
		record.setHostKernelVersion(jo.getString("hostKernelVersion"));
		record.setHostIp(jo.getString("hostIp"));
		if (jo.getString("hostDesc") != null) {
			record.setHostDesc(jo.getString("hostDesc"));
		}
		record.setHostCreator(jo.getInteger("userId"));
		record.setHostType((byte) jo.getByte("hostType"));
		record.setHostStatus((byte) Status.HOST.NORMAL.ordinal());
		record.setHostUser(jo.getString("name"));
		record.setHostPwd(jo.getString("password"));
		record.setHostCreatetime(new Date());
		try {
			result = hostMapper.insertHost(record);
		} catch (Exception e) {
			logger.error("create host fail" + e);
			result = 0;
		}
		return result;
	}

	@Override
	public int createHost(Host host) throws Exception {
		return hostMapper.insertHost(host);
	}

	@Override
	public int deleteHost(Integer hostId) {
		try {
			return hostMapper.deleteHost(hostId);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int deleteHosts(List<Integer> hostIds) {
		try {
			return hostMapper.deleteHosts(hostIds);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public int update(Host host) {
		try {
			return hostMapper.updateHost(host);
		} catch (Exception e) {
			logger.error("update host fail", e);
			return -1;
		}
	}

	@Override
	public GridBean getOnePageHostList(int userId, int page, int rows, HostModel hostModel) throws Exception {
		PageHelper.startPage(page, rows);
		Host host = new Host();
		/* 判断是否传入了主机类型的参数 */
		if (hostModel.getHostType() != null) {
			host.setHostType((byte) hostModel.getHostType().intValue());
		}
		List<Host> hosts = hostMapper.selectAllHost(host);

		/** 获取全部的集群和用户列表，方便后续匹配处理 */
		List<Cluster> clusters = clusterMapper.selectAllCluster();
		List<User> users = userMapper.fetchAllUser();

		/** @bug81_begin 主机管理,加入创建人的显示列 */
		Page<HostWithUser> pageObject = new Page<HostWithUser>();
		for (Host singleHost : hosts) {
			HostWithUser host_user = new HostWithUser(singleHost);
			/* 填充集群相关的信息 */
			Integer clusterId = singleHost.getClusterId();
			if (clusterId != null) {
				for (Cluster sinCluster : clusters) {
					if (clusterId.intValue() == sinCluster.getClusterId().intValue()) {
						host_user.setClusterName(sinCluster.getClusterName());
					}
				}
			}

			/* 遍历single_host所有信息，填充到host_user中 */
			Integer shUserId = singleHost.getHostCreator();
			if (shUserId != null) {
				for (User sinUser : users) {
					if (shUserId.intValue() == sinUser.getUserId().intValue()) {
						host_user.setCreatorName(sinUser.getUserName());
					}
				}
			}
			pageObject.add(host_user);
		}
		/** @bug81_finish */

		int totalpage = ((Page<?>) hosts).getPages();
		Long totalNum = ((Page<?>) hosts).getTotal();

		GridBean gridBean = new GridBean(page, totalpage, totalNum.intValue(), pageObject);
		return gridBean;
	}

	@Override
	public int countAllClusterList(HostModel hostModel) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Host getHost(Host host) throws Exception {
		return hostMapper.selectHost(host.getHostId());
	}

	@Override
	public Host getHostByIp(Host host) throws Exception {
		return hostMapper.selectHostByIp(host);
	}

	@Override
	public JSONObject getJsonObjectOfHost(Host host) throws Exception {
		return (JSONObject) JSONObject.toJSON((getHost(host)));
	}

	@Override
	public List<Host> listHostByType(int type) throws Exception {
		return hostMapper.selectHostList(type);
	}

	@Override
	public List<Host> listHostByClusterId(int clusterId) throws Exception {
		return hostMapper.selectHostListByClusterId(clusterId);
	}

	@Override
	public List<Host> listHostByTypeAndCluster(int type) throws Exception {
		return hostMapper.selectHostListByCluster(type);
	}

	@Override
	public GridBean searchAllHosts(int userId, int pageNum, int pageSize, HostModel hostModel) throws Exception {
		PageHelper.startPage(pageNum, pageSize);
		Host host = new Host();
		host.setHostName(hostModel.getSearch());
		List<Host> hosts = hostMapper.selectAllHost(host);

		/** 获取全部的集群和用户列表，方便后续匹配处理 */
		List<Cluster> clusters = clusterMapper.selectAllCluster();
		List<User> users = userMapper.fetchAllUser();

		/** @bug81_begin 主机管理,加入创建人的显示列 */
		Page<HostWithUser> pageObject = new Page<HostWithUser>();
		for (Host singleHost : hosts) {
			HostWithUser host_user = new HostWithUser(singleHost);
			/* 填充集群相关的信息 */
			Integer clusterId = singleHost.getClusterId();
			if (clusterId != null) {
				for (Cluster sinCluster : clusters) {
					if (clusterId == sinCluster.getClusterId()) {
						host_user.setClusterName(sinCluster.getClusterName());
					}
				}
			}

			/* 遍历single_host所有信息，填充到host_user中 */
			Integer shUserId = singleHost.getHostCreator();
			if (shUserId != null) {
				for (User sinUser : users) {
					if (shUserId == sinUser.getUserId()) {
						host_user.setCreatorName(sinUser.getUserName());
					}
				}
			}
			pageObject.add(host_user);
		}
		/** @bug81_finish */

		int totalpage = ((Page<?>) hosts).getPages();
		Long totalNum = ((Page<?>) hosts).getTotal();
		return new GridBean(pageNum, totalpage, totalNum.intValue(), pageObject);
	}

	@Override
	public List<Host> listHostByHostIds(List<Integer> host_ids) throws Exception {
		return hostMapper.selectHostListByHostIds(host_ids);
	}

	@Override
	public Host loadHost(Integer id) throws Exception {
		return hostMapper.selectHost(id);
	}

	@Override
	public Host getHostByName(String hostName) throws Exception {
		return hostMapper.selectHostByName(hostName);
	}

	@Override
	public List<Host> loadAllHost(Host host) throws Exception {
		return hostMapper.selectAllHost(host);
	}

	@Override
	public GridBean advancedSearchHost(Integer userId, int pagenumber, int pagesize, HostModel hostModel,
			JSONObject json_object) throws Exception {

		PageHelper.startPage(pagenumber, pagesize);

		/* 组装应用查询数据的条件 */
		HostWithUser hostuser = new HostWithUser();
		hostuser.setHostStatus((byte) Status.HOST.NORMAL.ordinal());

		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int i = 0; i < params.length; i++) {
			switch (params[i].trim()) {
			/* 填充主机名称 */
			case "1":
				hostuser.setHostName(values[i].trim());
				break;
			/* 主机类型 */
			case "2":
				if ("SWARM".indexOf(values[i].trim().toUpperCase()) != -1) {
					hostuser.setHostType((byte) Type.HOST.SWARM.ordinal());
				} else if ("DOCKER".indexOf(values[i].trim().toUpperCase()) != -1) {
					hostuser.setHostType((byte) Type.HOST.DOCKER.ordinal());
				} else if ("REGISTRY".indexOf(values[i].trim().toUpperCase()) != -1) {
					hostuser.setHostType((byte) Type.HOST.REGISTRY.ordinal());
				} else if ("NGINX".indexOf(values[i].trim().toUpperCase()) != -1) {
					hostuser.setHostType((byte) Type.HOST.NGINX.ordinal());
				} else {
					hostuser.setHostType((byte) Type.HOST.OTHER.ordinal());
				}
				break;
			/* CPU数量 */
			case "3":
				hostuser.setHostCpu(Integer.parseInt(values[i].trim()));
				break;
			/* 主机创建人姓名 */
			case "4":
				hostuser.setCreatorName(values[i].trim());
				break;
			default:
				break;
			}
		}

		List<Host> host_list = hostMapper.selectHostByHostUser(hostuser);

		Page<HostWithUser> page_obj = new Page<HostWithUser>();
		for (Host single_host : host_list)

		{
			HostWithUser host_user = new HostWithUser();
			/* 遍历single_host所有信息，填充到host_user中 */
			host_user.setHostId(single_host.getHostId());
			host_user.setHostUuid(single_host.getHostUuid());
			host_user.setHostName(single_host.getHostName());
			host_user.setHostUser(single_host.getHostUser());
			host_user.setHostPwd(single_host.getHostPwd());
			host_user.setHostType(single_host.getHostType());
			host_user.setHostIp(single_host.getHostIp());
			host_user.setHostCpu(single_host.getHostCpu());
			host_user.setHostMem(single_host.getHostMem());
			host_user.setHostStatus(single_host.getHostStatus());
			host_user.setHostDesc(single_host.getHostDesc());
			host_user.setHostKernelVersion(single_host.getHostKernelVersion());
			host_user.setClusterId(single_host.getClusterId());
			host_user.setHostCreatetime(single_host.getHostCreatetime());
			Integer user_id = single_host.getHostCreator();
			if (user_id != null) {
				host_user.setHostCreator(user_id);
				User user = new User();
				user.setUserId(user_id);
				user = userMapper.selectUser(user);
				if (user != null) {
					host_user.setCreatorName(user.getUserName());
				}
			}
			page_obj.add(host_user);
		}

		int totalpage = ((Page<?>) host_list).getPages();
		Long totalNum = ((Page<?>) host_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), page_obj);
		return gridBean;

	}

	@Override
	public List<Host> listAllHostByLBId(int lbid) throws Exception {
		List<Host> host_list = hostMapper.selectAllHostByLBId(lbid);
		return host_list;
	}

	@Override
	public Integer updateHostInCluster(Integer clusterId, List<Integer> hostIds) throws Exception {
		Map<String, Object> updateHostMap = new HashMap<String, Object>();
		updateHostMap.put("clusterId", clusterId);
		updateHostMap.put("hostIdList", hostIds);
		return hostMapper.updateHostInCluster(updateHostMap);
	}

	@Override
	public List<HostModel> getHostModelByIds(String[] hostids) throws Exception {
		List<HostModel> models = new ArrayList<HostModel>();

		List<Integer> hostIdList = new ArrayList<Integer>();
		for (String hostid : hostids) {
			hostIdList.add(Integer.valueOf(hostid));
		}
		List<Host> hostlist = hostMapper.selectHostListByHostIds(hostIdList);
		for (Host host : hostlist) {
			HostModel model = new HostModel();
			model.setHostId(host.getHostId());
			model.setHostIp(host.getHostIp());
			model.setHostName(host.getHostName());
			model.setHostUser(host.getHostUser());
			model.setHostPwd(host.getHostPwd());
			model.setHostCpu(host.getHostCpu());
			models.add(model);
		}
		return models;
	}

	@Override
	public List<Host> getListBySoftId(Integer id) throws SqlException {
		return hostMapper.getListBySoftId(id);
	}

}
