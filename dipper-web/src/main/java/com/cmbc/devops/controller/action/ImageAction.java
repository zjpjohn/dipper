package com.cmbc.devops.controller.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
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
import com.cmbc.devops.bean.PathObject;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.manager.ImageManager;
import com.cmbc.devops.model.ImageModel;
import com.cmbc.devops.model.RemoteImgModel;
import com.cmbc.devops.query.QueryList;

/**
 * @author luogan 2015年8月13日 上午10:53:41
 */

@Controller
@RequestMapping("image")
public class ImageAction {
	private static final Logger LOGGER = Logger.getLogger(ImageAction.class);

	@Resource
	private QueryList queryList;
	@Autowired
	private ImageManager imageManager;

	@RequestMapping("/detail/{id}.html")
	/** @date:2016年3月28日 添加租户维度 */
	public ModelAndView detail(HttpServletRequest request, @PathVariable Integer id) {
		User user = (User) request.getSession().getAttribute("user");
		ModelAndView mav = new ModelAndView("image/detail");
		Image image = imageManager.detail(user.getTenantId(), id);
		mav.addObject("image", image);
		return mav;
	}

	@RequestMapping(value = "/getImage", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public String getImage(HttpServletRequest request, int imageId) {
		User user = (User) request.getSession().getAttribute("user");
		Image image = imageManager.detail(user.getTenantId(), imageId);
		return JSONObject.toJSONString(image);
	}

	@RequestMapping(value = "/make", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public void make(HttpServletRequest request, ImageModel image) {
		User user = (User) request.getSession().getAttribute("user");
		imageManager.makeImage(user.getUserId(), user.getTenantId(), image);
	}

	/** 远程链接主机制作镜像 **/
	@RequestMapping(value = "/rmtMake", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public void rmtMake(HttpServletRequest request, RemoteImgModel rmt_image) {
		User user = (User) request.getSession().getAttribute("user");
		imageManager.rmtMakeImage(user.getUserId(), user.getTenantId(), rmt_image);
	}

	/** 远程制作镜像并发布 **/
	@RequestMapping(value = "/rmtMkPsh", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public void rmtMkPsh(HttpSession session, RemoteImgModel image) {
		User user = (User) session.getAttribute("user");
		imageManager.rmtMkPsh(user.getUserId(), user.getTenantId(), image);
	}

	/** 推送发布镜像 **/
	@RequestMapping(value = "/push", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public void push(HttpSession session, ImageModel image) {
		User user = (User) session.getAttribute("user");
		imageManager.pushImage(user.getUserId(), user.getTenantId(), image);
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, ImageModel imageModel) {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getUserId();
		int tenantId = user.getTenantId();
		return queryList.queryImageList(userId, tenantId, page, rows, imageModel);
	}

	@RequestMapping(value = "/remove/{id}", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public Result remove(HttpServletRequest request, @PathVariable Integer id) {
		User user = (User) request.getSession().getAttribute("user");
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		return imageManager.removeImage(user.getUserId(), user.getTenantId(), ids);
	}

	@RequestMapping(value = "/remove/batch", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public Result batchRemove(HttpServletRequest request, String ids) {
		User user = (User) request.getSession().getAttribute("user");
		List<Integer> idArray = new ArrayList<Integer>();
		for (String id : ids.split(",")) {
			idArray.add(Integer.valueOf(id));
		}
		return imageManager.removeImage(user.getUserId(), user.getTenantId(), idArray);
	}

	@RequestMapping(value = "/listByappId", method = { RequestMethod.GET })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean list(HttpServletRequest request, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "rows", required = true) int rows, String appId, String envId) {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getUserId();
		int tenantId = user.getTenantId();
		return queryList.queryImageList(userId, tenantId, page, rows, Integer.parseInt(appId), Integer.parseInt(envId));
	}

	@RequestMapping(value = "/mod", method = { RequestMethod.POST })
	@ResponseBody
	public Result modImage(HttpServletRequest request, Integer image, Integer app, String type, String envids) {
		User user = (User) request.getSession().getAttribute("user");
		return imageManager.modImage(user.getUserId(), user.getTenantId(), image, app, type, envids);
	}

	@RequestMapping(value = "/all", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public JSONArray imageAllList(HttpServletRequest request, ImageModel image) {
		User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = imageManager.imageAllList(user.getUserId(), user.getTenantId(), image);
		return ja;
	}

	@RequestMapping(value = "/pubimg", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	/** 获取全部的公共镜像信息 */
	public JSONArray publicImages(HttpServletRequest request, ImageModel image) {
		User user = (User) request.getSession().getAttribute("user");
		JSONArray ja = imageManager.publicImages(user.getUserId(), user.getTenantId(), image);
		return ja;
	}

	@RequestMapping(value = "/fastpush", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public void fastPush(HttpServletRequest request, Integer image) {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getUserId();
		int tenantId = user.getTenantId();
		this.imageManager.fastPush(userId, tenantId, image);
	}

	/** 前台向平台请求主机中某个文件夹内的文件列表 **/
	@RequestMapping(value = "/getFilelist", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject getFilelist(HttpServletRequest request,
			@RequestParam(value = "hostIP", required = true) String hostIP,
			@RequestParam(value = "hostUser", required = true) String hostUser,
			@RequestParam(value = "hostPasswd", required = true) String hostPasswd,
			@RequestParam(value = "filePath", required = true) String filePath) {
		// User user = (User) request.getSession().getAttribute("user");
		PathObject path_object = imageManager.getFilelist(hostIP, hostUser, hostPasswd, filePath);
		return (JSONObject) JSONObject.toJSON(path_object);
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
			@RequestParam(value = "rows", required = true) int pagesize, ImageModel image) {
		try {
			User user = (User) request.getSession().getAttribute("user");
			String params = request.getParameter("params").trim();
			String values = request.getParameter("values").trim();
			JSONObject json_object = new JSONObject();
			json_object.put("params", params);
			json_object.put("values", values);

			return imageManager.advancedSearchImage(user.getUserId(), user.getTenantId(), pagenumber, pagesize, image,
					json_object);

		} catch (Exception e) {
			LOGGER.error("查询主机列表失败！", e);
			return null;
		}
	}

	@RequestMapping(value = "/activeListByAppId", method = { RequestMethod.GET })
	@ResponseBody
	public JSONArray list(HttpServletRequest request, String appId) {
		User user = (User) request.getSession().getAttribute("user");
		int userId = user.getUserId();
		return queryList.activeImageListByAppId(userId, Integer.parseInt(appId));
	}

	@RequestMapping(value = "/exportImage", method = { RequestMethod.POST })
	@ResponseBody
	public Result exportImage(String hostIp, String hostUser, String hostPwd, String imageInfo, String imageName,
			String imageTag, String savePath, Integer imageId) {
		return imageManager.exportImage(hostIp, hostUser, hostPwd, imageInfo, imageName, imageTag, savePath, imageId);
	}

	/** 远程制作镜像并发布 **/
	@RequestMapping(value = "/importImage", method = { RequestMethod.POST })
	@ResponseBody
	/** @date:2016年3月28日 添加租户维度 */
	public void importImage(HttpSession session, RemoteImgModel image) {
		User user = (User) session.getAttribute("user");
		imageManager.importImage(user.getUserId(), user.getTenantId(), image);
	}

	/** 镜像持续集成部分的支持 */
	@RequestMapping(value = "/insert", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONObject insertImg(RemoteImgModel image) {
		return imageManager.insertImg(image);
	}

	/** 镜像持续集成部分的支持 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONObject updateImg(RemoteImgModel image) {
		return imageManager.updateImg(image);
	}

	/** 镜像持续集成部分的支持 */
	@RequestMapping(value = "/select", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JSONObject selectImg(Integer imageId) {
		return imageManager.selectImg(imageId);
	}

	/** 镜像持续集成部分的支持 */
	@RequestMapping(value = "/delete", method = { RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE })
	@ResponseBody
	public JSONObject deleteImg(Integer imageId) {
		return imageManager.deleteImg(imageId);
	}

}
