package com.cmbc.devops.util;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

@Repository
public class Initiation {
	@PostConstruct
	public void init(){
//		this.userService.initUser();
	}
}
