/**
 * 
 */
package com.cmbc.devops.manager;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.client.HttpClient;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.HttpClientConstant;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.dao.RegImageMapper;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.RegImage;
import com.cmbc.devops.entity.Registry;
import com.cmbc.devops.entity.RegistrySlaveImage;
import com.cmbc.devops.entity.SyncRegiResult;
import com.cmbc.devops.model.RegIdImageTypeModel;
import com.cmbc.devops.model.RegistryModel;
import com.cmbc.devops.model.RegistryWithIPModel;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.ImageService;
import com.cmbc.devops.service.RegistryService;
import com.cmbc.devops.util.CommandExcutor;
import com.cmbc.devops.util.SSH;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * date：2015年8月25日 下午2:29:42 project name：cmbc-devops-web
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：RegistryManager.java description：
 */
@Component
public class RegistryManager {
	private static final Logger LOGGER = Logger.getLogger(RegistryManager.class);
	@Resource
	private RegistryService registryService;
	@Resource
	private ImageService imageService;
	@Resource
	private HttpClient httpClient;
	@Resource
	private RegImageMapper regImageMapper;
	@Autowired
	private HostService hostService;

	/**
	 * @return JSONObject
	 * @version 1.0 2015年11月17日
	 */
	public JSONObject getRegistry(Registry record) {
		try {
			return registryService.getRegistry(record);
		} catch (Exception e) {
			LOGGER.error("查询仓库服务器失败。", e);
		}
		return null;
	}

	public List<RegistrySlaveImage> getImagesViaRegistryId(Integer tenantId, Integer registry_id) {
		try {
			return registryService.getImagesByRegistryId(tenantId, registry_id);
		} catch (Exception e) {
			LOGGER.error("查询仓库服务器失败。", e);
		}
		return null;
	}

	/**
	 * @return JSONObject
	 * @version 1.0 2015年11月17日
	 * @description 获取仓库类型的节点
	 */
	public JSONArray getRegistryMster() {
		try {
			return registryService.getRegistryMster();
		} catch (Exception e) {
			LOGGER.error("获取仓库类型的节点失败。", e);
		}
		return null;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月17日 10:24
	 * @description 增加了对于仓库下包含镜像所有
	 */
	public GridBean registrySlaveImages(Integer userId, int pagenumber, int pagesize,
			RegIdImageTypeModel regIdImageTypeModel, int registryid, byte imagestatus) {
		try {
			GridBean grid_bean = registryService.getOnePageRegistrysSlaveImages(userId, pagenumber, pagesize,
					regIdImageTypeModel, registryid, imagestatus);
			return grid_bean;
		} catch (Exception e) {
			LOGGER.error("查询仓库下所有的镜像列表失败。", e);
		}
		return null;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月17日 11:36
	 * @description 将原来返回字符串的方法修改为GridBean的方式(增加IP显示替换功能)
	 */
	public GridBean registryListSearch(Integer userId, int pagenumber, int pagesize,
			RegistryWithIPModel registryWithIPModel) {
		try {
			return registryService.getSearchRegistrysWithIP(userId, pagenumber, pagesize, registryWithIPModel);
		} catch (Exception e) {
			LOGGER.error("查询仓库列表详细信息失败。", e);
		}
		return null;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月17日 10:24
	 * @description 将原来返回字符串的方法修改为GridBean的方式(增加IP显示替换功能)
	 */
	public GridBean registryListWithIP(Integer userId, int pagenumber, int pagesize,
			RegistryWithIPModel registryWithIPModel) {
		try {
			return registryService.getOnePageRegistrysWithIP(userId, pagenumber, pagesize, registryWithIPModel);
		} catch (Exception e) {
			LOGGER.error("查询仓库列表详细信息失败。", e);
		}
		return null;
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年11月17日 10:24
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean registryList(Integer userId, int pagenumber, int pagesize, RegistryModel registryModel) {
		try {
			return registryService.getOnePageRegistrys(userId, pagenumber, pagesize, registryModel);
		} catch (Exception e) {
			LOGGER.error("查询仓库列表详细信息失败。", e);
		}
		return null;
	}

	/* 检查用户填写的仓库名称是否已经在数据库中存在 */
	public Result duplicateName(JSONObject json_object) {
		String registry_name = json_object.getString("registry_name");
		Registry registry = new Registry();
		registry.setRegistryName(registry_name);
		try {
			registry = registryService.loadRegistry(registry);
		} catch (Exception e) {
			LOGGER.error("load registry by registryName[" + registry_name + "] falied!", e);
			return new Result(false, "数据库查询仓库信息失败！");
		}

		if (registry != null) {
			return new Result(false, "仓库名称[<font color=\"red\"><b>" + registry_name + "</b></font>]已经存在，不能重复创建。");
		} else {
			return new Result(true, "仓库名称[<font color=\"red\"><b>" + registry_name + "</b></font>]已经不存在，可以使用。");
		}
	}

	/* 检查注册主机的可达性情况，以及是否在数据库中已经存在 */
	public Result reachRegiHost(JSONObject json_object) {
		HttpClient httpClient = new HttpClient();
		String regihost_ip = json_object.getString("registryIpaddr");
		String regihost_port = json_object.getString("registryPort");
		String regihost_id = json_object.getString("registryHost");
		/* 在数据库中查询是否存在同样的主机和端口的仓库信息 */
		String exist_message = "";
		Registry registry = new Registry();
		registry.setHostId(Integer.parseInt(regihost_id));
		registry.setRegistryPort(Integer.parseInt(regihost_port));
		try {
			registry = registryService.loadRegistry(registry);
		} catch (Exception e) {
			LOGGER.error("load registry by registryid[" + regihost_id + "] falied!", e);
			return new Result(false, "数据库查询仓库信息失败！");
		}

		/* 连接仓库所在主机，判断可达性是否完好 */
		HashMap<String, Object> return_map = (HashMap<String, Object>) httpClient.doGet_DOCKER(null,
				HttpClientConstant.getQueryRegistry(regihost_ip, regihost_port));
		/* 判断结果返回是否为成功 */
		if ((return_map.get("success").equals(true)) && (registry == null)) {
			return new Result(true, "<i class=\"fa fa-link\"></i>仓库[<font color=\"blue\"><b>" + regihost_ip
					+ "</b></font>:<font color=\"blue\"><b>" + regihost_port + "]</b></font>可以使用。");
		} else if (registry != null) {
			exist_message = "<i class=\"fa fa-exclamation-triangle\">&nbsp;</i>仓库[<font color=\"blue\"><b>"
					+ regihost_ip + "</b></font>:<font color=\"blue\"><b>" + regihost_port
					+ "]</b></font>在数据库中已经存在，不能重复创建。";
			return new Result(false, exist_message);
		} else {
			return new Result(false, "<i class=\"fa fa-chain-broken\"></i>仓库[<font color=\"red\"><b>" + regihost_ip
					+ "</b></font>:<font color=\"red\"><b>" + regihost_port + "</b></font>]连接无效。");
		}

	}

	/**
	 * @author yangqinglin
	 * @description 将仓库服务器的镜像数据同步到数据库中
	 */
	public Result syncBatchRegiSlaveImg(JSONObject json_object) {
		/* 从调用端获取仓库的ID、名称、地址、端口等数组 */
		String[] registry_ids = json_object.getString("registry_ids").split(",");
		String[] registry_names = json_object.getString("registry_names").split(",");
		String[] registry_ipaddrs = json_object.getString("registry_ipaddrs").split(",");
		String[] registry_ports = json_object.getString("registry_ports").split(",");

		/* 初始化返回的消息信息 */
		String return_message = "";

		for (int count = 0, length = registry_ids.length; count < length; count++) {
			/* 初始化对于镜像数据表处理的HashMap对象，String */
			Map<String, Image> DB_OperHashMap = new HashMap<String, Image>();

			/* 获取仓库中镜像的列表 */

			/* 获取仓库中镜像的列表 */
			SyncRegiResult syncregi_result = getRegiSlaveImages(registry_ipaddrs[count], registry_ports[count]);
			String Echo = syncregi_result.getEcho();
			List<String> slave_img_list = syncregi_result.getResultList();

			if (Echo.trim().equalsIgnoreCase("DISCONNECT")) {
				return_message = return_message + "仓库[<b>" + registry_names[count] + "</b>](" + registry_ipaddrs[count]
						+ ":" + registry_ports[count] + ")获取数据失败，请检查网络连接！\n";
			} else if (Echo.trim().equalsIgnoreCase("EMPTY")) {
				return_message = return_message + "仓库[<b>" + registry_names[count] + "</b>](" + registry_ipaddrs[count]
						+ ":" + registry_ports[count] + ")仓库没有可用镜像，请检查主机！\n";
			} else {
				/* 分别获取每个镜像的版本数据 */
				Map<String, Map<String, String>> regi_imgs_info = getRegiMappingImgs(registry_ipaddrs[count],
						registry_ports[count], slave_img_list);

				/* 定义两个链表分别保存数据库和在线信息 */
				List<Image> online_image_list = new ArrayList<Image>();
				List<Image> database_image_list = new ArrayList<Image>();

				for (Entry<String, Map<String, String>> img_entry : regi_imgs_info.entrySet()) {

					String img_name = img_entry.getKey();
					Map<String, String> img_tags = img_entry.getValue();
					/*
					 * dop_image表中的IMAGE_NAME字段，格式如下：192.168.1.117:5000/registry
					 */
					String IMAGE_NAME = registry_ipaddrs[count] + ":" + registry_ports[count] + "/" + img_name;

					/* 从数据库中获取某个镜像所有的版本列表信息 */
					Image database_image = new Image();
					database_image.setImageName(IMAGE_NAME);
					database_image.setImageStatus(null);
					/* 向数据库镜像数据链表中插入新查询的数据库结果 */
					try {
						database_image_list.addAll((List<Image>) imageService.selectActiveAllImages(database_image));
					} catch (Exception e) {
						LOGGER.error("select active all images falied!", e);
						return new Result(false, "查询镜像列表失败！");
					}

					for (Entry<String, String> tag_entry : img_tags.entrySet()) {
						Image online_image = new Image();
						String tag_name = tag_entry.getKey();
						/* 写入镜像的标签内容 */
						String IMAGE_TAG = tag_name;
						online_image.setImageTag(IMAGE_TAG);

						String tag_img_id = tag_entry.getValue();
						/* 取整个UUID的前12为作为IMAGE_UUID保存入数据库 */
						String IMAGE_UUID = tag_img_id.substring(0, 12);
						online_image.setImageUuid(IMAGE_UUID);
						online_image.setImageName(IMAGE_NAME);
						online_image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
						online_image.setAppId(null);

						/* 向链表中插入网络查询的镜像版本对象 */
						online_image_list.add(online_image);
					}
				}
				/* 比较连个链表，生成对于数据库操作的映射表 */
				DB_OperHashMap = CompareOnlineDBImageList(online_image_list, database_image_list);

				/* 根据多次循环遍历的结果，确定对于数据库的操作 */
				Iterator<String> img_iter = DB_OperHashMap.keySet().iterator();
				while (img_iter.hasNext()) {
					/* 获取哈希表的key值 */
					String key = img_iter.next();

					if (key.contains("INSERT")) {
						/* 当类型为插入时，对数据库进行插入操作 */
						Integer insert_imgid = imageService.create(DB_OperHashMap.get(key));
						/* 插入仓库与镜像的对应表 */
						RegImage reg_img = new RegImage();
						reg_img.setRegistryId(Integer.parseInt(registry_ids[count]));
						reg_img.setImageId(insert_imgid);
						try {
							regImageMapper.insert(reg_img);
						} catch (Exception e) {
							LOGGER.error("insert image falied!", e);
							return new Result(false, "更新数据库信息失败！");
						}

					} else if (key.contains("DELETE")) {
						/* 当类型为删除时，对数据库进行删除操作 */
						imageService.delete(DB_OperHashMap.get(key).getImageId());
						/* 在仓库与镜像对应表中删除对应元素 */
					}
				}

				return_message = return_message + "仓库[" + registry_names[count] + "]同步数据成功！\n";
			}

		}

		/* 根据HTTP获取的内容与数据库进行同步 */
		return new Result(true, return_message);

	}

	/**
	 * @author yangqinglin
	 * @description 更新同步方式，将通过HTTP的方式修改为远程SSH查询
	 * @date 2016-05-10
	 **/
	public Result syncDBwithRegiInfo(JSONObject json_object) {
		String regihost_ip = json_object.getString("registryIpaddr");
		// String regihost_port = json_object.getString("registryPort");
		String registry_id = json_object.getString("registryId");
		String registry_name = json_object.getString("registryName");
		Integer registryHostId = json_object.getInteger("registryHostId");
		/* 初始化对于镜像数据表处理的HashMap对象，String */
		Map<String, Image> DB_OperHashMap = new HashMap<String, Image>();

		/* 初始化返回的消息信息 */
		String return_message = "";

		/** 获取远程仓库主机的信息 */
		Host regiHost = new Host();
		regiHost.setHostId(registryHostId);
		try {
			regiHost = hostService.getHost(regiHost);
		} catch (Exception e1) {
			LOGGER.error("select registry host info falied!", e1);
			return new Result(false, "获取仓库主机(IP:" + regihost_ip + ")信息失败，请检查主机连接。");
		}

		SSH regiSsh = CommandExcutor.getSsh(regiHost.getHostIp(), regiHost.getHostUser(), regiHost.getHostPwd());
		try {
			regiSsh.connect();
			/* 获取远程仓库主机上的镜像列表信息 */
			String regiImgInfo = regiSsh.executeWithResult(HttpClientConstant.CONSTANTMAP.get("QUERY_REGICMD"));
			ArrayList<Image> onLineRegiImageList = convertStrToList(regiImgInfo);

			List<Image> dbImageList = new ArrayList<Image>();
			/* 向数据库镜像数据链表中插入新查询的数据库结果 */
			try {
				Image selImage = new Image();
				dbImageList.addAll((List<Image>) imageService.selectActiveAllImages(selImage));
			} catch (Exception e) {
				LOGGER.error("select active all images falied!", e);
				return new Result(false, "从数据库中查询镜像信息失败，请检查数据库链接！");
			}

			/* 比较两个链表，生成对于数据库操作的映射表 */
			DB_OperHashMap = CompareOnlineDBImageList(onLineRegiImageList, dbImageList);

			/* 根据多次循环遍历的结果，确定对于数据库的操作 */
			Iterator<String> img_iter = DB_OperHashMap.keySet().iterator();
			ArrayList<Integer> deleteImgIdList = new ArrayList<Integer>();

			while (img_iter.hasNext()) {
				/* 获取哈希表的key值 */
				String key = img_iter.next();

				if (key.contains("INSERT")) {
					/* 当类型为插入时，对数据库进行插入操作 */
					Integer insert_imgid = imageService.create(DB_OperHashMap.get(key));
					/* 插入仓库与镜像的对应表 */
					RegImage reg_img = new RegImage();
					reg_img.setRegistryId(Integer.parseInt(registry_id));
					reg_img.setImageId(insert_imgid);
					try {
						regImageMapper.insert(reg_img);
					} catch (Exception e) {
						LOGGER.error("insert  reimage  falied!", e);
						return new Result(false, "数据库增加镜像信息失败！");
					}

				} else if (key.contains("DELETE")) {
					/* 当类型为删除时，对数据库进行删除操作 */
					// imageService.delete(DB_OperHashMap.get(key).getImageId());
					deleteImgIdList.add(DB_OperHashMap.get(key).getImageId());
					/* 在仓库与镜像对应表中删除对应元素 */
				}
			}

			/* 判断需要删除的镜像链表是否为空 */
			if (!deleteImgIdList.isEmpty()) {
				imageService.batchDeleteImages(deleteImgIdList);
			}
		} catch (SocketException e1) {
			LOGGER.error("batch delete image id list falied!", e1);
			return new Result(false, "连接仓库主机失败，报SocketException异常。");
		} catch (IOException e1) {
			LOGGER.error("batch delete image id list falied!", e1);
			return new Result(false, "连接仓库主机失败，报IOException异常。");
		} catch (Exception e) {
			LOGGER.error("batch delete image id list falied!", e);
			return new Result(false, "批量删除镜像信息，报Exception异常。");
		}

		return_message = return_message + "仓库[" + registry_name + "]同步数据成功！";
		/* 根据HTTP获取的内容与数据库进行同步 */
		return new Result(true, return_message);

	}

	/** 将从仓库主机上面拿到的信息转化为镜像链表 */
	private ArrayList<Image> convertStrToList(String regiImgInfo) {
		ArrayList<Image> imageList = new ArrayList<Image>();
		String[] imgArray = regiImgInfo.split("\n");
		if (imgArray.length > 1) {
			int arrayLength = imgArray.length;
			for (int count = 1; count < arrayLength; count++) {
				Image sinImg = new Image();
				String[] imgInfo = imgArray[count].split(" ");
				sinImg.setImageName(imgInfo[0]);
				sinImg.setImageTag(imgInfo[1]);
				sinImg.setImageUuid(imgInfo[2]);
				sinImg.setImageSize(imgInfo[3]);
				sinImg.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
				sinImg.setImageCreatetime(new Date());
				sinImg.setAppId(null);
				imageList.add(sinImg);
			}
		}
		return imageList;
	}

	/**
	 * @author yangqinglin
	 * @description 将仓库服务器的镜像数据同步到数据库中
	 */
	public Result sycDBandRegiImgInfo(JSONObject json_object) {
		String regihost_ip = json_object.getString("registryIpaddr");
		String regihost_port = json_object.getString("registryPort");
		String registry_id = json_object.getString("registryId");
		String registry_name = json_object.getString("registryName");
		/* 初始化对于镜像数据表处理的HashMap对象，String */
		Map<String, Image> DB_OperHashMap = new HashMap<String, Image>();

		/* 初始化返回的消息信息 */
		String return_message = "";

		/* 获取仓库中镜像的列表 */
		SyncRegiResult syncregi_result = getRegiSlaveImages(regihost_ip, regihost_port);
		String Echo = syncregi_result.getEcho();
		List<String> slave_img_list = syncregi_result.getResultList();

		if (Echo.trim().equalsIgnoreCase("DISCONNECT")) {
			return new Result(false,
					"仓库[<b>" + registry_name + "</b>](" + regihost_ip + ":" + regihost_port + ")获取数据失败，请检查网络连接！");
		} else if (Echo.trim().equalsIgnoreCase("EMPTY")) {
			return new Result(false,
					"仓库[<b>" + registry_name + "</b>](" + regihost_ip + ":" + regihost_port + ")仓库没有可用镜像，请检查主机！");
		} else {
			/* 分别获取每个镜像的版本数据 */
			Map<String, Map<String, String>> regi_imgs_info = getRegiMappingImgs(regihost_ip, regihost_port,
					slave_img_list);

			/* 定义两个链表分别保存数据库和在线信息 */
			List<Image> online_image_list = new ArrayList<Image>();
			List<Image> database_image_list = new ArrayList<Image>();

			for (Entry<String, Map<String, String>> img_entry : regi_imgs_info.entrySet()) {

				String img_name = img_entry.getKey();
				Map<String, String> img_tags = img_entry.getValue();
				/* dop_image表中的IMAGE_NAME字段，格式如下：192.168.1.117:5000/registry */
				String IMAGE_NAME = regihost_ip + ":" + regihost_port + "/" + img_name;

				/* 从数据库中获取某个镜像所有的版本列表信息 */
				Image database_image = new Image();
				database_image.setImageName(IMAGE_NAME);
				/* 向数据库镜像数据链表中插入新查询的数据库结果 */
				try {
					database_image_list.addAll((List<Image>) imageService.selectActiveAllImages(database_image));
				} catch (Exception e) {
					LOGGER.error("select active all images falied!", e);
					return new Result(false, "从数据库中查询镜像信息失败，请检查数据库链接！");
				}

				for (Entry<String, String> tag_entry : img_tags.entrySet()) {
					Image online_image = new Image();
					String tag_name = tag_entry.getKey();
					/* 写入镜像的标签内容 */
					String IMAGE_TAG = tag_name;
					online_image.setImageTag(IMAGE_TAG);

					String tag_img_id = tag_entry.getValue();
					/* 取整个UUID的前12为作为IMAGE_UUID保存入数据库 */
					String IMAGE_UUID = tag_img_id.substring(0, 12);
					online_image.setImageUuid(IMAGE_UUID);
					online_image.setImageName(IMAGE_NAME);
					online_image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
					online_image.setAppId(null);

					/* 向链表中插入网络查询的镜像版本对象 */
					online_image_list.add(online_image);
				}
			}
			/* 比较两个链表，生成对于数据库操作的映射表 */
			DB_OperHashMap = CompareOnlineDBImageList(online_image_list, database_image_list);

			/* 根据多次循环遍历的结果，确定对于数据库的操作 */
			Iterator<String> img_iter = DB_OperHashMap.keySet().iterator();
			while (img_iter.hasNext()) {
				/* 获取哈希表的key值 */
				String key = img_iter.next();

				if (key.contains("INSERT")) {
					/* 当类型为插入时，对数据库进行插入操作 */
					Integer insert_imgid = imageService.create(DB_OperHashMap.get(key));
					/* 插入仓库与镜像的对应表 */
					RegImage reg_img = new RegImage();
					reg_img.setRegistryId(Integer.parseInt(registry_id));
					reg_img.setImageId(insert_imgid);
					try {
						regImageMapper.insert(reg_img);
					} catch (Exception e) {
						LOGGER.error("insert  reimage  falied!", e);
						return new Result(false, "数据库增加镜像信息失败！");
					}

				} else if (key.contains("DELETE")) {
					/* 当类型为删除时，对数据库进行删除操作 */
					imageService.delete(DB_OperHashMap.get(key).getImageId());
					/* 在仓库与镜像对应表中删除对应元素 */
				}
			}

			return_message = return_message + "仓库[" + registry_name + "]同步数据成功！";
		}

		/* 根据HTTP获取的内容与数据库进行同步 */
		return new Result(true, return_message);
	}

	/**
	 * @author youngtsinglin
	 * @description 增加比较两个查询结果链表的处理方法
	 */
	private Map<String, Image> CompareOnlineDBImageList(List<Image> online_list, List<Image> database_list) {
		int counter = 0;
		/* 保存两个链表之间的相对差集，以便后续数据库操作。 */
		HashMap<String, Image> oper_subtract_map = new HashMap<String, Image>();
		boolean match_sign = false;

		/* 如果http查询的链表比数据库多的情况下，则考虑插入数据库操作 */
		for (Image online_img : online_list) {
			match_sign = false;
			/* 比对镜像UUID、镜像名称、镜像标签是否一致 */
			for (Image db_img : database_list) {
				if ((db_img.getImageUuid().trim().equalsIgnoreCase(online_img.getImageUuid().trim()))
						&& (db_img.getImageName().trim().equalsIgnoreCase(online_img.getImageName().trim()))
						&& (db_img.getImageTag().trim().equalsIgnoreCase(online_img.getImageTag().trim()))) {
					match_sign = true;
				}
			}
			/* 遍历查询之后，没有在数据库表里找到匹配记录 */
			if (!match_sign) {
				oper_subtract_map.put("INSERT_" + counter, online_img);
				counter++;
			}
		}
		/* 如果数据库http比查询的链表多的情况下，则考虑删除数据库操作 */
		for (Image db_img : database_list) {
			match_sign = false;
			for (Image online_img : online_list) {
				if ((online_img.getImageUuid().trim().equalsIgnoreCase(db_img.getImageUuid().trim()))
						&& (online_img.getImageName().trim().equalsIgnoreCase(db_img.getImageName().trim()))
						&& (online_img.getImageTag().trim().equalsIgnoreCase(db_img.getImageTag().trim()))) {
					match_sign = true;
				}
			}
			/* 遍历查询之后，没有在在线查询结果里找到匹配记录 */
			if (!match_sign) {
				oper_subtract_map.put("DELETE_" + counter, db_img);
				counter++;
			}
		}

		return oper_subtract_map;
	}

	public static Map<String, Map<String, String>> getRegiMappingImgs(String IP, String Port,
			List<String> SlaveImgList) {
		Map<String, Map<String, String>> return_mapping = new HashMap<String, Map<String, String>>();
		HttpClient httpClient = new HttpClient();

		for (String ImgName : SlaveImgList) {
			Map<String, Object> return_image_map = (HashMap<String, Object>) httpClient.doGet_DOCKER(null,
					HttpClientConstant.getRegistryImgTag(IP, Port, ImgName));
			/* 判断结果返回是否为成功 */
			if (return_image_map.get("success").equals(true)) {
				Map<String, String> imgs_hashmap = new HashMap<String, String>();
				JsonNode json_image_node = (JsonNode) return_image_map
						.get(HttpClientConstant.CONSTANTMAP.get("RESULT_KEY"));
				Iterator<Entry<String, JsonNode>> iter_json = json_image_node.fields();
				while (iter_json.hasNext()) {
					Entry<String, JsonNode> temp_node = iter_json.next();
					imgs_hashmap.put(temp_node.getKey(), temp_node.getValue().asText());
				}
				return_mapping.put(ImgName, imgs_hashmap);
			}
		}

		return return_mapping;
	}

	/**
	 * @param IP
	 *            String
	 * @param port
	 *            String
	 * @description 通过IP地址和端口，链接仓库服务器，获取服务器上所有的镜像信息
	 * @DATE 2015年10月28日 添加对于cmbc/dcbs-demo-dm情况的处理
	 **/
	public static SyncRegiResult getRegiSlaveImages(String IP, String port) {
		List<String> RegiSlaveImgList = new ArrayList<String>();
		HttpClient httpClient = new HttpClient();
		SyncRegiResult syncregi_result = new SyncRegiResult();
		String Echo = "";

		/* 执行HTTP查询仓库下镜像的请求 */
		HashMap<String, Object> return_map = (HashMap<String, Object>) httpClient.doGet_DOCKER(null,
				HttpClientConstant.getQueryRegistry(IP, port));
		/* 判断结果返回是否为成功 */
		if (return_map.get("success").equals(true)) {
			JsonNode json_node = (JsonNode) return_map.get(HttpClientConstant.CONSTANTMAP.get("RESULT_KEY"));
			/** 判断返回的结果数量，进行判断是否为空的数据库 */
			JsonNode number_results = json_node.path("num_results");
			if (number_results != null) {
				/* 获取保存的镜像数量 */
				int number_images = number_results.asInt();
				/* 仓库非空，存在多个镜像 */
				if (number_images > 0) {
					JsonNode results_node = json_node.path("results");
					Iterator<JsonNode> registry_nodes = results_node.elements();
					while (registry_nodes.hasNext()) {
						JsonNode single_node = registry_nodes.next();
						JsonNode name_node = single_node.path("name");
						String name_content = name_node.asText();
						/*
						 * 取得类似于dingmw、cmbc、dmbc等字段
						 * ，例如：library/dingmw，可能出现：cmbc/dcbs-demo-dm情况的镜像
						 */
						RegiSlaveImgList.add(name_content);
						Echo = "FETCH";
					}
					/* 当仓库中镜像数量为0的情况下，进行空判断 */
				} else {
					/* 将返回镜像列表置为空 */
					RegiSlaveImgList = null;
					Echo = "EMPTY";
					LOGGER.error("Query Registry Location:" + IP + ":" + port + " is Empty.");
				}
			}
		} else {
			/* 将返回镜像列表置为空 */
			RegiSlaveImgList = null;
			Echo = "DISCONNECT";
			LOGGER.error("Query Slave Images Info error: Registry(" + IP + ":" + port + "), query failed.");
		}
		syncregi_result.setEcho(Echo);
		syncregi_result.setResultList(RegiSlaveImgList);

		return syncregi_result;
	}

	public Result createRegistry(JSONObject json_object) {
		try {
			/* 判断远程的服务器是否已经安装了docker程序 */
			/* 通过SSH链接远程服务器，进行创建私有仓库的操作 */
			/* 根据SSH创建仓库的结果，确定是否对数据库进行添加处理 */
			int result = registryService.createRegistry(json_object);

			if (result == 1) {
				LOGGER.info("Create registry success");
				return new Result(true, "添加仓库(" + json_object.getString("registryName") + ")成功");

			} else {
				LOGGER.info("Create registry fail");
				return new Result(true, "添加仓库(" + json_object.getString("registryName") + ")失败，请检查数据库链接。");
			}
		} catch (Exception e) {
			LOGGER.error("Create registry fail", e);
			return new Result(true, "添加仓库(" + json_object.getString("registryName") + ")失败，请检查数据库链接。");
		}
	}

	public Result updateRegistry(JSONObject json_object) {
		try {
			int result = registryService.updateRegistry(json_object);
			if (result == 1) {
				LOGGER.info("Update registry success");
				return new Result(true, "修改仓库(" + json_object.getString("registryName") + ")成功。");
			} else {
				LOGGER.info("Update registry fail");
				return new Result(true, "修改仓库(" + json_object.getString("registryName") + ")失败，请检查数据库链接。");
			}
		} catch (Exception e) {
			LOGGER.error("Update registry fail", e);
			return new Result(true, "修改仓库(" + json_object.getString("registryName") + ")失败，请检查数据库链接。");
		}
	}

	public Result deleteBatchRegistry(JSONObject json_object) {
		String exist = null;
		String name_array = json_object.getString("name_array");

		try {
			exist = registryService.checkImageIsExist(json_object);
		} catch (Exception e) {
			LOGGER.error("检查仓库(" + name_array + ")镜像是否存在失败", e);
			return new Result(false, "检查仓库(" + name_array + ")镜像是否存在失败." + e.getMessage());
		}
		if (exist != null && !"".equals(exist)) {
			LOGGER.info("Delete registry fail,The registry has images.");
			/** @bug245_begin:[仓库管理]批量删除仓库,当部分仓库存在镜像而删除失败,提示信息不正确 **/
			return new Result(false, "仓库中存在镜像，删除镜像失败!<br>详细信息：<br>" + exist + "");
			/** @bug245_finish **/
		} else {
			try {
				int result = registryService.deleteRegistry(json_object);
				if (result == 1) {
					LOGGER.info("Delete registry success");
					return new Result(true, "仓库(" + name_array + ")删除成功。");
				} else {
					LOGGER.info("Delete registry fail");
					return new Result(false, "仓库(" + name_array + ")中无镜像，数据库操作失败！");
				}
			} catch (Exception e) {
				LOGGER.error("Delete registry fail", e);
				return new Result(false, "删除仓库(" + name_array + ")失败！" + e.getMessage());
			}
		}
	}

	public JSONObject deleteRegistry(JSONObject json_object) {
		String exist = null;
		try {
			exist = registryService.checkImageIsExist(json_object);
		} catch (Exception e) {
			LOGGER.error("检查仓库镜像是否存在失败", e);
		}
		if (exist != null && !"".equals(exist)) {
			json_object.put("result", 2);
			json_object.put("exist", exist);
			LOGGER.info("Delete registry fail,The registry has images.");
		} else {
			try {
				int result = registryService.deleteRegistry(json_object);
				if (result == 1) {
					json_object.put("result", result);
					LOGGER.info("Delete registry success");
				} else {
					json_object.put("result", result);
					LOGGER.info("Delete registry fail");
				}
			} catch (Exception e) {
				LOGGER.error("Delete registry fail", e);
			}
		}
		return json_object;
	}

	public Registry detail(int tenantId, int registry_id) {
		try {
			return registryService.getRegistryByRegiId(tenantId, registry_id);
		} catch (Exception e) {
			LOGGER.error("get registry by id[" + registry_id + "] falied!", e);
			return null;
		}
	}

	/**
	 * 添加判断是否存在相同名称的仓库
	 */
	public Boolean checkRegiName(String regiName) {
		Registry registry = new Registry();
		registry.setRegistryName(regiName);
		try {
			registry = registryService.loadRegistry(registry);
		} catch (Exception e) {
			LOGGER.error("load registry by registryName[" + regiName + "] falied!", e);
			return false;
		}

		if (registry == null) {
			return true;
		} else {
			return false;
		}
	}

	public GridBean advancedSearchRegi(Integer userId, int pagenumber, int pagesize, RegistryModel registryModel,
			JSONObject json_object) {
		try {
			return registryService.advancedSearchRegi(userId, pagenumber, pagesize, registryModel, json_object);
		} catch (Exception e) {
			LOGGER.error("advanced search by json_object[" + json_object.toJSONString() + "] falied!", e);
		}
		return null;
	}
}
