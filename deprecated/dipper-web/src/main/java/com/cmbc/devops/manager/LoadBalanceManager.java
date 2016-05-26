package com.cmbc.devops.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.config.LoadBalanceConfig;
import com.cmbc.devops.constant.LoadBalanceConstants;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.core.LoadBalanceCore;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.ConPort;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.LoadBalance;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.model.LoadBalanceTemplate;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ConportService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.LoadBalanceService;
import com.cmbc.devops.util.TextUtil;
import com.cmbc.devops.util.TimeUtils;

/**
 * date：2015年9月11日 上午10:58:03 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：LoadBalanceManager.java description：
 */
@Component
public class LoadBalanceManager {

	@Autowired
	private LoadBalanceService loadBalanceService;
	@Autowired
	private AppService appService;
	@Autowired
	private HostService hostService;
	@Autowired
	private ConportService conportService;
	@Autowired
	private LoadBalanceCore loadBalanceCore;

	private static final Logger LOGGER = Logger.getLogger(LoadBalanceManager.class);

	/**
	 * @author langzi
	 * @param loadBalance
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	public Result createLoadBalance(LoadBalance loadBalance) {
		loadBalance.setLbCreatetime(new Date());
		int result;
		// 先判断需要添加到负载的主机或者备份主机是否存在
		Host info = new Host();
		if (loadBalance.getLbMainHost() != null) {
			info.setHostId(loadBalance.getLbMainHost());
			Host host = null;
			try {
				host = hostService.getHost(info);
			} catch (Exception e) {
				LOGGER.error("get main host[" + loadBalance.getLbMainHost() + "] failed", e);
				return new Result(false, "添加负载均衡失败，获取主服务器信息异常！");
			}
			if (host == null) {
				LOGGER.error("main host is null！");
				return new Result(false, "添加负载均衡失败,主服务器不存在！");
			}
		}
		if (loadBalance.getLbBackupHost() != null) {
			info.setHostId(loadBalance.getLbBackupHost());
			Host host = null;
			try {
				host = hostService.getHost(info);
			} catch (Exception e) {
				LOGGER.error("get backup host[" + loadBalance.getLbBackupHost() + "] failed", e);
				return new Result(false, "添加负载均衡失败，获取备服务器信息异常！");
			}
			if (host == null) {
				LOGGER.error("backup host is null！");
				return new Result(false, "添加负载均衡失败,备服务器不存在！");
			}
		}

		try {
			// 是否添加nginx位置有效性校验
			result = loadBalanceService.addLoadBalance(loadBalance);
			if (result > 0) {
				LOGGER.info("Create loadbalance success");
				return new Result(true, "添加负载均衡成功！");
			} else {
				LOGGER.error("Create loadbalance failed");
				return new Result(false, "添加负载均衡失败！");
			}
		} catch (Exception e) {
			LOGGER.error("Create loadBalance failed", e);
			return new Result(false, "添加负载均衡失败！");
		}
	}

	/**
	 * @author langzi
	 * @param loadBalance
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	public Result modifyLoadBalance(LoadBalance loadBalance) {
		try {
			int result = loadBalanceService.updateBalance(loadBalance);
			if (result > 0) {
				LOGGER.info("Update loadbalance success");
				return new Result(true, "更新负载均衡成功！");
			} else {
				LOGGER.error("Update loadbalance failed");
				return new Result(false, "更新负载均衡失败！");
			}
		} catch (Exception e) {
			LOGGER.error("Update loadbalance failed!", e);
			return new Result(false, "更新负载均衡失败！");
		}
	}

	/**
	 * @author langzi
	 * @param appIds
	 * @param lbId
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	public Result addLB(Integer tenantId, String[] appIds, String lbId, int fileFlag) {
		List<LoadBalance> lbs = new ArrayList<LoadBalance>();
		try {
			// 更新应用的balanceId
			for (String appId : appIds) {
				App app = new App();
				app = appService.findAppById(tenantId, Integer.parseInt(appId));
				app.setBalanceId(Integer.parseInt(lbId));
				int i = appService.modifyApp(app);
				if (i > 0) {
					continue;
				} else {
					break;
				}
			}
			LoadBalance lb = loadBalanceService.getLoadBalance(Integer.parseInt(lbId));
			if (lb != null) {
				lbs.add(lb);
			}
			Result result = reloadBalance(lbs, fileFlag);
			// 失败回滚
			if (!result.isSuccess()) {
				for (String appId : appIds) {
					App app = new App();
					app.setAppId(Integer.parseInt(appId));
					app.setBalanceId(null);
					int i = appService.modifyApp(app);
					if (i > 0) {
						continue;
					} else {
						break;
					}
				}
			}
			return result;
		} catch (Exception e) {
			return new Result(false, "添加应用到负载均衡失败");
		}
	}

	/**
	 * @author langzi
	 * @param appIds
	 * @param lbId
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	public Result removeApp(Integer tenantId, String[] appIds, String lbId, int fileFlag) {
		List<LoadBalance> lbs = new ArrayList<LoadBalance>();
		try {
			// 更新应用的balanceId为null
			for (String appId : appIds) {
				App app = new App();
				app = appService.findAppById(tenantId, Integer.parseInt(appId));
				app.setBalanceId(null);
				int i = appService.modifyApp(app);
				if (i > 0) {
					continue;
				} else {
					break;
				}
			}
			LoadBalance lb = loadBalanceService.getLoadBalance(Integer.parseInt(lbId));
			if (lb != null) {
				lbs.add(lb);
			}
			Result result = reloadBalance(lbs, fileFlag);
			// 失败回滚
			if (!result.isSuccess()) {
				// 移出应用负载失败，把应用重新关联到移出前的负载
				for (String appId : appIds) {
					App app = new App();
					app.setAppId(Integer.parseInt(appId));
					app.setBalanceId(Integer.parseInt(lbId));
					int i = appService.modifyApp(app);
					if (i > 0) {
						continue;
					} else {
						break;
					}
				}
			}
			return result;
		} catch (Exception e) {
			return new Result(false, "添加应用到负载均衡失败");
		}
	}

	/**
	 * @author langzi
	 * @param lbIds
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	public Result updateLB(String[] lbIds, int fileFlag) {
		List<LoadBalance> lbs = new ArrayList<LoadBalance>();
		if (lbIds.length > 0) {
			for (String lbId : lbIds) {
				try {
					LoadBalance lb = loadBalanceService.getLoadBalance(Integer.parseInt(lbId));
					if (lb != null) {
						lbs.add(lb);
					}
				} catch (Exception e) {
					LOGGER.error("Get load balance infos failed", e);
				}
			}
			return reloadBalance(lbs, fileFlag);
		} else {
			return new Result(false, "更新负载均衡不存在！");
		}

	}

	/**
	 * @author langzi
	 * @param conIds
	 * @param fileFlag
	 *            0代表只读，1代表reload
	 * @param actionFlag
	 *            0代表添加，1代表删除
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	public Result updateLBofContainer(String[] conIds, int fileFlag, int actionFlag) {
		List<LoadBalance> lbs = null;
		try {
			lbs = loadBalanceService.listLoadBalanceByConId(conIds);
		} catch (Exception e) {
			LOGGER.error("Get load balance error", e);
		}
		if (!lbs.isEmpty()) {
			if (actionFlag == 0) {
				return reloadBalance(lbs, fileFlag);
			} else {
				return reloadBalance(lbs, fileFlag, conIds);
			}
		} else {
			return new Result(false, "应用不在任何负载中，请先加入负载！");
		}
	}

	/**
	 * @author langzi
	 * @param lbIds
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	public Result removeLoadBalance(String[] lbIds) {
		String lbNames = "";

		for (String lbId : lbIds) {
			List<App> appList = new ArrayList<App>();
			try {
				appList = appService.getAppByLbId(Integer.parseInt(lbId));
			} catch (Exception e) {
				LOGGER.error("get application by LBid[" + lbId + "] falied!", e);
				return new Result(false, "删除负载均衡失败!");
			}
			if (appList.size() > 0) {
				// 获取负载对象，返回的错误信息中需要显示负载名称
				LoadBalance lbInfo = null;
				try {
					lbInfo = loadBalanceService.getLoadBalance(Integer.valueOf(lbId));
					if (lbInfo == null) {
						LOGGER.error("get loadBalance[" + lbId + "] failed!");
						return new Result(false, "删除负载均衡失败!");
					}
				} catch (Exception e) {
					LOGGER.error("get loadBalance[" + lbId + "] failed!", e);
				}
				lbNames += lbInfo.getLbName() + ",";
			}
		}
		if (!"".equals(lbNames)) {
			lbNames = lbNames.substring(0, lbNames.length() - 1);
			return new Result(false, lbNames + "中存在应用，请清空应用，再删除负载！");
		}
		try {
			int result = loadBalanceService.removeBalance(lbIds);
			if (result > 0) {
				LOGGER.info("Remove loadbalance success！");
				return new Result(true, "删除负载均衡成功！");
			} else {
				LOGGER.error("Remove loadbalance failed！");
				return new Result(false, "删除负载均衡失败！");
			}
		} catch (Exception e) {
			LOGGER.error("Remove loadbalance error！", e);
			return new Result(false, "删除负载均衡失败！");
		}
	}

	public Map<String, Object> detail(Integer id) {
		LoadBalance lb = null;
		Map<String, Object> lbMap = new HashMap<String, Object>();
		try {
			lb = loadBalanceService.getLoadBalance(id);
			if (lb != null) {
				lbMap.put("lbId", lb.getLbId());
				lbMap.put("lbName", lb.getLbName());
				lbMap.put("lbStatus", lb.getLbStatus() == Status.LOADBALANCE.NORMAL.ordinal() ? "正常" : "异常");
				List<App> apps = appService.getAppByLbId(id);
				String appNames = "";
				if (apps.size() > 0) {
					for (App app : apps) {
						appNames += app.getAppName() + ",";
					}
				}
				lbMap.put("appInfo", appNames);
				Host host = hostService.loadHost(lb.getLbMainHost());
				lbMap.put("hostServer", host != null ? host.getHostName() : "");
				lbMap.put("hostConf", lb.getLbMainConf());
				host = hostService.loadHost(lb.getLbBackupHost());
				lbMap.put("backupServer", host != null ? host.getHostName() : "");
				lbMap.put("backupConf", lb.getLbBackupConf());
				lbMap.put("lbDesc", lb.getLbDesc());
				lbMap.put("createTime", TimeUtils.formatTime(lb.getLbCreatetime()));
			}
		} catch (Exception e) {
			LOGGER.error("Get loadBalance infos error", e);
		}
		return lbMap;
	}

	/**
	 * @author langzi
	 * @param lbName
	 * @return
	 * @version 1.0 2015年10月21日
	 */
	public boolean checkName(String lbName) {
		try {
			return loadBalanceService.getLoadBalance(lbName) == null ? true : false;
		} catch (Exception e) {
			LOGGER.error("Get loadbalance from db error", e);
			return false;
		}
	}

	/**
	 * 更新负载均衡列表
	 * 
	 * @author langzi
	 * @param List<loadBalance>
	 *            负载均衡列表
	 * @param int
	 *            flag
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	private Result reloadBalance(List<LoadBalance> lbs, int fileFlag) {
		List<LoadBalanceTemplate> lbTemplates = getlbTemplate(lbs);
		if (fileFlag == 0) {
			String fileName = "";
			for (LoadBalanceTemplate balanceTemp : lbTemplates) {
				fileName += getNginxFileName(balanceTemp) + "#";
			}
			return new Result(true, fileName);
		} else {
			try {
				if (!lbTemplates.isEmpty()) {
					Integer successNum = loadBalanceCore.reloadBalance(lbTemplates);
					if (successNum == 0) {
						return new Result(false, "加载负载均衡失败!");
					}
					if (successNum == lbTemplates.size()) {
						LOGGER.info("Update loadbalance success");
						return new Result(true, "加载负载均衡成功！");
					} else {
						LOGGER.warn("Update loadbalance failed");
						return new Result(true, "部分负载均衡加载成功！");
					}
				} else {
					LOGGER.info("Update loadbalance success:no application to update");
					return new Result(false, "负载中不存在可用的应用，请先添加应用!");
				}

			} catch (Exception e) {
				LOGGER.error("Update loadbalance failed!", e);
				return new Result(false, "加载负载均衡失败！");
			}
		}
	}

	private Result reloadBalance(List<LoadBalance> lbs, int fileFlag, String[] conIds) {
		List<LoadBalanceTemplate> lbTemplates = getlbTemplate(lbs, conIds);
		if (fileFlag == 0) {
			String fileName = "";
			for (LoadBalanceTemplate balanceTemp : lbTemplates) {
				fileName += getNginxFileName(balanceTemp) + "#";
			}
			return new Result(true, fileName);
		} else {
			try {
				if (!lbTemplates.isEmpty()) {
					Integer successNum = loadBalanceCore.reloadBalance(lbTemplates);
					if (successNum == 0) {
						return new Result(false, "加载负载均衡失败!");
					}
					if (successNum == lbTemplates.size()) {
						LOGGER.info("Update loadbalance success");
						return new Result(true, "加载负载均衡成功！");
					} else {
						LOGGER.warn("Update loadbalance failed");
						return new Result(true, "部分负载均衡加载成功！");
					}
				} else {
					LOGGER.info("Update loadbalance success:no application to update");
					return new Result(false, "负载中不存在可用的应用，请先添加应用!");
				}

			} catch (Exception e) {
				LOGGER.error("Update loadbalance failed!", e);
				return new Result(false, "加载负载均衡失败！");
			}
		}
	}

	/**
	 * @author langzi
	 * @param balanceTemp
	 * @return
	 * @version 1.0 2015年10月28日
	 */
	private String getNginxFileName(LoadBalanceTemplate balanceTemp) {
		Properties p = new Properties();
		p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, balanceTemp.getLocalFile());
		Velocity.init(p);
		VelocityContext context = new VelocityContext();
		String fileName = balanceTemp.getLocalFile() + "nginx-" + UUID.randomUUID() + ".conf";
		// 组合配置信息
		context.put("upstream", balanceTemp.getUpStream());
		context.put("application_location", balanceTemp.getLocation());
		// 加载配置文件
		Template template = null;
		try {
			template = Velocity.getTemplate("nginx_temp.conf");
			// 生成新的配置文件
			File file = new File(fileName);
			if (!file.exists()) {
				// 不存在则创建新文件
				file.createNewFile();
			}
			// 替换文件内容
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter writer = new BufferedWriter(fw);
			template.merge(context, writer);
			writer.flush();
			writer.close();
			file.delete();
		} catch (Exception e) {
			LOGGER.error("Get nginx template infos error", e);
		}
		return fileName;
	}

	/**
	 * @author langzi
	 * @param fileName
	 * @return
	 * @version 1.0 2015年10月28日 Read nginx conf infos
	 */
	public String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		String content = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				content += "<p>" + tempString + "\n</p>";
			}
			reader.close();
		} catch (IOException e) {
			LOGGER.info("File is not exist", e);
			content = "没有内容";
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return content;
	}

	/**
	 * @author langzi
	 * @param lb
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	private List<LoadBalanceTemplate> getlbTemplate(List<LoadBalance> lbs) {

		List<LoadBalanceTemplate> lbTemplates = new ArrayList<LoadBalanceTemplate>();

		// 1.定义hostlist存储所有与负载有关联的主机（包括备机）
		Set<Host> hostset = new HashSet<Host>();
		List<Host> lbHosts = null;
		for (LoadBalance loadBalance : lbs) {
			lbHosts = new ArrayList<Host>();
			try {
				lbHosts = hostService.listAllHostByLBId(loadBalance.getLbId());
			} catch (Exception e) {
				LOGGER.error("get host list by lbid[" + loadBalance.getLbId() + "] error!", e);
				return null;
			}
			hostset.addAll(lbHosts);
		}

		// 2.通过主机列表获取该主机上的负载列表，并进行负载更新
		for (Host host : hostset) {
			// 标志负载配置文件只加载一次。（一个主机只有一个负载）
			boolean flag = true;

			LoadBalanceTemplate maintemplate = new LoadBalanceTemplate();
			// a.获取主机下负载列表
			List<LoadBalance> lblist = new ArrayList<LoadBalance>();
			try {
				lblist = loadBalanceService.listLoadBalanceByHostId(host.getHostId());
			} catch (Exception e) {
				LOGGER.error("get list LoadBalance By HostId[" + host.getHostId() + "] error!", e);
				return null;
			}

			// b.遍历负载，获取应用列表
			String location = "";
			String upstream = "";
			if (lblist.size() > 0) {
				for (LoadBalance lb : lblist) {
					// 获取负载中所有应用
					List<App> apps = new ArrayList<App>();
					try {
						apps = appService.getAppByLbId(lb.getLbId());
					} catch (Exception e1) {
						LOGGER.error("get application by Lbid[" + lb.getLbId() + "] falied!", e1);
						return null;
					}
					// c.遍历应用,拼接配置文件
					if (apps.size() > 0) {
						for (App app : apps) {
							String upstreamTemp = "";
							String locationTemp = "";
							try {
								// 根据应用id获取这个应用中容器的端口列表
								List<ConPort> ports = conportService.listConPortsByAppId(app.getAppId());
								upstreamTemp = "upstream ";
								upstreamTemp += app.getAppName();
								upstreamTemp += "{\n";
								if (ports.size() > 0) {
									for (ConPort port : ports) {
										if (port.getPriPort().equals(String.valueOf(app.getAppPriPort()))) {
											upstreamTemp += "server " + port.getConIp() + ":" + port.getPubPort()
													+ ";\n";
										}
									}
								}
								upstreamTemp += "server 127.0.0.1:8080 down;\n";
								upstreamTemp += "}\n";
								LOGGER.debug("nginx config file upstream:" + upstream);
								locationTemp = "location ^~/";
								/** @2016年2月14日，将Nginx中的location双斜线修正为单斜线 */
								String app_url = app.getAppUrl();
								if (!app_url.startsWith("/")) {
									locationTemp += app_url;
								} else {
									locationTemp += app_url.substring(1);
								}
								locationTemp += "/{\n";
								locationTemp += "proxy_set_header X-Real-IP $remote_addr;\n";
								locationTemp += "proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504 http_404;\n";
								locationTemp += "proxy_pass http://";
								locationTemp += app.getAppName();
								locationTemp += "/;\n}\n";
								LOGGER.debug("nginx config file location:" + locationTemp);
								upstream += upstreamTemp;
								location += locationTemp;
							} catch (Exception e) {
								LOGGER.error("Get container port infos error", e);
							}
						}
					} else {
						// 如果负载中没有应用则配置文件不含应用信息
						String upstreamTemp = "";
						String locationTemp = "";
						try {

							LOGGER.debug("nginx config file upstream:" + upstream);
							locationTemp = "location /";
							locationTemp += " {\n";
							locationTemp += "proxy_set_header X-Real-IP $remote_addr;\n";
							locationTemp += "proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504 http_404;\n";
							locationTemp += "}\n";
							LOGGER.debug("nginx config file location:" + locationTemp);
							upstream += upstreamTemp;
							location += locationTemp;
						} catch (Exception e) {
							LOGGER.error("Get container port infos error", e);
						}
					}
					// 负载主机配置路径
					if (flag) {
						maintemplate.setServerConfPath(lb.getLbMainConf());
						flag = false;
					}
				}
			}
			// when app does not contain any container，not update LB
			if (!upstream.isEmpty() || !location.isEmpty()) {
				maintemplate.setUpStream(upstream);
				maintemplate.setLocation(location);
				maintemplate.setLocalFile(LoadBalanceConfig.getValue(LoadBalanceConstants.BALANCE_FILE));
				maintemplate.setLocalTemplate(LoadBalanceConfig.getValue(LoadBalanceConstants.BALANCE_TEMP));
				maintemplate.setHostModel(getHostModel(host.getHostId()));
				lbTemplates.add(maintemplate);
			}
		}
		return lbTemplates;
	}

	private List<LoadBalanceTemplate> getlbTemplate(List<LoadBalance> lbs, String[] conIds) {

		List<LoadBalanceTemplate> lbTemplates = new ArrayList<LoadBalanceTemplate>();

		// 1.定义hostlist存储所有与负载有关联的主机（包括备机）
		Set<Host> hostset = new HashSet<Host>();
		List<Host> lbHosts = null;
		for (LoadBalance loadBalance : lbs) {
			lbHosts = new ArrayList<Host>();
			try {
				lbHosts = hostService.listAllHostByLBId(loadBalance.getLbId());
			} catch (Exception e) {
				LOGGER.error("get host list by lbid[" + loadBalance.getLbId() + "] error!", e);
				return null;
			}
			hostset.addAll(lbHosts);
		}

		// 2.通过主机列表获取该主机上的负载列表，并进行负载更新
		for (Host host : hostset) {
			// 标志负载配置文件只加载一次。（一个主机只有一个负载）
			boolean flag = true;

			LoadBalanceTemplate maintemplate = new LoadBalanceTemplate();
			// a.获取主机下负载列表
			List<LoadBalance> lblist = new ArrayList<LoadBalance>();
			try {
				lblist = loadBalanceService.listLoadBalanceByHostId(host.getHostId());
			} catch (Exception e) {
				LOGGER.error("get list LoadBalance By HostId[" + host.getHostId() + "] error!", e);
				return null;
			}

			// b.遍历负载，获取应用列表
			String location = "";
			String upstream = "";
			if (lblist.size() > 0) {
				for (LoadBalance lb : lblist) {
					// 获取负载中所有应用
					List<App> apps = new ArrayList<App>();
					try {
						apps = appService.getAppByLbId(lb.getLbId());
					} catch (Exception e1) {
						LOGGER.error("get application by Lbid[" + lb.getLbId() + "] falied!", e1);
						return null;
					}
					// c.遍历应用,拼接配置文件
					if (apps.size() > 0) {
						for (App app : apps) {
							String upstreamTemp = "";
							String locationTemp = "";
							try {
								// 根据应用id获取这个应用中容器的端口列表
								List<ConPort> ports = conportService.listConPortsByAppId(app.getAppId());
								upstreamTemp = "upstream ";
								upstreamTemp += app.getAppName();
								upstreamTemp += "{\n";
								if (ports.size() > 0) {
									for (ConPort port : ports) {
										String conId = String.valueOf(port.getContainerId());
										if (TextUtil.isIn(conId, conIds)) {
											continue;
										}
										if (port.getPriPort().equals(app.getAppPriPort().toString())) {
											upstreamTemp += "server " + port.getConIp() + ":" + port.getPubPort()
													+ ";\n";
										}
									}
								}
								upstreamTemp += "server 127.0.0.1:8080 down;\n";
								upstreamTemp += "}\n";
								LOGGER.debug("nginx config file upstream:" + upstream);
								locationTemp = "location ^~/";
								/** @2016年2月14日，将Nginx中的location双斜线修正为单斜线 */
								String app_url = app.getAppUrl();
								if (!app_url.startsWith("/")) {
									locationTemp += app_url;
								} else {
									locationTemp += app_url.substring(1);
								}
								locationTemp += "/{\n";
								locationTemp += "proxy_set_header X-Real-IP $remote_addr;\n";
								locationTemp += "proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504 http_404;\n";
								locationTemp += "proxy_pass http://";
								locationTemp += app.getAppName();
								locationTemp += "/;\n}\n";
								LOGGER.debug("nginx config file location:" + locationTemp);
								upstream += upstreamTemp;
								location += locationTemp;
							} catch (Exception e) {
								LOGGER.error("Get container port infos error", e);
							}
						}
					} else {
						// 如果负载中没有应用则配置文件不含应用信息
						String upstreamTemp = "";
						String locationTemp = "";
						try {

							LOGGER.debug("nginx config file upstream:" + upstream);
							locationTemp = "location /";
							locationTemp += " {\n";
							locationTemp += "proxy_set_header X-Real-IP $remote_addr;\n";
							locationTemp += "proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504 http_404;\n";
							locationTemp += "}\n";
							LOGGER.debug("nginx config file location:" + locationTemp);
							upstream += upstreamTemp;
							location += locationTemp;
						} catch (Exception e) {
							LOGGER.error("Get container port infos error", e);
						}
					}
					// 负载主机配置路径
					if (flag) {
						maintemplate.setServerConfPath(lb.getLbMainConf());
						flag = false;
					}
				}
			}
			// when app does not contain any container，not update LB
			if (!upstream.isEmpty() || !location.isEmpty()) {
				maintemplate.setUpStream(upstream);
				maintemplate.setLocation(location);
				maintemplate.setLocalFile(LoadBalanceConfig.getValue(LoadBalanceConstants.BALANCE_FILE));
				maintemplate.setLocalTemplate(LoadBalanceConfig.getValue(LoadBalanceConstants.BALANCE_TEMP));
				maintemplate.setHostModel(getHostModel(host.getHostId()));
				lbTemplates.add(maintemplate);
			}
		}
		return lbTemplates;
	}

	/**
	 * @author langzi
	 * @param hostId
	 * @return
	 * @version 1.0 2015年9月15日
	 */
	private HostModel getHostModel(Integer hostId) {
		Host host = new Host();
		host.setHostId(hostId);
		try {
			host = hostService.getHost(host);
		} catch (Exception e) {
			LOGGER.error("get host by hostid[" + hostId + "] failed!", e);
			return null;
		}
		HostModel model = new HostModel();
		model.setHostIp(host.getHostIp());
		model.setHostUser(host.getHostUser());
		model.setHostPwd(host.getHostPwd());
		return model;
	}

	public GridBean advancedSearchLoadbalance(Integer userId, int pagenumber, int pagesize, JSONObject json_object) {
		try {
			return loadBalanceService.advancedSearchLoadbalance(userId, pagenumber, pagesize, json_object);
		} catch (Exception e) {
			LOGGER.error("search loadbalance error", e);
		}
		return null;
	}

	/**
	 * 由负载id获取负载详细信息
	 * 
	 * @param id
	 * @return
	 */
	public JSONObject getHostOfLBId(Integer id) {
		try {
			return loadBalanceService.getHostOfLBId(id);
		} catch (Exception e) {
			LOGGER.error("get lb info error！", e);
			return null;
		}
	}
}
