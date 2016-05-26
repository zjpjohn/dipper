package com.cmbc.devops.manager;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.message.MessagePush;
import com.cmbc.devops.message.MessageUtilities;

/**
 * date：2015年8月17日 下午3:52:11 project name：cmbc-devops-web
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：MessageManager.java description：
 */
@Component
public class MessageManager {

	@Resource
	private MessagePush messagePush;

	public JSONObject checkLogin(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("登录成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("登录失败！"));
		}
		return jo;
	}

	public JSONObject createHost(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("创建主机成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("创建主机失败！"));
		}
		return jo;
	}

	public JSONObject createCluster(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("创建集群成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("创建集群失败！"));
		}
		return jo;
	}

	public JSONObject createApplication(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("新增应用成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("新增应用失败！"));
		}
		return jo;
	}

	public JSONObject updateApplication(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("修改应用成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("修改应用失败！"));
		}
		return jo;
	}

	public JSONObject deleteApplication(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getInteger("result") == 2) {
			messagePush.pushMessage(
					userId,
					MessageUtilities.stickyToSuccess("删除失败，应用【"
							+ jo.getString("containerPowerOn") + "】容器已启动！"));
		} else if (jo.getInteger("result") == 1) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("删除应用成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("删除应用失败！"));
		}
		return jo;
	}

	public JSONObject createParameter(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("新增参数成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("新增参数失败！"));
		}
		return jo;
	}

	public JSONObject createRegistry(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("新增仓库成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("新增仓库失败！"));
		}
		return jo;
	}

	public JSONObject updateRegistry(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("修改仓库成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("修改仓库失败！"));
		}
		return jo;
	}

	public JSONObject deleteRegistry(JSONObject jo) {
		int userId = jo.getInteger("userId");
		int result = jo.getInteger("result");
		if (jo.getInteger("result") == 2) {
			messagePush.pushMessage(
					userId,
					MessageUtilities.stickyToSuccess("删除失败，仓库【"
							+ jo.getString("exist") + "】存在使用中的镜像！"));
		} else if (result == 1) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("删除仓库成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("删除仓库失败！"));
		}
		return jo;
	}

	public JSONObject createContainer(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("容器创建成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("容器创建失败！"));
		}
		return jo;
	}

	public JSONObject startContainer(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("容器启动成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("容器启动失败！"));
		}
		return jo;
	}

	public JSONObject stopContainer(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("容器关闭成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("容器关闭失败！"));
		}
		return jo;
	}

	public JSONObject removeContainer(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("容器删除成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("容器删除失败！"));
		}
		return jo;
	}

	public JSONObject syncContainer(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("容器同步成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("容器同步失败！"));
		}
		return jo;
	}

	public JSONObject updateParameterDispath(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage

			(userId, MessageUtilities.stickyToSuccess("修改参数成功！"));

		} else {
			messagePush.pushMessage

			(userId, MessageUtilities.stickyToError("修改参数失败！"));

		}
		return jo;
	}

	public JSONObject deleteParameterDispath(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage

			(userId, MessageUtilities.stickyToSuccess("删除成功！"));

		} else {
			messagePush.pushMessage

			(userId, MessageUtilities.stickyToError("删除失败！"));
		}
		return jo;
	}

	public JSONObject addToCluster(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("主机添加到集群成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("主机添加到集群失败！"));
		}
		return jo;
	}

	public JSONObject removeFromCluster(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("主机从集群移除成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("主机从集群移除失败！"));
		}
		return jo;
	}

	public JSONObject updateHost(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("修改主机成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("修改主机失败！"));
		}
		return jo;
	}

	public JSONObject updateCluster(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("修改集群成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("修改集群失败！"));
		}
		return jo;
	}

	public JSONObject deleteHost(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("删除主机成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("删除主机失败！"));
		}
		return jo;
	}

	public JSONObject deleteHosts(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("删除主机成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("删除主机失败！"));
		}
		return jo;
	}

	public JSONObject deleteCluster(JSONObject jo) {
		int userId = jo.getInteger("userId");
		if (jo.getBoolean("result")) {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToSuccess("删除集群成功！"));
		} else {
			messagePush.pushMessage(userId,
					MessageUtilities.stickyToError("删除集群失败！"));
		}
		return jo;
	}
}
