package com.cmbc.devops.manager;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.MessageResult;
import com.cmbc.devops.bean.PathObject;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.core.ImageCore;
import com.cmbc.devops.dao.RegImageMapper;
import com.cmbc.devops.entity.AppEnvImg;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.entity.RegImage;
import com.cmbc.devops.entity.Registry;
import com.cmbc.devops.message.MessagePush;
import com.cmbc.devops.model.ImageModel;
import com.cmbc.devops.model.RemoteImgModel;
import com.cmbc.devops.service.AppEnvImgService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.ImageService;
import com.cmbc.devops.service.RegistryService;
import com.cmbc.devops.util.SSH;
import com.cmbc.devops.util.TextUtil;

/**
 * 镜像模块处理统一入口类，用来实现对镜像的所有操作
 * 
 * @author dmw
 *
 */
@Component
public class ImageManager {

	private static final Logger LOGGER = Logger.getLogger(ImageManager.class);
	@Resource
	private ImageService imageService;
	@Resource
	private ImageCore imageCore;
	@Resource
	private HostService hostService;
	@Resource
	private RegistryService registryService;
	@Resource
	private MessagePush messagePush;
	@Autowired
	private RegImageMapper regImageMapper;
	@Autowired
	private AppEnvImgService aeiService;

	/**
	 * 制作镜像
	 * 
	 * @param model
	 * @return
	 */
	public void makeImage(int userId, int tenant_id, ImageModel model) {
		Host host = new Host();
		host.setHostId(model.getHostId());
		try {
			host = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get host by hostid failed!", e);
			pushMessage(userId, new MessageResult(false, "查询【仓库主机】失败！", "制作镜像"));
		}
		if (host != null) {
			boolean basic = model.getImageType().equalsIgnoreCase("basic");

			/**
			 * @bug79_begin 同一账号多点登录,上传同一镜像文件,仓库暂存目录中意外将2个文件叠加, 合并为一个无效的大文件
			 * @bug80_begin 需求:上传镜像,仓库暂存空间主目录下根据登录发布平台的操作用户,在该目录生成相应子文件夹
			 */
			try {
				Result coreresult = imageCore.makeImage(host.getHostIp(), host.getHostUser(), host.getHostPwd(),
						model.getFileName(), model.getImageName(), model.getImageTag(), basic, model.getImageUuid());
				/** @bug79_finish @bug80_finish */
				MessageResult result = new MessageResult(coreresult.isSuccess(), coreresult.getMessage(), "制作镜像");
				LOGGER.info("make image result:" + result.toString());
				if (coreresult.isSuccess()) {
					Image image = new Image();
					image.setImageCreator(userId);
					image.setImageStatus((byte) Status.IMAGE.MAKED.ordinal());
					image.setAppId(model.getAppId());
					image.setImageName(model.getImageName());
					image.setImageTag(model.getImageTag());
					String[] imageInfos = coreresult.getMessage().split(":");
					LOGGER.info(imageInfos[0] + "----" + imageInfos[1]);
					String imageUuid = imageInfos[0];
					String imageSize = imageInfos[1];
					image.setImageUuid(imageUuid);
					image.setImageSize(imageSize);
					image.setImageType(model.getImageType());
					if (basic) {
						image.setAppId(null);
					}
					Integer imageId = imageService.create(image);
					boolean createResult = false;
					if (imageId > 0) {
						RegImage regImage = new RegImage();
						regImage.setImageId(imageId);
						regImage.setRegistryId(model.getRegistryId());
						try {
							createResult = regImageMapper.insert(regImage) > 0;
						} catch (Exception e) {
							LOGGER.error("insert image info error!", e);
							pushMessage(userId, new MessageResult(false, "制作【镜像】失败！", "制作镜像"));
							return;
						}
					}
					if (createResult) {
						result.setMessage("制作【镜像】成功！");
						/** 将应用-环境-镜像三元组插入到数据库对应的表中 */
						Integer appId = model.getAppId();
						List<Integer> envList = convertStrToIntList(model.getEnvIds());
						List<AppEnvImg> aeiList = new ArrayList<AppEnvImg>();
						for (int envCount = 0, envSize = envList.size(); envCount < envSize; envCount++) {
							AppEnvImg aei = new AppEnvImg(appId, envList.get(envCount), imageId);
							aeiList.add(aei);
						}
						int aeiRes = aeiService.batchInsert(aeiList);
						if (aeiRes == 0) {
							LOGGER.error("向（应用-环境-镜像）表中插入数据失败！");
						}

						pushMessage(userId, new MessageResult(true, "" + imageId, "IMAGEID"));
					} else {
						result.setSuccess(false);
						result.setMessage("制作【镜像】失败：数据库操作异常");
					}
				}
				pushMessage(userId, result);
			} catch (NullPointerException npe) {
				pushMessage(userId, new MessageResult(false, "【仓库主机】连接中断，请检查！", "制作镜像"));
				LOGGER.error("仓库主机连接中断，请检查！", npe);
			} catch (Exception e1) {
				pushMessage(userId, new MessageResult(false, "制作【镜像】失败！", "制作镜像"));
				LOGGER.error("制作镜像失败！", e1);
			}
		} else {
			pushMessage(userId, new MessageResult(false, "【仓库主机】不存在", "制作镜像"));
		}
	}

	/** 将环境的字符串分割转化为整数List链表 */
	private List<Integer> convertStrToIntList(String envIds) {
		String[] envArray = envIds.split(",");
		ArrayList<Integer> envList = new ArrayList<Integer>();
		for (String env : envArray) {
			envList.add(Integer.parseInt(env));
		}
		return envList;
	}

	/**
	 * 远程制作镜像
	 * 
	 * @param model
	 * @return
	 */
	public void rmtMakeImage(int userId, int tenant_id, RemoteImgModel rmtimg_model) {
		/** 首先获取仓库主机的全部信息 **/
		Registry registry = new Registry();
		registry.setRegistryId(rmtimg_model.getRegistryId());
		try {
			registry = registryService.loadRegistry(registry);
			/** @PROGRESS:核对目标仓库主机成功，进度条设为10% **/
			if (registry != null) {
				LOGGER.info("查询目标仓库记录(ID:" + rmtimg_model.getRegistryId() + ")成功！");
				pushMessage(userId, new MessageResult(false, "10#" + "查询目标仓库记录成功。", "远程制作镜像"));
			} else {
				LOGGER.error("查询目标仓库记录(ID:" + rmtimg_model.getRegistryId() + ")失败！");
				pushMessage(userId, new MessageResult(false, "10#" + "查询目标仓库记录成功。", "远程制作镜像"));
				return;
			}
		} catch (Exception e3) {
			LOGGER.error("查询仓库记录(ID:" + rmtimg_model.getRegistryId() + ")失败！请核对后重试。", e3);
			pushMessage(userId, new MessageResult(false,
					"fail#" + "查询仓库记录(ID:" + rmtimg_model.getRegistryId() + ")失败！请核对后重试。", "远程制作镜像"));
			return;
		}

		/** 通过仓库获取仓库主机节点 **/
		Host reg_host = new Host();
		reg_host.setHostId(registry.getHostId());
		try {
			reg_host = hostService.getHost(reg_host);
			/** @PROGRESS:核对目标仓库主机，进度条设为20% **/
			if (reg_host != null) {
				LOGGER.error("查询目标仓库主机(ID:" + registry.getHostId() + ")成功！");
				pushMessage(userId,
						new MessageResult(false, "20#" + "查询目标仓库主机(IP:" + reg_host.getHostIp() + ")成功。", "远程制作镜像"));
			}
		} catch (Exception e) {
			LOGGER.error("查询目标仓库主机(ID:" + registry.getHostId() + ")失败！请核对后重试。", e);
			pushMessage(userId, new MessageResult(false,
					"fail#" + "查询目标仓库主机(ID:" + registry.getHostId() + ")失败！请核对后重试。", "远程制作镜像"));
			return;
		}

		if (reg_host == null) {
			LOGGER.error("【仓库主机(ID:" + registry.getHostId() + ")】不存在,请检查后重试。");
			pushMessage(userId,
					new MessageResult(false, "fail#" + "【仓库主机(ID:" + registry.getHostId() + ")】不存在", "远程制作镜像"));
		}

		/** 生成UUID字符串 **/
		String rmt_uuid_folder = UUID.randomUUID().toString();
		String local_uuid_folder = UUID.randomUUID().toString();
		/* 分别保存仓库服务器和本地的暂存地址 */
		String reg_tmp_path = "";
		String local_tmp_path = "";
		/** 在本地创建临时文件夹，保存远程文件 **/
		File folder = new File(local_uuid_folder);
		if (!(folder.exists() && folder.isDirectory())) {
			folder.mkdirs();
		}
		local_tmp_path = folder.getAbsolutePath();

		/** @PROGRESS:Web服务器的临时文件夹创建成功，进度条设为30% **/
		if (StringUtils.hasText(local_tmp_path)) {
			LOGGER.info("在Web服务器创建临时文件夹:" + local_tmp_path + "成功！");
			/* 向用户提示已经创建完成临时保存文件夹 */
			pushMessage(userId, new MessageResult(false, "30#" + "在Web服务器创建临时文件夹:" + local_tmp_path + "成功！", "远程制作镜像"));
		} else {
			LOGGER.error("在Web服务器创建临时文件夹:" + local_tmp_path + "失败！");
			/* 向用户提示已经创建完成临时保存文件夹 */
			pushMessage(userId,
					new MessageResult(false, "fail#" + "在Web服务器创建临时文件夹:" + local_tmp_path + "失败！", "远程制作镜像"));
			return;
		}

		/* 在仓库服务器上创建临时文件夹 */
		try {
			Result create_result = imageCore.createRegTempFolder(reg_host.getHostIp(), reg_host.getHostUser(),
					reg_host.getHostPwd(), rmt_uuid_folder);
			/**
			 * @PROGRESS:在仓库服务器上创建临时文件成功， 进度条设置为40%
			 **/
			if (create_result.isSuccess()) {
				/** 去掉字符串末尾的回车字符 **/
				reg_tmp_path = TextUtil.replaceBlank(create_result.getMessage());
				/* 向用户提示已经在仓库节点创建完成临时保存文件夹 */
				pushMessage(userId, new MessageResult(false, "40#" + "仓库主机创建暂存目录:" + reg_tmp_path + "成功！", "远程制作镜像"));
			} else {
				LOGGER.error("目标仓库主机(ID:" + rmtimg_model.getRegistryId() + ")创建暂存目录失败！");
				/* 清理Web服务器，删除本地创建的文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
				pushMessage(userId, new MessageResult(false,
						"fail#" + "目标仓库主机(ID:" + rmtimg_model.getRegistryId() + ")创建暂存目录失败！", "远程制作镜像"));
				return;

			}
		} catch (Exception e2) {
			LOGGER.error("目标仓库主机(ID:" + rmtimg_model.getRegistryId() + ")创建暂存目录失败！", e2);
			/* 清理Web服务器，删除本地创建的文件夹 */
			deleteDir(folder);
			pushMessage(userId, new MessageResult(false,
					"fail#" + "目标仓库主机(ID:" + rmtimg_model.getRegistryId() + ")创建暂存目录失败！", "远程制作镜像"));
			return;
		}

		/* 当仓库主机和镜像数据主机均不为空时，可以进行制作景象操作 */
		boolean basic = rmtimg_model.getImageType().equalsIgnoreCase("basic");

		/** （一）首先将镜像文件从服务器上下载下来 **/
		SSH imgSsh = new SSH(rmtimg_model.getHostIP(), rmtimg_model.getHostUser(), rmtimg_model.getHostPasswd());
		try {
			if (imgSsh.connect()) {
				boolean recv_succ = imgSsh.fetchFile(rmtimg_model.getFileFolder() + "/" + rmtimg_model.getFileName(),
						local_tmp_path + "/");
				LOGGER.info("从远程文件:(" + rmtimg_model.getFileFolder() + "/" + rmtimg_model.getFileName() + ")拷贝到本地目录:("
						+ local_tmp_path + "/" + ")");
				if (!recv_succ) {
					LOGGER.error("从镜像文件所在主机(IP:" + rmtimg_model.getHostIP() + ")拷贝文件失败！请检查网络连接情况。");
					pushMessage(userId, new MessageResult(false,
							"fail#" + "从镜像文件所在主机(IP:" + rmtimg_model.getHostIP() + ")拷贝文件失败！请检查网络连接情况。", "远程制作镜像"));
					return;
				}
				imgSsh.close();
				/* Web服务器向仓库服务器传输镜像文件成功！ */
				/**
				 * @PROGRESS:在仓库服务器上创建临时文件成功， 进度条设置为50%
				 **/
				LOGGER.info("从【远程主机】向【Web服务器】传输镜像文件成功！");
				pushMessage(userId, new MessageResult(false, "50#" + "从远程主机-->Web服务器传输镜像文件成功！", "远程制作镜像"));

			} else {
				LOGGER.error("与镜像文件主机(IP:" + rmtimg_model.getHostIP() + ")未建立SSH链接！请检查网络情况。");
				pushMessage(userId, new MessageResult(false,
						"fail#" + "与镜像文件主机(IP:" + rmtimg_model.getHostIP() + ")未建立SSH链接！请检查网络情况。", "远程制作镜像"));
				/* 清理Web服务器暂存文件夹，暂存文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
				return;
			}
		} catch (SocketException e2) {
			LOGGER.error("ssh connect error:", e2);
		} catch (IOException e2) {
			LOGGER.error("ssh connect error:", e2);
		}

		/** (二)将文件从本地传输到远程的仓库服务器中 **/
		SSH regSsh = new SSH(reg_host.getHostIp(), reg_host.getHostUser(), reg_host.getHostPwd());
		try {
			if (regSsh.connect()) {
				boolean send_succ = regSsh.scpFile(local_tmp_path + "/" + rmtimg_model.getFileName(),
						reg_tmp_path + "/");
				if (!send_succ) {
					LOGGER.error("向仓库主机服务器(IP:" + reg_host.getHostIp() + ")传输镜像失败！请检查网络连接情况。");
					pushMessage(userId, new MessageResult(false,
							"fail#" + "向仓库主机服务器(IP:" + reg_host.getHostIp() + ")传输镜像失败！请检查网络连接情况。", "远程制作镜像"));
					return;
				}
				regSsh.close();

				/**
				 * @PROGRESS:Web服务器向仓库服务器传输镜像文件成功！， 进度条设置为60%
				 **/
				LOGGER.info("从【Web服务器】向【仓库主机】传输镜像文件成功！");
				pushMessage(userId, new MessageResult(false, "60#" + "从【Web服务器】向【仓库主机】传输镜像文件成功！", "远程制作镜像"));
				/* 清理Web服务器暂存文件夹，暂存文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
			} else {
				LOGGER.error("与仓库主机服务器(IP:" + reg_host.getHostIp() + ")未建立SSH链接！请检查网络情况。");
				pushMessage(userId, new MessageResult(false,
						"fail#" + "与仓库主机服务器(IP:" + reg_host.getHostIp() + ")未建立SSH链接！请检查网络情况。", "远程制作镜像"));
				return;
			}
		} catch (SocketException e2) {
			LOGGER.error("ssh connect error:", e2);
		} catch (IOException e2) {
			LOGGER.error("ssh connect error:", e2);
		}

		try {
			/** 执行远程制作镜像命令 **/
			String regi_ip = reg_host.getHostIp();
			String regi_user = reg_host.getHostUser();
			String regi_pwd = reg_host.getHostPwd();

			Result coreresult = imageCore.makeRemoteImage(regi_ip, regi_user, regi_pwd, reg_tmp_path,
					rmtimg_model.getFileName(), rmtimg_model.getImageName(), rmtimg_model.getImageTag(), basic);
			/* Web服务器向仓库服务器传输镜像文件成功！ */
			if (coreresult.isSuccess() == false) {
				pushMessage(userId, new MessageResult(false, "fail#" + coreresult.getMessage(), "远程制作镜像"));
				/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
				deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
				return;
			} else {
				/**
				 * @PROGRESS:执行远程制作镜像命令成功！， 进度条设置为80%
				 **/
				LOGGER.info("执行远程制作镜像命令成功！！");
				pushMessage(userId, new MessageResult(false, "80#" + coreresult.getMessage(), "远程制作镜像"));
			}
			// MessageResult result = new MessageResult(coreresult.isSuccess(),
			// coreresult.getMessage(), "远程制作镜像");
			// LOGGER.info("远程制作镜像结果:" + result.toString());
			if (coreresult.isSuccess()) {
				Image image = new Image();
				image.setImageCreator(userId);
				image.setImageStatus((byte) Status.IMAGE.MAKED.ordinal());
				image.setAppId(rmtimg_model.getAppId());
				image.setImageName(rmtimg_model.getImageName());
				image.setImageTag(rmtimg_model.getImageTag());
				String[] imageInfos = coreresult.getMessage().split(":");
				LOGGER.info(imageInfos[0] + "----" + imageInfos[1]);
				String imageUuid = imageInfos[0];
				String imageSize = imageInfos[1];
				image.setImageUuid(imageUuid);
				image.setImageSize(imageSize);
				image.setImageType(rmtimg_model.getImageType());
				if (basic) {
					image.setAppId(null);
				}
				Integer imageId = imageService.create(image);
				boolean createResult = false;
				if (imageId > 0) {
					RegImage regImage = new RegImage();
					regImage.setImageId(imageId);
					regImage.setRegistryId(rmtimg_model.getRegistryId());
					try {
						createResult = regImageMapper.insert(regImage) > 0;
					} catch (Exception e) {
						LOGGER.error("insert image info error!", e);
						pushMessage(userId, new MessageResult(false, "fail#远程制作【镜像】失败：数据库操作异常！", "远程制作镜像"));
						/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
						deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
						return;
					}
				}
				if (createResult) {
					/** 将应用-环境-镜像三元组插入到数据库对应的表中 */
					Integer appId = rmtimg_model.getAppId();
					List<Integer> envList = convertStrToIntList(rmtimg_model.getEnvIds());
					List<AppEnvImg> aeiList = new ArrayList<AppEnvImg>();
					for (int envCount = 0, envSize = envList.size(); envCount < envSize; envCount++) {
						AppEnvImg aei = new AppEnvImg(appId, envList.get(envCount), imageId);
						aeiList.add(aei);
					}
					int aeiRes = aeiService.batchInsert(aeiList);
					if (aeiRes == 0) {
						LOGGER.error("向（应用-环境-镜像）表中插入数据失败！");
					}

					pushMessage(userId, new MessageResult(true, "100#远程制作【镜像(ID:" + imageId + ")】写入数据库成功！", "远程制作镜像"));
					/* 在制作镜像成功的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "success#制作镜像成功", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
					return;
				} else {
					pushMessage(userId, new MessageResult(false, "fail#远程制作【镜像】失败：数据库操作异常！", "远程制作镜像"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
					return;
				}
			}
		} catch (NullPointerException npe) {
			pushMessage(userId, new MessageResult(false, "【仓库主机】连接中断，请检查！", "远程制作镜像"));
			LOGGER.error("仓库主机连接中断，请检查！", npe);
		} catch (Exception e1) {
			pushMessage(userId, new MessageResult(false, "制作【镜像】失败！", "远程制作镜像"));
			LOGGER.error("制作镜像失败！", e1);
		}
	}

	// 调用dockerAPI，打镜像的标记
	public Result tagImage(Integer hostId, Integer registryPort, String imageName, String tag) {
		Host host = new Host();
		host.setHostId(hostId);
		try {
			host = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get Registry host failed!");
			return new Result(false, "查询【主机】信息失败！");
		}
		if (host == null) {
			LOGGER.error("Registry host not exist!");
			return new Result(false, "【主机】不存在");
		} else {
			String sourceImage = imageName + ":" + tag;
			String targetImage = "localhost:" + registryPort + "/" + sourceImage;
			Result result = imageCore.tagImage(host.getHostIp(), host.getHostUser(), host.getHostPwd(), sourceImage,
					targetImage);
			return result;
		}
	}

	// 调用dockerAPI，打镜像的标记
	public Result tagLoadImage(Integer hostId, Integer registryPort, String imageUuid, String imageName, String tag) {
		Host host = new Host();
		host.setHostId(hostId);
		try {
			host = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get Registry host failed!");
			return new Result(false, "查询【主机】信息失败！");
		}
		if (host == null) {
			LOGGER.error("Registry host not exist!");
			return new Result(false, "【主机】不存在");
		} else {
			String targetImage = "localhost:" + registryPort + "/" + imageName + ":" + tag;
			Result result = imageCore.tagLoadImage(host.getHostIp(), host.getHostUser(), host.getHostPwd(), imageUuid,
					targetImage);
			return result;
		}
	}

	/* 删除远程主机的目录信息 */
	private void deleteRmtHostFolder(int user_id, String mk_result, String host_name, String host_ip, String host_user,
			String host_pwd, String target_folder) {
		Result dele_result = null;
		try {
			dele_result = imageCore.deleteRegTempFolder(host_ip, host_user, host_pwd, target_folder);
		} catch (Exception e) {
			LOGGER.error("删除目标主机(IP:" + host_ip + ")的文件夹失败!", e);
		}
		if (dele_result.isSuccess()) {
			pushMessage(user_id, new MessageResult(false, mk_result + "，已经删除" + host_name + "暂存目录。", "远程制作镜像"));
		} else {
			pushMessage(user_id, new MessageResult(false, mk_result + "，删除" + host_name + "暂存目录失败，请手工删除。", "远程制作镜像"));
		}
	}

	/**
	 * 镜像发布
	 * 
	 * @param jsonObject
	 * @return
	 */
	public void pushImage(int userId, int tenant_id, ImageModel model) {
		Host host = new Host();
		host.setHostId(model.getHostId());
		Registry registry = new Registry();
		registry.setRegistryId(model.getRegistryId());
		JSONObject registryObj = new JSONObject();
		try {
			registryObj = this.registryService.getRegistry(registry);
		} catch (Exception e1) {
			LOGGER.error("get registry by registryid[" + model.getRegistryId() + "] failed!", e1);
			pushMessage(userId, new MessageResult(false, "查询【仓库主机】信息失败！", "镜像发布"));
		}
		/** 判断从查询的仓库数据是否为空？ */
		Integer registryPort = null;
		if (!registryObj.isEmpty()) {
			registryPort = registryObj.getInteger("registryPort");
		} else {
			/** @bug166 如果获取主机仓库主机信息失败，则直接返回错误提示 */
			pushMessage(userId, new MessageResult(false, "仓库服务获取【主机】信息失败！", "镜像发布"));
			return;
		}
		try {
			host = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get host by hostid failed!", e);
			pushMessage(userId, new MessageResult(false, "查询【仓库主机】信息失败！", "镜像发布"));
			return;
		}
		if (null == host) {
			pushMessage(userId, new MessageResult(false, "仓库【主机】不存在", "镜像发布"));
			return;
		}
		String imageName = model.getImageName();
		String tag = model.getImageTag();
		// TODO 最终存放在数据库中镜像的名称，可以根据此名称直接从镜像仓库中拉取，后续会修改
		String finalName = host.getHostIp() + ":" + registryPort + "/" + imageName;
		String pushImage = "localhost:" + registryPort + "/" + imageName + ":" + tag;// 推送时的镜像名称
		Result tagresult = this.tagImage(host.getHostId(), registryPort, imageName, tag);
		// 镜像打标
		MessageResult message = new MessageResult(tagresult.isSuccess(), tagresult.getMessage(), "镜像发布");
		if (tagresult.isSuccess()) {// 打标成功
			LOGGER.info("tag image success");
			Result pushResult = imageCore.pushImage(host.getHostIp(), host.getHostUser(), host.getHostPwd(), pushImage);
			if (pushResult.isSuccess()) {
				LOGGER.info("push image into registry success!");
				Image image = new Image();
				try {
					image = imageService.loadImage(tenant_id, model.getImageId());
				} catch (Exception e) {
					LOGGER.error("load image by imageid[" + model.getImageId() + "] failed!", e);
					pushMessage(userId, new MessageResult(false, "查询数据库信息失败！", "镜像发布"));
					return;
				}
				// TODO 这里要注意的是后续要改成单纯的用户输入的镜像名，如果带有仓库的ip port则该镜像只能存放在一个仓库中。
				// 事实上同样的镜像会存在于多个仓库中。后续会对此进行修改。
				/** 2016年1月13日修正发布同时被删除的镜像bug **/
				if (image != null) {
					image.setImageName(finalName);
					image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
					boolean result = false;
					try {
						result = imageService.update(image);
					} catch (Exception e) {
						LOGGER.error("update image by imageid[" + model.getImageId() + "] failed!", e);
						pushMessage(userId, new MessageResult(false, "更新数据库信息失败！", "镜像发布"));
						return;
					}
					if (result) {
						message.setMessage("发布镜像成功！");
					} else {
						message.setSuccess(false);
						message.setMessage("发布镜像失败：数据库保存异常！");
					}
				} else {
					message.setSuccess(false);
					message.setMessage("发布镜像失败：数据库中没有找到镜像记录！请核对后重试。");
				}
			} else {
				LOGGER.error("push image into registry fail!!");
				message.setSuccess(false);
				message.setMessage("发布镜像失败：" + pushResult.getMessage());
			}
		} else {
			LOGGER.error("tag image fail!");
			message.setMessage("发布【镜像】失败：" + tagresult.getMessage());
		}
		pushMessage(userId, message);
	}

	/** @date:2016年3月28日 添加租户维度 */
	public Result removeImage(int userId, int tenantId, List<Integer> imageList) {
		if (null == imageList || imageList.isEmpty()) {
			return new Result(true, "删除成功！");
		}
		try {
			String id_str = "";
			for (Integer imageId : imageList) {
				id_str = id_str + imageId + ",";
				imageService.delete(imageId);
			}
			LOGGER.warn("User (ID:" + userId + ") in Tenant(ID:" + tenantId + ") delete imagelist (" + id_str + ")");

			return new Result(true, "删除【镜像】成功！");
		} catch (Exception e) {
			LOGGER.error("删除镜像异常：", e);
			return new Result(false, "删除【镜像】失败！");
		}
	}

	/** 快速发布镜像 **/
	public void fastPush(int userId, int tenantId, int imageId) {
		Image image = new Image();
		try {
			image = imageService.loadImage(tenantId, imageId);
		} catch (Exception e1) {
			LOGGER.error("load image by imageid[" + imageId + "] falied!", e1);
			pushMessage(userId, new MessageResult(false, "查询【镜像】信息失败！请核对镜像。", "镜像快速发布"));
			return;
		}
		Registry reg = new Registry();
		try {
			reg = this.getRegByImage(imageId);
		} catch (Exception e2) {
			LOGGER.error("get registry by imageid[" + imageId + "] falied!", e2);
			pushMessage(userId, new MessageResult(false, "通过镜像查询【主机仓库】信息失败！请核对镜像。", "镜像快速发布"));
			return;
		}

		/** @bug171_begin:[镜像管理]当"镜像不存在！或者仓库主机不存在"发布镜像失败,没有将失败信息返回给终端用户 */
		if (null == image || null == reg) {
			/** @bug223_begin:[镜像管理]发布镜像,目标主机找不到或镜像找不到而发布失败,两类异常分开处理并提示原因 *****/
			/** 根据返回的结果判断，组装向用户返回的消息 **/
			String push_message = "";
			if (null == image) {
				push_message += "【镜像】不存在，";
			}
			if (null == reg) {
				push_message += "【目标仓库】不存在，";
			}
			push_message += "请核对检查。";
			/** @bug223_finish **/
			pushMessage(userId, new MessageResult(false, push_message, "镜像快速发布"));
		} else {
			Host host = new Host();
			try {
				host = this.hostService.loadHost(reg.getHostId());
			} catch (Exception e) {
				LOGGER.error("load host by hostid failed!", e);
				new MessageResult(false, "查询【主机】信息失败！", "镜像发布");
			}
			String imageName = image.getImageName();
			String tag = image.getImageTag();
			// TODO 最终存放在数据库中镜像的名称，可以根据此名称直接从镜像仓库中拉取，后续会修改
			String finalName = host.getHostIp() + ":" + reg.getRegistryPort() + "/" + imageName;
			String pushImage = "localhost:" + reg.getRegistryPort() + "/" + imageName + ":" + tag;// 推送时的镜像名称
			Result tagresult = this.tagImage(host.getHostId(), reg.getRegistryPort(), imageName, tag);
			// 镜像打标
			MessageResult message = new MessageResult(tagresult.isSuccess(), tagresult.getMessage(), "镜像发布");
			if (tagresult.isSuccess()) {// 打标成功
				LOGGER.info("tag image success");
				Result pushResult = imageCore.pushImage(host.getHostIp(), host.getHostUser(), host.getHostPwd(),
						pushImage);
				if (pushResult.isSuccess()) {
					LOGGER.info("push image into registry success!");
					// TODO 这里要注意的是后续要改成单纯的用户输入的镜像名，如果带有仓库的ip
					// port则该镜像只能存放在一个仓库中。
					// 事实上同样的镜像会存在于多个仓库中。后续会对此进行修改。
					image.setImageName(finalName);
					image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
					boolean result = false;
					try {
						result = imageService.update(image);
					} catch (Exception e) {
						LOGGER.error("update image by imageid[" + imageId + "] falied!", e);
						pushMessage(userId, new MessageResult(false, "数据库更新【镜像】信息失败！", "镜像快速发布"));
						return;
					}
					if (result) {
						message.setMessage("发布【镜像】成功！");
					} else {
						message.setSuccess(false);
						message.setMessage("发布【镜像】失败：数据库保存异常！");
					}
				} else {
					LOGGER.error("push image into registry fail!!");
					message.setSuccess(false);
					message.setMessage("发布【镜像】失败：" + pushResult.getMessage());
				}
			} else {
				LOGGER.error("tag image fail!");
				message.setMessage("发布【镜像】失败：" + tagresult.getMessage());
			}
			pushMessage(userId, message);
		}
	}

	private Registry getRegByImage(int image) throws Exception {
		return registryService.getByImage(image);
	}

	/** 获取远程主机中给定路径下的文件列表 **/
	public PathObject getFilelist(String host_ip, String host_user, String host_passwd, String path) {
		try {
			return imageCore.queryPathElement(host_ip, host_user, host_passwd, path);
		} catch (Exception e) {
			LOGGER.error("查询主机（IP:" + host_ip + "）文件夹[" + path + "]失败，请检查主机状态或网络连接后重试。", e);
			return new PathObject(7, "查询主机（IP:" + host_ip + "）文件夹[" + path + "]失败，请检查主机状态或网络连接后重试。", null);
		}

	}

	public Image detail(Integer tenant_id, Integer id) {
		try {
			return imageService.loadImage(tenant_id, id);
		} catch (Exception e) {
			LOGGER.error("get image by imageid[" + id + "] falied!", e);
			return null;
		}
	}

	public Result modImage(Integer userId, Integer tenantId, Integer imageId, Integer appId, String type,
			String envids) {
		Image image = new Image();
		try {
			image = imageService.loadImage(tenantId, imageId);
		} catch (Exception e) {
			LOGGER.error("load image by imageid[" + imageId + "] falied!", e);
			return new Result(false, "数据库查询镜像信息失败！");
		}
		if (null == image) {
			return new Result(false, "镜像不存在");
		}
		/** 获取镜像原始的状态信息，如果是已制作的，则需要修改发布状态信息，如果是已发布，则无需修改。 */
		Integer rawImgStatus = image.getImageStatus().intValue();

		image.setAppId(appId);
		image.setImageType(type.toUpperCase());
		image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());

		/** 添加镜像所在仓库的路径 */
		if (rawImgStatus == (Status.IMAGE.MAKED.ordinal())) {
			Integer selImageId = image.getImageId();
			/* 获取镜像所在仓库信息 */
			RegImage selRegImg = new RegImage();
			selRegImg.setImageId(selImageId);
			List<RegImage> regImgList = null;
			try {
				regImgList = regImageMapper.selectAll(selRegImg);
			} catch (Exception e1) {
				LOGGER.error("load regimg info by imageId [" + selImageId + "] falied!", e1);
				return new Result(false, "通过镜像ID(" + selImageId + ")查询镜像仓库对应信息失败！");
			}
			if (!regImgList.isEmpty()) {
				RegImage sinRegImg = regImgList.get(0);
				Integer registryId = sinRegImg.getRegistryId();
				Registry selRegistry = new Registry();
				selRegistry.setRegistryId(registryId);
				try {
					selRegistry = registryService.loadRegistry(selRegistry);
				} catch (Exception e) {
					LOGGER.error("load registry info by registryId [" + registryId + "] falied!", e);
					return new Result(false, "通过仓库ID(" + registryId + ")查询仓库对应信息失败！");
				}
				if (!selRegistry.getHostId().equals(null)) {
					Host regiHost = null;
					try {
						regiHost = hostService.loadHost(selRegistry.getHostId());
					} catch (Exception e) {
						LOGGER.error("load host info by hostId [" + selRegistry.getHostId() + "] falied!", e);
						return new Result(false, "通过仓库主机ID(" + selRegistry.getHostId() + ")查询主机对应信息失败！");
					}
					// TODO 最终存放在数据库中镜像的名称，可以根据此名称直接从镜像仓库中拉取，后续会修改
					String finalName = regiHost.getHostIp() + ":" + selRegistry.getRegistryPort() + "/"
							+ image.getImageName();
					image.setImageName(finalName);
				} else {
					LOGGER.error("load host by registry.hostid [" + selRegistry.getHostId() + "] falied!");
					return new Result(false, "通过仓库中的主机ID(" + selRegistry.getHostId() + ")信息为空！");
				}
			} else {
				LOGGER.error("load registry by imageid[" + imageId + "] falied!");
				return new Result(false, "通过镜像(ID:" + imageId + ")查询仓库信息失败！");
			}
		}

		boolean success = false;

		/** 变更镜像类型为基础镜像，将镜像所属的应用ID清空为NULL */
		if (type.equalsIgnoreCase("BASIC")) {
			try {
				success = imageService.restoreImage(image);
				if (success) {
					aeiService.removeByImgId(imageId);
				}
			} catch (Exception e) {
				LOGGER.error("update image by imageid[" + imageId + "] falied!", e);
				return new Result(false, "数据库更新【镜像】信息失败！");
			}
		} else if (type.equalsIgnoreCase("APP")) {
			try {
				boolean flag = false;
				flag = imageService.update(image);
				if (flag) {
					if (!org.apache.commons.lang.StringUtils.isEmpty(envids)) {
						aeiService.removeByImgId(imageId);
						for (String iterable_element : envids.split(",")) {
							AppEnvImg aei = new AppEnvImg(appId, Integer.valueOf(iterable_element), imageId);
							aeiService.insert(aei);
						}
						success = true;
					} else {
						aeiService.removeByImgId(imageId);
						success = true;
					}

				}

			} catch (Exception e) {
				LOGGER.error("update image by imageid[" + imageId + "] falied!", e);
				return new Result(false, "数据库更新【镜像】信息失败！");
			}
		}

		return success ? new Result(true, "修正成功！") : new Result(false, "修正失败：数据库保存异常！");
	}

	private void pushMessage(final Integer userId, final MessageResult message) {
		messagePush.pushMessage(userId, JSONObject.toJSONString(message));
		LOGGER.info("Send message :" + message + "to:" + userId);
	}

	public GridBean advancedSearchImage(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			ImageModel image, JSONObject json_object) {
		try {
			return imageService.advancedSearchImage(userId, tenantId, pagenumber, pagesize, image, json_object);
		} catch (Exception e) {
			LOGGER.info("高级查询镜像内容失败！", e);
		}
		return null;
	}

	public JSONArray imageAllList(Integer userId, Integer tenantId, ImageModel image) {
		try {
			return imageService.imageAllList(userId, tenantId, image);
		} catch (Exception e) {
			LOGGER.info("获取全部镜像json数组失败！", e);
			return null;
		}
	}

	/** @描述： 远程制作并发布镜像 **/
	public void rmtMkPsh(Integer userId, Integer tenantId, RemoteImgModel rmtimg_model) {
		/** 首先获取仓库主机的全部信息 **/
		Registry select_registry = new Registry();
		select_registry.setRegistryId(rmtimg_model.getRegistryId());
		try {
			select_registry = registryService.loadRegistry(select_registry);
			/*-----------10%-----------*/
			pushMessage(userId,
					new MessageResult(false, "10#" + "查询仓库记录(ID:" + rmtimg_model.getRegistryId() + ")成功。", "远程制作镜像"));
		} catch (Exception e3) {
			LOGGER.error("查询仓库记录(ID:" + rmtimg_model.getRegistryId() + ")失败！请核对后重试。", e3);
			pushMessage(userId, new MessageResult(false,
					"fail#" + "查询仓库记录(ID:" + rmtimg_model.getRegistryId() + ")失败！请核对后重试。", "远程制作镜像"));
			return;
		}

		/** 通过仓库获取仓库主机节点 **/
		Host reg_host = new Host();
		reg_host.setHostId(select_registry.getHostId());
		try {
			reg_host = hostService.getHost(reg_host);
			/*-----------20%-----------*/
			pushMessage(userId,
					new MessageResult(false, "20#" + "查询仓库主机(IP:" + reg_host.getHostIp() + ")成功。", "远程制作镜像"));
		} catch (Exception e) {
			LOGGER.error("查询目标仓库主机(ID:" + select_registry.getHostId() + ")失败！请核对后重试。", e);
			pushMessage(userId, new MessageResult(false,
					"fail#" + "查询目标仓库主机(ID:" + select_registry.getHostId() + ")失败！请核对后重试。", "远程制作镜像"));
			return;
		}

		/** 生成UUID字符串 **/
		String rmt_uuid_folder = UUID.randomUUID().toString();
		String local_uuid_folder = UUID.randomUUID().toString();
		/* 分别保存仓库服务器和本地的暂存地址 */
		String reg_tmp_path = "";
		String local_tmp_path = "";
		/** 在本地创建临时文件夹，保存远程文件 **/
		File folder = new File(local_uuid_folder);
		if (!(folder.exists() && folder.isDirectory())) {
			folder.mkdirs();
		}
		local_tmp_path = folder.getAbsolutePath();
		/*-----------30%-----------*/
		LOGGER.info("在Web服务器创建临时文件夹:" + local_tmp_path + "成功！");
		/* 向用户提示已经创建完成临时保存文件夹 */
		pushMessage(userId, new MessageResult(false, "30#" + "在Web服务器创建临时文件夹:" + local_tmp_path + "成功！", "远程制作镜像"));
		/* 在仓库服务器上创建临时文件夹 */
		try {
			Result create_result = imageCore.createRegTempFolder(reg_host.getHostIp(), reg_host.getHostUser(),
					reg_host.getHostPwd(), rmt_uuid_folder);
			/* 在仓库服务器上创建临时文件成功， */
			if (create_result.isSuccess()) {
				/*-----------40%：仓库服务器创建临时文件夹-----------*/
				reg_tmp_path = TextUtil.replaceBlank(create_result.getMessage());
				LOGGER.info("仓库服务器创建临时文件夹:" + reg_tmp_path + "成功！");
				/* 向用户提示已经创建完成临时保存文件夹 */
				pushMessage(userId, new MessageResult(false, "40#" + "仓库主机创建暂存目录:" + reg_tmp_path + "成功！", "远程制作镜像"));
			} else {
				LOGGER.error("目标仓库主机(IP:" + reg_host.getHostIp() + ")创建暂存目录失败！错误信息:" + create_result.getMessage());
				pushMessage(userId, new MessageResult(false,
						"fail#" + "目标仓库主机(IP:" + reg_host.getHostIp() + ")创建暂存目录失败！错误信息:" + create_result.getMessage(),
						"远程制作镜像"));
				/* 清理Web服务器暂存文件夹，暂存文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
				return;
			}
		} catch (Exception e2) {
			LOGGER.error("目标仓库主机(IP:" + reg_host.getHostIp() + ")创建暂存目录失败！", e2);
			pushMessage(userId,
					new MessageResult(false, "fail#" + "目标仓库主机(IP:" + reg_host.getHostIp() + ")创建暂存目录失败！", "远程制作镜像"));
			/* 清理Web服务器暂存文件夹，暂存文件夹 */
			deleteDir(folder);
			LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
			return;
		}

		/* 当仓库主机和镜像数据主机同时均不为空时，可以进行制作镜像操作 */
		boolean basic = rmtimg_model.getImageType().equalsIgnoreCase("basic");
		/* 远程主机保存镜像文件的路径 */
		String rmt_image_path = rmtimg_model.getFileFolder() + "/" + rmtimg_model.getFileName();
		rmt_image_path = rmt_image_path.replace("\\", "/");
		/* 本地暂存文件夹 */
		local_tmp_path = local_tmp_path.replace("\\", "/");
		/* 仓库主机保存的文件夹 */
		reg_tmp_path = reg_tmp_path.replace("\\", "/");

		/** （一）首先将镜像文件从服务器上下载下来 **/
		SSH imgSsh = new SSH(rmtimg_model.getHostIP(), rmtimg_model.getHostUser(), rmtimg_model.getHostPasswd());
		try {
			if (imgSsh.connect()) {
				boolean recv_succ = imgSsh.fetchFile(rmtimg_model.getFileFolder() + "/" + rmtimg_model.getFileName(),
						local_tmp_path + "/");
				LOGGER.info("从远程文件:(" + rmtimg_model.getFileFolder() + "/" + rmtimg_model.getFileName() + ")拷贝到本地目录:("
						+ local_tmp_path + "/" + ")");
				if (!recv_succ) {
					LOGGER.error("从镜像文件所在主机(IP:" + rmtimg_model.getHostIP() + ")拷贝文件失败！请检查网络连接情况。");
					pushMessage(userId, new MessageResult(false,
							"fail#" + "从镜像文件所在主机(IP:" + rmtimg_model.getHostIP() + ")拷贝文件失败！请检查网络连接情况。", "远程制作镜像"));
					return;
				}
				imgSsh.close();
				/*-----------50%：从镜像所在主机拷贝到Web服务器上-----------*/
				LOGGER.info("从远程主机-->Web服务器传输镜像文件成功！");
				/* 向用户提示已经创建完成临时保存文件夹 */
				pushMessage(userId, new MessageResult(false, "50#" + "从【远程主机】向【Web服务器】传输镜像文件成功！", "远程制作镜像"));
			} else {
				LOGGER.error("与镜像文件主机(IP:" + rmtimg_model.getHostIP() + ")未建立SSH链接！请检查网络情况。");
				pushMessage(userId, new MessageResult(false,
						"fail#" + "与镜像文件主机(IP:" + rmtimg_model.getHostIP() + ")未建立SSH链接！请检查网络情况。", "远程制作镜像"));
				/* 清理Web服务器暂存文件夹，暂存文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
				return;
			}
		} catch (SocketException e3) {
			LOGGER.error("ssh connect error:", e3);
		} catch (IOException e3) {
			LOGGER.error("ssh connect error:", e3);
		}

		/** (二)将文件从本地传输到远程的仓库服务器中 **/
		String regi_ip = reg_host.getHostIp();
		String regi_user = reg_host.getHostUser();
		String regi_pwd = reg_host.getHostPwd();

		SSH regSsh = new SSH(reg_host.getHostIp(), reg_host.getHostUser(), reg_host.getHostPwd());
		try {
			if (regSsh.connect()) {
				boolean send_succ = regSsh.scpFile(local_tmp_path + "/" + rmtimg_model.getFileName(),
						reg_tmp_path + "/");

				if (!send_succ) {
					LOGGER.error("向仓库主机服务器(IP:" + reg_host.getHostIp() + ")传输镜像失败！请检查网络连接情况。");
					pushMessage(userId, new MessageResult(false,
							"fail#" + "向仓库主机服务器(IP:" + reg_host.getHostIp() + ")传输镜像失败！请检查网络连接情况。", "远程制作镜像"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
					return;
				}
				regSsh.close();
				/*-----------60%：将文件从Web服务器发送到仓库服务器的临时文件夹中-----------*/
				LOGGER.info("从Web服务器-->仓库主机传输镜像文件成功！");
				/* 向用户提示已经创建完成临时保存文件夹 */
				pushMessage(userId, new MessageResult(false, "60#" + "从【Web服务器】向【仓库主机】传输镜像文件成功！", "远程制作镜像"));
				/* 清理Web服务器暂存文件夹，暂存文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
			} else {
				LOGGER.error("与仓库主机服务器(IP:" + reg_host.getHostIp() + ")未建立SSH链接！请检查网络情况。");
				pushMessage(userId, new MessageResult(false,
						"fail#" + "与仓库主机服务器(IP:" + reg_host.getHostIp() + ")未建立SSH链接！请检查网络情况。", "远程制作镜像"));
				/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
				deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
				return;
			}
		} catch (SocketException e3) {
			LOGGER.error("ssh connect error:", e3);
		} catch (IOException e3) {
			LOGGER.error("ssh connect error:", e3);
		}

		try {
			/** 执行远程制作镜像命令 **/
			Result coreresult = imageCore.makeRemoteImage(reg_host.getHostIp(), reg_host.getHostUser(),
					reg_host.getHostPwd(), reg_tmp_path, rmtimg_model.getFileName(), rmtimg_model.getImageName(),
					rmtimg_model.getImageTag(), basic);

			if (coreresult.isSuccess() == false) {
				pushMessage(userId, new MessageResult(false, "fail#" + coreresult.getMessage(), "远程制作镜像"));
				/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
				deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
				return;
			} else {
				/* Web服务器向仓库服务器传输镜像文件成功！ */
				/*-----------70%：利用仓库服务器远程制作镜像成功-----------*/
				pushMessage(userId, new MessageResult(false, "70#" + coreresult.getMessage(), "远程制作镜像"));
			}

			if (coreresult.isSuccess()) {
				Image image = new Image();
				image.setImageCreator(userId);
				image.setImageStatus((byte) Status.IMAGE.MAKED.ordinal());
				image.setAppId(rmtimg_model.getAppId());
				image.setImageName(rmtimg_model.getImageName());
				image.setImageTag(rmtimg_model.getImageTag());
				String[] imageInfos = coreresult.getMessage().split(":");
				LOGGER.info(imageInfos[0] + "----" + imageInfos[1]);
				String imageUuid = imageInfos[0];
				String imageSize = imageInfos[1];
				image.setImageUuid(imageUuid);
				image.setImageSize(imageSize);
				image.setImageType(rmtimg_model.getImageType());
				if (basic) {
					image.setAppId(null);
				}
				Integer imageId = imageService.create(image);
				boolean createResult = false;
				if (imageId > 0) {
					RegImage regImage = new RegImage();
					regImage.setImageId(imageId);
					regImage.setRegistryId(rmtimg_model.getRegistryId());
					try {
						createResult = regImageMapper.insert(regImage) > 0;
					} catch (Exception e) {
						LOGGER.error("Insert registry-image info error!", e);
						pushMessage(userId, new MessageResult(false, "fail#" + "远程制作【镜像】失败！", "远程制作镜像"));
						/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
						deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
						return;
					}
				}
				if (createResult) {
					/** 将应用-环境-镜像三元组插入到数据库对应的表中 */
					Integer appId = rmtimg_model.getAppId();
					List<Integer> envList = convertStrToIntList(rmtimg_model.getEnvIds());
					List<AppEnvImg> aeiList = new ArrayList<AppEnvImg>();
					for (int envCount = 0, envSize = envList.size(); envCount < envSize; envCount++) {
						AppEnvImg aei = new AppEnvImg(appId, envList.get(envCount), imageId);
						aeiList.add(aei);
					}
					int aeiRes = aeiService.batchInsert(aeiList);
					if (aeiRes == 0) {
						LOGGER.error("向（应用-环境-镜像）表中插入数据失败！");
					}

					/*-----------80%：制作镜像之后，写入数据库成功-----------*/
					pushMessage(userId, new MessageResult(false, "80#远程制作的镜像数据写入数据库成功！", "远程制作镜像"));
					// pushMessage(userId, new MessageResult(true, "" + imageId,
					// "IMAGEID"));
				} else {
					pushMessage(userId, new MessageResult(false, "fail#远程制作【镜像】失败：数据库操作异常！", "远程制作镜像"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
					return;
				}

				/** 镜像制作完成之后，进行推送发布处理 **/
				Image new_image = new Image();
				/** 查询获取镜像信息 **/
				new_image = imageService.loadImage(tenantId, imageId);

				/** 查询目标仓库的信息 **/
				Registry registry = new Registry();
				try {
					registry = this.getRegByImage(imageId);
				} catch (Exception e2) {
					LOGGER.error("get registry by imageid[" + imageId + "] falied!", e2);
					pushMessage(userId, new MessageResult(false, "fail#" + "通过镜像查询【主机仓库】信息失败！请核对镜像。", "远程制作镜像"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#通过镜像查询【主机仓库】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
							reg_tmp_path);
					return;
				}

				String imageName = new_image.getImageName();
				String tag = new_image.getImageTag();
				// TODO 最终存放在数据库中镜像的名称，可以根据此名称直接从镜像仓库中拉取，后续会修改
				String finalName = reg_host.getHostIp() + ":" + registry.getRegistryPort() + "/" + imageName;
				String pushImage = "localhost:" + registry.getRegistryPort() + "/" + imageName + ":" + tag;// 推送时的镜像名称
				Result tagresult = this.tagImage(reg_host.getHostId(), registry.getRegistryPort(), imageName, tag);
				/* 对于已经制作好的镜像打标 */
				if (tagresult.isSuccess()) {// 打标成功
					LOGGER.info("对于镜像(名称：" + imageName + ")打标成功。");
					Result pushResult = imageCore.pushImage(reg_host.getHostIp(), reg_host.getHostUser(),
							reg_host.getHostPwd(), pushImage);
					if (pushResult.isSuccess()) {
						LOGGER.info("push image into registry success!");
						/*-----------90%：制作镜像之后，镜像打标成功-----------*/
						/** 镜像发布：SSH登录仓库主机，并执行镜像发布脚本 **/
						pushMessage(userId, new MessageResult(false, "90#向仓库推送发布镜像数据成功！", "远程制作镜像"));
						// TODO 这里要注意的是后续要改成单纯的用户输入的镜像名，如果带有仓库的ip、
						// port则该镜像只能存放在一个仓库中。事实上同样的镜像会存在于多个仓库中。后续会对此进行修改。
						image.setImageName(finalName);
						image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
						boolean bool_result = false;
						try {
							bool_result = imageService.update(image);
						} catch (Exception e) {
							LOGGER.error("update image by imageid[" + imageId + "] falied!", e);
							pushMessage(userId, new MessageResult(false, "fail#" + "数据库更新【镜像】信息失败！", "远程制作镜像"));
							/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
							deleteRmtHostFolder(userId, "fail#数据库更新【镜像】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
									reg_tmp_path);
							return;
						}
						if (bool_result) {
							/*-----------100%：制作镜像之后，镜像打标成功-----------*/
							pushMessage(userId, new MessageResult(true, "100#向仓库制作并发布【镜像】成功！", "远程制作镜像"));
							/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
							deleteRmtHostFolder(userId, "success#制作和发布镜像成功", "仓库主机", regi_ip, regi_user, regi_pwd,
									reg_tmp_path);
							return;
						} else {
							LOGGER.error("发布【镜像】失败：数据库保存异常！");
							pushMessage(userId, new MessageResult(false, "fail#" + "发布【镜像】失败：数据库保存异常！", "远程制作镜像"));
							/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
							deleteRmtHostFolder(userId, "fail#数据库更新【镜像】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
									reg_tmp_path);
							return;
						}
					} else {
						String return_str = "fail#" + "向仓库推送发布【镜像】失败：" + pushResult.getMessage();
						LOGGER.error(return_str);
						pushMessage(userId, new MessageResult(false, return_str, "远程制作镜像"));
						/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
						deleteRmtHostFolder(userId, "fail#数据库更新【镜像】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
								reg_tmp_path);
						return;
					}
				} else {
					String return_str = "fail#" + "对于【镜像】打标失败：" + tagresult.getMessage();
					LOGGER.error(return_str);
					pushMessage(userId, new MessageResult(false, return_str, "远程制作镜像"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#数据库更新【镜像】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
							reg_tmp_path);
					return;
				}
			}
		} catch (NullPointerException npe) {
			pushMessage(userId, new MessageResult(false, "fail#" + "【仓库主机】连接中断，请检查！", "远程制作镜像"));
			LOGGER.error("仓库主机连接中断，请检查！", npe);
		} catch (Exception e1) {
			pushMessage(userId, new MessageResult(false, "fail#" + "制作【镜像】失败！", "远程制作镜像"));
			LOGGER.error("制作镜像失败！", e1);
		}
	}

	/** 获取全部公共镜像类型 */
	public JSONArray publicImages(Integer userId, Integer tenantId, ImageModel image) {
		try {
			return imageService.publicImages(userId, tenantId, image);
		} catch (Exception e) {
			LOGGER.info("获取全部镜像json数组失败！", e);
			return null;
		}
	}

	/** 导出镜像处理函数 */
	public Result exportImage(String hostIp, String hostUser, String hostPwd, String imageInfo, String imageName,
			String imageTag, String savePath, Integer imageId) {
		// Result result = imageCore.exportImage(hostIp, hostUser, hostPwd,
		// imageInfo, imageName, imageTag, savePath);
		/** (1)通过镜像的ID获取仓库的ID */
		RegImage regImg = new RegImage();
		regImg.setImageId(imageId);
		List<RegImage> riList;
		try {
			riList = regImageMapper.selectAll(regImg);
			if (riList.size() < 1) {
				return new Result(false, "镜像没有包含相应的仓库信息，请检查！");
			}
			regImg = riList.get(0);
		} catch (Exception e2) {
			LOGGER.info("通过镜像(ID:" + imageId + ")获取对应仓库信息失败！", e2);
			return new Result(false, "通过镜像(ID:" + imageId + ")获取对应仓库信息失败！");
		}

		Integer registryId = regImg.getRegistryId();
		/** (2)通过registryId获取对应主机的信息 */
		Registry selRegi = new Registry();
		selRegi.setRegistryId(registryId);
		try {
			selRegi = registryService.loadRegistry(selRegi);
		} catch (Exception e2) {
			LOGGER.info("获取仓库(ID:" + registryId + ")与镜像对应的信息失败！", e2);
			return new Result(false, "获取仓库(ID:" + registryId + ")与镜像对应的信息失败！");
		}
		Integer regiHostId = selRegi.getHostId();
		/** (3)通过主机ID获取主机的细心 */
		Host selRegiHost = new Host();
		selRegiHost.setHostId(regiHostId);
		try {
			selRegiHost = hostService.getHost(selRegiHost);
		} catch (Exception e2) {
			LOGGER.info("获取仓库主机(ID:" + regiHostId + ")信息失败！", e2);
			return new Result(false, "获取仓库主机(ID:" + regiHostId + ")信息失败！");
		}

		/** (4)在仓库主机上执行导出镜像的命令，写入到文件夹下面 ，添加从仓库中拉取镜像环节 */
		String saveImgCommand = "cd;mkdir -p /home/" + selRegiHost.getHostUser() + "/temp/;" + "docker pull "
				+ imageInfo + ";" + "docker save -o /home/" + selRegiHost.getHostUser() + "/temp/" + imageName + ":"
				+ imageTag.trim() + ".tar " + imageInfo;
		LOGGER.info("save image command in registry host:" + saveImgCommand);

		SSH regSsh = new SSH(selRegiHost.getHostIp(), selRegiHost.getHostUser(), selRegiHost.getHostPwd());
		try {
			if (regSsh.connect()) {
				regSsh.executeWithResult(saveImgCommand);
				regSsh.close();

				/** （5）将仓库的主机上保存的到处镜像文件保存到本地文件夹下 */
				String uuid = UUID.randomUUID().toString();
				/** 在本地创建临时文件夹，保存远程文件 **/
				File folder = new File("imgTar" + uuid);
				if (!(folder.exists() && folder.isDirectory())) {
					folder.mkdirs();
				}
				String localTempPath = folder.getAbsolutePath();
				regSsh.connect();
				boolean bFetchSucc = regSsh.fetchFile(
						"/home/" + selRegiHost.getHostUser() + "/temp/" + imageName + ":" + imageTag.trim() + ".tar",
						localTempPath + "/");
				/* 判断从仓库主机获取tar文件是否成功，失败则清理现场 */
				if (!bFetchSucc) {
					regSsh.close();
					deleteDir(folder);
					return new Result(false, "从仓库主机(IP:" + selRegiHost.getHostIp() + ")获取文件(" + imageName + ":"
							+ imageTag.trim() + ".tar" + ")失败！");
				}
				regSsh.close();
				regSsh.connect();
				/* 删除仓库主机上面的镜像导出文件和镜像文件 */
				regSsh.executeWithResult(
						"rm -rf /home/" + selRegiHost.getHostUser() + "/temp/;docker rmi " + imageInfo + ";");
				regSsh.close();
				LOGGER.info(
						"Fetch image.tar and image(docker rmi) file from(" + selRegiHost.getHostIp() + ") finished.");

				/** (6)从本地传输到远程的目标主机上 */
				SSH targetSsh = new SSH(hostIp, hostUser, hostPwd);
				if (targetSsh.connect()) {
					/* 尝试在远程主机上创建需要保存的文件夹 */
					targetSsh.executeWithResult("mkdir -p " + savePath + ";");
					targetSsh.close();
					targetSsh.connect();
					boolean bScpSucc = targetSsh
							.scpFile(localTempPath + "/" + imageName + ":" + imageTag.trim() + ".tar", savePath);
					/* 判断向目标主机传输tar文件是否成功，失败则清理现场 */
					if (!bScpSucc) {
						targetSsh.close();
						deleteDir(folder);
						return new Result(false,
								"向目标主机(IP:" + hostIp + ")传输文件(" + imageName + ":" + imageTag.trim() + ".tar" + ")失败！");
					}
					targetSsh.close();
					/* 删除本地的上面的镜像文件 */
					LOGGER.info("Delete temp folder(" + folder.getAbsolutePath() + ").");
					deleteDir(folder);
					LOGGER.info("Scp image.tar file to(" + hostIp + ") finished.");
				} else {
					/** 连接失败的情况下，缓存文件夹删除处理 */
					LOGGER.info("Delete temp folder(" + folder.getAbsolutePath() + ").");
					deleteDir(folder);
				}
				return new Result(true, "镜像文件导出(" + imageName + ")成功！");
			} else {
				return new Result(false, "连接数据库主机(" + selRegiHost.getHostIp() + ")失败！");
			}
		} catch (SocketException e) {
			return new Result(false, "连接数据库主机(" + selRegiHost.getHostIp() + ")失败！");
		} catch (IOException e) {
			return new Result(false, "连接数据库主机(" + selRegiHost.getHostIp() + ")失败！");
		}
	}

	/** 导入镜像处理函数 */
	public void importImage(Integer userId, Integer tenantId, RemoteImgModel rmtimg_model) {

		// 1.查询镜像仓库信息
		Registry select_registry = new Registry();
		select_registry.setRegistryId(rmtimg_model.getRegistryId());
		try {
			select_registry = registryService.loadRegistry(select_registry);
			pushMessage(userId,
					new MessageResult(false, "10#" + "查询仓库记录(ID:" + rmtimg_model.getRegistryId() + ")成功。", "版本导入"));
		} catch (Exception e3) {
			LOGGER.error("查询仓库记录(ID:" + rmtimg_model.getRegistryId() + ")失败！请核对后重试。", e3);
			pushMessage(userId, new MessageResult(false,
					"fail#" + "查询仓库记录(ID:" + rmtimg_model.getRegistryId() + ")失败！请核对后重试。", "版本导入"));
			return;
		}

		// 2.获取仓库主机节点
		Host reg_host = new Host();
		reg_host.setHostId(select_registry.getHostId());
		try {
			reg_host = hostService.getHost(reg_host);
			pushMessage(userId, new MessageResult(false, "20#" + "查询仓库主机(IP:" + reg_host.getHostIp() + ")成功。", "版本导入"));
		} catch (Exception e) {
			LOGGER.error("查询目标仓库主机(ID:" + select_registry.getHostId() + ")失败！请核对后重试。", e);
			pushMessage(userId, new MessageResult(false,
					"fail#" + "查询目标仓库主机(ID:" + select_registry.getHostId() + ")失败！请核对后重试。", "版本导入"));
			return;
		}

		// 3.在web服务器和仓库服务器上创建临时文件夹
		// 远程仓库临时目录
		String rmt_uuid_folder = UUID.randomUUID().toString();
		String reg_tmp_path = "";
		// web服务器临时目录
		String local_uuid_folder = UUID.randomUUID().toString();
		String local_tmp_path = "";
		// web服务器创建文件夹保存远程镜像文件
		File folder = new File(local_uuid_folder);
		if (!(folder.exists() && folder.isDirectory())) {
			folder.mkdirs();
		}
		local_tmp_path = folder.getAbsolutePath();
		LOGGER.info("在Web服务器创建临时文件夹:" + local_tmp_path + "成功！");
		pushMessage(userId, new MessageResult(false, "30#" + "在Web服务器创建临时文件夹:" + local_tmp_path + "成功！", "版本导入"));
		// 在仓库服务器上创建临时文件夹
		try {
			Result create_result = imageCore.createRegTempFolder(reg_host.getHostIp(), reg_host.getHostUser(),
					reg_host.getHostPwd(), rmt_uuid_folder);
			/* 在仓库服务器上创建临时文件成功， */
			if (create_result.isSuccess()) {
				reg_tmp_path = TextUtil.replaceBlank(create_result.getMessage());
				LOGGER.info("仓库服务器创建临时文件夹:" + reg_tmp_path + "成功！");
				pushMessage(userId, new MessageResult(false, "40#" + "仓库主机创建暂存目录:" + reg_tmp_path + "成功！", "版本导入"));
			} else {
				LOGGER.error("目标仓库主机(IP:" + reg_host.getHostIp() + ")创建暂存目录失败！错误信息:" + create_result.getMessage());
				pushMessage(userId, new MessageResult(false,
						"fail#" + "目标仓库主机(IP:" + reg_host.getHostIp() + ")创建暂存目录失败！错误信息:" + create_result.getMessage(),
						"版本导入"));
				/* 清理Web服务器暂存文件夹，暂存文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
				return;
			}
		} catch (Exception e2) {
			LOGGER.error("目标仓库主机(IP:" + reg_host.getHostIp() + ")创建暂存目录失败！", e2);
			pushMessage(userId,
					new MessageResult(false, "fail#" + "目标仓库主机(IP:" + reg_host.getHostIp() + ")创建暂存目录失败！", "版本导入"));
			/* 清理Web服务器暂存文件夹，暂存文件夹 */
			deleteDir(folder);
			LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
			return;
		}

		/* 当仓库主机和镜像数据主机同时均不为空时，可以进行制作镜像操作 */
		boolean basic = rmtimg_model.getImageType().equalsIgnoreCase("basic");
		/* 远程主机保存镜像文件的路径 */
		String rmt_image_path = rmtimg_model.getFileFolder() + "/" + rmtimg_model.getFileName();
		rmt_image_path = rmt_image_path.replace("\\", "/");
		/* 本地暂存文件夹 */
		local_tmp_path = local_tmp_path.replace("\\", "/");
		/* 仓库主机保存的文件夹 */
		reg_tmp_path = reg_tmp_path.replace("\\", "/");

		/** （一）首先将镜像文件从服务器上下载下来 **/
		SSH imgSsh = new SSH(rmtimg_model.getHostIP(), rmtimg_model.getHostUser(), rmtimg_model.getHostPasswd());
		try {
			if (imgSsh.connect()) {
				boolean recv_succ = imgSsh.fetchFile(rmtimg_model.getFileFolder() + "/" + rmtimg_model.getFileName(),
						local_tmp_path + "/");
				LOGGER.info("从远程文件:(" + rmtimg_model.getFileFolder() + "/" + rmtimg_model.getFileName() + ")拷贝到本地目录:("
						+ local_tmp_path + "/" + ")");
				if (!recv_succ) {
					LOGGER.error("从镜像文件所在主机(IP:" + rmtimg_model.getHostIP() + ")拷贝文件失败！请检查网络连接情况。");
					pushMessage(userId, new MessageResult(false,
							"fail#" + "从镜像文件所在主机(IP:" + rmtimg_model.getHostIP() + ")拷贝文件失败！请检查网络连接情况。", "版本导入"));
					return;
				}
				imgSsh.close();
				/*-----------50%：从镜像所在主机拷贝到Web服务器上-----------*/
				LOGGER.info("从远程主机-->Web服务器传输镜像文件成功！");
				/* 向用户提示已经创建完成临时保存文件夹 */
				pushMessage(userId, new MessageResult(false, "50#" + "从【远程主机】向【Web服务器】传输镜像文件成功！", "版本导入"));

			} else {
				LOGGER.error("与镜像文件主机(IP:" + rmtimg_model.getHostIP() + ")未建立SSH链接！请检查网络情况。");
				pushMessage(userId, new MessageResult(false,
						"fail#" + "与镜像文件主机(IP:" + rmtimg_model.getHostIP() + ")未建立SSH链接！请检查网络情况。", "版本导入"));
				/* 清理Web服务器暂存文件夹，暂存文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
				return;
			}
		} catch (SocketException e3) {
			LOGGER.error("ssh connect error:", e3);
		} catch (IOException e3) {
			LOGGER.error("ssh connect error:", e3);
		}

		/** (二)将文件从本地传输到远程的仓库服务器中 **/
		String regi_ip = reg_host.getHostIp();
		String regi_user = reg_host.getHostUser();
		String regi_pwd = reg_host.getHostPwd();

		SSH regSsh = new SSH(reg_host.getHostIp(), reg_host.getHostUser(), reg_host.getHostPwd());
		try {
			if (regSsh.connect()) {
				boolean send_succ = regSsh.scpFile(local_tmp_path + "/" + rmtimg_model.getFileName(),
						reg_tmp_path + "/");

				if (!send_succ) {
					LOGGER.error("向仓库主机服务器(IP:" + reg_host.getHostIp() + ")传输镜像失败！请检查网络连接情况。");
					pushMessage(userId, new MessageResult(false,
							"fail#" + "向仓库主机服务器(IP:" + reg_host.getHostIp() + ")传输镜像失败！请检查网络连接情况。", "版本导入"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
					return;
				}
				regSsh.close();
				/*-----------60%：将文件从Web服务器发送到仓库服务器的临时文件夹中-----------*/
				LOGGER.info("从Web服务器-->仓库主机传输镜像文件成功！");
				/* 向用户提示已经创建完成临时保存文件夹 */
				pushMessage(userId, new MessageResult(false, "60#" + "从【Web服务器】向【仓库主机】传输镜像文件成功！", "版本导入"));
				/* 清理Web服务器暂存文件夹，暂存文件夹 */
				deleteDir(folder);
				LOGGER.info("删除Web服务器暂存文件夹(" + local_tmp_path + "/)操作成功！");
			} else {
				LOGGER.error("与仓库主机服务器(IP:" + reg_host.getHostIp() + ")未建立SSH链接！请检查网络情况。");
				pushMessage(userId, new MessageResult(false,
						"fail#" + "与仓库主机服务器(IP:" + reg_host.getHostIp() + ")未建立SSH链接！请检查网络情况。", "版本导入"));
				/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
				deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
				return;
			}
		} catch (SocketException e3) {
			LOGGER.error("ssh connect error:", e3);
		} catch (IOException e3) {
			LOGGER.error("ssh connect error:", e3);
		}

		try {
			/** 执行版本导入命令 **/
			Result coreresult = imageCore.makeRemoteImage(reg_host.getHostIp(), reg_host.getHostUser(),
					reg_host.getHostPwd(), reg_tmp_path, rmtimg_model.getFileName(), rmtimg_model.getImageName(),
					rmtimg_model.getImageTag(), basic);

			if (coreresult.isSuccess() == false) {
				pushMessage(userId, new MessageResult(false, "fail#" + coreresult.getMessage(), "版本导入"));
				/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
				deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
				return;
			} else {
				/* Web服务器向仓库服务器传输镜像文件成功！ */
				/*-----------70%：利用仓库服务器版本导入成功-----------*/
				pushMessage(userId, new MessageResult(false, "70#" + coreresult.getMessage(), "版本导入"));
			}

			if (coreresult.isSuccess()) {
				Image image = new Image();
				image.setImageCreator(userId);
				image.setImageStatus((byte) Status.IMAGE.MAKED.ordinal());
				image.setAppId(rmtimg_model.getAppId());
				image.setImageName(rmtimg_model.getImageName());
				image.setImageTag(rmtimg_model.getImageTag());
				String[] imageInfos = coreresult.getMessage().split(":");
				LOGGER.info("imageTag:" + imageInfos[0] + "----" + "imageUuid:" + imageInfos[1] + "----" + "imageSize:"
						+ imageInfos[2]);
				String imageUuid = imageInfos[1];
				String imageSize = imageInfos[2];
				image.setImageUuid(imageUuid);
				image.setImageSize(imageSize);

				image.setImageType("APP");
				Integer imageId = imageService.create(image);
				boolean createResult = false;
				if (imageId > 0) {
					RegImage regImage = new RegImage();
					regImage.setImageId(imageId);
					regImage.setRegistryId(rmtimg_model.getRegistryId());
					try {
						createResult = regImageMapper.insert(regImage) > 0;
					} catch (Exception e) {
						LOGGER.error("Insert registry-image info error!", e);
						pushMessage(userId, new MessageResult(false, "fail#" + "远程制作【镜像】失败！", "版本导入"));
						/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
						deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
						return;
					}
				}
				if (createResult) {
					/** 将应用-环境-镜像三元组插入到数据库对应的表中 */
					Integer appId = rmtimg_model.getAppId();
					List<Integer> envList = convertStrToIntList(rmtimg_model.getEnvIds());
					List<AppEnvImg> aeiList = new ArrayList<AppEnvImg>();
					for (int envCount = 0, envSize = envList.size(); envCount < envSize; envCount++) {
						AppEnvImg aei = new AppEnvImg(appId, envList.get(envCount), imageId);
						aeiList.add(aei);
					}
					int aeiRes = aeiService.batchInsert(aeiList);
					if (aeiRes == 0) {
						LOGGER.error("向（应用-环境-镜像）表中插入数据失败！");
					}

					/*-----------80%：制作镜像之后，写入数据库成功-----------*/
					pushMessage(userId, new MessageResult(false, "80#远程制作的镜像数据写入数据库成功！", "版本导入"));
					// pushMessage(userId, new MessageResult(true, "" + imageId,
					// "IMAGEID"));
				} else {
					pushMessage(userId, new MessageResult(false, "fail#远程制作【镜像】失败：数据库操作异常！", "版本导入"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#制作镜像失败", "仓库主机", regi_ip, regi_user, regi_pwd, reg_tmp_path);
					return;
				}

				/** 镜像制作完成之后，进行推送发布处理 **/
				Image new_image = new Image();
				/** 查询获取镜像信息 **/
				new_image = imageService.loadImage(tenantId, imageId);

				/** 查询目标仓库的信息 **/
				Registry registry = new Registry();
				try {
					registry = this.getRegByImage(imageId);
				} catch (Exception e2) {
					LOGGER.error("get registry by imageid[" + imageId + "] falied!", e2);
					pushMessage(userId, new MessageResult(false, "fail#" + "通过镜像查询【主机仓库】信息失败！请核对镜像。", "版本导入"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#通过镜像查询【主机仓库】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
							reg_tmp_path);
					return;
				}

				String imageName = new_image.getImageName();
				String tag = new_image.getImageTag();
				imageUuid = new_image.getImageUuid();
				// TODO 最终存放在数据库中镜像的名称，可以根据此名称直接从镜像仓库中拉取，后续会修改
				String finalName = reg_host.getHostIp() + ":" + registry.getRegistryPort() + "/" + imageName;
				String pushImage = "localhost:" + registry.getRegistryPort() + "/" + imageName + ":" + tag;// 推送时的镜像名称
				Result tagresult = this.tagLoadImage(reg_host.getHostId(), registry.getRegistryPort(), imageUuid,
						imageName, tag);
				/* 对于已经制作好的镜像打标 */
				if (tagresult.isSuccess()) {// 打标成功
					LOGGER.info("对于镜像(名称：" + imageName + ")打标成功。");
					Result pushResult = imageCore.pushImage(reg_host.getHostIp(), reg_host.getHostUser(),
							reg_host.getHostPwd(), pushImage);
					if (pushResult.isSuccess()) {
						LOGGER.info("push image into registry success!");
						/*-----------90%：制作镜像之后，镜像打标成功-----------*/
						/** 镜像发布：SSH登录仓库主机，并执行镜像发布脚本 **/
						pushMessage(userId, new MessageResult(false, "90#向仓库推送发布镜像数据成功！", "版本导入"));
						// TODO 这里要注意的是后续要改成单纯的用户输入的镜像名，如果带有仓库的ip、
						// port则该镜像只能存放在一个仓库中。事实上同样的镜像会存在于多个仓库中。后续会对此进行修改。
						image.setImageName(finalName);
						image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
						boolean bool_result = false;
						try {
							bool_result = imageService.update(image);
						} catch (Exception e) {
							LOGGER.error("update image by imageid[" + imageId + "] falied!", e);
							pushMessage(userId, new MessageResult(false, "fail#" + "数据库更新【镜像】信息失败！", "版本导入"));
							/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
							deleteRmtHostFolder(userId, "fail#数据库更新【镜像】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
									reg_tmp_path);
							return;
						}
						if (bool_result) {
							/*-----------100%：制作镜像之后，镜像打标成功-----------*/
							pushMessage(userId, new MessageResult(true, "100#向仓库制作并发布【镜像】成功！", "版本导入"));
							/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
							deleteRmtHostFolder(userId, "success#制作和发布镜像成功", "仓库主机", regi_ip, regi_user, regi_pwd,
									reg_tmp_path);
							return;
						} else {
							LOGGER.error("发布【镜像】失败：数据库保存异常！");
							pushMessage(userId, new MessageResult(false, "fail#" + "发布【镜像】失败：数据库保存异常！", "版本导入"));
							/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
							deleteRmtHostFolder(userId, "fail#数据库更新【镜像】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
									reg_tmp_path);
							return;
						}
					} else {
						String return_str = "fail#" + "向仓库推送发布【镜像】失败：" + pushResult.getMessage();
						LOGGER.error(return_str);
						pushMessage(userId, new MessageResult(false, return_str, "版本导入"));
						/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
						deleteRmtHostFolder(userId, "fail#数据库更新【镜像】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
								reg_tmp_path);
						return;
					}
				} else {
					String return_str = "fail#" + "对于【镜像】打标失败：" + tagresult.getMessage();
					LOGGER.error(return_str);
					pushMessage(userId, new MessageResult(false, return_str, "版本导入"));
					/* 在制作镜像失败的情况下，删除掉仓库主机上的暂存文件夹 */
					deleteRmtHostFolder(userId, "fail#数据库更新【镜像】信息失败", "仓库主机", regi_ip, regi_user, regi_pwd,
							reg_tmp_path);
					return;
				}
			}
		} catch (NullPointerException npe) {
			pushMessage(userId, new MessageResult(false, "fail#" + "【仓库主机】连接中断，请检查！", "版本导入"));
			LOGGER.error("仓库主机连接中断，请检查！", npe);
		} catch (Exception e1) {
			pushMessage(userId, new MessageResult(false, "fail#" + "版本导入失败！", "版本导入"));
			LOGGER.error("版本导入失败！", e1);
		}
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	/** (持续集成)远程传入参数插入镜像数据 */
	public JSONObject insertImg(RemoteImgModel rmtImage) {
		JSONObject retJson = new JSONObject();

		Image image = new Image();
		image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
		image.setAppId(rmtImage.getAppId());
		image.setImageName(rmtImage.getImageName());
		image.setImageTag(rmtImage.getImageTag());
		image.setImageUuid(rmtImage.getImageUuid());
		image.setImageSize(rmtImage.getImageSize());
		image.setImageCreator(rmtImage.getUserId());
		image.setImageType("APP");
		Integer imageId = imageService.create(image);

		/* 返回是否插入镜像数据成功json消息 */
		if (imageId > 0) {
			retJson.put("imageId", imageId);
			retJson.put("status", "success");
		} else {
			retJson.put("status", "failure");
		}
		return retJson;
	}

	/** (持续集成)远程传入参数更新镜像数据 */
	public JSONObject updateImg(RemoteImgModel rmtImage) {
		JSONObject retJson = new JSONObject();

		Image image = new Image();
		// image.setImageStatus((byte) Status.IMAGE.MAKED.ordinal());
		/* (1)首先获取镜像的ID信息 */
		if (rmtImage.getImageId().equals(null)) {
			retJson.put("status", "failure");
			retJson.put("message", "image id is null!");
			return retJson;
		}

		/* 检测插入应用ID信息 */
		if (!rmtImage.getAppId().equals(null)) {
			image.setAppId(rmtImage.getAppId());
		}

		/* 检测镜像名称信息 */
		if (!rmtImage.getImageName().equals(null)) {
			image.setImageName(rmtImage.getImageName());
		}

		/* 检测获取镜像标签 */
		if (!rmtImage.getImageTag().equals(null)) {
			image.setImageTag(rmtImage.getImageTag());
		}

		/* 检测获取镜像UUID */
		if (!rmtImage.getImageUuid().equals(null)) {
			image.setImageUuid(rmtImage.getImageUuid());
		}

		/* 检测镜像大小 */
		if (!rmtImage.getImageSize().equals(null)) {
			image.setImageSize(rmtImage.getImageSize());
		}

		/* 检测用户ID信息 */
		if (!rmtImage.getUserId().equals(null)) {
			image.setImageCreator(rmtImage.getUserId());
		}

		// image.setImageType("APP");
		boolean isSuccess = false;
		try {
			isSuccess = imageService.update(image);
		} catch (Exception e) {
			LOGGER.error("更新镜像(ID:" + rmtImage.getImageId() + ")数据失败！", e);
			retJson.put("status", "failed");
			retJson.put("message", "update image exception!");
		}

		/* 返回是否插入镜像数据成功json消息 */
		if (isSuccess) {
			retJson.put("status", "success");
		} else {
			retJson.put("status", "failed");
		}
		return retJson;
	}

	/** (持续集成)远程查询镜像数据 */
	public JSONObject selectImg(Integer imageId) {
		JSONObject retJson = new JSONObject();
		// TODO 后期添加租户限制部分
		try {
			Image selImage = imageService.loadImage(null, imageId);
			retJson = (JSONObject) JSONObject.toJSON(selImage);
			retJson.put("status", "success");
			return retJson;
		} catch (Exception e) {
			LOGGER.error("通过镜像ID(" + imageId + ")查询镜像内容异常！", e);
			retJson.put("status", "failure");
			retJson.put("message", "image id (" + imageId + ") exception!");
			return retJson;
		}
	}

	/** (持续集成)远程查询镜像数据 */
	public JSONObject deleteImg(Integer imageId) {
		JSONObject retJson = new JSONObject();
		// TODO 后期添加租户限制部分
		boolean isSuccess = imageService.delete(imageId);
		if (isSuccess) {
			retJson.put("status", "success");
			retJson.put("message", "delete image (id:" + imageId + ") success!");
			return retJson;
		} else {
			retJson.put("status", "failure");
			retJson.put("message", "delete image (id:" + imageId + ") failed!");
			return retJson;
		}
	}
}
