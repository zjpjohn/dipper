package com.cmbc.devops.query;

import java.util.List;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Authority;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterWithIPAndUser;
import com.cmbc.devops.entity.Env;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.LoadBalance;
import com.cmbc.devops.entity.Role;
import com.cmbc.devops.entity.RoleAction;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.model.ApplicationModel;
import com.cmbc.devops.model.ContainerModel;
import com.cmbc.devops.model.HostModel;
import com.cmbc.devops.model.ImageModel;
import com.cmbc.devops.service.AppService;
import com.cmbc.devops.service.ApplicationReleaseService;
import com.cmbc.devops.service.AuthorityService;
import com.cmbc.devops.service.ClusterService;
import com.cmbc.devops.service.ConportService;
import com.cmbc.devops.service.ContainerService;
import com.cmbc.devops.service.EnvService;
import com.cmbc.devops.service.HostService;
import com.cmbc.devops.service.ImageService;
import com.cmbc.devops.service.LoadBalanceService;
import com.cmbc.devops.service.RoleService;
import com.cmbc.devops.service.UserService;

/**
 * @author luogan 2015年8月17日 下午3:49:55
 */
@Component
public class QueryList {

	@Autowired
	private HostService hostService;
	@Autowired
	private ClusterService clusterService;
	@Autowired
	private AppService appService;
	@Autowired
	private EnvService envService;
	@Autowired
	private ApplicationReleaseService releaseService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private ConportService conportService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private LoadBalanceService loadBalanceService;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private AuthorityService authorityService;

	private static Logger logger = Logger.getLogger(QueryList.class);

	public GridBean queryHostList(int userId, int pageNum, int pageSize, HostModel hostModel) {
		try {
			return hostService.getOnePageHostList(userId, pageNum, pageSize, hostModel);
		} catch (Exception e) {
			logger.error("get page host list failed!", e);
			return null;
		}
	}

	public Cluster queryCluster(Host host) {
		try {
			return clusterService.getClusterByHost(host);
		} catch (Exception e) {
			logger.error("get cluster by host failed!", e);
			return null;
		}
	}

	public GridBean queryClusterList(int userId, int tenantId, int pageNum, int pageSize,
			ClusterWithIPAndUser clusterWithIPAndUser) {
		try {
			return clusterService.getOnePageClusterList(userId, tenantId, pageNum, pageSize, clusterWithIPAndUser);
		} catch (Exception e) {
			logger.error("get one page cluster list falied!", e);
			return null;
		}
	}

	public JSONArray queryAllClusterList(int userId,int tenantId, ClusterWithIPAndUser clusterWithIPAndUser) {
		try {
			return clusterService.getAllClusterList(userId,tenantId, clusterWithIPAndUser);
		} catch (Exception e) {
			logger.error("get all cluster list failed!", e);
			return null;
		}
	}
	
	public JSONArray getOrphanClus() {
		try {
			return clusterService.getOrphanClus();
		} catch (Exception e) {
			logger.error("get all cluster list failed!", e);
			return null;
		}
	}

	public JSONArray queryAllClustersInApp(int appId) {
		try {
			return clusterService.listClustersByappId(appId);
		} catch (Exception e) {
			logger.error("get clusters by appid failed!", e);
			return null;
		}
	}

	public JSONArray queryAllRoleList(int userId, Role role) {
		try {
			return roleService.getAllRoleList(userId, role);
		} catch (Exception e) {
			logger.error("get all role list falied!", e);
			return null;
		}
	}

	public JSONArray getRoleAuthList(int userId, RoleAction roleAction) {
		try {
			return (JSONArray) JSONArray.toJSON(roleService.getRoleAuthList(userId, roleAction));
		} catch (Exception e) {
			logger.error("get role auth list falied!", e);
			return null;
		}
	}

	public JSONArray queryAllRoleListByUserId(int userId) {
		try {
			return (JSONArray) JSONArray.toJSON(roleService.getAllRoleListByUserId(userId));
		} catch (Exception e) {
			logger.error("get all role list by userid[" + userId + "] falied!", e);
			return null;
		}
	}

	public JSONArray queryAllList(int userId, Authority authority) {
		try {
			return (JSONArray) JSONArray.toJSON(authorityService.getAllAuthList(userId, authority));
		} catch (Exception e) {
			logger.error("get all authority list fslied!", e);
			return null;
		}
	}

	public List<Authority> queryAllAuthList(int userId, Authority authority) {
		try {
			return authorityService.getAllAuthList(userId, authority);
		} catch (Exception e) {
			logger.error("get all auth list falied!", e);
			return null;
		}
	}

	/**
	 * 通过权限的父id查找它下面的所有子节点
	 * 
	 * @param userid
	 *            [用户id] 暂时没有用到
	 * @param id
	 *            [父节点id]
	 * @return
	 */
	public List<Authority> queryListByActionParentId(Integer userid, Integer id) {
		try {
			return authorityService.ListByActionParentId(id);
		} catch (Exception e) {
			logger.error("ge auth list by parentId[" + id + "] falied!", e);
			return null;
		}
	}

	public List<Host> queryAllHostList(int userId, Integer hostType) {
		try {
			return hostService.listHostByTypeAndCluster(hostType);
		} catch (Exception e) {
			logger.error("list host by hostType failed!", e);
			return null;
		}
	}

	public List<Host> queryAllClusterHostList(int userId, Integer clusterId) {
		try {
			return hostService.listHostByClusterId(clusterId);
		} catch (Exception e) {
			logger.error("list Host By ClusterId failed!", e);
			return null;
		}
	}

	/** @date:2016-3-28添加租户维度 */
	public GridBean queryImageList(int userId, int tenantId, int pageNum, int pageSize, ImageModel imageModel) {
		try {
			return imageService.pagination(userId, tenantId, pageNum, pageSize, imageModel);
		} catch (Exception e) {
			logger.error("get image list falied!", e);
			return null;
		}
	}

	public GridBean queryImageList(int userId, int tenantId, int pageNum, int pageSize, int appId, int evnId) {
		try {
			return imageService.getImageListByappIdAndEnvId(userId, tenantId, pageNum, pageSize, appId, evnId);
		} catch (Exception e) {
			logger.error("get image list by appid[" + appId + "] falied!", e);
			return null;
		}
	}

	public JSONArray queryClusterMasterList(int tenantId) {
		try {
			return clusterService.getOnePageClusterMasterList(tenantId);
		} catch (Exception e) {
			logger.error("get one page cluster master list failed!", e);
			return null;
		}
	}

	public JSONArray listConPortByConId(int conId) {
		try {
			return (JSONArray) JSONArray.toJSON(conportService.listConPorts(conId));
		} catch (Exception e) {
			logger.error("Get container port infos failed", e);
			return null;
		}
	}

	public JSONArray listAllEnv() {
		try {
			List<Env> envs = envService.listAll();
			return (JSONArray) JSONArray.toJSON(envs);
		} catch (Exception e) {
			logger.error("list all env error", e);
			return null;
		}
	}

	public GridBean listOnePageEnvs(int pagenum, int pagesize) {
		try {
			return envService.getOnePageEnvs(pagenum, pagesize);
		} catch (Exception e) {
			logger.error("list one page envs error", e);
			return null;
		}
	}

	public GridBean listOnePageApps(int pagenum, int pagesize) {
		try {
			return appService.getOnePageApps(pagenum, pagesize);
		} catch (Exception e) {
			logger.error("list one page apps error", e);
			return null;
		}
	}

	/* #多租户部分，增加添加用户ID透传部分 */
	public GridBean listOnePageApps(int pagenum, int pagesize, int tenant_id) {
		try {
			return appService.getOnePageApps(pagenum, pagesize, tenant_id);
		} catch (Exception e) {
			logger.error("list one page apps error", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @param model
	 * @return
	 * @version 1.0 2015年8月21日
	 */
	public GridBean listOnePageContainer(Integer userId, Integer tenantId, int pagenum, int pagesize,
			ContainerModel model) {
		try {
			return containerService.listOnePageContainers(userId, tenantId, pagenum, pagesize, model);
		} catch (Exception e) {
			logger.error("list one page containers error", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @param userId
	 * @param pagenum
	 * @param pagesize
	 * @param model
	 * @return
	 * @version 1.0 2015年12月15日
	 */
	public GridBean listOnePageContainer(Integer userId, Integer tenantId, int pagenum, int pagesize, int appId) {
		try {
			return containerService.listOnePageContainers(userId, tenantId, pagenum, pagesize, appId);
		} catch (Exception e) {
			logger.error("list one page containers error", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @param userId
	 * @param pagenum
	 * @param pagesize
	 * @param model
	 * @return
	 * @version 1.0 2015年12月10日
	 */
	public GridBean listOnePageAppReleased(Integer userId, int pagenum, int pagesize, ApplicationModel model) {
		try {
			return releaseService.listOnePageReleasedApp(userId, pagenum, pagesize, model);
		} catch (Exception e) {
			logger.error("list application infos error", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	public JSONArray listContainers(Integer tenantId) {
		try {
			return containerService.listAllContainersJsonArray(tenantId);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 用户列表分页
	 * 
	 * @param userId
	 * @param name
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public GridBean userList(Integer userId, int pageNum, int pageSize, User user) {
		try {
			return userService.list(userId, pageNum, pageSize, user);
		} catch (Exception e) {
			logger.error("get user list falied!", e);
			return null;
		}
	}

	/**
	 * 参数列表分页
	 * 
	 * @param userId
	 * @param name
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public GridBean roleList(Integer userId, int pageNum, int pageSize, Role role) {
		try {
			return roleService.list(userId, pageNum, pageSize, role);
		} catch (Exception e) {
			logger.error("get page role list falied!", e);
			return null;
		}
	}

	/**
	 * 参数列表分页
	 * 
	 * @param userId
	 * @param name
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public GridBean authorityList(Integer userId, int pageNum, int pageSize, Authority authority) {
		try {
			return authorityService.list(userId, pageNum, pageSize, authority);
		} catch (Exception e) {
			logger.error("get page auth list falied!", e);
			return null;
		}
	}

	/**
	 * @author youngtsinglin
	 * @time 2015年10月8日 14:48
	 * @description 将原来返回字符串的方法修改为GridBean的方式
	 */
	public GridBean hostSearchAllList(int userId, int pageNum, int pageSize, HostModel hostModel) {
		try {
			return hostService.searchAllHosts(userId, pageNum, pageSize, hostModel);
		} catch (Exception e) {
			logger.error("search all hosts failed.", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @param userId
	 * @param pagenum
	 * @param pagesize
	 * @param model
	 * @return
	 * @version 1.0 2015年9月11日
	 */
	public GridBean listOnePageLoadBalance(Integer userId, int pagenum, int pagesize, LoadBalance loadBalance) {
		try {
			return loadBalanceService.listOnePageBalances(userId, pagenum, pagesize, loadBalance);
		} catch (Exception e) {
			logger.error("Get one page loadbalance list failed.", e);
			return null;
		}
	}

	public JSONArray listAllLoadBalance() {
		try {
			List<LoadBalance> balances = loadBalanceService.listAll(null);
			return (JSONArray) JSONArray.toJSON(balances);
		} catch (Exception e) {
			logger.error("list all reload balance error", e);
			return null;
		}
	}

	/**
	 * @author langzi
	 * @param type
	 * @return
	 * @version 1.0 2015年9月11日 @
	 */
	public List<Host> listHostByType(int type) {
		try {
			return hostService.listHostByType(type);
		} catch (Exception e) {
			logger.error("list host by type failed!", e);
			return null;
		}
	}

	/**
	 * 获取该角色下父权限id为authId的所有权限
	 * 
	 * @param roleId
	 * @param authId
	 * @return
	 */
	public List<Authority> getAuthListByRoleId(Integer roleId, Integer authId) {
		try {
			return authorityService.getAuthListByRoleId(roleId, authId);
		} catch (Exception e) {
			logger.error("get auth list by roleid[" + roleId + "] ,authParentid[" + authId + "] failed!", e);
			return null;
		}
	}

/*	public GridBean queryAppReleasedByAppName(Integer userId, int pagenumber, int pagesize, ApplicationModel model) {
		try {
			return releaseService.queryAppReleasedByAppName(userId, pagenumber, pagesize, model);
		} catch (Exception e) {
			logger.error("list application infos error", e);
			return null;
		}
	}*/

	public GridBean listConInfoByAppid(Integer userId, Integer tenantId, int pagenumber, int pagesize, Integer appId, Integer imageId) {
		try {
			return containerService.listContainersByAppid(userId, tenantId, pagenumber, pagesize, appId, imageId);
		} catch (Exception e) {
			logger.error("List containers by application id error!", e);
			return null;
		}
	}

	/* 根据容器实例的搜索名称和应用ID模糊查询 */
	public GridBean listSearchConIns(Integer userId, Integer tenantId, int pagenumber, int pagesize,
			JSONObject param_json) {
		try {
			return containerService.listSearchConIns(userId, tenantId, pagenumber, pagesize, param_json);
		} catch (Exception e) {
			logger.error("List containers by application id and container name error!", e);
			return null;
		}
	}

	/* 根据请求的容器状态，显示全部容器的信息（全部、运行中、暂停的） */
	public GridBean listPowerConInfo(Integer userId, Integer tenantId, int pagenumber, int pagesize, int power_status) {
		try {
			return containerService.listPowerConInfo(userId, tenantId, pagenumber, pagesize, power_status);
		} catch (Exception e) {
			switch (power_status) {
			case (0): {
				logger.error("User(ID:" + userId + ") in Tenant(ID:" + tenantId + ") 根据状态请求全部容器链表失败!", e);
				return null;
			}
			case (1): {
				logger.error("User(ID:" + userId + ") in Tenant(ID:" + tenantId + ") 根据状态请求全部容器链表失败!", e);
				return null;
			}
			case (2): {
				logger.error("User(ID:" + userId + ") in Tenant(ID:" + tenantId + ") 根据状态请求全部容器链表失败!", e);
				return null;
			}
			default: {
				return null;
			}
			}
		}
	}
	
	/* 根据应用id取出应用的有效版本列表（即该版本下存在运行的容器）*/
	public JSONArray activeImageListByAppId(int userId,Integer appId) {
		try {
			return  imageService.activeImageListByAppId(userId,appId);
		} catch (Exception e) {
			logger.error("get active image list by appid[" + appId + "] falied!", e);
			return null;
		}
	}
}
