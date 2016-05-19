package com.cmbc.devops.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.entity.Software;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SoftwareServiceTest {

	@Autowired
	private SoftwareService softwareService;
	
	@Test
	public void testListAllByType() {
		try {
			List<Software> softlist=softwareService.listAllByType(1);
			assertTrue(softlist.size()>0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testListOnePageSofts() {
		try {
			GridBean GB=softwareService.listOnePageSofts(1, 10,0);
			assertTrue(GB.getRecords()>0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetOneById() {
		try {
			Software sw=softwareService.getOneById(1);
			assertTrue(StringUtils.hasText(sw.getSwName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
