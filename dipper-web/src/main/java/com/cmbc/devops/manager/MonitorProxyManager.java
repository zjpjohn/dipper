/**
 * 
 */
package com.cmbc.devops.manager;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.MonitorProxy;
import com.cmbc.devops.service.MonitorProxyService;

/**
 * date：2015年8月19日 上午10:56:15 project name：cmbc-devops-web
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationManager.java description：
 */
@Component
public class MonitorProxyManager {
	private static final Logger LOGGER = Logger.getLogger(MonitorProxyManager.class);
	@Autowired
	private MonitorProxyService monitorProxyService;

	/**
	 * @author yangqinglin
	 * @return JSONObject
	 * @version 1.0 2015年11月16日
	 */
	public Result updateMonitorProxy(JSONObject jO) {
		try {
			monitorProxyService.updateMonitorProxy(jO);
			return new Result(true, "更新监控代理(" + jO.getString("mpName") + ")成功！");
		} catch (Exception e) {
			LOGGER.error("更新监控代理失败！", e);
			return new Result(false, "更新监控代理(" + jO.getString("mpName") + ")失败，原因：" + e.getMessage());
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 14:05
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean mntrPxySearchAllList(Integer userId, int pagenum, int pagesize, String searchName) {
		try {
			return monitorProxyService.searchAllMntrPxy(userId, pagenum, pagesize, searchName);
		} catch (Exception e) {
			LOGGER.error("搜索应用列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月16日 14
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean mntrProxyAllList(Integer userId, int pagenum, int pagesize, MonitorProxy proxy) {
		try {
			return monitorProxyService.selectAllMntrProxy(userId, pagenum, pagesize, proxy);
		} catch (Exception e) {
			LOGGER.error("查询应用列表失败！", e);
		}
		return null;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月16日 14
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public JSONArray listAll(Integer userId, MonitorProxy proxy) {
		try {
			return monitorProxyService.listAll(userId, proxy);
		} catch (Exception e) {
			LOGGER.error("查询应用列表失败！", e);
		}
		return null;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 14:05
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean advancedSearchMp(Integer userId, int pagenum, int pagesize, JSONObject jO) {
		try {
			return monitorProxyService.advancedSearchMp(userId, pagenum, pagesize, jO);
		} catch (Exception e) {
			LOGGER.error("查询应用列表失败！", e);
		}
		return null;
	}

	public Result createMonitorProxy(JSONObject jO) {
		try {
			int result = monitorProxyService.createMonitorProxy(jO);
			if (result == 1) {
				LOGGER.info("添加监控代理(<b>" + jO.getString("mpName") + "</b>)成功。");
				return new Result(true, "添加监控代理(<b>" + jO.getString("mpName") + "</b>)成功。");
			} else {
				LOGGER.info("添加监控代理(<b>" + jO.getString("mpName") + "</b>)失败。");
				return new Result(false, "添加监控代理(<b>" + jO.getString("mpName") + "</b>)失败，数据库插入报错。");
			}
		} catch (Exception e) {
			LOGGER.error("添加监控代理(<b>" + jO.getString("mpName") + "</b>)失败。", e);
			return new Result(true, "添加监控代理(<b>" + jO.getString("mpName") + "</b>)失败。\n" + "原因：" + e.getMessage());
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月18日 16:20
	 * @description 添加删除
	 */
	public Result delete(List<Integer> mpIds, String mpNames) {
		try {
			int result = monitorProxyService.deleteMultiMP(mpIds);
			if (result > 0) {
				LOGGER.info("删除监控代理(" + mpNames + ")成功！");
				return new Result(true, "删除监控代理(" + mpNames + ")成功！");
			} else {
				LOGGER.info("删除监控代理(" + mpNames + ")失败！");
				return new Result(false, "删除监控代理(" + mpNames + ")失败！");
			}
		} catch (Exception except) {
			LOGGER.error("删除监控代理(" + mpNames + ")失败！", except);
			return new Result(false, "删除监控代理(" + mpNames + ")失败！");
		}
	}

	public Boolean duplicateMpName(JSONObject jO) {
		String mpName = jO.getString("mpName");
		try {
			return monitorProxyService.getMonitorProxyByName(mpName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get Monitor Proxy by mpName[" + mpName + "] failed!", e);
			return false;
		}
	}

	public Result duplicateMpIpPort(Integer userId, MonitorProxy proxy) {
		try {
			JSONArray mpArray = monitorProxyService.listAll(userId, proxy);
			if (mpArray.size() > 0) {
				JSONObject sinMp = (JSONObject) mpArray.get(0);
				return new Result(false, "监控代理（" + sinMp.getString("mpName") + "）已经使用了此IP地址和端口，请重新填写。");
			} else {
				return new Result(true, "此IP地址和端口组合可以使用。");
			}
		} catch (Exception e) {
			LOGGER.error("get Monitor Proxy by IP and Port(" + proxy.getMpIP() + ":" + proxy.getMpPort() + ") failed!",
					e);
			return new Result(false, "根据IP地址和端口查询失败！");
		}
	}
}
