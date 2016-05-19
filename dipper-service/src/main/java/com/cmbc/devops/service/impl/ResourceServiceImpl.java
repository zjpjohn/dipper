package com.cmbc.devops.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.NormalConstant;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.ResourceMapper;
import com.cmbc.devops.dao.UserMapper;
import com.cmbc.devops.service.ResourceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.cmbc.devops.entity.DkResource;
import com.cmbc.devops.entity.DkResourceWithUser;
import com.cmbc.devops.entity.User;

/**
 * @date 2015年12月17日
 * @author yangqinglin
 * @version 1.0
 */
@Component
public class ResourceServiceImpl implements ResourceService {

	@Resource
	private ResourceMapper resourceMapper;
	@Resource
	private UserMapper userMapper;

	@Override
	public JSONArray getAllResource() throws Exception {
		DkResource resource = new DkResource();
		List<DkResource> dkresource_list = resourceMapper.selectAll(resource);
		JSONArray return_jsonarray = (JSONArray) JSONArray.toJSON(dkresource_list);
		return return_jsonarray;
	}

	@Override
	public String getResourceById(Integer resId) throws Exception {
		DkResource resource = resourceMapper.selectResource(resId);
		/* 获取CPU限制数值 */
		String dk_cpu = "--cpu-shares=" + resource.getResCPU();
		/* 获取内存限制量 */
		String dk_mem = "--memory=" + resource.getResMEM() + "m";
		/* 获取磁盘IO的相对权重 */
		String dk_blkio = "--blkio-weight=" + resource.getResBLKIO();
		String return_string = " " + dk_cpu + " " + dk_mem + " " + dk_blkio + " ";

		return return_string;
	}

	@Override
	public JSONObject getResJsonById(Integer resId) throws Exception {
		DkResource resource = resourceMapper.selectResource(resId);
		JSONObject return_json = (JSONObject) JSONObject.toJSON(resource);
		return return_json;
	}

	@Override
	public GridBean resourceAllList(Integer userId, int pagenumber, int pagesize) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		DkResource resource = new DkResource();
		List<DkResource> dkresource_list = resourceMapper.selectAll(resource);
		/** 获取全部用户的列表 **/
		List<User> userList = userMapper.fetchAllUser();

		Page<DkResourceWithUser> page_obj = new Page<DkResourceWithUser>();
		if (!dkresource_list.isEmpty()) {
			for (DkResource one_res : dkresource_list) {
				DkResourceWithUser res_user = new DkResourceWithUser(one_res);
				/* 遍历single_host所有信息，填充到host_user中 */
				Integer user_id = one_res.getResCreator();

				if (user_id != null) {
					for (User sinUser : userList) {
						if (sinUser.getUserId() == user_id) {
							res_user.setResUserName(sinUser.getUserName());
						}
					}
				}

				page_obj.add(res_user);
			}
		}

		int totalpage = ((Page<?>) dkresource_list).getPages();
		Long totalNum = ((Page<?>) dkresource_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), page_obj);
		return gridBean;
	}

	@Override
	/** 创建单个定制资源 */
	public int createResource(DkResource dk_resource) throws Exception {
		return resourceMapper.insertResource(dk_resource);
	}

	@Override
	/** 修改单个定制资源 */
	public int updateResource(DkResource dk_resource) throws Exception {
		return resourceMapper.updateResource(dk_resource);
	}

	@Override
	/** 删除单个定制资源 */
	public int deleteResource(DkResource dk_resource) throws Exception {
		return resourceMapper.deleteResource(dk_resource.getResId());
	}

	@Override
	public int deleteResources(JSONObject json_object) throws Exception {
		String resource_ids = json_object.getString("res_ids");

		String[] resource_array = resource_ids.split(",");
		ArrayList<Integer> resid_list = new ArrayList<Integer>();
		for (int count = 0, size = resource_array.length; count < size; count++) {
			resid_list.add(Integer.parseInt(resource_array[count]));
		}

		return resourceMapper.deleteResources(resid_list);
	}

	@Override
	public DkResource getResObjById(Integer resId) throws Exception {
		return resourceMapper.selectResource(resId);
	}

	@Override
	public DkResourceWithUser detail(int resId) throws Exception {
		DkResource dk_resource = resourceMapper.selectResource(resId);
		DkResourceWithUser dk_resuser = new DkResourceWithUser(dk_resource);

		/** 注入从数据库获取的数据信息 */
		Integer user_id = dk_resource.getResCreator();
		if (user_id != null) {
			dk_resuser.setResCreator(user_id);
			User user = new User();
			user.setUserId(user_id);
			user = userMapper.selectUser(user);
			if (user != null) {
				dk_resuser.setResUserName(user.getUserName());
			}
		}

		return dk_resuser;
	}

	@Override
	public DkResource getResourceByName(String resName) throws Exception {
		DkResource dk_resource = new DkResource();
		List<DkResource> dkres_list = resourceMapper.selectAll(dk_resource);
		for (DkResource single_dkr : dkres_list) {
			if (resName.equals(single_dkr.getResName())) {
				return single_dkr;
			}
		}
		return null;
	}

	@Override
	public GridBean searchAllResource(Integer userId, int pagenumber, int pagesize, String search_name)
			throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		DkResource resource = new DkResource();
		resource.setResName(search_name);
		List<DkResource> dkresource_list = resourceMapper.selectAll(resource);
		/** 获取全部用户的列表 **/
		List<User> userList = userMapper.fetchAllUser();

		Page<DkResourceWithUser> page_obj = new Page<DkResourceWithUser>();
		if (!dkresource_list.isEmpty()) {
			for (DkResource one_res : dkresource_list) {
				DkResourceWithUser res_user = new DkResourceWithUser(one_res);
				/* 遍历single_host所有信息，填充到host_user中 */
				Integer user_id = one_res.getResCreator();

				if (user_id != null) {
					for (User sinUser : userList) {
						if (sinUser.getUserId() == user_id) {
							res_user.setResUserName(sinUser.getUserName());
						}
					}
				}
				page_obj.add(res_user);
			}
		}

		int totalpage = ((Page<?>) dkresource_list).getPages();
		Long totalNum = ((Page<?>) dkresource_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), page_obj);
		return gridBean;
	}

	@Override
	public GridBean advancedSearchRes(Integer userId, int pagenum, int pagesize, JSONObject json_object)
			throws Exception {
		PageHelper.startPage(pagenum, pagesize);
		DkResource dks = new DkResource();
		/* 组装应用查询数据的条件 */
		dks.setResStatus((byte) Status.RESOURCE.NORMAL.ordinal());

		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int i = 0; i < params.length; i++) {
			switch (params[i].trim()) {
			case "1":
				dks.setResName(values[i].trim());
				break;
			case "2":
				dks.setResDesc(values[i].trim());
				break;
			case "3":
				dks.setResComment(values[i].trim());
				break;
			default:
				break;
			}
		}

		List<DkResource> dkresource_list = resourceMapper.selectAll(dks);
		/** 获取全部用户的列表 **/
		List<User> userList = userMapper.fetchAllUser();

		Page<DkResourceWithUser> page_obj = new Page<DkResourceWithUser>();
		if (!dkresource_list.isEmpty()) {
			for (DkResource one_res : dkresource_list) {
				DkResourceWithUser res_user = new DkResourceWithUser(one_res);
				/* 获取资源的创建用户ID */
				Integer user_id = one_res.getResCreator();

				if (user_id != null) {
					for (User sinUser : userList) {
						if (user_id == sinUser.getUserId()) {
							res_user.setResUserName(sinUser.getUserName());
						}
					}
				}
				page_obj.add(res_user);
			}
		}

		int totalpage = ((Page<?>) dkresource_list).getPages();
		Long totalNum = ((Page<?>) dkresource_list).getTotal();

		GridBean gridBean = new GridBean(pagenum, totalpage, totalNum.intValue(), page_obj);
		return gridBean;

	}

	@Override
	public List<DkResource> selectAllViaIds(List<Integer> res_ids) throws Exception {
		return resourceMapper.selectAllViaIds(res_ids);
	}

	@Override
	/** 获取全部公共基础资源组合列表 */
	public JSONArray publicResources() throws Exception {
		DkResource resource = new DkResource();
		resource.setTenantId(NormalConstant.ADMIN_TENANTID);
		List<DkResource> dkresource_list = resourceMapper.selectAll(resource);
		JSONArray return_jsonarray = (JSONArray) JSONArray.toJSON(dkresource_list);
		return return_jsonarray;
	}

	@Override
	public List<DkResource> selectMultiReses(List<Integer> resIdList) throws Exception {
		return resourceMapper.selectMultiReses(resIdList);
	}

	@Override
	public Integer batchInsertReses(List<DkResource> resList) throws Exception {
		return resourceMapper.batchInsertReses(resList);
	}

	@Override
	public List<DkResource> selectAll(DkResource dkResource) throws Exception {
		return resourceMapper.selectAll(dkResource);
	}
}
