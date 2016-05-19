package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.expand.ContainerExpand;
import com.cmbc.devops.model.ContainerModel;
import com.cmbc.devops.model.SimpleContainer;

/**
 * date：2015年8月21日 下午2:45:54 project name：cmbc-devops-service
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：ContainerService.java description：
 */
public interface ContainerService {

	/**
	 * @author yangqinglin
	 * @return 返回属于应用下所有的容器列表
	 * @version 1.0 2015年12月24日
	 * @throws Exception
	 */
	public List<Container> listAllContainerInApp(Container container) throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年8月21日
	 * @throws Exception
	 */
	public List<Container> listAllContainer(Container container) throws Exception;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年9月15日
	 */
	public List<Container> listContainersByAppId(Integer appId) throws Exception;

	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年9月15日
	 */
	public List<Container> listContainersByHostId(Integer hostId) throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年8月21日
	 * @throws Exception
	 */
	public JSONArray listAllContainersJsonArray(Integer tenantId) throws Exception;

	/**
	 * @author langzi
	 * @param userId
	 * @param pagenumber
	 * @param pagesize
	 * @param model
	 * @return
	 * @version 1.0 2015年9月6日
	 */
	public GridBean listOnePageContainers(Integer userId, Integer tenantId, int pagenum, int pagesize,
			ContainerModel model);

	/**
	 * @author langzi
	 * @param userId
	 * @param pagenum
	 * @param pagesize
	 * @param appId
	 * @return
	 * @version 1.0 2015年12月15日
	 */
	public GridBean listOnePageContainers(Integer userId, Integer tenantId, int pagenum, int pagesize, int appId);

	/**
	 * @author langzi
	 * @param container
	 * @return
	 * @version 1.0 2015年8月21日
	 * @throws Exception
	 */
	public Container getContainer(Container container) throws Exception;

	/**
	 * @author langzi
	 * @param container
	 * @return
	 * @version 1.0 2015年8月21日
	 * @throws Exception
	 */
	public JSONObject getContainerJsonObject(Container container) throws Exception;

	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年11月5日
	 */
	public Integer getLastConId() throws Exception;

	/**
	 * @author langzi
	 * @param ja
	 * @return
	 * @version 1.0 2015年8月26日
	 * @throws Exception
	 */
	public List<SimpleContainer> selectContainerUuid(String[] containerIds) throws Exception;

	/**
	 * @author langzi
	 * @param container
	 * @return
	 * @version 1.0 2015年8月21日
	 * @throws Exception
	 */
	public int addContaier(Container container) throws Exception;

	/**
	 * @author langzi
	 * @param conId
	 * @return
	 * @version 1.0 2015年8月21日
	 * @throws Exception
	 */
	public int modifyContainer(Container container) throws Exception;

	/**
	 * @author langzi
	 * @param power
	 * @param ja
	 * @return
	 * @version 1.0 2015年8月26日
	 * @throws Exception
	 */
	public int modifyConStatus(ContainerExpand ce) throws Exception;

	/**
	 * @author langzi
	 * @param conId
	 * @return
	 * @version 1.0 2015年8月21日
	 * @throws Exception
	 */
	public int removeContainer(Integer conId) throws Exception;

	/**
	 * @author yangqinglin
	 * @param conId
	 * @return
	 * @version 1.0 2015年11月4日
	 * @throws Exception
	 */
	public GridBean advancedSearchContainer(Integer userId, Integer tenantId, int pagenumber, int pagesize, JSONObject json_object)
			throws Exception;

	/**
	 * yangqinglin
	 * 
	 * @param appId(应用id)
	 * @return
	 * @throws Exception
	 * @version 1.0 2015年9月15日
	 */
	public GridBean listApp(Integer userId, int pagenumber, int pagesize, ContainerModel model) throws Exception;

	/**
	 * yangqinglin
	 * 
	 * @param appId(应用id)
	 * @return 查询容器界面显示的信息
	 * @throws Exception
	 * @version 1.0 2015年12月21日
	 */
	public GridBean listContainersByAppid(Integer userId, Integer tenantId, int pagenum, int pagesize, int appId, Integer imageId) throws Exception;

	/**
	 * @param userId
	 * @param tenantId
	 * @param pagenumber
	 * @param pagesize
	 * @param power_status
	 * @return
	 * @throws Exception
	 * 根据容器的状态，列出全部的、运行中的、已关闭的列表
	 */
	public GridBean listPowerConInfo(Integer userId, Integer tenantId, int pagenumber, int pagesize, int power_status) throws Exception;

	/**
	 * 通过镜像id获取flag个运行数量的实例（时间排序）
	 * @param imageId
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public abstract List<SimpleContainer> selectContainerByImageId(Integer imageId,Integer flag) throws Exception;
	
	/**
	 * @param userId
	 * @param tenantId
	 * @param pagenumber
	 * @param pagesize
	 * @param param_json
	 * @return
	 * @throws Exception
	 * 根据容器的标识关键字和所属应用ID模糊查询容器实例列表
	 */
	public GridBean listSearchConIns(Integer userId, Integer tenantId, int pagenumber, int pagesize, JSONObject param_json)
			throws Exception;
}
