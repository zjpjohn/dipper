package com.cmbc.devops.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.NormalConstant;
import com.cmbc.devops.dao.AppMapper;
import com.cmbc.devops.dao.ContainerMapper;
import com.cmbc.devops.dao.ImageMapper;
import com.cmbc.devops.dao.RegImageMapper;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.ImageWithApp;
import com.cmbc.devops.model.ImageModel;
import com.cmbc.devops.service.ImageService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * @author luogan 2015年8月17日 上午9:10:26
 */
@Component
public class ImageServiceImpl implements ImageService {

	private static Logger logger = Logger.getLogger(ImageServiceImpl.class);
	@Resource
	private ImageMapper imageMapper;
	@Autowired
	private RegImageMapper regImageMapper;
	@Autowired
	private AppMapper appMapper;
	@Autowired
	private ContainerMapper containerMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.ImageService#create(com.cmbc.devops.entity.Image)
	 */
	@Override
	public int create(Image image) {
		image.setImageCreatetime(new Date());
		try {
			int result = imageMapper.insertImage(image);
			if (result > 0) {
				return image.getImageId();
			} else {
				logger.warn("save image in database error");
				return 0;
			}
		} catch (Exception e) {
			logger.error("save image in database error:", e);
			return 0;
		}
	}

	@Override
	public boolean delete(int imageId) {
		try {
			imageMapper.deleteImage(imageId);
			regImageMapper.deleteByImageId(imageId);
			return true;
		} catch (Exception e) {
			logger.error("delete image in database error:", e);
			return false;
		}
	}

	@Override
	public boolean update(Image image) throws Exception {
		return imageMapper.updateImage(image) > 0;
	}

	@Override
	public boolean restoreImage(Image image) throws Exception {
		return imageMapper.restoreImage(image) > 0;
	}

	@Override
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean pagination(int userId, int tenantId, int page, int rows, ImageModel imageModel) throws Exception {
		PageHelper.startPage(page, rows);
		Image image = new Image();
		image.setImageName(imageModel.getImageName());
		image.setAppId(imageModel.getAppId());
		image.setTenantId(tenantId);
		List<Image> images = imageMapper.selectAllImage(image);
		int totalPage = ((Page<?>) images).getPages();
		Long totalNum = ((Page<?>) images).getTotal();
		/* 在页面中的镜像列表中添加所属应用的名称 */
		Page<ImageWithApp> page_obj = new Page<ImageWithApp>();
		/* 镜像列表中显示应用的名称信息 */
		for (Image single_image : images) {
			ImageWithApp image_app = new ImageWithApp();
			image_app.setImageId(single_image.getImageId());
			image_app.setImageUuid(single_image.getImageUuid());
			image_app.setImageStatus(single_image.getImageStatus());
			image_app.setImageName(single_image.getImageName());
			image_app.setImageTag(single_image.getImageTag());
			image_app.setImageSize(single_image.getImageSize());
			image_app.setImageType(single_image.getImageType());
			image_app.setImageDesc(single_image.getImageDesc());
			image_app.setImagePort(single_image.getImagePort());
			image_app.setImageCreator(single_image.getImageCreator());
			image_app.setImageCreatetime(single_image.getImageCreatetime());

			// 获取镜像下运行容器数量
			int num = containerMapper.upContainerNum(single_image.getImageId());
			image_app.setInstanceNum(num);

			Integer app_id = single_image.getAppId();
			if (app_id != null) {
				image_app.setAppId(app_id);
				App application = appMapper.select(app_id);
				if (application != null) {
					image_app.setAppName(application.getAppName());
				}
			}

			page_obj.add(image_app);
		}
		GridBean gridBean = new GridBean(page, totalPage, totalNum.intValue(), page_obj);
		return gridBean;
	}

	@Override
	public List<Image> selectActiveAllImages(Image image) throws Exception {
		List<Image> images = imageMapper.selectAllImage(image);
		return images;
	}

	@Override
	public List<Image> selectMultiImages(List<Integer> imageIdList) throws Exception {
		List<Image> images = imageMapper.selectMultiImages(imageIdList);
		return images;
	}

	@Override
	public Integer batchInsertImages(List<Image> imageList) throws Exception {
		return imageMapper.batchInsertImages(imageList);
	}

	@Override
	public Integer batchDeleteImages(List<Integer> imageIdList) throws Exception {
		return imageMapper.changeStatus(imageIdList);
	}

	@Override
	public GridBean getImageListByappIdAndEnvId(int userId, int tenantId, int page, int rows, int appId, int envId)
			throws Exception {
		PageHelper.startPage(page, rows);
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("appId", appId);
		map.put("envId", envId);
		List<Image> images = imageMapper.selectImagesByappIdAndEnvId(map);
		int totalpage = ((Page<?>) images).getPages();
		Long totalNum = ((Page<?>) images).getTotal();
		GridBean gridBean = new GridBean(page, totalpage, totalNum.intValue(), images);
		return gridBean;
	}

	@Override
	public int countAllImageList(ImageModel imageModel) {
		return 0;
	}

	@Override
	public Image loadImage(Integer tenant_id, Integer id) throws Exception {
		return imageMapper.selectByPrimaryKey(id);
	}

	@Override
	public JSONObject getJsonObjectOfImage(Image image) {
		return null;
	}

	@Override
	/** @date:2016年3月28日 添加租户维度 */
	public GridBean advancedSearchImage(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			ImageModel imageModel, JSONObject json_object) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);

		/* 组装应用查询数据的条件 */
		Image image = new Image();
		/** @bug222_begin:[镜像管理]高级查询,以镜像类型为条件列,当搜索关键字输入应用或应用类型,无法获得查询结果 **/
		/** @bug222_finish **/

		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int i = 0; i < params.length; i++) {
			switch (params[i].trim()) {
			/* 填充镜像UUID */
			case "1":
				image.setImageUuid(values[i].trim());
				break;
			/* 镜像名称 */
			case "2":
				image.setImageName(values[i].trim());
				break;
			/* 镜像版本 */
			case "3":
				image.setImageTag(values[i].trim());
				break;
			/* 镜像类型 ，BASIC和APP */
			case "4":
				if ("基础镜像".indexOf(values[i].trim()) != -1) {
					image.setImageType("BASIC");
				} else if ("应用镜像".indexOf(values[i].trim()) != -1) {
					image.setImageType("APP");
				} else if ("未知镜像".indexOf(values[i].trim()) != -1) {
					image.setImageType("");
				}
				break;
			/* 镜像状态 */
			case "5":
				if ("已删除".indexOf(values[i].trim()) != -1) {
					image.setImageStatus((byte) 0);
				} else if ("已发布".indexOf(values[i].trim()) != -1) {
					image.setImageStatus((byte) 1);
				} else if ("已制作".indexOf(values[i].trim()) != -1) {
					image.setImageStatus((byte) 2);
				} else if ("异常".indexOf(values[i].trim()) != -1) {
					image.setImageStatus((byte) 3);
				} else {
					/** 用Integer的最大值表述不匹配的情况 **/
					image.setImageStatus((byte) Integer.MAX_VALUE);
				}
				break;
			default:
				break;
			}
		}

		image.setTenantId(tenantId);

		List<Image> image_list = imageMapper.selectAllImage(image);

		/* 在页面中的镜像列表中添加所属应用的名称 */
		Page<ImageWithApp> page_obj = new Page<ImageWithApp>();
		/* 镜像列表中显示应用的名称信息 */
		for (Image single_image : image_list) {
			ImageWithApp image_app = new ImageWithApp();
			image_app.setImageId(single_image.getImageId());
			image_app.setImageUuid(single_image.getImageUuid());
			image_app.setImageStatus(single_image.getImageStatus());
			image_app.setImageName(single_image.getImageName());
			image_app.setImageTag(single_image.getImageTag());
			image_app.setImageSize(single_image.getImageSize());
			image_app.setImageType(single_image.getImageType());
			image_app.setImageDesc(single_image.getImageDesc());
			image_app.setImagePort(single_image.getImagePort());
			image_app.setImageCreator(single_image.getImageCreator());
			image_app.setImageCreatetime(single_image.getImageCreatetime());

			Integer app_id = single_image.getAppId();
			if (app_id != null) {
				image_app.setAppId(app_id);
				App app = appMapper.select(app_id);
				if (app != null) {
					image_app.setAppName(app.getAppName());
				}
			}

			page_obj.add(image_app);
		}

		int totalpage = ((Page<?>) image_list).getPages();
		Long totalNum = ((Page<?>) image_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), page_obj);
		return gridBean;
	}

	@Override
	/** @date:2016年3月28日 添加租户维度 */
	public JSONArray imageAllList(Integer userId, Integer tenantId, ImageModel image) throws Exception {
		Image single_image = new Image();
		single_image.setTenantId(tenantId);
		List<Image> allimg_list = imageMapper.selectAllImage(single_image);
		if (!allimg_list.isEmpty()) {
			JSONArray json_array = (JSONArray) JSONArray.toJSON(allimg_list);
			return json_array;
		} else {
			return null;
		}
	}

	@Override
	public JSONArray activeImageListByAppId(int userId, Integer appId) throws Exception {
		List<Image> images = imageMapper.selectImagesByappId(appId);
		JSONArray ja = new JSONArray();
		if (images.isEmpty()) {
			return ja;
		}
		for (Image image : images) {
			// 获取该版本下运行容器数量
			int num = containerMapper.upContainerNum(image.getImageId());
			if (num == 0) {
				continue;
			} else {
				JSONObject jo = new JSONObject();
				jo.put("imageId", image.getImageId());
				jo.put("imageName", image.getImageName());
				jo.put("imageTag", image.getImageTag());
				jo.put("conNum", num);
				ja.add(jo);
			}

		}
		return ja;
	}

	@Override
	public JSONArray publicImages(Integer userId, Integer tenantId, ImageModel image) throws Exception {
		Image single_image = new Image();
		/** 获取超级管理员租户ID信息 */
		single_image.setTenantId(NormalConstant.ADMIN_TENANTID);
		List<Image> allimg_list = imageMapper.selectAllImage(single_image);
		if (!allimg_list.isEmpty()) {
			JSONArray json_array = (JSONArray) JSONArray.toJSON(allimg_list);
			return json_array;
		} else {
			return null;
		}
	}

}
