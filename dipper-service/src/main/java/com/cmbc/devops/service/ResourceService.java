package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.DkResource;
import com.cmbc.devops.entity.DkResourceWithUser;

/**
 * date：2015年8月19日 上午11:43:28 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：LogService.java description：
 */
public interface ResourceService {

	/**
	 * @description 获取全部资源内容的对象数组
	 * @author youngtsinglin
	 * @date 2015年12月17日
	 * @throws Exception
	 */
	public JSONArray getAllResource() throws Exception;

	/**
	 * @description 通过资源ID获取资源配置运行时的字符串，例如:--cpu-shares=100 --memory=512m
	 *              --blkio=100
	 * @author youngtsinglin
	 * @date 2015年12月17日
	 * @throws Exception
	 */
	public String getResourceById(Integer resId) throws Exception;

	/**
	 * @param resId
	 * @return
	 * @throws Exception
	 *             通过资源ID获取定制资源的对象信息
	 */
	public DkResource getResObjById(Integer resId) throws Exception;

	/**
	 * @description 获取单一资源内容的对象
	 * @author youngtsinglin
	 * @date 2015年12月17日
	 * @throws Exception
	 */
	public JSONObject getResJsonById(Integer resId) throws Exception;

	/**
	 * @description 获取全部资源内容的GridBean
	 * @author youngtsinglin
	 * @throws Exception
	 * @date 2015年12月22日
	 */
	public GridBean resourceAllList(Integer userId, int pagenumber, int pagesize) throws Exception;

	/**
	 * @description 获取全部资源内容的GridBean
	 * @author youngtsinglin
	 * @throws Exception
	 * @date 2015年12月22日
	 */
	public GridBean searchAllResource(Integer userId, int pagenumber, int pagesize, String search_name)
			throws Exception;

	/**
	 * @description 创建单个定制资源
	 * @author youngtsinglin
	 * @throws Exception
	 * @date 2015年12月23日
	 */
	public int createResource(DkResource dk_resource) throws Exception;

	/**
	 * @description 修改单个定制资源
	 * @author youngtsinglin
	 * @throws Exception
	 * @date 2015年12月23日
	 */
	public int updateResource(DkResource dk_resource) throws Exception;

	/**
	 * @description 删除单个定制资源
	 * @author youngtsinglin
	 * @throws Exception
	 * @date 2015年12月23日
	 */
	public int deleteResource(DkResource dk_resource) throws Exception;

	/**
	 * @description 删除多个定制资源
	 * @author youngtsinglin
	 * @throws Exception
	 * @date 2015年12月25日
	 */
	public int deleteResources(JSONObject json_object) throws Exception;

	/**
	 * @description 通过资源ID获取资源对象
	 * @author youngtsinglin
	 * @throws Exception
	 * @date 2015年12月25日
	 */
	public DkResourceWithUser detail(int resId) throws Exception;

	/**
	 * @description 通过资源的名称
	 * @author youngtsinglin
	 * @throws Exception
	 * @date 2016年1月7日
	 */
	public DkResource getResourceByName(String resName) throws Exception;

	/**
	 * @description 通过资源的名称
	 * @author yangqinglin
	 * @throws Exception
	 * @date 2016年1月13日
	 */
	public GridBean advancedSearchRes(Integer userId, int pagenum, int pagesize, JSONObject json_object)
			throws Exception;

	/**
	 * @description 通过资源的ID列表获取全部资源信息
	 * @author yangqinglin
	 * @throws Exception
	 * @date 2016年1月14日
	 */
	public List<DkResource> selectAllViaIds(List<Integer> res_ids) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	public JSONArray publicResources() throws Exception;

	public List<DkResource> selectMultiReses(List<Integer> resIdList) throws Exception;

	/** 批量插入资源记录 */
	public Integer batchInsertReses(List<DkResource> resList) throws Exception;

	/** 获取全部资源链表 **/
	public List<DkResource> selectAll(DkResource dkResource) throws Exception;

}
