package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Env;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class EnvMapperTest {
	@Resource
	private EnvMapper envMapper;
	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库Env对象 */
		Env ins_env = new Env();
		ins_env.setEnvName("junit_envname");
		ins_env.setEnvStatus((byte) Status.ENVIRONMENT.NORMAL.ordinal());
		ins_env.setEnvDesc("Junit Test Env Desc");
		ins_env.setEnvCreatetime(new Date());
		ins_env.setEnvCreator(1);

		/* 插入数据库记录中 */
		try {
			envMapper.insert(ins_env);
			/* 获取插入后的Env对象ID信息 */
			int ins_envId = ins_env.getEnvId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_envId;
			/* 插入对象返回值大于0 */
			assertThat(ins_envId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_Select() {
		try {
			Env sel_env = envMapper.select(record_id);
			assertThat(sel_env.getEnvName(), equalTo("junit_envname"));
			assertThat(sel_env.getEnvStatus(), equalTo((byte) Status.ENVIRONMENT.NORMAL.ordinal()));
			assertThat(sel_env.getEnvDesc(), equalTo("Junit Test Env Desc"));
			assertThat(sel_env.getEnvCreator(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_Update() {
		/* 组装修改数据库的Env对象 */
		Env upd_env = new Env();
		upd_env.setEnvId(record_id);
		upd_env.setEnvName("junit_envname_update");
		upd_env.setEnvDesc("Junit Test Env Desc Update");

		try {
			int result = envMapper.update(upd_env);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的Env对象 */
		try {
			upd_env = envMapper.select(record_id);
			assertThat(upd_env.getEnvName(), equalTo("junit_envname_update"));
			assertThat(upd_env.getEnvDesc(), equalTo("Junit Test Env Desc Update"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_Delete() {
		try {
			/* 首先查询，确认Env对象存在数据库中 */
			Env bef_env = envMapper.select(record_id);
			assertNotNull(bef_env);

			/* 删除完成后，重新查询此Env对象 */
			envMapper.delete(record_id);
			Env aft_env = envMapper.select(record_id);
			assertNull(aft_env);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
