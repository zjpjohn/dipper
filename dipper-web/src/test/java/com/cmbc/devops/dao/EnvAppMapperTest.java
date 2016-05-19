package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.entity.EnvApp;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class EnvAppMapperTest {
	@Resource
	private EnvAppMapper envAppMapper;

	private static int record_id = 0;
	private static int app_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库EnvApp对象 */
		EnvApp ins_envapp = new EnvApp();
		ins_envapp.setAppId(12);
		ins_envapp.setEnvId(16);

		/* 插入数据库记录中 */
		try {
			envAppMapper.insert(ins_envapp);
			/* 获取插入后的EnvApp对象ID信息 */

			int ins_envappId = ins_envapp.getId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_envappId;
			app_id = ins_envapp.getAppId();
			/* 插入对象返回值大于0 */
			assertThat(ins_envappId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_Select() {
		try {
			EnvApp sel_envapp = envAppMapper.select(record_id);
			assertThat(sel_envapp.getAppId(), equalTo(12));
			assertThat(sel_envapp.getEnvId(), equalTo(16));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_Update() {
		/* 组装修改数据库的EnvApp对象 */
		EnvApp upd_envapp = new EnvApp();
		upd_envapp.setId(record_id);
		upd_envapp.setAppId(18);
		upd_envapp.setEnvId(23);
		app_id = 18;

		try {
			int result = envAppMapper.update(upd_envapp);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的EnvApp对象 */
		try {
			upd_envapp = envAppMapper.select(record_id);
			assertThat(upd_envapp.getAppId(), equalTo(18));
			assertThat(upd_envapp.getEnvId(), equalTo(23));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_Delete() {
		try {
			/* 首先查询，确认EnvApp对象存在数据库中 */
			EnvApp bef_envapp = envAppMapper.select(record_id);
			assertNotNull(bef_envapp);

			envAppMapper.delete(app_id);
			/* 删除完成后，重新查询此EnvApp对象 */
			EnvApp aft_envapp = envAppMapper.select(record_id);
			assertNull(aft_envapp);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
