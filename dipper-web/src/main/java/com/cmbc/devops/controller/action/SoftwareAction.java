package com.cmbc.devops.controller.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Software;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.SoftwareManager;

/**
 * date：2016年1月5日 下午3:04:31 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：AppAction.java description：
 */
@RequestMapping("software")
@Controller
public class SoftwareAction {

	@Autowired
	private SoftwareManager softwareManager;

	private final static Logger LOGGER = Logger.getLogger(SoftwareAction.class);

	@RequestMapping("/list")
	@ResponseBody
	/** Tenant Finished */
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize) {
		/* #采用多租户方式，透传租户资源的ID */
		User user = (User) request.getSession().getAttribute("user");
		/* 直接传递用户的租户资源ID */
		return softwareManager.listOnePageSofts(pagenumber, pagesize, user.getTenantId());
	}

	@RequestMapping(value = "/typeList", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONArray typeList(HttpServletRequest request, int softType) {
		/** 界面中基础软件和中间件程序分别对应为1和2，而在平台上分别对应0和1 */
		switch (softType) {
		/** 基础软件 */
		case (1):
			softType = 0;
			break;
		/** 中间件程序 */
		case (2):
			softType = 1;
			break;
		}
		return softwareManager.typeList(softType);
	}

	@RequestMapping(value = "/install", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public void install(HttpServletRequest request, String hostIds, Integer softId) {
		if (StringUtils.hasText(hostIds)) {
			String[] hostArray = hostIds.split(",");
			User user = (User) request.getSession().getAttribute("user");
			softwareManager.installSoftware(hostArray, softId, user.getUserId());
		}
	}

	@RequestMapping(value = "/update", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result updateSoft(HttpServletRequest request, Software software) {
		User user = (User) request.getSession().getAttribute("user");
		return softwareManager.modifySoft(user.getUserId(), software);
	}

	@RequestMapping(value = "/insert", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result insertSoft(HttpServletRequest request, Software software) {
		User user = (User) request.getSession().getAttribute("user");
		software.setSwStatus((byte) Status.SOFTWARE.ACTIVATE.ordinal());
		software.setSwCreatetime(new Date());
		software.setSwCreator(user.getUserId());
		return softwareManager.addSoft(user.getUserId(), software);
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Result insertSoft(HttpServletRequest request, String softIds) {
		User user = (User) request.getSession().getAttribute("user");
		if (StringUtils.hasText(softIds)) {
			return softwareManager.deleteSoft(user.getUserId(), softIds);
		} else {
			return new Result(false, "请求删除数据（" + softIds + "）错误.");
		}
	}

	/**
	 * @author yangqinglin
	 * @time 2016年4月14日 10:24
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/listSearch")
	@ResponseBody
	public GridBean listSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, Software software) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			int tenantId = user.getTenantId();
			/* 获取查询仓库的关键字信息 */
			String searchName = request.getParameter("searchName").trim();
			software.setTenantId(tenantId);
			software.setSwName(searchName);
			return softwareManager.ListSearch(user.getUserId(), pagenumber, pagesize, software);
		} catch (Exception e) {
			LOGGER.error("查询带有物理机IP地址的仓库列表失败！", e);
			return null;
		}
	}

	/**
	 * @author yangqinglin
	 * @time 2016年4月14日 10:24
	 * @description 高级搜索的方式
	 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public JSONArray advancedSearch(HttpServletRequest request, Software software) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			int tenantId = user.getTenantId();
			/* 获取查询仓库的关键字信息 */
			software.setTenantId(tenantId);
			return softwareManager.advancedSearch(user.getUserId(), software);
		} catch (Exception e) {
			LOGGER.error("查询带有物理机IP地址的仓库列表失败！", e);
			return null;
		}
	}
	
	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(@PathVariable Integer id) {
		ModelAndView mav = new ModelAndView("software/detail");
		Software soft = softwareManager.detail(id);
		List<String> hosts=softwareManager.getHostsBySoftId(id);
		mav.addObject("soft", soft);
		mav.addObject("clusterList", hosts);
		return mav;
	}
	
	@RequestMapping("/checkSoftName")
	@ResponseBody
	public Boolean checkSoftName(HttpServletRequest request, String softName) {
		User user = (User) request.getSession().getAttribute("user");
		int tenantId = user.getTenantId();
		return softwareManager.checkSoftName(softName);
	}
}
