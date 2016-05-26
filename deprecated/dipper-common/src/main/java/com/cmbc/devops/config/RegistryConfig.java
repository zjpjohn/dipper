package com.cmbc.devops.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("registryConfig")
public class RegistryConfig {
	@Value("${temp.path}")
	private String tempPath;

	public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}
}
