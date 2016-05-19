/**
 * 
 */
package com.cmbc.devops.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.entity.DkResource;
import com.cmbc.devops.entity.DkResourceWithUser;
import com.cmbc.devops.message.MessagePush;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.ResourceService;

/**
 * date：2015年8月19日 上午10:56:15 project name：cmbc-devops-web
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ApplicationManager.java description：
 */
@Component
public class ResourceManager {
	private static final Logger LOGGER = Logger.getLogger(ResourceManager.class);
	@Resource
	private MessagePush messagePush;
	@Resource
	private HostService hostService;
	@Resource
	private ResourceService resourceService;

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 14:05
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean resourceSearchAllList(Integer userId, int pagenum, int pagesize, String search_name) {
		try {
			return resourceService.searchAllResource(userId, pagenum, pagesize, search_name);
		} catch (Exception e) {
			LOGGER.error("搜索资源列表失败！", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年9月6日 14:05
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean advancedSearchRes(Integer userId, int pagenum, int pagesize, JSONObject json_object) {
		try {
			return resourceService.advancedSearchRes(userId, pagenum, pagesize, json_object);
		} catch (Exception e) {
			LOGGER.error("查询资源列表失败！", e);
		}
		return null;
	}

	public List<Integer> removeDuplicate(List<Integer> srcList) {
		return new ArrayList<Integer>(new HashSet<Integer>(srcList));
	}

	public DkResourceWithUser detail(int resId) {
		try {
			return resourceService.detail(resId);
		} catch (Exception e1) {
			LOGGER.error("get Resource by Id[" + resId + "] falied!", e1);
		}
		return null;
	}

	public Boolean checkAppName(String resName) {
		try {
			return resourceService.getResourceByName(resName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("get resource by resname[" + resName + "] falied!", e);
			return false;
		}
	}

	/**
	 * @author youngtsinglin
	 * @description：查询全部资源列表
	 */
	public GridBean resourceAllList(Integer userId, int pagenumber, int pagesize) {
		try {
			return resourceService.resourceAllList(userId, pagenumber, pagesize);
		} catch (Exception e) {
			LOGGER.error("查询资源全部列表失败！", e);
		}
		return null;
	}

	/**
	 * @author youngtsinglin
	 * @description：创建定制资源
	 */
	public Result createResource(DkResource dk_resource) {
		try {
			int ret = resourceService.createResource(dk_resource);
			if (ret > 0) {
				return new Result(true, "创建定制资源(<b>" + dk_resource.getResName() + "</b>)成功。");
			} else {
				return new Result(false, "创建定制资源(<b>" + dk_resource.getResName() + "</b>)失败。");
			}
		} catch (Exception e) {
			LOGGER.error("创建定制资源失败！", e);
			return new Result(false, "创建定制资源(<b>" + dk_resource.getResName() + "</b>)失败。");
		}
	}

	/**
	 * @author youngtsinglin
	 * @description：修改定制资源
	 */
	public Result updateResource(DkResource dk_resource) {
		try {
			int ret = resourceService.updateResource(dk_resource);
			if (ret > 0) {
				return new Result(true, "修改定制资源(<b>" + dk_resource.getResName() + "</b>)成功。");
			} else {
				return new Result(false, "修改定制资源(<b>" + dk_resource.getResName() + "</b>)失败。");
			}
		} catch (Exception e) {
			LOGGER.error("修改定制资源失败！", e);
			return new Result(false, "修改定制资源(<b>" + dk_resource.getResName() + "</b>)失败。");
		}
	}

	/**
	 * @author youngtsinglin
	 * @description：删除定制资源
	 */
	public Result deleteResource(DkResource dk_resource) {
		try {
			int ret = resourceService.deleteResource(dk_resource);
			if (ret > 0) {
				LOGGER.info("删定制资源(" + dk_resource.getResName() + ")成功！");
				return new Result(true, "删除定制资源(<b>" + dk_resource.getResName() + "</b>)成功。");
			} else {
				LOGGER.info("删定制资源(" + dk_resource.getResName() + ")失败！");
				return new Result(false, "删除定制资源(<b>" + dk_resource.getResName() + "</b>)失败。");
			}
		} catch (Exception e) {
			LOGGER.error("删除定制资源失败！", e);
			return new Result(false, "删除定制资源(<b>" + dk_resource.getResName() + "</b>)失败。");
		}
	}

	public JSONArray allResJsonArray(Integer userId, DkResource dk_resource) {
		try {
			return resourceService.getAllResource();
		} catch (Exception e) {
			LOGGER.error("删除全部资源的JSONArray列表失败！", e);
			return null;
		}

	}

	public Result deleteResources(JSONObject json_object) {
		try {
			/** @bug263_begin：[资源管理]删除定制资源加入限制,如资源被某应用配置使用不允许删除并给出提示信息 **/
			/*
			*//** 首先判断在应用配置和资源映射表中，资源是否被使用 **//*
												 * String resource_ids =
												 * json_object.getString(
												 * "res_ids");
												 * 
												 * String[] resource_array =
												 * resource_ids.split(",");
												 * ArrayList<Integer> resid_list
												 * = new ArrayList<Integer>();
												 * for (int count = 0, size =
												 * resource_array.length; count
												 * < size; count++) {
												 * resid_list.add(Integer.
												 * parseInt(resource_array[count
												 * ])); }
												 */

			/** 如果不存在配置资源映射关系则直接删除 **/
			int result = resourceService.deleteResources(json_object);
			if (result > 0) {
				LOGGER.info("删除定制资源(ID:" + json_object.getString("res_ids") + ",NAMES:"
						+ json_object.getString("res_names") + ")成功！");
				return new Result(true,
						"删除定制资源(<font color=\"blue\">" + json_object.getString("res_names") + "</font>)成功！");

			} else {
				LOGGER.warn("删除定制资源(" + json_object.getString("res_names") + ")失败！");
				return new Result(false, "删除定制资源(ID:<font color=\"blue\">" + json_object.getString("res_ids")
						+ "</font>,NAMES:<font color=\"blue\">" + json_object.getString("res_names") + "</font>)失败！");
			}
		} catch (Exception e) {
			LOGGER.error("删除定制资源失败！", e);
			return new Result(false, "删除定制资源(ID:" + json_object.getString("res_ids") + ",NAMES:"
					+ json_object.getString("res_names") + ")失败！");
		}
	}

	public JSONArray publicResources() {
		try {
			return resourceService.publicResources();
		} catch (Exception e) {
			LOGGER.error("删除公共资源的JSONArray列表失败！", e);
			return null;
		}

	}
}
