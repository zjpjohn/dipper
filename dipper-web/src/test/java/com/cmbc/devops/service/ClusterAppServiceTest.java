package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
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
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Cluster;
import com.cmbc.devops.entity.ClusterApp;
import com.cmbc.devops.model.ClusterModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ClusterAppServiceTest {
	@Autowired
	private ClusterAppService clusterAppService;
	@Autowired
	private AppService appService;
	@Autowired
	private ClusterService clusterService;

	/* 保存记录的ID信息 */
	private static String cluster_name = "";
	private static Integer cluster_id = 0;
	private static Integer app_id = 0;

	@Before
	public void initClusterAppService() {
		/* 向数据库中插入应用对象App */
		/* 组装插入数据库App对象 */
		App ins_app = new App();
		ins_app.setEnvIds("1,2,3");
		ins_app.setClusterIds("11,12");
		ins_app.setClusterNames("cluster11,cluster12");
		ins_app.setAppName("dm_cmbc" + getRandom());
		ins_app.setAppStatus((byte) Status.APP_STATUS.NORMAL.ordinal());
		ins_app.setAppCpu(2);
		ins_app.setAppMem(512);
		ins_app.setAppPortMap(true);
		ins_app.setAppPubPort(1234);
		ins_app.setAppPriPort(5678);
		ins_app.setAppEnv("-v /var/log");
		ins_app.setEnvNames("-v,-t,-i");
		ins_app.setBalanceId(16);
		ins_app.setAppVolumn("/temp/runapper:/var");
		ins_app.setAppHealth((byte) 1);
		ins_app.setAppMonitor((byte) 1);
		ins_app.setAppCommand("cd;");
		ins_app.setAppUrl("/junit/dm_cmbc");
		ins_app.setAppProxy("192.3.166.166:10051");
		ins_app.setAppGrayPolicy("grey_policy");
		ins_app.setAppDesc("test desc");
		ins_app.setAppCreatetime(new Date());
		ins_app.setAppCreator(1);

		try {
			int result = appService.addApp(ins_app);
			app_id = ins_app.getAppId();
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 生成集群的名称 */
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
		cluster_model.setMasteHostId(16);
		cluster_model.setUserId(1);
		cluster_model.setLogFile("cluster3303.log");

		try {
			int result = clusterService.createCluster(cluster_model);
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 首先根据集群的名称获取集群对象的ID */
		try {
			Cluster sel_cluster = clusterService.getClusterByName(cluster_name);
			cluster_id = sel_cluster.getClusterId();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 插入集群应用对象 */
		ClusterApp ins_cluapp = new ClusterApp();
		ins_cluapp.setAppId(app_id);
		ins_cluapp.setClusterId(cluster_id);
		try {
			int result = clusterAppService.addClusterApp(ins_cluapp);
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试创建集群应用映射记录接口 */
	public void test1_ClusterAppService() {
		/* 通过应用ID获取全部的集群应用映射 */
		Integer[] appids = new Integer[1];
		appids[0] = app_id;
		try {
			List<ClusterApp> ca_list = clusterAppService.listClusterAppsByAppId(appids);
			assertThat(ca_list.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 通过集群ID获取全部的集群应用映射 */
		Integer[] clusterids = new Integer[1];
		clusterids[0] = cluster_id;
		try {
			List<ClusterApp> ca_list = clusterAppService.listClusterAppsByClusterId(clusterids);
			assertThat(ca_list.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@After
	/* 销毁新创建的测试对象 */
	public void destroy_ClusterAppService() {
		Integer[] appids = new Integer[1];
		appids[0] = app_id;
		try {
			int result = clusterAppService.removeClusterAppByAppId(appids);
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 通过应用ID获取全部的集群应用映射 */
		try {
			List<ClusterApp> ca_list = clusterAppService.listClusterAppsByAppId(appids);
			assertThat(ca_list.size(), equalTo(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 数据库删除集群对象 */
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

		/* 数据库删除应用对象 */
		try {
			int result = appService.removeApp(app_id);
			assertThat(result, equalTo(1));
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
