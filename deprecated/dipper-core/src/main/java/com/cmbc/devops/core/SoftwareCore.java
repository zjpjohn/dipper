package com.cmbc.devops.core;

import java.util.List;

import com.cmbc.devops.model.HostModel;

public interface SoftwareCore {
	public List<HostModel> installSoftware(List<HostModel> hostlist, String yumcall);
	
}
  