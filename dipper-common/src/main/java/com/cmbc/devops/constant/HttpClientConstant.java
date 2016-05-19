package com.cmbc.devops.constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HttpClientConstant {

	private HttpClientConstant() {
	}

	/* 从Docker Http Api返回的结果的key值 */
	public final static String RESULT_KEY = "json_node";
	/* 基于HTTP的docker的基础协议 */
	public final static String BASE_TCP_PROTOCOL = "http://";
	/* 查询的端口的版本号 */
	public final static String QUERY_VERSION = "/v1/";
	/* 查询全部仓库的命令 */
	public final static String QUERY_SEARCH = "search";
	/* 查询仓库下的镜像ID命令 */
	public final static String QUERY_REPOSITORIES = "repositories/";
	/* 查询镜像的命令 */
	public final static String QUERY_IMAGES = "/images";
	/* 查询镜像的标签命令 */
	public final static String QUERY_TAGS = "/tags";
	/* 查询获取镜像列表的命令 */
	public final static String QUERY_REGICMD = "docker images |awk '{print $1\" \"$2\" \"$3\" \"$7$8}' ";

	public final static Map<String, String> CONSTANTMAP = new ConcurrentHashMap<String, String>();

	static {
		/* 从Docker Http Api返回的结果的key值 */
		CONSTANTMAP.put("RESULT_KEY", "json_node");
		/* 基于HTTP的docker的基础协议 */
		CONSTANTMAP.put("BASE_TCP_PROTOCOL", "http://");
		/* 查询的端口的版本号 */
		CONSTANTMAP.put("QUERY_VERSION", "/v1/");
		/* 查询全部仓库的命令 */
		CONSTANTMAP.put("QUERY_SEARCH", "search");
		/* 查询仓库下的镜像ID命令 */
		CONSTANTMAP.put("QUERY_REPOSITORIES", "repositories/");
		/* 查询镜像的命令 */
		CONSTANTMAP.put("QUERY_IMAGES", "/images");
		/* 查询镜像的标签命令 */
		CONSTANTMAP.put("QUERY_TAGS", "/tags");
		/* 查询获取镜像列表的命令 */
		CONSTANTMAP.put("QUERY_REGICMD", "docker images |awk '{print $1\" \"$2\" \"$3\" \"$7$8}' ");
	}

	/**
	 * @author youngtsinglin
	 * @description 获取完整查询仓库列表的路径
	 * @date 2015年9月15日
	 */
	public static String getQueryRegistry(String ip, String port) {
		return CONSTANTMAP.get("BASE_TCP_PROTOCOL") + ip + ":" + port + CONSTANTMAP.get("QUERY_VERSION")
				+ CONSTANTMAP.get("QUERY_SEARCH");
	}

	/**
	 * @author youngtsinglin
	 * @description 获取某个仓库下所有的镜像ID信息
	 * @date 2015年9月15日
	 */
	public static String getRegistryImages(String ip, String port, String imageName) {
		return CONSTANTMAP.get("BASE_TCP_PROTOCOL") + ip + ":" + port + CONSTANTMAP.get("QUERY_VERSION")
				+ CONSTANTMAP.get("QUERY_REPOSITORIES") + imageName + CONSTANTMAP.get("QUERY_IMAGES");
	}

	/**
	 * @author youngtsinglin
	 * @description 获取某个仓库下所有的镜像ID信息
	 * @date 2015年9月15日
	 */
	public static String getRegistryImgTag(String ip, String port, String imageName) {
		return CONSTANTMAP.get("BASE_TCP_PROTOCOL") + ip + ":" + port + CONSTANTMAP.get("QUERY_VERSION")
				+ CONSTANTMAP.get("QUERY_REPOSITORIES") + imageName + CONSTANTMAP.get("QUERY_TAGS");
	}
}
