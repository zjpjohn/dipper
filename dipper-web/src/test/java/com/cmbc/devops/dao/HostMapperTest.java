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
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.Host;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class HostMapperTest {
	@Resource
	private HostMapper hostMapper;

	/* 保存主机的ID */
	private static int record_id = 0;

	@Test
	public void test1_InsertHost() {
		/* 组装插入数据库主机Host对象 */
		Host ins_host = new Host();
		ins_host.setHostUuid(UUID.randomUUID().toString());
		ins_host.setHostName("junit_test_hostname");
		ins_host.setHostUser("testdeploy");
		ins_host.setHostPwd("123456");
		ins_host.setHostType((byte) Type.HOST.DOCKER.ordinal());
		ins_host.setHostIp("192.3.168.168");
		ins_host.setHostCpu(2);
		ins_host.setHostMem(512);
		ins_host.setHostStatus((byte) Status.HOST.NORMAL.ordinal());
		ins_host.setHostDesc("junit test host description.");
		ins_host.setHostKernelVersion("3.12.6");
		ins_host.setClusterId(12);
		ins_host.setHostCreatetime(new Date());
		ins_host.setHostCreator(1);

		/* 插入数据库记录中 */
		try {
			hostMapper.insertHost(ins_host);
			/* 获取插入后的集群对象ID信息 */
			int ins_hostId = ins_host.getHostId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_hostId;
			/* 插入对象返回值大于0 */
			assertThat(ins_hostId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectHost() {
		try {
			Host sel_host = hostMapper.selectHost(record_id);
			assertThat(sel_host.getHostName(), equalTo("junit_test_hostname"));
			assertThat(sel_host.getHostUser(), equalTo("testdeploy"));
			assertThat(sel_host.getHostPwd(), equalTo("123456"));
			assertThat(sel_host.getHostType(), equalTo((byte) Type.HOST.DOCKER.ordinal()));
			assertThat(sel_host.getHostIp(), equalTo("192.3.168.168"));
			assertThat(sel_host.getHostCpu(), equalTo(2));
			assertThat(sel_host.getHostMem(), equalTo(512));
			assertThat(sel_host.getHostStatus(), equalTo((byte) Status.HOST.NORMAL.ordinal()));
			assertThat(sel_host.getHostDesc(), equalTo("junit test host description."));
			assertThat(sel_host.getHostKernelVersion(), equalTo("3.12.6"));
			assertThat(sel_host.getClusterId(), equalTo(12));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateHost() {
		/* 组装修改数据库的Cluster对象 */
		Host upd_host = new Host();
		upd_host.setHostId(record_id);
		upd_host.setHostName("update_junit_test_hostname");
		upd_host.setHostUser("root");
		upd_host.setHostPwd("654321");
		upd_host.setHostType((byte) Type.HOST.NGINX.ordinal());
		upd_host.setHostIp("192.3.169.169");
		upd_host.setHostCpu(8);
		upd_host.setHostMem(4096);
		upd_host.setHostStatus((byte) Status.HOST.ABNORMAL.ordinal());
		upd_host.setHostDesc("update junit test host description.");
		upd_host.setHostKernelVersion("3.24.16");
		upd_host.setClusterId(23);

		try {
			int result = hostMapper.updateHost(upd_host);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的集群对象 */
		try {
			upd_host = hostMapper.selectHost(record_id);
			assertThat(upd_host.getHostName(), equalTo("update_junit_test_hostname"));
			assertThat(upd_host.getHostUser(), equalTo("root"));
			assertThat(upd_host.getHostPwd(), equalTo("654321"));
			assertThat(upd_host.getHostType(), equalTo((byte) Type.HOST.NGINX.ordinal()));
			assertThat(upd_host.getHostIp(), equalTo("192.3.169.169"));
			assertThat(upd_host.getHostCpu(), equalTo(8));
			assertThat(upd_host.getHostMem(), equalTo(4096));
			assertThat(upd_host.getHostStatus(), equalTo((byte) Status.HOST.ABNORMAL.ordinal()));
			assertThat(upd_host.getHostDesc(), equalTo("update junit test host description."));
			assertThat(upd_host.getHostKernelVersion(), equalTo("3.24.16"));
			assertThat(upd_host.getClusterId(), equalTo(23));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteHost() {
		try {
			/* 首先查询，确认集群对象存在数据库中 */
			Host bef_host = hostMapper.selectHost(record_id);
			assertNotNull(bef_host);

			/* 删除完成后，重新查询此集群对象 */
			hostMapper.deleteHost(record_id);
			Host aft_host = hostMapper.selectHost(record_id);
			assertNull(aft_host);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
