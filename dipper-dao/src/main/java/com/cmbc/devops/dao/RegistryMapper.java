package com.cmbc.devops.dao;


import java.util.List;

import com.cmbc.devops.entity.RegIdImageType;
import com.cmbc.devops.entity.Registry;
import com.cmbc.devops.entity.RegistrySlaveImage;
import com.cmbc.devops.entity.RegistryWithIP;
import com.cmbc.devops.exception.SqlException;

/**  
 * date：2015年8月19日 下午4:43:06
 * project name：cmbc-devops-dao
 * @author  mayh
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：RegistryMapper.java
 * description：  
 */
public interface RegistryMapper {
	/**
	* TODO
	* @author mayh
	* @return int
	* @throws SqlException
	* @version 1.0
	* 2015年8月19日
	* insert registry entity to DB
	 */
	public int insertRegistry(Registry record) throws SqlException;
	
	/**
	* TODO
	* @author mayh
	* @return int
	* @throws SqlException
	* @version 1.0
	* 2015年8月19日
	* delete registry entity from DB by registry Id(primary key)
	 */
	public int deleteRegistry(Integer registryId) throws SqlException;
	
	/**
    * TODO
    * @author mayh
    * @throws SqlException
    * @return int
    * @version 1.0
    * 2015年8月19日
    * update registry entity to DB
     */
	public int updateRegistry(Registry record) throws SqlException;
	
	/**
	* TODO
	* @author mayh
	* @throws SqlException
	* @return List<Registry>
	* @version 1.0
	* 2015年8月19日
	* select all registry entities from DB which meet registry conditions
	 */
	public List<Registry> selectAll(Registry record) throws SqlException; 
	
	/**
	* TODO
	* @author mayh
	* @throws SqlException
	* @return List<Registry>
	* @version 1.0
	* 2015年8月19日
	* select all registry entity from DB by host Id
	 */
	public List<Registry> selectRegistryByHostId(Integer hostId) throws SqlException; 
	
	/**
	* TODO
	* @author mayh
	* @throws SqlException
	* @return Registry
	* @version 1.0
	* 2015年8月27日
	* select all registry entity from DB which meet registry condition
	*/
	public Registry selectRegistry(Registry record) throws SqlException;
	
	/**
	* TODO
	* @author mayh
	* @throws SqlException
	* @return void
	* @version 1.0
	* 2015年8月27日
	* chance registry status to DB by registry ids;
	*/
	public int changeStatus(List<Integer> list) throws SqlException;
	
	/**
	* TODO
	* @author yangqinglin
	* @throws SqlException
	* @return List<RegistryWithIP>
	* @description 查询带有IP地址的仓库信息
	* 2015年9月10日
	*/
	public List<RegistryWithIP> selectAllWithIP(RegistryWithIP registrywithip) throws SqlException;
	
	/**
	* TODO
	* @author yangqinglin
	* @throws SqlException
	* @return List<Image>
	* @description 按仓库ID和镜像的类型查询仓库下所有的镜像列表
	* 2015年8月27日
	*/
	public List<RegistrySlaveImage> selectAllImageInRegistry(RegIdImageType regIdImageType) throws SqlException; 
	
}