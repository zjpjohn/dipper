package com.cmbc.dap.monitor.model;

import java.util.Map;

public class ZxCache {
	/* 通过主机的ID和主机监控对象保存全部监控主机信息 */
	private static Map<String, ZxHost> cacheHost;

	public static Map<String, ZxHost> getCacheHost() {
		return cacheHost;
	}

	public static void setCacheHost(Map<String, ZxHost> cacheHost) {
		ZxCache.cacheHost = cacheHost;
	}

}
