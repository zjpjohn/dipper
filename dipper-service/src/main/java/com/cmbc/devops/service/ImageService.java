package com.cmbc.devops.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.model.ImageModel;

/**
 * 镜像服务接口，主要处理与DB相关的服务
 * 
 * @author dmw
 *
 */
public interface ImageService {
	/**
	 * 添加镜像
	 * 
	 * @param image
	 * @return
	 */
	public abstract int create(Image image);

	/**
	 * 删除镜像
	 * 
	 * @param imageId
	 * @return
	 */
	public abstract boolean delete(int imageId);

	/**
	 * 更新镜像
	 * 
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public abstract boolean update(Image image) throws Exception;

	/**
	 * 镜像分页
	 * 
	 * @param userId
	 * @param page
	 * @param rows
	 * @param imageModel
	 * @return
	 * @throws Exception
	 */
	public abstract GridBean pagination(int userId, int tenantId, int page, int rows, ImageModel imageModel)
			throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return select imageList
	 * @version 1.0 2015年8月28日
	 * @throws Exception
	 */
	public abstract GridBean getImageListByappIdAndEnvId(int userId, int tenantId, int page, int rows, int appId,
			int envId) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return select imageList
	 * @version 1.0 2015年8月28日
	 */
	public abstract int countAllImageList(ImageModel imageModel);

	/**
	 * @param tenant_id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Image loadImage(Integer tenant_id, Integer id) throws Exception;

	/**
	 * @author luogan
	 * @param host
	 * @return select image
	 * @version 1.0 2015年8月28日
	 */
	public JSONObject getJsonObjectOfImage(Image image);

	/**
	 * @author yangqinglin
	 * @param null
	 * @return select all active image list
	 * @version 1.0 2015年9月24日
	 * @throws Exception
	 */
	public List<Image> selectActiveAllImages(Image image) throws Exception;

	/**
	 * @author yangqinglin
	 * @param null
	 * @return select all active image list
	 * @version 1.0 2015年10月30日
	 * @throws Exception
	 */
	public GridBean advancedSearchImage(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			ImageModel image, JSONObject json_object) throws Exception;

	/**
	 * @author yangqinglin
	 * @param null
	 * @return 恢复应用镜像为基础镜像，置空绑定应用ID
	 * @version 1.0 2015年12月15日
	 * @throws Exception
	 */
	public boolean restoreImage(Image image) throws Exception;

	/**
	 * @author yangqinglin
	 * @param null
	 * @return 获取全部镜像的json数组
	 * @version 1.0 2015年12月15日
	 * @throws Exception
	 */
	public JSONArray imageAllList(Integer userId, Integer tanantId, ImageModel image) throws Exception;

	/**
	 * @author zll
	 * @param host
	 * @return select imageList
	 * @version 1.0 2016年3月28日
	 * @throws Exception
	 */
	public abstract JSONArray activeImageListByAppId(int userId, Integer appId) throws Exception;

	/**
	 * @param userId
	 * @param tenantId
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public JSONArray publicImages(Integer userId, Integer tenantId, ImageModel image) throws Exception;

	/**
	 * 通过镜像的ID链表，获取符合条件的镜像列表
	 */
	public List<Image> selectMultiImages(List<Integer> imageIdList) throws Exception;

	/**
	 * 批量插入镜像链表
	 */
	public Integer batchInsertImages(List<Image> imageList) throws Exception;

	/**
	 * 批量删除镜像列表
	 */
	public Integer batchDeleteImages(List<Integer> imageIdList) throws Exception;
}
