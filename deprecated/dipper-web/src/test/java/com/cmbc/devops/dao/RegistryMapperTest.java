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
import com.cmbc.devops.entity.Registry;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RegistryMapperTest {
	@Resource
	private RegistryMapper registryMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertRegistry() {
		/* 组装插入数据库Registry对象 */
		Registry ins_registry = new Registry();
		ins_registry.setRegistryName("JunitRegName");
		ins_registry.setRegistryPort(5501);
		ins_registry.setRegistryStatus((byte) Status.REGISTRY.NORMAL.ordinal());
		ins_registry.setHostId(12);
		ins_registry.setRegistryDesc("Junit Regi Desc");
		ins_registry.setRegistryCreatetime(new Date());
		ins_registry.setRegistryCreator(1);

		/* 插入数据库记录中 */
		try {
			registryMapper.insertRegistry(ins_registry);
			/* 获取插入后的Registry对象ID信息 */
			int ins_regiId = ins_registry.getRegistryId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_regiId;
			/* 插入对象返回值大于0 */
			assertThat(ins_regiId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectRegistry() {
		try {
			Registry sel_registry = new Registry();
			sel_registry.setRegistryId(record_id);
			sel_registry = registryMapper.selectRegistry(sel_registry);
			assertThat(sel_registry.getRegistryName(), equalTo("JunitRegName"));
			assertThat(sel_registry.getRegistryPort(), equalTo(5501));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateRegistry() {
		/* 组装修改数据库的Registry对象 */
		Registry upd_registry = new Registry();
		upd_registry.setRegistryId(record_id);
		upd_registry.setRegistryName("JunitRegNameUpdate");
		upd_registry.setRegistryPort(7703);
		try {
			int result = registryMapper.updateRegistry(upd_registry);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的Registry对象 */
		try {
			Registry sel_registry = new Registry();
			sel_registry.setRegistryId(record_id);
			sel_registry = registryMapper.selectRegistry(sel_registry);
			assertThat(sel_registry.getRegistryName(), equalTo("JunitRegNameUpdate"));
			assertThat(sel_registry.getRegistryPort(), equalTo(7703));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteRegistry() {
		try {
			/* 首先查询，确认Registry对象存在数据库中 */
			Registry bef_registry = new Registry();
			bef_registry.setRegistryId(record_id);
			bef_registry = registryMapper.selectRegistry(bef_registry);
			assertNotNull(bef_registry);

			/* 删除完成后，重新查询此Registry对象 */
			registryMapper.deleteRegistry(record_id);

			Registry aft_registry = new Registry();
			aft_registry.setRegistryId(record_id);
			aft_registry = registryMapper.selectRegistry(aft_registry);
			assertNull(aft_registry);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
