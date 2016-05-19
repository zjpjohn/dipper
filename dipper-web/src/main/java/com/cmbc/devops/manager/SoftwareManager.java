package com.cmbc.devops.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.MessageResult;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.core.SoftwareCore;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.HostSoft;
import com.cmbc.devops.entity.Software;
import com.cmbc.devops.message.MessagePush;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.HostSoftService;
import com.cmbc.devops.service.SoftwareService;

@Component
public class SoftwareManager {

	private final static Logger LOGGER = Logger.getLogger(SoftwareManager.class);

	@Autowired
	private SoftwareService softService;
	@Autowired
	private SoftwareCore softwareCore;
	@Autowired
	private HostService hostService;
	@Autowired
	private HostSoftService hostsoftService;
	@Resource
	private MessagePush messagePush;

	/** 获取单页的软件列表信息 */
	public GridBean listOnePageSofts(int pagenumber, int pagesize, int tenantId) {
		try {
			return softService.listOnePageSofts(pagenumber, pagesize, tenantId);
		} catch (Exception e) {
			LOGGER.error("list one page software failed!", e);
			return null;
		}
	}

	/** 根据请求的软件类型，获取软件及版本列表 **/
	public JSONArray typeList(int softType) {
		try {
			List<Software> softList = softService.listAllByType(softType);
			if (!softList.isEmpty()) {
				JSONArray jsonArray = (JSONArray) JSONArray.toJSON(softList);
				return jsonArray;
			}
		} catch (Exception e) {
			LOGGER.error("list service by type(" + softType + ")", e);
		}
		return null;
	}

	/**
	 * 软件安装
	 * 
	 * @param hostids
	 * @param softid
	 * @param userId
	 */
	public void installSoftware(String[] hostids, int softid, int userId) {

		// 1.或取主机列表
		List<HostModel> hostlist = null;
		try {
			hostlist = hostService.getHostModelByIds(hostids);
		} catch (Exception e) {
			LOGGER.error("get hostModel list by hostids error");
		}
		pushMessage(userId, new MessageResult(false, "10#" + "查询目标主机记录成功。", "软件安装"));
		// 2.获取安装软件详情
		Software softinfo = null;
		try {
			softinfo = softService.getOneById(softid);
		} catch (Exception e) {
			LOGGER.error("get software by softid[" + softid + "] error!");
		}
		pushMessage(userId, new MessageResult(false, "20#" + "查询软件相关信息成功,开始安装。", "软件安装"));

		int averageNum = 75 / hostlist.size(), start = 20, count = 1;

		// 记录安装出错的主机
		List<String> errorHosts = new ArrayList<String>();
		boolean flag = false;

		// 3.批量安装软件
		List<HostModel> reHosts = softwareCore.installSoftware(hostlist, softinfo.getSwYumcall());
		for (HostModel hostModel : reHosts) {
			JSONObject obj = hostModel.getHostJo();
			// 安装成功，保存数据库
			if (obj.getBoolean("result")) {
				HostSoft hs = new HostSoft();
				hs.setCreatetime(new Date());
				hs.setHostId(hostModel.getHostId());
				hs.setSwId(softid);
				hs.setCreator(userId);
				hostsoftService.createRecord(hs);
			} else {
				errorHosts.add(hostModel.getHostName());
				flag = true;
			}
			pushMessage(userId, new MessageResult(false, start + (averageNum * count) + "#" + "【"
					+ hostModel.getHostName() + "】:" + obj.getString("message"), "软件安装"));
		}

		if (flag) {
			if (errorHosts.size() == hostlist.size()) {
				pushMessage(userId, new MessageResult(false, "100#" + "null$主机软件安装全部出错。", "软件安装"));
			} else {
				pushMessage(userId, new MessageResult(false,
						"100#" + "part$" + StringUtils.join(errorHosts, "，") + "软件安装出错。其余主机已安装完成。", "软件安装"));
			}
		} else {
			pushMessage(userId, new MessageResult(false, "100#" + "all$主机软件已安装完成。", "软件安装"));
		}
	}

	private void pushMessage(final Integer userId, final MessageResult message) {
		messagePush.pushMessage(userId, JSONObject.toJSONString(message));
		LOGGER.info("Send message :" + message + "to:" + userId);
	}

	/** 对于软件信息修正处理 */
	public Result modifySoft(final Integer userId, Software software) {
		try {
			int result = softService.updateSoft(software);
			if (result > 0) {
				return new Result(true, "修改软件(" + software.getSwName() + ")信息成功!");
			} else {
				return new Result(false, "修改软件(" + software.getSwName() + ")信息失败!");
			}
		} catch (Exception e) {
			LOGGER.info("User(ID:" + userId + ") update software(ID:" + software.getSwId() + ") failed!", e);
		}
		return new Result(false, "修改软件(" + software.getSwName() + ")信息失败!");
	}

	/** 新建软件的处理 */
	public Result addSoft(Integer userId, Software software) {
		try {
			int result = softService.insertSoft(software);
			if (result > 0) {
				return new Result(true, "新建软件(" + software.getSwName() + ")信息成功!");
			} else {
				return new Result(false, "新建软件(" + software.getSwName() + ")信息失败!");
			}
		} catch (Exception e) {
			LOGGER.info("User(ID:" + userId + ") insert software(ID:" + software.getSwId() + ") failed!", e);
		}
		return new Result(false, "新建软件(" + software.getSwName() + ")信息失败!");
	}

	public Result deleteSoft(Integer userId, String softIds) {
		String[] softArray = softIds.split(",");
		List<Integer> softIdList = new ArrayList<Integer>();
		for (int count = 0, length = softArray.length; count < length; count++) {
			softIdList.add(Integer.parseInt(softArray[count]));
		}

		try {
			int result = softService.deleteSofts(softIdList);
			if (result > 0) {
				return new Result(true, "删除软件记录成功!");
			} else {
				return new Result(false, "删除软件(" + softIds + ")信息失败!");
			}
		} catch (Exception e) {
			LOGGER.info("User(ID:" + userId + ") insert software(ID:" + softIds + ") failed!", e);
		}
		return new Result(false, "删除软件(" + softIds + ")信息失败!");
	}

	/** 对于软件列表进行模糊查询 */
	public GridBean ListSearch(Integer userId, int pagenumber, int pagesize, Software software) {
		try {
			return softService.listSearch(userId, pagenumber, pagesize, software);
		} catch (Exception e) {
			LOGGER.error("list Search software failed!", e);
			return null;
		}
	}

	public JSONArray advancedSearch(Integer userId, Software software) {
		try {
			return softService.advancedSearch(userId, software);
		} catch (Exception e) {
			LOGGER.error("list Search software failed!", e);
			return null;
		}
	}

	/**
	 * 根据软件id获取软件详细信息
	 * 
	 * @param id
	 * @return
	 */
	public Software detail(Integer id) {
		try {
			Software softinfo = softService.getOneById(id);
			return softinfo;
		} catch (Exception e) {
			LOGGER.error("get software by softid[" + id + "] error!", e);
			return null;
		}
	}

	public List<String> getHostsBySoftId(Integer id) {
		List<String> listStr = new ArrayList<String>();
		try {
			List<Host> hostlist = hostService.getListBySoftId(id);
			if (!hostlist.isEmpty()) {
				// 获取集群id
				List<Integer> clusteridList = new LinkedList<Integer>();
				for (Host host : hostlist) {
					if (!clusteridList.contains(host.getClusterId())) {
						clusteridList.add(host.getClusterId());
						// 存放集群中的主机
						List<String> hosts = new ArrayList<String>();
						for (Host info : hostlist) {
							if (host.getClusterId().equals(info.getClusterId())) {
								hosts.add(info.getHostName());
							}
						}
						// 每个集群的主机拼接成字符串
						String str = host.getClusterName() + " ( " + StringUtils.join(hosts, " , ") + " ) ";
						listStr.add(str);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Search hostlist by SoftId(ID:" + id + ") failed", e);
		}
		return listStr;
	}

	public Boolean checkSoftName(String softName) {
		try {
			return softService.getSoftByName(softName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get Software by softName[" + softName + "] failed!", e);
			return false;
		}
	}
}
