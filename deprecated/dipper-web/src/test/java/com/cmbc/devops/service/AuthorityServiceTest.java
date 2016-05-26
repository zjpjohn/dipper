package com.cmbc.devops.service;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AuthorityServiceTest {
	@Autowired
	private AuthorityService authorityService;

	@Before
	public void init_AuthorityService() {
	}

	@Test
	/* 测试创建仓库记录接口 */
	public void test1_AuthorityService() {
	}

	@After
	public void destroy_AuthorityService() {
	}

	/* 生成10为随机数字符串 */
	private static Random random = new Random();

	public static String getRandom() {
		long num = Math.abs(random.nextLong() % 10000000000L);
		String s = String.valueOf(num);
		for (int i = 0; i < 10 - s.length(); i++) {
			s = "0" + s;
		}
		return s;
	}

}
