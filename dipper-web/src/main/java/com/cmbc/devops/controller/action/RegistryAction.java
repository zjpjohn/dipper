/**
 * 
 */
package com.cmbc.devops.controller.action;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
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
import com.cmbc.devops.entity.Registry;
import com.cmbc.devops.entity.RegistrySlaveImage;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.DashboardManager;
import com.cmbc.devops.manager.RegistryManager;
import com.cmbc.devops.model.RegIdImageTypeModel;
import com.cmbc.devops.model.RegistryModel;
import com.cmbc.devops.model.RegistryWithIPModel;

/**
 * date：2015年8月24日 上午1:14:51 project name：cmbc-devops-web
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：RegistryAction.java description：
 */
@Controller
@RequestMapping("registry")
public class RegistryAction {
	private static final Logger LOGGER = Logger.getLogger(RegistryAction.class);

	@Resource
	private RegistryManager registryManager;
	@Resource
	private DashboardManager dashboardManager;

	/**
	 * @author youngtsinglin
	 * @time 2015年10月8日 10:24
	 * @description 添加查询仓库详细信息
	 */
	/** @date:2016年3月29日 添加租户维度 */
	@RequestMapping("/detail/{id}.html")
	public ModelAndView detail(HttpServletRequest request, @PathVariable Integer id) {
		User user = (User) request.getSession().getAttribute("user");
		int tenantId = user.getTenantId();

		ModelAndView mav = new ModelAndView("registry/detail");
		Registry registry = registryManager.detail(tenantId, id);
		mav.addObject("registry", registry);
		return mav;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年10月8日 10:24
	 * @description 添加查询仓库详细信息
	 */
	/** @date:2016年3月29日 添加租户维度 */
	@RequestMapping("/images/{id}.html")
	public ModelAndView images(HttpServletRequest request, @PathVariable Integer id) {
		ModelAndView mav = new ModelAndView("registry/images");
		try {
			User user = (User) request.getSession().getAttribute("user");
			int tenantId = user.getTenantId();
			/* 显示正常状态的镜像内容 */
			List<RegistrySlaveImage> regi_image_list = registryManager.getImagesViaRegistryId(tenantId, id);
			mav.addObject("regi_image_list", regi_image_list);
		} catch (Exception e) {
			LOGGER.error("查询所包含镜像的列表失败！", e);
		}
		return mav;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:24
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	/** @date:2016年3月29日 添加租户维度 */
	@RequestMapping("/list")
	@ResponseBody
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, RegistryModel registryModel) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			int tenantId = user.getTenantId();
			registryModel.setTenantId(tenantId);
			return registryManager.registryList(user.getUserId(), pagenumber, pagesize, registryModel);
		} catch (Exception e) {
			LOGGER.error("查询仓库列表失败", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:24
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	/** @date:2016年3月29日 添加租户维度 */
	@RequestMapping("/listWithIP")
	@ResponseBody
	public GridBean listWithIP(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, RegistryWithIPModel registryWithIPModel) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			int tenantId = user.getTenantId();
			registryWithIPModel.setTenantId(tenantId);
			return registryManager.registryListWithIP(user.getUserId(), pagenumber, pagesize, registryWithIPModel);
		} catch (Exception e) {
			LOGGER.error("查询带有物理机IP地址的仓库列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:24
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	@RequestMapping("/listSearch")
	@ResponseBody
	public GridBean listSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, RegistryWithIPModel registryWithIPModel) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			int tenantId = user.getTenantId();
			/* 获取查询仓库的关键字信息 */
			String search_name = request.getParameter("search_name").trim();
			registryWithIPModel.setSearch(search_name);
			registryWithIPModel.setTenantId(tenantId);
			return registryManager.registryListSearch(user.getUserId(), pagenumber, pagesize, registryWithIPModel);
		} catch (Exception e) {
			LOGGER.error("查询带有物理机IP地址的仓库列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 10:35
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	/** @date:2016年3月29日 添加租户维度 */
	@RequestMapping("/advancedSearch")
	@ResponseBody
	public GridBean advancedSearch(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, RegistryModel registryModel) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			int tenantId = user.getTenantId();
			registryModel.setTenantId(tenantId);
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);
			return registryManager.advancedSearchRegi(user.getUserId(), pagenumber, pagesize, registryModel,
					json_object);

		} catch (Exception e) {
			LOGGER.error("查询应用列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月10日 11:32
	 * @description 将原来返回字符串的方法修改为GridBean的方式,列出包含在特定仓库下面所有的镜像信息
	 */
	/** @date:2016年3月29日 添加租户维度 */
	@RequestMapping("/listslaveimages")
	@ResponseBody
	public GridBean listSlaveImages(HttpServletRequest request,
			@RequestParam(value = "page", required = true) int pagenumber,
			@RequestParam(value = "rows", required = true) int pagesize, RegIdImageTypeModel regIdImageTypeModel) {
		User user = (User) request.getSession().getAttribute("user");
		int tenantId = user.getTenantId();
		regIdImageTypeModel.setTenantId(tenantId);
		int registryid = Integer.parseInt(request.getParameter("registry_id"));
		/* 显示正常状态的镜像内容 */
		byte imagestatus = (byte) Status.IMAGE.NORMAL.ordinal();
		return registryManager.registrySlaveImages(user.getUserId(), pagenumber, pagesize, regIdImageTypeModel,
				registryid, imagestatus);
	}

	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月29日 添加租户维度 */
	public Result create(HttpServletRequest request, RegistryModel registryModel) {
		User user = (User) request.getSession().getAttribute("user");
		int tenantId = user.getTenantId();
		if (registryModel != null) {
			JSONObject params = (JSONObject) JSONObject.toJSON(registryModel);
			/* 获取仓库的详细信息 */
			String registry_name = request.getParameter("registry_name");
			String registry_port = request.getParameter("registry_port");
			String registry_hostid = request.getParameter("registry_hostid");
			String registry_desc = request.getParameter("registry_desc");
			params.put("registryName", registry_name);
			params.put("registryPort", registry_port);
			params.put("hostId", registry_hostid);
			params.put("registryDesc", registry_desc);
			params.put("userId", user.getUserId());
			params.put("tenantId", tenantId);

			Result return_result = registryManager.createRegistry(params);
			LOGGER.info(user.getUserName() + ":" + return_result.getMessage());
			return return_result;
		} else {
			Result return_result = new Result(false, "参数输入异常!");
			LOGGER.info(user.getUserName() + ":" + return_result.getMessage());
			return return_result;
		}
	}

	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public Result update(HttpServletRequest request, RegistryModel registryModel) {
		User user = (User) request.getSession().getAttribute("user");
		if (registryModel != null) {
			JSONObject params = (JSONObject) JSONObject.toJSON(registryModel);
			/* 获取仓库的详细信息 */
			String registry_id = request.getParameter("registry_id");
			String registry_name = request.getParameter("registry_name");
			String registry_port = request.getParameter("registry_port");
			// String registry_status = request.getParameter("registry_status");
			String registry_hostid = request.getParameter("registry_hostid");
			String registry_desc = request.getParameter("registry_desc");

			params.put("userId", user.getUserId().toString());
			params.put("registryId", registry_id);
			params.put("registryName", registry_name);
			params.put("registryPort", registry_port);
			params.put("hostId", registry_hostid);
			params.put("registryDesc", registry_desc);

			Result return_result = registryManager.updateRegistry(params);
			LOGGER.info(user.getUserName() + ":" + return_result.getMessage());
			return return_result;
		} else {
			Result return_result = new Result(false, "参数输入异常!");
			LOGGER.info(user.getUserName() + ":" + return_result.getMessage());
			return return_result;
		}
	}

	@RequestMapping(value = "/registryHosts", method = { RequestMethod.GET })
	@ResponseBody
	public String clusterMasterList(HttpServletRequest request, RegistryModel registryModel) {
		JSONArray ja = registryManager.getRegistryMster();
		return ja.toString();
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.POST })
	@ResponseBody
	public Result delete(HttpServletRequest request,
			@RequestParam(value = "registryIds", required = true) String registryIds) {
		String registry_names = request.getParameter("registryName");
		User user = (User) request.getSession().getAttribute("user");
		JSONObject json_object = new JSONObject();
		json_object.put("array", registryIds);
		json_object.put("name_array", registry_names);
		json_object.put("userId", user.getUserId());
		// String registry_name = request.getParameter("registryName");
		Result return_result = registryManager.deleteBatchRegistry(json_object);
		LOGGER.info(user.getUserName() + ":" + return_result.getMessage());
		return return_result;
	}

	@RequestMapping(value = "/reachRegiHost", method = { RequestMethod.POST })
	@ResponseBody
	public Result reachRegiHost(HttpServletRequest request) {
		JSONObject params = new JSONObject();
		/* 获取仓库主机的IP地址 */
		String regihost_ip = request.getParameter("registry_ipaddr").trim();
		/* 获取仓库主机的端口 */
		String regihost_port = request.getParameter("registry_port").trim();
		/* 获取仓库主机的ID信息 */
		String regihost_id = request.getParameter("reg_host_id").trim();

		User user = (User) request.getSession().getAttribute("user");
		params.put("userId", user.getUserId());
		params.put("registryIpaddr", regihost_ip);
		params.put("registryPort", regihost_port);
		params.put("registryHost", regihost_id);

		/* 检查节点IP地址和端口的可达性 */
		Result result = registryManager.reachRegiHost(params);

		return result;
	}

	@RequestMapping(value = "/duplicateName", method = { RequestMethod.POST })
	@ResponseBody
	public Result duplicateName(HttpServletRequest request) {
		JSONObject params = new JSONObject();
		/* 获取仓库主机的IP地址 */
		User user = (User) request.getSession().getAttribute("user");
		params.put("userId", user.getUserId());
		String registry_name = request.getParameter("registry_name").trim();
		params.put("registry_name", registry_name);

		/* 检查节点IP地址和端口的可达性 */
		Result result = registryManager.duplicateName(params);
		return result;

	}

	/* 增加数据库与实际注册服务器（仓库）之间的镜像同步操作 */
	@RequestMapping(value = "/syncRegiImgInfo", method = { RequestMethod.POST })
	@ResponseBody
	public Result syncRegiImgInfo(HttpServletRequest request) {
		JSONObject params = new JSONObject();
		/* 获取仓库主机的IP地址 */
		String regihost_ip = request.getParameter("registry_ipaddr");
		/* 获取仓库主机的端口 */
		String regihost_port = request.getParameter("registry_port");
		/* 获取仓库的ID值，同步时插入仓库镜像对应表中 */
		String registry_id = request.getParameter("registry_id");
		/* 获取仓库的名称信息 */
		String registry_name = request.getParameter("registry_name");
		/* 获取仓库主机的ID信息 */
		String registry_host = request.getParameter("registry_host");

		User user = (User) request.getSession().getAttribute("user");
		params.put("userId", user.getUserId());
		params.put("registryIpaddr", regihost_ip);
		params.put("registryPort", regihost_port);
		params.put("registryId", registry_id);
		params.put("registryName", registry_name);
		params.put("registryHostId", registry_host);

		/* 检查节点IP地址和端口的可达性 */
		Result result = registryManager.sycDBandRegiImgInfo(params);
		// Result result = registryManager.syncDBwithRegiInfo(params);
		return result;
	}

	/**
	 * @author yangqinglin
	 * @time 2015年9月6日 9:38
	 * @description 增加处理查询仓库中包含的镜像列表内容
	 * 
	 */
	@RequestMapping(value = "/select", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject getRegistry(HttpServletRequest request, @RequestParam Integer registryId) {
		Registry record = new Registry();
		record.setRegistryId(registryId);
		JSONObject jo = registryManager.getRegistry(record);
		return jo;

	}

	@RequestMapping(value = "/getRegistryMster", method = { RequestMethod.POST })
	@ResponseBody
	public String getRegistryMster(HttpServletRequest request) {
		JSONArray ja = registryManager.getRegistryMster();
		return ja.toString();

	}

	/**
	 * 同时同步多个仓库中包含的镜像
	 * 
	 * @author youngtsinglin
	 * @param request
	 */
	@RequestMapping(value = "/sync/batch", method = { RequestMethod.POST })
	@ResponseBody
	public Result syncbatch(HttpServletRequest request) {
		String registry_ids = request.getParameter("regi_ids");
		String registry_names = request.getParameter("regi_names");
		String registry_ipaddrs = request.getParameter("regi_ipaddrs");
		String registry_ports = request.getParameter("regi_ports");

		if (null == registry_ids || registry_ids.isEmpty()) {
			Result ret_result = new Result(false, "传入参数为空！");
			return ret_result;
		} else {
			JSONObject json_object = new JSONObject();
			json_object.put("registry_ids", registry_ids);
			json_object.put("registry_names", registry_names);
			json_object.put("registry_ipaddrs", registry_ipaddrs);
			json_object.put("registry_ports", registry_ports);
			User user = (User) request.getSession().getAttribute("user");

			Result ret_result = registryManager.syncBatchRegiSlaveImg(json_object);
			LOGGER.info(user.getUserName() + "" + ret_result.getMessage());
			return ret_result;
		}
	}

	@RequestMapping(value = "/remove/batch", method = { RequestMethod.POST })
	@ResponseBody
	public Result deletebatch(HttpServletRequest request) {
		String registry_ids = request.getParameter("regi_ids");
		String registry_names = request.getParameter("regi_names");
		User user = (User) request.getSession().getAttribute("user");
		JSONObject json_object = new JSONObject();
		json_object.put("array", registry_ids);
		json_object.put("name_array", registry_names);
		json_object.put("userId", user.getUserId());

		// int result = registryService.deleteRegistry(json_object);
		Result return_result = registryManager.deleteBatchRegistry(json_object);
		LOGGER.info(user.getUserName() + ":" + return_result.getMessage());
		return return_result;
	}

	@RequestMapping(value = "/checkRegiName", method = { RequestMethod.POST })
	@ResponseBody
	public Boolean checkName(HttpServletRequest request, String regiName) {
		return registryManager.checkRegiName(regiName);
	}

	@RequestMapping(value = "/dashboard", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject getRegistry(HttpServletRequest request) {
		JSONObject json_object = dashboardManager.getDashboardData();
		return json_object;
	}

	@RequestMapping(value = "/queryRegistryById", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject queryRegistryById(HttpServletRequest request, Integer registryId) {
		Registry registry = new Registry();
		registry.setRegistryId(registryId);
		JSONObject json_object = registryManager.getRegistry(registry);
		if (json_object != null) {
			json_object.put("success", true);
			return json_object;
		} else {
			return null;
		}
	}
}
