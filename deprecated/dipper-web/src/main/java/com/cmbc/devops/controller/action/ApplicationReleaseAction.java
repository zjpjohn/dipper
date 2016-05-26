package com.cmbc.devops.controller.action;

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

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.ApplicationReleaseManager;
import com.cmbc.devops.model.ApplicationModel;
import com.cmbc.devops.model.ApplicationReleaseModel;
import com.cmbc.devops.query.QueryList;

/**
 * date：2015年12月10日 上午9:50:06 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationRelease.java description：
 */
@RequestMapping("/appRelease")
@Controller
public class ApplicationReleaseAction {
	private static final Logger LOGGER = Logger.getLogger(ApplicationReleaseAction.class);

	@Autowired
	private QueryList query;
	@Autowired
	private ApplicationReleaseManager appReleaseManager;

	/**
	 * @author langzi
	 * @param request
	 * @param pagenumber
	 * @param pagesize
	 * @param model
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public GridBean listContainerByPage(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, ApplicationModel model) {
		User user = (User) request.getSession().getAttribute("user");
		return query.listOnePageAppReleased(user.getUserId(), pagenumber, pagesize, model);
	}

	/**
	 * @author yangqinglin
	 * @param request
	 * @param pagenumber
	 * @param pagesize
	 * @param model
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	@RequestMapping(value = "/listSearch")
	@ResponseBody
	public GridBean listSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, ApplicationModel model) {
		User user = (User) request.getSession().getAttribute("user");
		return query.listOnePageAppReleased(user.getUserId(), pagenumber, pagesize, model);
	}

	/**
	 * @author langzi
	 * @param id
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	@RequestMapping("/detail/{id}/{imageId}/{balanceId}.html")
	public ModelAndView detail(@PathVariable Integer id, @PathVariable Integer imageId,@PathVariable Integer balanceId) {
		ModelAndView mav = new ModelAndView("apprelease/detail");
		mav.addObject("appId", id);
		mav.addObject("imageId",imageId);
		mav.addObject("balanceId", balanceId);
		return mav;
	}

	/**
	 * @author yangqinglin
	 * @param id
	 * @return 根据容器的开关机状态显示全部（0：全部，1：开机，2：关机）的容器
	 * @version 1.0 2016年1月26日
	 */
	@RequestMapping("/power/{power_status}.html")
	public ModelAndView power(@PathVariable Integer power_status) {
		ModelAndView mav = new ModelAndView("apprelease/power");
		mav.addObject("powerStatus", power_status);
		return mav;
	}

	/**
	 * @author langzi
	 * @param request
	 * @param model
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	@RequestMapping(value = "/release", method = { RequestMethod.POST })
	@ResponseBody
	public Result appRelease(HttpServletRequest request, ApplicationReleaseModel model) {
		User user = (User) request.getSession().getAttribute("user");
		model.setUserId(user.getUserId());
		model.setTenantId(user.getTenantId());
		return appReleaseManager.appRelease(model);

	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, ApplicationModel applicationModel) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();

			/* 获取用户填写的各项查询条件 */
			String[] paramArray = params.split(",");
			String[] valueArray = values.split(",");

			/* 遍历填充各项查询条件 */
			for (int i = 0; i < paramArray.length; i++) {
				switch (paramArray[i].trim()) {
					case "1": 
						applicationModel.setAppName(valueArray[i].trim());
						break;
					case "2": 
						applicationModel.setAppVersion(valueArray[i].trim());
						break;
					case "3": 
						applicationModel.setAppUrl(valueArray[i].trim());
						break;
					default:
						break;
				}
			}
			
			return query.listOnePageAppReleased(user.getUserId(), pagenumber, pagesize, applicationModel);
		} catch (Exception e) {
			LOGGER.error("查询应用列表失败！", e);
			return null;
		}
	}

}
