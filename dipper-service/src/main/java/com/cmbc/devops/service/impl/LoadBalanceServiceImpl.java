package com.cmbc.devops.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.HostMapper;
import com.cmbc.devops.dao.LoadBalanceMapper;
import com.cmbc.devops.dao.UserMapper;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.LoadBalance;
import com.cmbc.devops.entity.LoadBalanceWithIPUser;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.service.LoadBalanceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年9月10日 下午4:00:55 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：LoadBalanceServiceImpl.java description：
 */
@Component
public class LoadBalanceServiceImpl implements LoadBalanceService {

	private static final Logger logger = Logger.getLogger(LoadBalanceServiceImpl.class);

	@Autowired
	private LoadBalanceMapper loadBalanceMapper;
	@Autowired
	private HostMapper hostMapper;
	@Autowired
	private UserMapper userMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.LoadBalanceService#listAll()
	 */
	@Override
	public List<LoadBalance> listAll(LoadBalance lb) throws Exception {
		return loadBalanceMapper.selectAll(lb);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.LoadBalanceService#listLoadBalanceByConId(java.
	 * lang.String[])
	 */
	@Override
	public List<LoadBalance> listLoadBalanceByConId(String[] conIds) throws Exception {
		return loadBalanceMapper.selectLoadBalanceByConId(conIds);
	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.service.LoadBalanceService#listLoadBalanceByHostId(java.lang.Integer)
	 */
	@Override
	public List<LoadBalance> listLoadBalanceByHostId(Integer hostId) throws Exception {
		return loadBalanceMapper.selectLoadBalanceByHostId(hostId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.LoadBalanceService#listOnePageBalances(java.lang.
	 * Integer, int, int)
	 */
	@Override
	public GridBean listOnePageBalances(Integer userId, int pagenum, int pagesize, LoadBalance lb) throws Exception {
		PageHelper.startPage(pagenum, pagesize);
		Page<LoadBalanceWithIPUser> lbInfos = new Page<LoadBalanceWithIPUser>();
		List<LoadBalance> lbs = listAll(lb);
		/* 将负载均衡的列表修变更为带有IP地址和用户名称的列表 */
		for (int i = 0; i < lbs.size(); i++) {
			LoadBalanceWithIPUser lbInfo = new LoadBalanceWithIPUser();
			BeanUtils.copyProperties(lbs.get(i), lbInfo);
			int mainHostId = lbInfo.getLbMainHost();
			String hostIp = getHostIp(mainHostId);
			/* 填充主负载均衡机器的IP地址 */
			lbInfo.setLbMainHostIP(hostIp);
			/* 填充备用负载均衡机器的IP地址 */
			Integer backupHostId = lbInfo.getLbBackupHost();
			if (backupHostId != null) {
				String backUphostIp = getHostIp(backupHostId);
				lbInfo.setLbBackupHostIP(backUphostIp);
			}
			int createId = lbInfo.getLbCreator();
			/* 填充用户名称 */
			String userName = getUserName(createId);
			lbInfo.setLbCreatorName(userName);
			lbInfos.add(lbInfo);
		}
		int totalPage = ((Page<?>) lbs).getPages();
		Long totalNum = ((Page<?>) lbs).getTotal();
		return new GridBean(pagenum, totalPage, totalNum.intValue(), lbInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.LoadBalanceService#getLoadBalance(java.lang.
	 * Integer)
	 */
	@Override
	public LoadBalance getLoadBalance(Integer lbId) throws Exception {
		return loadBalanceMapper.selectLoadBalance(lbId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.LoadBalanceService#addLoadBalance(com.cmbc.devops
	 * .entity.LoadBalance)
	 */
	@Override
	public Integer addLoadBalance(LoadBalance loadBalance) throws Exception {
		return loadBalanceMapper.insertLoadBalance(loadBalance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.LoadBalanceService#updateBalance(com.cmbc.devops.
	 * entity.LoadBalance)
	 */
	@Override
	public Integer updateBalance(LoadBalance loadBalance) throws Exception {
		return loadBalanceMapper.updateLoadBalance(loadBalance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.LoadBalanceService#removeBalance(java.lang.
	 * Integer)
	 */
	@Override
	public Integer removeBalance(String[] lbIds) throws Exception {
		return loadBalanceMapper.deleteLoadBalance(lbIds);
	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.service.LoadBalanceService#getLoadBalance(java.lang.String)
	 */
	@Override
	public LoadBalance getLoadBalance(String lbName) throws Exception {
		return loadBalanceMapper.selectLbByName(lbName);
	}

	@Override
	public GridBean advancedSearchLoadbalance(Integer userId, int pagenum, int pagesize, JSONObject searchParams)
			throws Exception {
		/* 组装应用查询数据的条件 */
		LoadBalance lb = new LoadBalance();
		lb.setLbStatus((byte) Status.LOADBALANCE.NORMAL.ordinal());

		/* 获取用户填写的各项查询条件 */
		String[] params = searchParams.getString("params").split(",");
		String[] values = searchParams.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int i=0; i < params.length; i++) {
			switch (params[i].trim()) {
			/* 负载均衡的名称 */
			case "1":
				lb.setLbName(values[i].trim());
				break;
				/* 配置文件的路径 */
			case "2":
				lb.setLbMainConf(values[i].trim());
				break;
				/* 负载均衡描述信息 */
			case "3":
				lb.setLbDesc(values[i].trim());
				break;
			default:
				break;
			}
		}
		return listOnePageBalances(userId, pagenum, pagesize, lb);

	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.service.LoadBalanceService#getHostOfLBId(java.lang.Integer)
	 */
	@Override
	public JSONObject getHostOfLBId(Integer lbId) throws Exception {
		JSONObject param=new JSONObject();
		LoadBalance lb=loadBalanceMapper.selectLoadBalance(lbId);
		if (lb==null) {
			return null;
		}
		try {
			Host host=hostMapper.selectHost(lb.getLbMainHost());
			if(host!=null){
				param.put("mainhostId", host.getHostId());
				param.put("mainhostName", host.getHostName());
			}
		} catch (Exception e) {
			logger.error("get main host["+lb.getLbMainHost()+"] failed",e);
			return null;
		}
		if (lb.getLbBackupHost() == null) {
			return param;
		}
		try {
			Host host=hostMapper.selectHost(lb.getLbBackupHost());
			if(host!=null){
				param.put("backhostId", host.getHostId());
				param.put("backhostName", host.getHostName());
			}
		} catch (Exception e) {
			logger.error("get backup host["+lb.getLbBackupHost()+"] failed",e);
			return null;
		}
		return param;
	}
	
	private String getHostIp(int hostId) throws Exception{
		Host host = hostMapper.selectHost(hostId);
		return host != null ? host.getHostIp():"";
	}
	
	private String getUserName(int userId) throws Exception{
		User user = new User();
		user.setUserId(userId);
		user = userMapper.selectUser(user);
		return user!=null ? user.getUserName():"";
	}

}
