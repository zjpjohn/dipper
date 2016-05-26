package com.cmbc.devops.service;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.entity.HostSoft;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HostSoftServiceTest {

	@Autowired
	private HostSoftService HSSoftService;
	
	@Test
	public void testCreateRecord() {
		HostSoft hs=new HostSoft();
		hs.setHostId(100);
		hs.setSwId(100);
		hs.setCreatetime(new Date());
		hs.setCreator(1);
		int result=HSSoftService.createRecord(hs);
		assertTrue(result>0);
	}

}