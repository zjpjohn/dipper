package com.cmbc.devops.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.Host;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class HostServiceTest {
	@Autowired
	private HostService hostService;

	/* 保存集群和主机记录的ID信息 */
	private static Integer cluster_id = 23;
	private static Integer host_id = 0;

	/* 保存主机的IP地址 */
	private static String host_ip = "197.3.166.168";

	@Before
	public void initialize() {
		System.out.println("Run Before!");
		/* 创建新的主机节点 */
		/* 组装插入数据库主机Host对象 */
		Host ins_host = new Host();
		ins_host.setHostUuid(UUID.randomUUID().toString());
		ins_host.setHostName("junit_hostname" + getRandom());
		ins_host.setHostUser("dap_deploy");
		ins_host.setHostPwd("123456");
		ins_host.setHostType((byte) Type.HOST.DOCKER.ordinal());
		ins_host.setHostIp(host_ip);
		ins_host.setHostCpu(2);
		ins_host.setHostMem(512);
		ins_host.setHostStatus((byte) Status.HOST.NORMAL.ordinal());
		ins_host.setHostDesc("junit test host description.");
		ins_host.setHostKernelVersion("3.12.6");
		ins_host.setClusterId(cluster_id);
		ins_host.setHostCreatetime(new Date());
		ins_host.setHostCreator(1);

		/* 将主机信息写入数据库中 */
		try {
			int result = hostService.createHost(ins_host);
			assertThat(result, equalTo(1));
			host_id = ins_host.getHostId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("host_id:" + host_id);

		System.out.println("Init HostService Finished.");
	}

	@Test
	/* 测试HostService相关的所有方法 */
	public void test1_HostService() {
		/* (1)根据主机的IP地址查询获取主机 ，必须同时设置IP地址和主机类型两个条件 */
		Host selip_host = new Host();
		selip_host.setHostIp(host_ip);
		selip_host.setHostType((byte) Type.HOST.DOCKER.ordinal());
		try {
			selip_host = hostService.getHostByIp(selip_host);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(selip_host.getHostId(), equalTo(host_id));

		/* (2)通过集群的ID获取所有包含主机的链表 */
		try {
			List<Host> host_list = hostService.listHostByClusterId(cluster_id);
			assertThat(host_list.size(), equalTo(1));
			Host single_host = host_list.get(0);
			assertThat(single_host.getHostId(), equalTo(host_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void afterHostService() {
		/* 删除新建的主机节点 */
		try {
			int delh_result = hostService.deleteHost(host_id);
			assertThat(delh_result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 确认集群和主机节点已经删除 */
		try {
			Host sel_host = new Host();
			sel_host.setHostId(host_id);
			sel_host = hostService.getHost(sel_host);
			assertNull(sel_host);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* 生成10为随机数字符串 */
	private static Random r = new Random();

	private static String getRandom() {
		long num = Math.abs(r.nextLong() % 10000000000L);
		String s = String.valueOf(num);
		for (int i = 0; i < 10 - s.length(); i++) {
			s = "0" + s;
		}
		return s;
	}

}
