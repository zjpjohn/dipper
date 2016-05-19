package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
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
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.Host;
import com.cmbc.devops.model.ClusterModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ClusterServiceTest {
	@Autowired
	private ClusterService clusterService;
	@Autowired
	private HostService hostService;

	/* 保存记录的ID信息 */
	private static String cluster_name = "";
	private static Integer host_id = 0;
	private static String host_ip = "192.168.123.123";

	@Before
	public void initCluster() {

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
		ins_host.setHostDesc("junit test cluster-host description.");
		ins_host.setHostKernelVersion("3.12.6");
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

		cluster_name = "junit_cluster" + getRandom();

		/* 组装插入数据库Cluster对象 */
		ClusterModel cluster_model = new ClusterModel();
		cluster_model.setClusterUuid(UUID.randomUUID().toString());
		cluster_model.setClusterName(cluster_name);
		cluster_model.setClusterType((byte) Type.CLUSTER.DOCKER.ordinal());
		cluster_model.setClusterStatus((byte) Status.CLUSTER.NORMAL.ordinal());
		cluster_model.setClusterPort("3303");
		cluster_model.setManagePath("cluster_p3303");
		cluster_model.setClusterDesc("junit description");
		cluster_model.setMasteHostId(host_id);
		cluster_model.setUserId(1);
		cluster_model.setLogFile("cluster3303.log");

		try {
			int result = clusterService.createCluster(cluster_model);
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试创建仓库记录接口 */
	public void test1_Cluster() {
		/* 通过集群的名称查询集群对象 */
		try {
			Cluster sel_cluster = clusterService.getClusterByName(cluster_name);
			assertThat(sel_cluster.getClusterName(), equalTo(cluster_name));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		/* 获取全部的集群列表 */
		try {
			List<Cluster> cluster_list = clusterService.listAllCluster();
			assertThat(cluster_list.size(), greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 通过主机的ID查询获取集群的列表信息 */
		try {
			List<Cluster> cluster_list2 = clusterService.listClustersByhostId(host_id);
			assertThat(cluster_list2.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void destroyCluster() {
		/* 查询获取集群对象 */
		try {
			Cluster sel_cluster = clusterService.getClusterByName(cluster_name);
			assertThat(sel_cluster.getClusterName(), equalTo(cluster_name));

			/* 通过获取集群的ID删除对应集群 */
			Integer cluster_id = sel_cluster.getClusterId();
			int result = clusterService.deleteCluster(cluster_id);
			assertThat(result, equalTo(1));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		/* 判断集群对象是否已经被删除 */
		try {
			Cluster sel_cluster = clusterService.getClusterByName(cluster_name);
			assertNull(sel_cluster);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			Host sel_host = hostService.loadHost(host_id);
			assertNotNull(sel_host);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			int result = hostService.deleteHost(host_id);
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Host sel_host = hostService.loadHost(host_id);
			assertNull(sel_host);
		} catch (Exception e1) {
			e1.printStackTrace();
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
