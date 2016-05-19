
package com.cmbc.dap.monitor.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.dap.monitor.request.ObjectRequest;
import com.cmbc.dap.monitor.request.Request;
import com.cmbc.dap.monitor.request.RequestBuilder;

/**
 * 默认zabbix api 实现类
 * @author dmw
 *
 */
public class DefaultZabbixApi implements ZabbixApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultZabbixApi.class);
	private CloseableHttpClient httpClient;
	private URI uri;
	private String auth;

	public DefaultZabbixApi(String url) {
		try {
			uri = new URI(url.trim());
			httpClient = HttpClients.createDefault();
		} catch (URISyntaxException e) {
			LOGGER.error("url invalid", e);
		}
	}

	public DefaultZabbixApi(URI uri) {
		this.uri = uri;
	}

	public DefaultZabbixApi(String url, CloseableHttpClient httpClient) {
		this(url);
		this.httpClient = httpClient;
	}

	public DefaultZabbixApi(URI uri, CloseableHttpClient httpClient) {
		this(uri);
		this.httpClient = httpClient;
	}

	@Override
	public void init() {
		if (httpClient == null) {
			httpClient = HttpClients.custom().build();
		}
	}

	@Override
	public void destory() {
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (Exception e) {
				LOGGER.error("close httpclient error!", e);
			}
		}
	}

	@Override
	public boolean login(String user, String password) {
		Request request = RequestBuilder.newBuilder().paramEntry("user", user).paramEntry("password", password)
				.method(ZabbixMethod.AUTH.getMethod()).build();
		JSONObject response = call(request);
		String auth = response.getString("result");
		if (auth != null && !auth.isEmpty()) {
			this.auth = auth;
			return true;
		}
		return false;
	}

	@Override
	public String apiVersion() {
		Request request = RequestBuilder.newBuilder().method("apiinfo.version").build();
		JSONObject response = call(request);
		return response.getString("result");
	}

	public boolean hostExists(String name) {
		Request request = RequestBuilder.newBuilder().method("host.exists").paramEntry("name", name).build();
		JSONObject response = call(request);
		return response.getBooleanValue("result");
	}

	public boolean hostgroupExists(String name) {
		Request request = RequestBuilder.newBuilder().method("hostgroup.exists").paramEntry("name", name).build();
		JSONObject response = call(request);
		return response.getBooleanValue("result");
	}

	/**
	 * 
	 * @param name
	 * @return groupId
	 */
	public String hostgroupCreate(String name) {
		Request request = RequestBuilder.newBuilder().method("hostgroup.create").paramEntry("name", name).build();
		JSONObject response = call(request);
		return response.getJSONObject("result").getJSONArray("groupids").getString(0);
	}

	@Override
	public JSONObject call(Request request) {
		if (request.getAuth() == null) {
			request.setAuth(auth);
		}
		return invoke(request);
	}

	@Override
	public JSONObject call(ObjectRequest request) {
		if (request.getAuth() == null) {
			request.setAuth(auth);
		}
		return invoke(request);
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public JSONObject invoke(Object object) {
		try {
			HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder.post().setUri(uri)
					.addHeader("Content-Type", "application/json")
					.setEntity(new StringEntity(JSON.toJSONString(object),"utf-8")).build();
			
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			byte[] data = EntityUtils.toByteArray(entity);
			return (JSONObject) JSON.parse(data);
		} catch (IOException e) {
			LOGGER.error("DefaultZabbixApi call exception!", e);
			return null;
		}
	}
}
