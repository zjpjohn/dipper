package com.cmbc.devops.dao;

import java.util.List;
import java.util.Map;

import com.cmbc.devops.entity.Image;
import com.cmbc.devops.exception.SqlException;

/**
 * @author luogan 2015年8月29日 下午4:04:25
 */
public interface ImageMapper {

	/**
	 * @author luogan
	 * @param image
	 * @return create image
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 insert image entity to DB
	 */
	public int insertImage(Image record) throws SqlException;

	/**
	 * @author luogan
	 * @param image
	 * @return delete image
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 delete image entity from DB by image Id(primary
	 *          key)
	 */
	public int deleteImage(Integer imageId) throws SqlException;

	/**
	 * @author luogan
	 * @param image
	 * @return update image
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 update image entity to DB
	 */
	public int updateImage(Image record) throws SqlException;

	/**
	 * @author luogan
	 * @param image
	 * @return update image
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 select image entity from DB by image Id(primary
	 *          key)
	 */
	public Image selectByPrimaryKey(Integer imageId) throws SqlException;

	/**
	 * @author luogan
	 * @param image
	 * @return update imageList
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 select image entities from DB which meet image
	 *          conditions
	 */
	public List<Image> selectAllImage(Image image) throws SqlException;

	/**
	 * @author luogan
	 * @param image
	 * @return update imageByAppId
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 select image entities from DB by appId
	 */
	public List<Image> selectImagesByappIdAndEnvId(Map<String, Integer> map) throws SqlException;

	/**
	 * @author luogan
	 * @param image
	 * @return update imageByAppId
	 * @throws SqlException
	 * @version 1.0 2015年8月28日 select image entities from DB by appId
	 */
	public List<Image> selectImagesByappId(int appId) throws SqlException;

	/**
	 * @author yangqinglin
	 * @param image
	 * @return 将应用镜像恢复为基础镜像，修正类型并置空绑定应用ID
	 * @throws SqlException
	 * @version 1.0 2015年8月28日
	 */
	public int restoreImage(Image image) throws SqlException;

	/** 通过镜像的ID列表获取全部的镜像列表信息 */
	public List<Image> selectMultiImages(List<Integer> imageIdList) throws Exception;

	/** 批量插入镜像记录 */
	public Integer batchInsertImages(List<Image> imageList) throws Exception;

	/** 批量将镜像状态设置为删除 */
	public int changeStatus(List<Integer> list) throws SqlException;

}