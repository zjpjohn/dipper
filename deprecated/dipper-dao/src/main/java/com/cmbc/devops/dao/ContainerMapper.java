package com.cmbc.devops.dao;


import java.util.List;
import java.util.Map;

import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.expand.ContainerExpand;

/**
 * @author langzi
 *
 */
public interface ContainerMapper {
	
	/**
	 * @author langzi
	 * @param container
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月21日
	 * insert container entity to DB
	 */
	public int insertContainer(Container container) throws Exception;
	
	/**
	 * @author langzi
	 * @param conId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月21日
	 * update container entity to DB
	 */
	public int updateContainer(Container container) throws Exception;
	
	/**
	 * @author langzi
	 * @param power
	 * @param ja
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月26日
	 * update container entity status to DB
	 */
	public int updateConStatus(ContainerExpand containerExpand) throws Exception;
	
	/**
	 * @author langzi
	 * @param conId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月21日
	 * delete container entity from DB by container id(primary key)
	 */
	public int deleteContainer(Integer conId) throws Exception;
	
	/**
	 * @author langzi
	 * @param container
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月21日
	 * select container entity from DB by container
	 */
	public Container selectContainer(Container container) throws Exception;
	
	/**
	 * @author langzi
	 * @param ja
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月26日
	 * select container entities from DB by containerIds
	 */
	public List<Container> selectContainers(String[] containerIds) throws Exception;
	
	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年11月5日
	 * select last container Id from DB
	 */
	public Integer selectLastConId() throws Exception;
	
	/**根据容器的Uuid前8位进行搜索**/
	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月21日
	 * select container entities from DB which meet container conditions
	 */
	public List<Container> selectAll(Container container) throws Exception;
	
	/**
	 * @author langzi
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年8月21日
	 * select container entities from DB by short uuid
	 */
	public List<Container> selectContainersByShortUUIDAll(Container container) throws Exception;
	
	/**
	 * @author langzi
	 * @param appId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 * select container entities from DB by application id
	 */
	public List<Container> selectContainerByAppId(Integer appId) throws Exception;
	
	/**
	 * @author langzi
	 * @param appId
	 * @param imgId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年12月10日
	 * select container entities from DB by application id and image id
	 */
	public List<Container> selectContainerByAppIdAndImgId(Integer appId, Integer imgId) throws Exception;
	
	/**
	 * @author langzi
	 * @param hostId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 * select container entities from DB by host Id
	 */
	public List<Container> selectContainerByHostId(Integer hostId) throws Exception;
	
	/**
	 * @author zll
	 * @param  imageid
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2016年3月28日
	 * get up container number by imageid
	 */
	public int upContainerNum(Integer imageId) throws Exception;
	
	/**
	 * @author zll
	 * @param imageId
	 * @return
	 * @throws Exception
	 * @version 1.0
	 * 2015年9月15日
	 * select container entities from DB by image Id
	 */
	public List<Container> selectContainerByImageId(Map<String, Integer> map) throws Exception;
	
}