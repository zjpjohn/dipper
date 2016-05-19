package com.cmbc.devops.controller.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.LoadBalance;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.ContainerManager;
import com.cmbc.devops.manager.LoadBalanceManager;
import com.cmbc.devops.query.QueryList;

/**
 * date：2015年9月11日 上午10:22:39 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：LoadBalanceAction.java description：
 */
@RequestMapping("/loadbalance")
@Controller
public class LoadBalanceAction {

	private static final Logger LOGGER = Logger.getLogger(LoadBalanceAction.class);

	@Autowired
	private QueryList queryList;
	@Autowired
	private LoadBalanceManager loadbalanceManager;
	@Autowired
	private ContainerManager containerManager;

	/**
	 * @author langzi
	 * @param request
	 * @param pagenumber
	 * @param pagesize
	 * @param model
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public GridBean listOnePageLoadBalance(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, LoadBalance loadBalance) {
		User user = (User) request.getSession().getAttribute("user");
		return queryList.listOnePageLoadBalance(user.getUserId(), pagenumber, pagesize, loadBalance);
	}

	@RequestMapping(value = "/listAll")
	@ResponseBody
	public String listAllBalance(HttpServletRequest request) {
		return queryList.listAllLoadBalance().toString();
	}

	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(@PathVariable Integer id) {
		ModelAndView mav = new ModelAndView("lb/detail");
		Map<String, Object> lbMap = loadbalanceManager.detail(id);
		mav.addObject("lb", lbMap);
		return mav;
	}

	/**
	 * @author langzi
	 * @param request
	 * @param model
	 * @version 1.0 2015年9月11日
	 */
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	public Result createLoadBalance(HttpServletRequest request, LoadBalance loadBalance) {
		if (loadBalance != null) {
			User user = (User) request.getSession().getAttribute("user");
			loadBalance.setLbCreator(user.getUserId());
			loadBalance.setLbStatus((byte) Status.LOADBALANCE.NORMAL.ordinal());
			return loadbalanceManager.createLoadBalance(loadBalance);
		} else {
			return new Result(false, "创建负载均衡失败：添加参数异常");
		}

	}

	/**
	 * @author langzi
	 * @param request
	 * @param appIds
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	@RequestMapping(value = "/reload", method = { RequestMethod.POST })
	@ResponseBody
	public Result reloadBalance(HttpServletRequest request, String[] lbIds, int fileFlag) {
		if (lbIds.length == 0) {
			return new Result(false, "重新加载负载均衡失败：传入参数异常");
		} else {
			return loadbalanceManager.updateLB(lbIds, fileFlag);
		}
	}

	/**
	 * @author langzi
	 * @param request
	 * @param conIds
	 * @return
	 * @version 1.0 2015年10月28日
	 */
	@RequestMapping(value = "/reloadApp", method = { RequestMethod.POST })
	@ResponseBody
	public Result reloadContainer(HttpServletRequest request, String[] conIds, int fileFlag, int actionFlag) {
		if (conIds.length == 0) {
			return new Result(false, "重新加载负载均衡失败：传入应用实例id为空");
		} else {
			if(fileFlag==1 && actionFlag == 1){
				loadbalanceManager.updateLBofContainer(conIds, fileFlag, actionFlag);
				return containerManager.stopContainer(conIds);
			}else if(fileFlag==1 && actionFlag == 2){
				loadbalanceManager.updateLBofContainer(conIds, fileFlag, actionFlag);
				containerManager.stopContainer(conIds);
				return containerManager.removeContainer(conIds);
			}
			else{
				return loadbalanceManager.updateLBofContainer(conIds, fileFlag, actionFlag);
			}
		}
	}

	/**
	 * @author langzi
	 * @param request
	 * @param appIds
	 * @param lbId
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	@RequestMapping(value = "/addApp", method = { RequestMethod.POST })
	@ResponseBody
	public Result addBalance(HttpServletRequest request, String[] appIds, String lbId, int fileFlag) {
		/* 获取用户的保存在Session中的用户信息 */
		User user = (User) request.getSession().getAttribute("user");
		if (appIds.length == 0 || ("").equals(lbId)) {
			return new Result(false, "应用加入负载失败：传入参数异常！");
		} else {
			return loadbalanceManager.addLB(user.getTenantId(), appIds, lbId, fileFlag);
		}
	}

	/**
	 * @author langzi
	 * @param request
	 * @param appIds
	 * @param lbId
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	@RequestMapping(value = "/removeApp", method = { RequestMethod.POST })
	@ResponseBody
	public Result removeApp(HttpServletRequest request, String[] appIds, String lbId, int fileFlag) {
		/* 获取用户的保存在Session中的用户信息 */
		User user = (User) request.getSession().getAttribute("user");
		if (appIds.length == 0 || ("").equals(lbId)) {
			return new Result(false, "应用加入负载失败：传入参数异常！");
		} else {
			return loadbalanceManager.removeApp(user.getTenantId(), appIds, lbId, fileFlag);
		}
	}

	/**
	 * @author langzi
	 * @param request
	 * @param model
	 * @version 1.0 2015年9月11日
	 */
	@RequestMapping(value = "/modify", method = { RequestMethod.POST })
	@ResponseBody
	public Result modifyLoadBalance(HttpServletRequest request, LoadBalance loadBalance) {
		if (loadBalance == null) {
			return new Result(false, "更新负载均衡失败：传入参数异常");
		} else {
			/** 当没有选择备负载均衡节点时，设置此值为null */
			if (loadBalance.getLbBackupHost() == 0) {
				loadBalance.setLbBackupHost(null);
			}
			return loadbalanceManager.modifyLoadBalance(loadBalance);
		}
	}

	/**
	 * @author langzi
	 * @param request
	 * @param model
	 * @version 1.0 2015年9月11日
	 */
	@RequestMapping(value = "/remove", method = { RequestMethod.POST })
	@ResponseBody
	public Result removeLoadBalance(HttpServletRequest request, @RequestParam String[] lbIds) {
		if (lbIds.length == 0) {
			return new Result(false, "删除负载均衡失败：传入参数异常");
		} else {
			return loadbalanceManager.removeLoadBalance(lbIds);
		}
	}

	/**
	 * @author langzi
	 * @param request
	 * @param type
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	@RequestMapping(value = "/listHost", method = { RequestMethod.GET })
	@ResponseBody
	public String listHostByType(HttpServletRequest request) {
		int type = Type.HOST.NGINX.ordinal();
		return JSONArray.toJSONString(queryList.listHostByType(type));
	}

	/**
	 * @author langzi
	 * @param request
	 * @param lbName
	 * @return
	 * @version 1.0 2015年10月21日
	 */
	@RequestMapping(value = "/checkName", method = { RequestMethod.POST })
	@ResponseBody
	public boolean checkName(HttpServletRequest request, String lbName) {
		return loadbalanceManager.checkName(lbName);
	}

	/**
	 * @author langzi
	 * @param request
	 * @param fileName
	 * @return
	 * @version 1.0 2015年10月28日
	 */
	@RequestMapping(value = "/readConfFile", method = { RequestMethod.POST })
	@ResponseBody
	public String readConfFile(HttpServletRequest request, String fileName) {
		return loadbalanceManager.readFileByLines(fileName);
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月3日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);

			return loadbalanceManager.advancedSearchLoadbalance(user.getUserId(), pagenumber, pagesize, json_object);
		} catch (Exception e) {
			LOGGER.error("查询主机列表失败！", e);
			return null;
		}
	}

	@RequestMapping(value = "/hostOfLBId", method = { RequestMethod.GET })
	@ResponseBody
	public String hostOfLBId(HttpServletRequest request, Integer id) {
		JSONObject obj = loadbalanceManager.getHostOfLBId(id);
		return obj.toString();
	}

}
