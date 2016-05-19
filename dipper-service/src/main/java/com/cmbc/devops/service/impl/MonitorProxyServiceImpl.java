package com.cmbc.devops.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.MntrProxyMapper;
import com.cmbc.devops.entity.MonitorProxy;
import com.cmbc.devops.entity.MonitorProxyUser;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.service.MonitorProxyService;
import com.cmbc.devops.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年8月18日 下午11:48:01 project name：cmbc-devops-service
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationServiceImpl.java description：
 */
@Component
public class MonitorProxyServiceImpl implements MonitorProxyService {
	
	@Resource
	private MntrProxyMapper mntrProxyMapper;
	@Resource
	private UserService userService;

	/** 创建监控代理对象 **/
	@Override
	public int createMonitorProxy(JSONObject json_object) throws Exception {
		MonitorProxy monitor_proxy = new MonitorProxy();
		monitor_proxy.setMpName(json_object.getString("mpName"));
		monitor_proxy.setMpIP(json_object.getString("mpIP"));
		monitor_proxy.setMpPort(json_object.getInteger("mpPort"));
		monitor_proxy.setMpDesc(json_object.getString("mpDesc"));
		monitor_proxy.setMpComment(json_object.getString("mpComment"));
		monitor_proxy.setMpCreator(json_object.getInteger("userId"));
		monitor_proxy.setMpStatus((byte) Status.MONITOR_PROXY.NORMAL.ordinal());
		monitor_proxy.setMpCreatetime(new Date());
		return mntrProxyMapper.insertMntrProxy(monitor_proxy);
	}

	@Override
	/**
	 * logger.error("(更新监控代理失败)update app exception", e);
	 */
	public int updateMonitorProxy(JSONObject json_object) throws Exception {
		int result = 0;
		MonitorProxy mntr_proxy = JSONObject.toJavaObject(json_object, MonitorProxy.class);
		result = mntrProxyMapper.updateMntrProxy(mntr_proxy);
		return result;
	}

	@Override
	/**
	 * logger.error("(删除多个应用失败)delete multi app exceprion", e);
	 */
	public int deleteMultiMP(List<Integer> mpid_list) throws Exception {
		int result = 0;
		result = mntrProxyMapper.deleteMntrProxy(mpid_list);
		return result;
	}

	@Override
	/**
	 * logger.error("(查询负载均衡列表失败)query loadbalance list exception", e);
	 * 对于监控代理的列表内容进行模糊查询。
	 */
	public GridBean searchAllMntrPxy(Integer userId, int pagenumber, int pagesize, String search_name)
			throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		MonitorProxy select_mntrpxy = new MonitorProxy();
		select_mntrpxy.setMpName(search_name);
		List<MonitorProxy> mntrpxy_list = mntrProxyMapper.selectAll(select_mntrpxy);

		Page<MonitorProxyUser> mntr_user_list = new Page<MonitorProxyUser>();

		for (MonitorProxy single_mntrpxy : mntrpxy_list) {
			MonitorProxyUser mntr_user = new MonitorProxyUser();
			mntr_user.setMpId(single_mntrpxy.getMpId());
			mntr_user.setMpName(single_mntrpxy.getMpName());
			mntr_user.setMpStatus(single_mntrpxy.getMpStatus());
			mntr_user.setMpIP(single_mntrpxy.getMpIP());
			mntr_user.setMpPort(single_mntrpxy.getMpPort());
			mntr_user.setMpDesc(single_mntrpxy.getMpDesc());
			mntr_user.setMpComment(single_mntrpxy.getMpComment());
			mntr_user.setMpCreatetime(single_mntrpxy.getMpCreatetime());

			/** 获取创建者的ID和名称信息 **/
			Integer user_id = single_mntrpxy.getMpCreator();
			mntr_user.setMpCreator(user_id);
			User select_user = new User();
			select_user.setUserId(user_id);
			select_user = userService.getUser(select_user);
			if (select_user.getUserName() != null) {
				mntr_user.setUserName(select_user.getUserName());
			}
			mntr_user_list.add(mntr_user);
		}

		int totalpage = ((Page<?>) mntrpxy_list).getPages();
		Long totalNum = ((Page<?>) mntrpxy_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), mntr_user_list);
		return gridBean;
	}

	@Override
	public MonitorProxy getMonitorProxyByName(String mpName) throws Exception {
		MonitorProxy mntr_proxy = new MonitorProxy();
		List<MonitorProxy> mp_list = mntrProxyMapper.selectAll(mntr_proxy);
		for (MonitorProxy single_mp : mp_list) {
			if (single_mp.getMpName().equals(mpName)) {
				return single_mp;
			}
		}
		return null;
	}

	@Override
	/**
	 * logger.error("(查询负载均衡列表失败)query loadbalance list exception", e);
	 */
	public GridBean advancedSearchMp(Integer userId, int pagenumber, int pagesize, JSONObject json_object)
			throws Exception {

		PageHelper.startPage(pagenumber, pagesize);
		/* 组装应用查询数据的条件 */
		MonitorProxy select_mntrpxy = new MonitorProxy();
		select_mntrpxy.setMpStatus((byte) Status.MONITOR_PROXY.NORMAL.ordinal());

		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int array_count = 0, array_length = params.length; array_count < array_length; array_count++) {
			switch (params[array_count].trim()) {
			case "1": 
				select_mntrpxy.setMpName(values[array_count].trim());
				break;
			case "2": 
				select_mntrpxy.setMpIP(values[array_count].trim());
				break;
			case "3": 
				select_mntrpxy.setMpPort(Integer.parseInt(values[array_count].trim()));
				break;
			case "4": 
				select_mntrpxy.setMpDesc(values[array_count].trim());
				break;
			case "5": 
				select_mntrpxy.setMpComment(values[array_count].trim());
				break;
			default:
				break;
			}
		}
		List<MonitorProxy> mntrpxy_list = mntrProxyMapper.selectAll(select_mntrpxy);
		Page<MonitorProxyUser> mntr_user_list = new Page<MonitorProxyUser>();

		for (MonitorProxy single_mntrpxy : mntrpxy_list) {
			MonitorProxyUser mntr_user = new MonitorProxyUser();
			mntr_user.setMpId(single_mntrpxy.getMpId());
			mntr_user.setMpName(single_mntrpxy.getMpName());
			mntr_user.setMpStatus(single_mntrpxy.getMpStatus());
			mntr_user.setMpIP(single_mntrpxy.getMpIP());
			mntr_user.setMpPort(single_mntrpxy.getMpPort());
			mntr_user.setMpDesc(single_mntrpxy.getMpDesc());
			mntr_user.setMpComment(single_mntrpxy.getMpComment());
			mntr_user.setMpCreatetime(single_mntrpxy.getMpCreatetime());

			/** 获取创建者的ID和名称信息 **/
			Integer user_id = single_mntrpxy.getMpCreator();
			mntr_user.setMpCreator(user_id);
			User select_user = new User();
			select_user.setUserId(user_id);
			select_user = userService.getUser(select_user);
			if (select_user.getUserName() != null) {
				mntr_user.setUserName(select_user.getUserName());
			}
			mntr_user_list.add(mntr_user);
		}

		int totalpage = ((Page<?>) mntrpxy_list).getPages();
		Long totalNum = ((Page<?>) mntrpxy_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), mntr_user_list);
		return gridBean;
	}

	@Override
	public GridBean selectAllMntrProxy(Integer userId, int pagenumber, int pagesize, MonitorProxy mntr_proxy)
			throws Exception {
		PageHelper.startPage(pagenumber, pagesize);

		MonitorProxy select_mntrpxy = new MonitorProxy();
		List<MonitorProxy> mntrpxy_list = mntrProxyMapper.selectAll(select_mntrpxy);

		Page<MonitorProxyUser> mntr_user_list = new Page<MonitorProxyUser>();

		for (MonitorProxy single_mntrpxy : mntrpxy_list) {
			MonitorProxyUser mntr_user = new MonitorProxyUser();
			mntr_user.setMpId(single_mntrpxy.getMpId());
			mntr_user.setMpName(single_mntrpxy.getMpName());
			mntr_user.setMpStatus(single_mntrpxy.getMpStatus());
			mntr_user.setMpIP(single_mntrpxy.getMpIP());
			mntr_user.setMpPort(single_mntrpxy.getMpPort());
			mntr_user.setMpDesc(single_mntrpxy.getMpDesc());
			mntr_user.setMpComment(single_mntrpxy.getMpComment());
			mntr_user.setMpCreatetime(single_mntrpxy.getMpCreatetime());

			/** 获取创建者的ID和名称信息 **/
			Integer user_id = single_mntrpxy.getMpCreator();
			mntr_user.setMpCreator(user_id);
			User select_user = new User();
			select_user.setUserId(user_id);
			select_user = userService.getUser(select_user);
			if (select_user.getUserName() != null) {
				mntr_user.setUserName(select_user.getUserName());
			}
			mntr_user_list.add(mntr_user);
		}

		int totalpage = ((Page<?>) mntrpxy_list).getPages();
		Long totalNum = ((Page<?>) mntrpxy_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), mntr_user_list);
		return gridBean;

	}

	@Override
	public JSONArray listAll(Integer userId, MonitorProxy mntr_proxy) throws Exception {
		List<MonitorProxy> mntrpxy_list = mntrProxyMapper.selectAll(mntr_proxy);
		return (JSONArray) JSONArray.toJSON(mntrpxy_list);

	}
}
