package com.cmbc.dap.monitor.request;

import java.util.concurrent.atomic.AtomicInteger;

public class ObjectRequestBuilder {
	private static final AtomicInteger NEXTID = new AtomicInteger(1);

	ObjectRequest request = new ObjectRequest();

	private ObjectRequestBuilder() {

	}

	static public ObjectRequestBuilder newBuilder() {
		return new ObjectRequestBuilder();
	}

	public ObjectRequest build() {
		if (request.getId() == null) {
			request.setId(NEXTID.getAndIncrement());
		}
		return request;
	}

	public ObjectRequestBuilder version(String version) {
		request.setJsonrpc(version);
		return this;
	}

	public ObjectRequestBuilder paramEntry(Object value) {
		request.putParam(value);
		return this;
	}

	/**
	 * Do not necessary to call this method.If don not set id, ZabbixApi will
	 * auto set request auth..
	 * 
	 * @param auth
	 * @return
	 */
	public ObjectRequestBuilder auth(String auth) {
		request.setAuth(auth);
		return this;
	}

	public ObjectRequestBuilder method(String method) {
		request.setMethod(method);
		return this;
	}

	/**
	 * Do not necessary to call this method.If don not set id, RequestBuilder
	 * will auto generate.
	 * 
	 * @param id
	 * @return
	 */
	public ObjectRequestBuilder id(Integer id) {
		request.setId(id);
		return this;
	}
}
