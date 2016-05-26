package com.cmbc.dap.monitor.model;

public class HostInfo {

	private int type;
	private int main;
	private int useip;
	private String ip;
	private String dns;
	//约定为10050
	private String port;
	//约定为agent_{hostIp}
	private String hostname;
	//约定为host的Proxyid
	private String proxyHostId;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMain() {
		return main;
	}

	public void setMain(int main) {
		this.main = main;
	}

	public int getUseip() {
		return useip;
	}

	public void setUseip(int useip) {
		this.useip = useip;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	

	public String getProxyHostId() {
		return proxyHostId;
	}

	public void setProxyHostId(String proxyHostId) {
		this.proxyHostId = proxyHostId;
	}

	public HostInfo(String hostname, int type, int main, int useip, String ip, String dns, String port) {
		super();
		this.type = type;
		this.main = main;
		this.useip = useip;
		this.ip = ip;
		this.dns = dns;
		this.port = port;
		this.hostname = hostname;
	}

	public HostInfo() {
		super();
	}

	public HostInfo(String ip, String dns,String uuid,String proxyHostId) {
		super();
		this.ip = ip;
		this.dns = dns;
		this.port = "10050";
		this.hostname = "Container_"+ip+"_"+uuid;
		this.type = 1;
		this.main = 1;
		this.useip = 1;
		this.setProxyHostId(proxyHostId);
	}

	@Override
	public String toString() {
		return "HostIntf [type=" + type + ", main=" + main + ", useip=" + useip + ", ip=" + ip + ", dns=" + dns
				+ ", port=" + port + ", hostname=" + hostname + "]";
	}


}
