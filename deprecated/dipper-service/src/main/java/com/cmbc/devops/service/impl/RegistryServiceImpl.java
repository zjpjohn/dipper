/**
 * 
 */
package com.cmbc.devops.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.dao.AppMapper;
import com.cmbc.devops.dao.HostMapper;
import com.cmbc.devops.dao.ImageMapper;
import com.cmbc.devops.dao.RegImageMapper;
import com.cmbc.devops.dao.RegistryMapper;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.RegIdImageType;
import com.cmbc.devops.entity.RegImage;
import com.cmbc.devops.entity.Registry;
import com.cmbc.devops.entity.RegistrySlaveImage;
import com.cmbc.devops.entity.RegistryWithIP;
import com.cmbc.devops.model.RegIdImageTypeModel;
import com.cmbc.devops.model.RegistryModel;
import com.cmbc.devops.model.RegistryWithIPModel;
import com.cmbc.devops.service.RegistryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * date：2015年8月24日 上午12:21:24 project name：cmbc-devops-service
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：RegistryServiceImpl.java description：
 */
@Component
public class RegistryServiceImpl implements RegistryService {
	private static final Logger logger = Logger.getLogger(RegistryServiceImpl.class);

	@Resource
	private RegistryMapper registryMapper;
	@Resource
	private HostMapper hostMapper;
	@Resource
	private RegImageMapper regImageMapper;
	@Resource
	private ImageMapper imageMapper;
	@Resource
	private AppMapper appMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.RegistryService#getOnePageRegistrys(int,
	 * com.cmbc.devops.model.RegistryModel)
	 */
	@Override
	public JSONArray getOnePageRegistrys(int userId, RegistryModel RegistryModel) throws Exception {
		PageHelper.startPage(RegistryModel.getPage(), RegistryModel.getLimit());
		Registry registry = new Registry();
		registry.setRegistryName(RegistryModel.getSearch());
		registry.setRegistryStatus((byte) Status.REGISTRY.NORMAL.ordinal());
		List<Registry> registries = registryMapper.selectAll(registry);
		// page total
		long totalpage = ((Page<?>) registries).getPages();
		long currentpage = ((Page<?>) registries).getPageNum();
		long pageSize = ((Page<?>) registries).getPageSize();
		long totalNum = ((Page<?>) registries).getTotal();

		JSONArray ja = new JSONArray();
		ja.add(totalpage);
		ja.add(currentpage);
		ja.add(pageSize);
		ja.add(totalNum);

		for (Registry registry2 : registries) {
			JSONObject jo = (JSONObject) JSONObject.toJSON(registry2);
			if (registry2.getHostId() != null) {
				Host host = new Host();
				try {
					host = hostMapper.selectHost(registry2.getHostId());
				} catch (Exception e) {
					logger.error("select host by host id[" + registry2.getHostId() + "] failed", e);
				}
				jo.put("hostName", host == null ? "" : host.getHostName());
			} else {
				jo.put("hostName", "");
			}

			ja.add(jo);
		}
		return ja;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.RegistryService#createRegistry(net.sf.json.
	 * JSONObject)
	 */
	@Override
	public int createRegistry(JSONObject jo) {
		int result = 1;
		Registry ins_regi = new Registry();
		ins_regi.setRegistryName(jo.getString("registryName"));
		ins_regi.setRegistryDesc(jo.getString("registryDesc"));
		ins_regi.setRegistryPort(jo.getInteger("registryPort"));
		ins_regi.setRegistryStatus((byte) Status.REGISTRY.NORMAL.ordinal());
		ins_regi.setRegistryCreatetime(new Date());
		ins_regi.setRegistryCreator(jo.getInteger("userId"));
		ins_regi.setHostId(jo.getInteger("hostId"));
		ins_regi.setTenantId(jo.getInteger("tenantId"));
		try {
			registryMapper.insertRegistry(ins_regi);
		} catch (Exception e) {
			logger.error("(创建仓库失败)create registry exception", e);
			result = 0;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.RegistryService#updateRegistry(net.sf.json.
	 * JSONObject)
	 */
	@Override
	public int updateRegistry(JSONObject jo) {
		int result = 1;
		try {
			Registry registry = JSONObject.toJavaObject(jo, Registry.class);
			registryMapper.updateRegistry(registry);
		} catch (Exception e) {
			logger.error("(更新仓库失败)update registry exception", e);
			result = 0;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.RegistryService#deleteRegistry(net.sf.json.
	 * JSONObject)
	 */
	@Override
	public int deleteRegistry(JSONObject jo) {
		int result = 1;
		String arrayString = jo.getString("array");
		List<Integer> list = new ArrayList<>();

		String[] jaArray = arrayString.split(",");
		for (String registryid : jaArray) {
			if (registryid == null || "".equals(registryid)) {
			} else {
				list.add(Integer.valueOf(registryid));
			}
		}
		try {
			registryMapper.changeStatus(list);
		} catch (Exception e) {
			logger.error("(删除仓库失败)delete registry exception", e);
			result = 0;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.RegistryService#getRegistry(com.cmbc.devops.
	 * entity.Registry)
	 */
	@Override
	public JSONObject getRegistry(Registry record) throws Exception {
		Registry registry = registryMapper.selectRegistry(record);
		if (registry != null) {
			JSONObject jo = (JSONObject) JSONObject.toJSON(registry);
			return jo;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.RegistryService#getRegistry(com.cmbc.devops.
	 * entity.Registry)
	 */
	@Override
	public Registry loadRegistry(Registry record) throws Exception {
		Registry registry = registryMapper.selectRegistry(record);
		return registry;
	}

	@Override
	public Registry getRegistryByRegiId(int tenantId, int registry_id) throws Exception {
		Registry sel_regi = new Registry();
		sel_regi.setRegistryId(registry_id);
		sel_regi.setTenantId(tenantId);
		return registryMapper.selectRegistry(sel_regi);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cmbc.devops.service.RegistryService#getRegistryMster()
	 */
	@Override
	public JSONArray getRegistryMster() {
		JSONArray ja = new JSONArray();
		List<Host> hostList = new ArrayList<Host>();
		try {
			hostList = hostMapper.selectHostList(Type.HOST.REGISTRY.ordinal());
		} catch (Exception e) {
			logger.error("select host list failed", e);
			return null;
		}
		if (hostList != null) {
			for (Host host : hostList) {
				JSONObject jo = new JSONObject();
				jo.put("hostName", host.getHostName());
				jo.put("hostIp", host.getHostIp());
				jo.put("hostId", host.getHostId());
				ja.add(jo);
			}
		}
		return ja;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cmbc.devops.service.RegistryService#checkImageIsExist(net.sf.json.
	 * JSONObject)
	 */
	@Override
	public String checkImageIsExist(JSONObject json_object) throws Exception {
		String array = json_object.getString("array");
		String[] registry_ids = array.split(",");
		String result = "";
		for (String retistry_id : registry_ids) {
			RegImage regImage = new RegImage();
			regImage.setRegistryId(Integer.parseInt(retistry_id));
			List<RegImage> regimg_list = regImageMapper.selectAll(regImage);
			if (regimg_list.size() > 0) {// 如果存在镜像，返回对象中添加此仓库的名称
				for (RegImage single_regimg : regimg_list) {
					Image image = imageMapper.selectByPrimaryKey(single_regimg.getImageId());
					/** @bug 当仓库中存在已发布或者已制作的镜像的情况下，不允许删除仓库 **/
					if ((image.getImageStatus() == Status.IMAGE.NORMAL.ordinal())
							|| (image.getImageStatus() == Status.IMAGE.MAKED.ordinal())) {// 判断此镜像是否可用,如果可用，仓库不能删除
						Registry registry = new Registry();
						registry.setRegistryId(Integer.parseInt(retistry_id));
						registry = registryMapper.selectRegistry(registry);
						result += ("<i class=\"fa fa-university\"></i>" + registry.getRegistryName()
								+ ": 存在镜像[<font color=\"red\"><b>" + image.getImageName() + ":" + image.getImageTag()
								+ "</b></font>]<br>");
					}
				}
			}
		}
		return result;
	}

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月6日 11:15
	 * @description 将原来返回json字符串的方法修改为GridBean的方式
	 */
	@Override
	public GridBean getOnePageRegistrys(int userId, int pagenumber, int pagesize, RegistryModel registryModel)
			throws Exception {

		PageHelper.startPage(pagenumber, pagesize);
		Registry registry = new Registry();
		registry.setRegistryName(registryModel.getSearch());
		registry.setTenantId(registryModel.getTenantId());
		registry.setRegistryStatus((byte) Status.REGISTRY.NORMAL.ordinal());
		List<Registry> registries = registryMapper.selectAll(registry);
		int totalpage = ((Page<?>) registries).getPages();
		Long totalNum = ((Page<?>) registries).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), registries);
		return gridBean;
	}

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月6日 11:15
	 * @description 将原来返回json字符串的方法修改为GridBean的方式
	 */
	@Override
	public GridBean getOnePageRegistrysWithIP(int userId, int pagenumber, int pagesize,
			RegistryWithIPModel registryWithIPModel) throws Exception {

		PageHelper.startPage(pagenumber, pagesize);
		RegistryWithIP registrywithip = new RegistryWithIP();
		registrywithip.setTenantId(registryWithIPModel.getTenantId());
		registrywithip.setRegistryStatus((byte) Status.REGISTRY.NORMAL.ordinal());
		List<RegistryWithIP> registries = registryMapper.selectAllWithIP(registrywithip);
		int totalpage = ((Page<?>) registries).getPages();
		Long totalNum = ((Page<?>) registries).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), registries);
		return gridBean;
	}

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年10月8日 11:25
	 * @description 添加对于查询的支持，查询的关键字保存在仓库名称属性中
	 */
	@Override
	public GridBean getSearchRegistrysWithIP(int userId, int pagenumber, int pagesize,
			RegistryWithIPModel registryWithIPModel) throws Exception {

		PageHelper.startPage(pagenumber, pagesize);
		RegistryWithIP registrywithip = new RegistryWithIP();
		registrywithip.setRegistryStatus((byte) Status.REGISTRY.NORMAL.ordinal());
		registrywithip.setRegistryName(registryWithIPModel.getSearch());
		registrywithip.setTenantId(registryWithIPModel.getTenantId());
		List<RegistryWithIP> registries = registryMapper.selectAllWithIP(registrywithip);
		int totalpage = ((Page<?>) registries).getPages();
		Long totalNum = ((Page<?>) registries).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), registries);
		return gridBean;
	}

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月6日 11:15
	 * @description 查询仓库ID下所有的镜像信息，将原来返回镜像列表
	 */
	@Override
	public List<RegistrySlaveImage> getImagesByRegistryId(Integer tenantId, Integer registry_id) throws Exception {
		RegIdImageType redIdImageType = new RegIdImageType();
		redIdImageType.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
		redIdImageType.setTenantId(tenantId);
		redIdImageType.setRegistryId(registry_id);
		List<RegistrySlaveImage> regislaveimages = (ArrayList<RegistrySlaveImage>) registryMapper
				.selectAllImageInRegistry(redIdImageType);
		/* 查询所有应用类型，循环填充到返回的列表中 */
		App app = new App();
		app.setAppStatus((byte) Status.APPLICATION.NORMAL.ordinal());
		/* 向数据库中查询所有的应用类型 */
		List<App> app_list = new ArrayList<App>();
		try {
			app_list = appMapper.selectAll();
		} catch (Exception e) {
			logger.error("select all application list failed", e);
			return null;
		}
		List<RegistrySlaveImage> new_regislaveimages = new ArrayList<RegistrySlaveImage>();

		/* 向元素中添加应用名称信息 */
		for (RegistrySlaveImage reg_slave_img : regislaveimages) {
			if (reg_slave_img.getAppId() == null) {
				reg_slave_img.setAppName("");
			} else {
				String app_name = searchAppName(reg_slave_img.getAppId() + "", app_list);
				reg_slave_img.setAppName(app_name);
			}
			new_regislaveimages.add(reg_slave_img);
		}

		regislaveimages.clear();
		for (RegistrySlaveImage reg_slave_img : new_regislaveimages) {
			regislaveimages.add(reg_slave_img);
		}

		return regislaveimages;
	}

	/**
	 * @author youngtsinglin
	 * @throws Exception
	 * @time 2015年9月6日 11:15
	 * @description 查询仓库ID下所有的镜像信息，将原来返回json字符串的方法修改为GridBean的方式
	 */
	@Override
	public GridBean getOnePageRegistrysSlaveImages(int userId, int pagenumber, int pagesize,
			RegIdImageTypeModel regIdImageTypeModel, int registryid, byte imagestatus) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);

		/* 通过仓库的ID查询所有在仓库对应的镜像ID列表 */
		RegImage reg_image = new RegImage();
		reg_image.setRegistryId(registryid);
		List<RegImage> regimg_list = regImageMapper.selectAll(reg_image);

		/* 定义返回的pages类型 */
		Page<RegistrySlaveImage> return_regslaveimages = new Page<RegistrySlaveImage>();

		/* 查询所有应用类型，循环填充到返回的列表中 */
		/* 向数据库中查询所有的应用类型 */
		List<App> app_list = new ArrayList<App>();
		try {
			app_list = appMapper.selectAll();
		} catch (Exception e) {
			logger.error("select all application list failed", e);
			return null;
		}

		if (null != regimg_list) {
			for (RegImage single_regimg : regimg_list) {
				RegistrySlaveImage regslave_img = new RegistrySlaveImage();
				Integer image_id = single_regimg.getImageId();
				Image image = imageMapper.selectByPrimaryKey(image_id);
				/* 复制全部image的原始信息 */
				regslave_img.setImageId(image.getImageId());
				regslave_img.setImageUuid(image.getImageUuid());
				regslave_img.setImageStatus((byte) image.getImageStatus());
				regslave_img.setImageName(image.getImageName());
				regslave_img.setImageTag(image.getImageTag());
				regslave_img.setImageSize(image.getImageSize());
				regslave_img.setImageType(image.getImageType());
				regslave_img.setImageDesc(image.getImageDesc());
				Integer application_id = image.getAppId();
				if (application_id != null) {
					regslave_img.setAppId(image.getAppId());
					String app_name = searchAppName(application_id + "", app_list);
					regslave_img.setAppName(app_name);
				}
				regslave_img.setImagePort(image.getImagePort());
				regslave_img.setImageCreatetime(image.getImageCreatetime());
				regslave_img.setImageCreator(image.getImageCreator());
				return_regslaveimages.add(regslave_img);
			}
		}

		int totalPage = ((Page<?>) regimg_list).getPages();
		Long totalNum = ((Page<?>) regimg_list).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalPage, totalNum.intValue(), return_regslaveimages);
		return gridBean;
	}

	public String searchAppName(String app_id, List<App> app_list) {
		String app_name = "";
		for (App app : app_list) {
			if (app.getAppId() == (Integer.parseInt(app_id))) {
				app_name = app.getAppName();
			}
		}
		return app_name;
	}

	@Override
	public List<Registry> listRegistrysByHostId(Integer hostId) throws Exception {
		return registryMapper.selectRegistryByHostId(hostId);
	}

	@Override
	public int SyncRegistryImages(String RegistryNodeIP, String ServerPort) {
		return 0;
	}

	@Override
	public Registry getByImage(int imageId) throws Exception {
		RegImage regImage = new RegImage();
		regImage.setImageId(imageId);
		List<RegImage> regImageList = this.regImageMapper.selectAll(regImage);
		if (null == regImageList || regImageList.isEmpty()) {
			return null;
		} else {
			Integer regId = regImageList.get(0).getRegistryId();
			Registry record = new Registry();
			record.setRegistryId(regId);
			return this.registryMapper.selectRegistry(record);
		}
	}

	@Override
	public List<Registry> loadAllRegistries(Registry record) throws Exception {
		return registryMapper.selectAll(record);
	}

	@Override
	public GridBean advancedSearchRegi(Integer userId, int pagenumber, int pagesize, RegistryModel registryModel,
			JSONObject json_object) throws Exception {
		PageHelper.startPage(pagenumber, pagesize);
		RegistryWithIP registrywithip = new RegistryWithIP();
		registrywithip.setRegistryStatus((byte) Status.REGISTRY.NORMAL.ordinal());
		/* 添加所有的查询信息 */
		/* 获取用户填写的各项查询条件 */
		String[] params = json_object.getString("params").split(",");
		String[] values = json_object.getString("values").split(",");

		/* 遍历填充各项查询条件 */
		for (int i = 0; i < params.length; i++) {
			switch (params[i].trim()) {
			/* 仓库名称 */
			case "1":
				registrywithip.setRegistryName(values[i].trim());
				break;
			/* 仓库端口 */
			case "2":
				registrywithip.setRegistryPort(Integer.parseInt(values[i].trim()));
				break;
			/* 描述信息 */
			case "3":
				registrywithip.setRegistryDesc(values[i].trim());
				break;
			/* 主机地址 */
			case "4":
				registrywithip.setHostIP(values[i].trim());
				break;
			/* 用户名称 */
			case "5":
				registrywithip.setCreatorName(values[i].trim());
				break;
			default:
				break;
			}
		}

		/** @date:2016年3月29日 添加租户维度 */
		registrywithip.setTenantId(registryModel.getTenantId());

		List<RegistryWithIP> registries = registryMapper.selectAllWithIP(registrywithip);
		int totalpage = ((Page<?>) registries).getPages();
		Long totalNum = ((Page<?>) registries).getTotal();

		GridBean gridBean = new GridBean(pagenumber, totalpage, totalNum.intValue(), registries);
		return gridBean;
	}

}
