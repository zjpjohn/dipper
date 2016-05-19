package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Container;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ContainerMapperTest {
	@Resource
	private ContainerMapper containerMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertContainer() {
		/* 组装插入数据库Container对象 */
		Container ins_container = new Container();
		ins_container.setConUuid(UUID.randomUUID().toString());
		ins_container.setConImgid(16);
		ins_container.setConCreator(1);
		ins_container.setConName("junit_test");
		ins_container.setConPower((byte) Status.POWER.UP.ordinal());
		ins_container.setAppStatus((byte) Status.APP_STATUS.NORMAL.ordinal());
		ins_container.setMonitorStatus((byte) Status.MONITOR_STATUS.NORMAL.ordinal());
		/* 此处Exit是状态1的情况，才可以进行后续的update操作 */
		ins_container.setConStatus((byte) Status.CONTAINER.EXIT.ordinal());
		ins_container.setConStartCommand("/bin/bash");
		ins_container.setConStartParam("-v:/var/log");
		ins_container.setConCpu(2);
		ins_container.setConMem(512);
		ins_container.setConDesc("junit test desc");
		ins_container.setAppId(12);
		ins_container.setMonitorHostId("16");
		ins_container.setClusterIp("197.3.155.155");
		ins_container.setClusterPort("7000");
		ins_container.setHostId(12);
		ins_container.setConCreatetime(new Date());

		/* 插入数据库记录中 */
		try {
			containerMapper.insertContainer(ins_container);
			/* 获取插入后的集群对象ID信息 */
			int ins_conId = ins_container.getConId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_conId;
			/* 插入对象返回值大于0 */
			assertThat(ins_conId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectContainer() {
		try {
			Container sel_container = new Container();
			sel_container.setConId(record_id);
			sel_container = containerMapper.selectContainer(sel_container);
			assertThat(sel_container.getConImgid(), equalTo(16));
			assertThat(sel_container.getConCreator(), equalTo(1));
			assertThat(sel_container.getConName(), equalTo("junit_test"));
			assertThat(sel_container.getConPower(), equalTo((byte) Status.POWER.UP.ordinal()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateContainer() {
		/* 组装修改数据库的Container对象 */
		Container upd_container = new Container();
		upd_container.setConId(record_id);
		upd_container.setConName("junit_test_update");
		try {
			int result = containerMapper.updateContainer(upd_container);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的Container对象 */
		try {
			Container sel_container = new Container();
			sel_container.setConId(record_id);
			upd_container = containerMapper.selectContainer(sel_container);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteContainer() {
		try {
			/* 首先查询，确认Container对象存在数据库中 */
			Container bef_container = new Container();
			bef_container.setConId(record_id);
			bef_container = containerMapper.selectContainer(bef_container);
			assertNotNull(bef_container);

			/* 删除完成后，重新查询此Container对象 */
			containerMapper.deleteContainer(record_id);

			Container aft_container = new Container();
			aft_container.setConId(record_id);
			aft_container = containerMapper.selectContainer(aft_container);
			assertNull(aft_container);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
