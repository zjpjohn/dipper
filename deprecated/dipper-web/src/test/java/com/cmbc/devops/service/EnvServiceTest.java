package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
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

import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Env;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class EnvServiceTest {
	@Autowired
	private EnvService envService;

	/* 保存记录的ID信息 */
	private static Integer env_id = 0;
	private static String env_name = "";

	@Before
	public void initAppImage() {
		/* 组装插入数据库Env对象 */
		Env ins_env = new Env();
		env_name = "junit_envname" + getRandom();
		ins_env.setEnvName(env_name);
		ins_env.setEnvStatus((byte) Status.ENVIRONMENT.NORMAL.ordinal());
		ins_env.setEnvDesc("Junit Test Env Desc");
		ins_env.setEnvCreatetime(new Date());
		ins_env.setEnvCreator(1);

		try {
			int result = envService.add(ins_env);
			env_id = ins_env.getEnvId();
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试创建仓库记录接口 */
	public void test1_EnvService() {
		/** (1)测试查询初始化插入的Env对象 **/
		Env sel_env = null;
		try {
			sel_env = envService.find(env_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(sel_env.getEnvId(), equalTo(env_id));
		assertThat(sel_env.getEnvName(), equalTo(env_name));

		/** (2)通过获取全部的Env对象链表 **/
		List<Env> env_list = null;
		try {
			env_list = envService.listAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(env_list.size(), greaterThan(0));

		/** (3)通过Env的名称获取Env对象 **/
		try {
			sel_env = envService.getEnvByName(env_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(sel_env.getEnvId(), equalTo(env_id));
		assertThat(sel_env.getEnvName(), equalTo(env_name));
	}

	@After
	public void destroyAppImage() {
		try {
			int result = envService.remove(env_id);
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* 重新查询数据库，确认应用和镜像已经删除 */
		try {
			Env sel_env = envService.find(env_id);
			assertNull(sel_env);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* 生成10为随机数字符串 */
	private static Random random = new Random();

	private static String getRandom() {
		long num = Math.abs(random.nextLong() % 10000000000L);
		String s = String.valueOf(num);
		for (int i = 0; i < 10 - s.length(); i++) {
			s = "0" + s;
		}
		return s;
	}

}
