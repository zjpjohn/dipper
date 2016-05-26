/**
 * 
 */
package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Registry;
import com.cmbc.devops.entity.RegistrySlaveImage;
import com.cmbc.devops.model.RegIdImageTypeModel;
import com.cmbc.devops.model.RegistryModel;
import com.cmbc.devops.model.RegistryWithIPModel;

/**
 * date：2015年8月24日 上午12:21:10 project name：cmbc-devops-service
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：RegistryService.java description：
 */
public interface RegistryService {
	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return JSONArray
	 * @version 1.0 2015年8月24日
	 * @throws Exception
	 */
	public JSONArray getOnePageRegistrys(int userId, RegistryModel RegistryModel) throws Exception;

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月6日 11:00
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean getOnePageRegistrys(int userId, int pagenumber, int pagesize, RegistryModel RegistryModel)
			throws Exception;

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月9日 09:26
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean getOnePageRegistrysWithIP(int userId, int pagenumber, int pagesize,
			RegistryWithIPModel registryWithIPModel) throws Exception;

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return int
	 * @version 1.0 2015年8月24日
	 */
	public int createRegistry(JSONObject jo);

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return int
	 * @version 1.0 2015年8月24日
	 * @throws Exception
	 */
	public List<Registry> listRegistrysByHostId(Integer hostId) throws Exception;

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return int
	 * @version 1.0 2015年8月24日
	 */
	public int updateRegistry(JSONObject jo);

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return int
	 * @version 1.0 2015年8月24日
	 */
	public int deleteRegistry(JSONObject jo);

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return JSONObject
	 * @version 1.0 2015年8月27日
	 * @throws Exception
	 */
	public JSONObject getRegistry(Registry record) throws Exception;

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return JSONObject
	 * @version 1.0 2015年8月27日
	 */
	public JSONArray getRegistryMster();

	/**
	 * TODO
	 * 
	 * @author mayh
	 * @return boolean
	 * @version 1.0 2015年8月31日
	 * @throws Exception
	 */
	public String checkImageIsExist(JSONObject jo) throws Exception;

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月9日 09:26
	 * @description 添加对于特定仓库下所有的镜像方法，返回GridBean类型
	 */
	GridBean getOnePageRegistrysSlaveImages(int userId, int pagenumber, int pagesize,
			RegIdImageTypeModel regIdImageTypeModel, int registryid, byte imagestatus) throws Exception;

	/**
	 * TODO
	 * 
	 * @author yangqinglin
	 * @return int
	 * @version 1.0 2015年9月16日
	 */
	public int SyncRegistryImages(String RegistryNodeIP, String ServerPort);

	/**
	 * TODO
	 * 
	 * @author yangqinglin
	 * @description 添加查询功能，根据前台传输的关键字进行查询处理
	 * @version 1.0 2015年10月8日
	 * @throws Exception
	 */
	public GridBean getSearchRegistrysWithIP(int userId, int pagenumber, int pagesize,
			RegistryWithIPModel registryWithIPModel) throws Exception;

	/**
	 * TODO
	 * 
	 * @author yangqinglin
	 * @description 添加根据ID信息查询仓库的功能
	 * @version 1.0 2015年10月8日
	 * @throws Exception
	 */
	public Registry getRegistryByRegiId(int tenantId, int registry_id) throws Exception;

	/**
	 * 获取镜像所在的仓库
	 * 
	 * @param imageId
	 * @return
	 * @throws Exception
	 */
	public Registry getByImage(int imageId) throws Exception;

	/**
	 * 从数据库中获取仓库对象
	 * 
	 * @param Registry
	 * @return
	 * @throws Exception
	 */
	public Registry loadRegistry(Registry record) throws Exception;

	/**
	 * 从数据库中获取所有仓库的对象链表
	 * 
	 * @param List<Registry>
	 * @return
	 * @throws Exception
	 */
	public List<Registry> loadAllRegistries(Registry record) throws Exception;

	/**
	 * 添加高级检索部分的功能
	 * 
	 * @throws Exception
	 */
	public GridBean advancedSearchRegi(Integer userId, int pagenumber, int pagesize, RegistryModel registryModel,
			JSONObject json_object) throws Exception;

	/**
	 * 通过仓库ID信息获取下属所有镜像列表
	 * 
	 * @throws Exception
	 */
	public List<RegistrySlaveImage> getImagesByRegistryId(Integer tenantId, Integer registry_id) throws Exception;

}
