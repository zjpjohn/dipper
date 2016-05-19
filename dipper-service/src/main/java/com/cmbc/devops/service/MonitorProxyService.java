package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.MonitorProxy;

/**
 * date：2015年8月18日 下午11:42:16 project name：cmbc-devops-service
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationService.java description：
 */
public interface MonitorProxyService {

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return void
	 * @version 1.0 2015年8月19日
	 * @throws Exception
	 */
	public int createMonitorProxy(JSONObject jo) throws Exception;

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return int
	 * @version 1.0 2015年8月23日
	 * @throws Exception
	 */
	public int updateMonitorProxy(JSONObject jo) throws Exception;

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回JSON字符串的方法修改为GridBean的方式
	 */
	public GridBean selectAllMntrProxy(Integer userId, int pagenumber, int pagesize, MonitorProxy mntr_proxy)
			throws Exception;

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回JSON字符串的方法修改为GridBean的方式
	 * 
	 */
	public JSONArray listAll(Integer userId, MonitorProxy mntr_proxy) throws Exception;

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月15日 16:59
	 * @description 添加删除多个Application的接口函数
	 */
	public int deleteMultiMP(List<Integer> mpid_list) throws Exception;

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年10月8日 14:07
	 * @description 添加查询应用的处理
	 */
	public GridBean searchAllMntrPxy(Integer userId, int pagenumber, int pagesize, String search_name) throws Exception;

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年10月21日 15:23
	 * @description 添加根据应用名称查询应用
	 */
	public MonitorProxy getMonitorProxyByName(String mpName) throws Exception;

	/**
	 * 添加高级查询部分内容
	 * 
	 * @throws Exception
	 */
	public GridBean advancedSearchMp(Integer userId, int pagenum, int pagesize, JSONObject json_object)
			throws Exception;
}
