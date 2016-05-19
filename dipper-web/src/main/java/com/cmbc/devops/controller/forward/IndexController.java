package com.cmbc.devops.controller.forward;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.service.UserService;
import com.cmbc.devops.util.Base64;

@Controller
public class IndexController {

	private static Logger LOGGER = Logger.getLogger(IndexController.class);

	@Resource
	private UserService userService;

	@RequestMapping("/")
	public ModelAndView defaultIndex() {
		return new ModelAndView("index");
	}

	@RequestMapping("index.html")
	public ModelAndView index() {
		return new ModelAndView("index");
	}

	@RequestMapping("login.html")
	public ModelAndView login() {
		return new ModelAndView("account/login");
	}

	@RequestMapping("application/index.html")
	public ModelAndView app() {
		return new ModelAndView("application/index");
	}

	@RequestMapping("host/index.html")
	public ModelAndView host(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("host/index");
		String request_type = request.getParameter("hostType");
		mav.addObject("hostType", request_type);
		return mav;
	}

	@RequestMapping("cluster/index.html")
	public ModelAndView cluster() {
		return new ModelAndView("cluster/index");
	}

	@RequestMapping("image/index.html")
	public ModelAndView image() {
		return new ModelAndView("image/index");
	}

	/** 监控代理主页 */
	@RequestMapping("mntrproxy/index.html")
	public ModelAndView mntrproxy() {
		return new ModelAndView("mntrproxy/index");
	}

	/** 资源管理默认主页 */
	@RequestMapping("resource/index.html")
	public ModelAndView resource() {
		return new ModelAndView("resource/index");
	}

	/** 环境管理默认主页 */
	@RequestMapping("env/index.html")
	public ModelAndView env() {
		return new ModelAndView("env/index");
	}

	/** 查询当前网站所有页面是否需要进行重新载入 */
	@RequestMapping(value = "check/reload", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONObject checkReload(HttpServletRequest request) {
		JSONObject ret_obj = new JSONObject();
		String reload_devops = (String) request.getSession().getAttribute("reload_devops");
		if (StringUtils.hasText(reload_devops)) {
			if (!Boolean.parseBoolean(reload_devops)) {
				request.getSession().setAttribute("reload_devops", "false");
				ret_obj.put("reload_devops", "false");
			} else {
				/** 返回给浏览器，需要重新加载页面，将session设置为false */
				ret_obj.put("reload_devops", "true");
				request.getSession().setAttribute("reload_devops", "false");
			}
		} else {
			/** 返回给浏览器，需要重新加载页面，将session设置为false */
			ret_obj.put("reload_devops", "true");
			request.getSession().setAttribute("reload_devops", "false");
		}
		return ret_obj;
	}

	@RequestMapping("registry/index.html")
	public ModelAndView registry() {
		return new ModelAndView("registry/index");
	}

	/** 添加多租户管理部分 */
	@RequestMapping("tenant/index.html")
	public ModelAndView tenant() {
		return new ModelAndView("tenant/index");
	}

	/** 添加软件管理部分 */
	@RequestMapping("software/index.html")
	public ModelAndView software() {
		return new ModelAndView("software/index");
	}

	@RequestMapping("registry/images.html")
	public ModelAndView registryImage(@RequestParam(value = "registry_id", required = true) Integer id) {
		ModelAndView mav = new ModelAndView("registry/images");
		id = null == id ? 0 : id;
		mav.addObject("reg_id", id);
		return mav;
	}

	@RequestMapping("container/index.html")
	public ModelAndView container() {
		return new ModelAndView("container/index");
	}

	@RequestMapping("apprelease/index.html")
	public ModelAndView appRelease() {
		return new ModelAndView("apprelease/index");
	}

	@RequestMapping("monitor/index.html")
	public ModelAndView monitor() {
		return new ModelAndView("monitor/app_view");
	}

	@RequestMapping("monitor/cluster_view.html")
	public ModelAndView monitor_cluster() {
		return new ModelAndView("monitor/cluster_view");
	}

	@RequestMapping("warning/index.html")
	public ModelAndView warning() {
		return new ModelAndView("warning/index");
	}

	@RequestMapping("service/index.html")
	public ModelAndView service() {
		return new ModelAndView("service/index");
	}

	@RequestMapping("exception/index.html")
	public ModelAndView excep() {
		return new ModelAndView("exception/index");
	}

	@RequestMapping("statistic/index.html")
	public ModelAndView statistic() {
		return new ModelAndView("statistic/index");
	}

	@RequestMapping("log/index.html")
	public ModelAndView log() {
		return new ModelAndView("log/index");
	}

	@RequestMapping("user/index.html")
	public ModelAndView user() {
		return new ModelAndView("user/index");
	}

	@RequestMapping("auth/index.html")
	public ModelAndView auth() {
		return new ModelAndView("auth/index");
	}

	@RequestMapping("role/index.html")
	public ModelAndView role() {
		return new ModelAndView("role/index");
	}

	@RequestMapping("permission/index.html")
	public ModelAndView permission() {
		return new ModelAndView("permission/index");
	}

	@RequestMapping("param/index.html")
	public ModelAndView param() {
		return new ModelAndView("param/index");
	}

	/**
	 * 用户修改密码
	 * 
	 * @return
	 */
	@RequestMapping("user/updateUser/{id}")
	public ModelAndView updateUser(HttpServletRequest request, @PathVariable String id) {
		String path = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
				+ "/";
		User user = new User();
		if ("".equals(id) || id == null) {
			return null;
		} else {
			if (Base64.isNum(Base64.getFromBase64(id))) {
				user.setUserId(Integer.valueOf(Base64.getFromBase64(id)));

				try {
					user = userService.getUser(user);
				} catch (Exception e) {
					LOGGER.error("get user by userid[" + Base64.getFromBase64(id) + "] falied!", e);
					return null;
				}
				if (user == null) {
					return null;
				}
				request.setAttribute("userName", user.getUserName());
				request.setAttribute("UD", id);
				request.setAttribute("basePath", basePath);
				return new ModelAndView("user/updateUser");
			} else {
				return null;
			}

		}

	}

	@RequestMapping("forgetPass.html")
	public ModelAndView forgetPass() {
		return new ModelAndView("user/forgetPass");
	}

	@RequestMapping("lb/index.html")
	public ModelAndView lb() {
		return new ModelAndView("lb/index");
	}

	/**
	 * page not found
	 * 
	 * @author langzi
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/404", method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView pageNotFound(HttpServletRequest request) {
		return new ModelAndView("account/404");
	}

	/**
	 * page not auth
	 * 
	 * @author langzi
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/403", method = { RequestMethod.GET })
	@ResponseBody
	public ModelAndView pageNotAuth(HttpServletRequest request) {
		return new ModelAndView("account/403");
	}
}
