package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
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

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.entity.LoadBalance;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class LoadBalanceServiceTest {
	@Autowired
	private LoadBalanceService loadBalanceService;
	@Autowired
	private HostService hostService;

	/* 保存记录负载均衡的名称信息 */
	private static String lb_name = "";
	private static Integer lb_id = 0;
	/* 分别保存主机和备机的ID信息 */
	private static Integer mainhost_id = 0;
	private static Integer backhost_id = 0;

	@Before
	public void init1_AddLoadBalance() {
		/* 新增主机节点和备份主机节点 */
		/* 组装插入数据库主机Host对象 */
		Host ins_mainhost = new Host();
		ins_mainhost.setHostUuid(UUID.randomUUID().toString());
		ins_mainhost.setHostName("junit_mainhost" + getRandom());
		ins_mainhost.setHostUser("testdeploy");
		ins_mainhost.setHostPwd("123456");
		ins_mainhost.setHostType((byte) Type.HOST.DOCKER.ordinal());
		ins_mainhost.setHostIp("192.3.168.168");
		ins_mainhost.setHostCpu(2);
		ins_mainhost.setHostMem(512);
		ins_mainhost.setHostStatus((byte) Status.HOST.NORMAL.ordinal());
		ins_mainhost.setHostDesc("junit test main host description.");
		ins_mainhost.setHostKernelVersion("3.12.6");
		ins_mainhost.setClusterId(12);
		ins_mainhost.setHostCreatetime(new Date());
		ins_mainhost.setHostCreator(1);

		Host ins_backhost = new Host();
		ins_backhost.setHostUuid(UUID.randomUUID().toString());
		ins_backhost.setHostName("junit_backhost" + getRandom());
		ins_backhost.setHostUser("testdeploy");
		ins_backhost.setHostPwd("123456");
		ins_backhost.setHostType((byte) Type.HOST.DOCKER.ordinal());
		ins_backhost.setHostIp("192.3.168.169");
		ins_backhost.setHostCpu(2);
		ins_backhost.setHostMem(512);
		ins_backhost.setHostStatus((byte) Status.HOST.NORMAL.ordinal());
		ins_backhost.setHostDesc("junit test back host description.");
		ins_backhost.setHostKernelVersion("3.12.6");
		ins_backhost.setClusterId(12);
		ins_backhost.setHostCreatetime(new Date());
		ins_backhost.setHostCreator(1);

		/* 添加主机和备机节点 */
		try {
			hostService.createHost(ins_mainhost);
			mainhost_id = ins_mainhost.getHostId();
			assertThat(mainhost_id, greaterThan(0));

			hostService.createHost(ins_backhost);
			backhost_id = ins_backhost.getHostId();
			assertThat(backhost_id, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试创建LoadBalance记录接口 */
	public void test1_AddLoadBalance() {
		/* 组装插入数据库LoadBalance对象 */
		LoadBalance ins_loadbalance = new LoadBalance();
		lb_name = "junit_loadbalance" + getRandom();
		ins_loadbalance.setLbName(lb_name);
		ins_loadbalance.setLbDesc("Junit LoadBalance Desc");
		ins_loadbalance.setLbMainHost(mainhost_id);
		ins_loadbalance.setLbMainConf("/var/config");
		ins_loadbalance.setLbBackupHost(backhost_id);
		ins_loadbalance.setLbBackupConf("/var/config");
		ins_loadbalance.setLbCreatetime(new Date());
		ins_loadbalance.setLbCreator(1);
		ins_loadbalance.setLbStatus((byte) Status.LOADBALANCE.NORMAL.ordinal());

		int result = 0;
		try {
			result = loadBalanceService.addLoadBalance(ins_loadbalance);
			/* 填充负载均衡ID值 */
			lb_id = ins_loadbalance.getLbId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* LoadBalance记录创建成功 */
		assertThat(result, greaterThan(0));

		/* 测试通过通过名称和ID获取LoadBalance对象的方法 */
		LoadBalance sel_lb = null;
		try {
			System.out.println("lb_name:" + lb_name);
			sel_lb = loadBalanceService.getLoadBalance(lb_name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertThat(sel_lb.getLbName(), equalTo(lb_name));
		assertThat(sel_lb.getLbDesc(), equalTo("Junit LoadBalance Desc"));
		assertThat(sel_lb.getLbMainConf(), equalTo("/var/config"));
		assertThat(sel_lb.getLbMainHost(), equalTo(mainhost_id));

		/* 根据负载均衡ID获取LoadBalance对象 */
		LoadBalance selid_lb = null;
		try {
			selid_lb = loadBalanceService.getLoadBalance(lb_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertThat(selid_lb.getLbName(), equalTo(lb_name));
		assertThat(selid_lb.getLbDesc(), equalTo("Junit LoadBalance Desc"));
		assertThat(selid_lb.getLbMainConf(), equalTo("/var/config"));
		assertThat(selid_lb.getLbMainHost(), equalTo(mainhost_id));

		/* 获取全部负载均衡的JSON数组信息 */
		JSONObject host_json = null;
		try {
			host_json = loadBalanceService.getHostOfLBId(lb_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer host_id = null;
		host_id = host_json.getInteger("mainhostId");
		if (host_id.equals(null)) {
			host_id = host_json.getInteger("backhostId");
		}

		/* 通过负载均衡ID反向获取的主机和备机的ID信息 */
		assertThat(host_id, anyOf(equalTo(mainhost_id), equalTo(backhost_id)));
	}

	@After
	public void destroy1_GetHostOfLBId() {
		/* 删除负载均衡记录 */
		String[] lbid_array = new String[1];
		lbid_array[0] = lb_id + "";
		try {
			loadBalanceService.removeBalance(lbid_array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* 判断是否清除负载均衡 */
		try {
			LoadBalance sel_loadbalance = loadBalanceService.getLoadBalance(lb_id);
			assertNull(sel_loadbalance);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 删除主机和备机节点记录 */
		ArrayList<Integer> delhost_list = new ArrayList<Integer>();
		delhost_list.add(mainhost_id);
		delhost_list.add(backhost_id);
		int result = hostService.deleteHosts(delhost_list);
		assertThat(result, greaterThan(0));
		/* 判断是否已经删除主备节点 */
		try {
			Host main_host = hostService.loadHost(mainhost_id);
			Host back_host = hostService.loadHost(backhost_id);
			assertNull(main_host);
			assertNull(back_host);
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
