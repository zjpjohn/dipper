package com.cmbc.devops.constant;

/**
 * date：2015年8月12日 下午3:45:56 project name：cmbc-devops-common
 * 
 * @author langzi
 * @version 1.0
 * @since JDK 1.7.0_21 file name：Type.java description：
 */
public final class Type {

	private Type() {
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum USER {
		ADMIN, USER, NOTTYPE
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum HOST {
		SWARM, DOCKER, REGISTRY, NGINX, OTHER
	}

	/**
	 * @author langzi
	 * @version 1.0
	 */
	public static enum CLUSTER {
		DOCKER, REGISTRY
	}

	public static enum CLUSTER_MODE {
		ZOOKEEPER, CONFIG
	}
	//资源配置
	public static enum CLUSTER_RES {
		SHARE, PRIVATE
	}

	/**
	 * @author langzi
	 * @version 1.0
	 *
	 */
	public static enum ACTION {
		USER, DETAIL, MODAL
	}

	/**
	 * @author langzi
	 * @version 1.0
	 *
	 */
	public static enum AUTHORITY {
		PARENT, CHILD
	}

	/**
	 * @author yangqinglin
	 * @version 1.0
	 *
	 */
	public static enum APPLICATION {
		EXTERNAL, MIDDLEWARE
	}

	/**
	 * @author yangqinglin
	 * @version 1.0
	 *
	 */
	public static enum IMAGE_OPER {
		INSERT, DELETE, UPDATE
	}

	public static enum IMAGE_TYPE {
		BASIC, APP
	}

	public static enum PARAMETER {
		NULL, INT, STRING, MAP, BOOL
	}

	public static enum TEMPLATE {
		DATABASE, MIDWARE, APPS
	}

	public static enum TENANT {
		TOTAL, CLIENTELE, TENANT
	}

	public static enum SOFTWARE {
		BASEFRAME, MIDDLEWARE, APPLICATION
	}
}
