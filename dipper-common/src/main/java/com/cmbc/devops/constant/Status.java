package com.cmbc.devops.constant;

/**
 * date：2015年8月12日 下午3:45:56 project name：cmbc-devops-common
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：Status.java description：
 */
public final class Status {

	private Status() {
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum USER {
		DELETE, NORMAL
	}

	/**
	 * @author yangqinglin
	 * @version 1.0
	 */
	public static enum ROLE {
		DELETE, NORMAL
	}

	/**
	 * @author zll
	 * @version 1.0 0 管理员 1租户 2用户
	 */
	public static enum ROLELEAVEL {
		ADMINISTRATOR, LESSEE, USER
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum HOST {
		DELETE, NORMAL, ABNORMAL
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum CLUSTER {
		DELETE, NORMAL, ABNORMAL
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum REGISTRY {
		DELETE, NORMAL, ABNORMAL
	}

	/**
	 * date：2015年8月31日 下午4:50:31 project name：cmbc-devops-common
	 * 
	 * @author mayh
	 * @version 1.0
	 * @since JDK 1.7.0_21 file name：Status.java description：
	 */
	public static enum IMAGE {
		DELETED, NORMAL, MAKED, ABNORMAL
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum APPLICATION {
		DELETE, NORMAL, ABNORMAL
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum CONTAINER {
		DELETE, EXIT, UP, HALT
	}

	public static enum POWER {
		OFF, UP
	}

	public static enum APP_STATUS {
		ABNORMAL, NORMAL, ERROR, UNDEFINED
	}

	public static enum MONITOR_STATUS {
		ABNORMAL, NORMAL, UNDEFINED
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum PARAMETER {
		BLOCK, ACTIVATE
	}

	public static enum LOADBALANCE {
		DELETE, NORMAL, ABNORMAL
	}

	public static enum AUTHTYPE {
		PAGE, BUTTON
	}

	public static enum TEMPLATE {
		DELETE, NORMAL, ABNORMAL
	}

	public static enum BIZ_TYPE {
		DELETE, NORMAL, ABNORMAL
	}

	public static enum TPLPARAM {
		DELETE, NORMAL, ABNORMAL
	}

	public static enum APP_CONFIG {
		DELETE, NORMAL, ABNORMAL
	}

	public static enum RESOURCE {
		DELETE, NORMAL
	}

	public static enum MONITOR_PROXY {
		DELETE, NORMAL
	}

	public static enum ENVIRONMENT {
		DELETE, NORMAL, TRANSITION
	}

	public static enum TENANT {
		BLOCK, ACTIVATE
	}

	public static enum SOFTWARE {
		BLOCK, ACTIVATE
	}
}
