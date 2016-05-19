package com.cmbc.devops.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
//邮件配置表
@Component("EmailCfg")
public class EmailCfg{
	@Value("${email.host}")
	private String emailHost;
	@Value("${email.from}")
	private String emailFrom;
	@Value("${email.account}")
	private String account;
	@Value("${email.password}")
	private String password;

	public String getAccount() {
		return account;
	}

	public String getPassword() {
		return password;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
}
