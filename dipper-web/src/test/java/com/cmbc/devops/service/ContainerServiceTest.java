package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
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
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Container;
import com.cmbc.devops.entity.Host;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ContainerServiceTest {
	@Autowired
	private ContainerService containerService;
	@Autowired
	private AppService appService;
	@Autowired
	private HostService hostService;

	/* 保存记录的ID信息 */
	private static Integer container_id = 0;
	private static Integer app_id = 0;
	private static Integer host_id = 0;

	@Before
	public void initContainerService() {
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
			assertThat(result, greaterThan(0));
			app_id = ins_app.getAppId();

		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 组装插入数据库主机Host对象 */
		Host ins_host = new Host();
		ins_host.setHostUuid(UUID.randomUUID().toString());
		ins_host.setHostName("junit_hostname" + getRandom());
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

		try {
			int result = hostService.createHost(ins_host);
			assertThat(result, greaterThan(0));
			host_id = ins_host.getHostId();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		/* 组装插入数据库Container对象 */
		Container ins_container = new Container();
		ins_container.setConUuid(UUID.randomUUID().toString());
		ins_container.setConImgid(16);
		ins_container.setConCreator(1);
		ins_container.setConName("junit_test" + getRandom());
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
		ins_container.setAppId(app_id);
		ins_container.setMonitorHostId("16");
		ins_container.setClusterIp("197.3.155.155");
		ins_container.setClusterPort("7000");
		ins_container.setHostId(host_id);
		ins_container.setConCreatetime(new Date());

		try {
			int result = containerService.addContaier(ins_container);
			assertThat(result, greaterThan(0));
			container_id = ins_container.getConId();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试ContainerService各个方法接口 */
	public void test_ContainerService() {
		/** (1)测试获取所有容器的链表 **/
		Container sel_container = new Container();
		try {
			List<Container> container_list = containerService.listAllContainer(sel_container);
			assertThat(container_list.size(), greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** (2)测试通过应用ID获取全部容器列表 **/
		try {
			List<Container> selapp_list = containerService.listContainersByAppId(app_id);
			assertThat(selapp_list.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** (3)测试通过主机的ID获取全部容器列表 **/
		try {
			List<Container> selhost_list = containerService.listContainersByHostId(host_id);
			assertThat(selhost_list.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** (4)通过应用查询所有挂在上面的容器列表 **/
		Container selapp_container = new Container();
		selapp_container.setAppId(app_id);
		try {
			List<Container> container_list = containerService.listAllContainerInApp(selapp_container);
			assertThat(container_list.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void destroyContainerService() {
		/** (1)首先确认应用、主机和容器的记录都存在 **/
		App sel_app = null;
		try {
			sel_app = appService.findAppById(1,app_id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertThat(sel_app.getAppId(), equalTo(app_id));

		Host sel_host = null;
		try {
			sel_host = hostService.loadHost(host_id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertThat(sel_host.getHostId(), equalTo(host_id));

		Container sel_container = new Container();
		sel_container.setConId(container_id);
		try {
			sel_container = containerService.getContainer(sel_container);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertThat(sel_container.getConId(), equalTo(container_id));

		/** (2)分别删除应用、主机和容器记录 **/
		try {
			int result = appService.removeApp(app_id);
			assertThat(result, greaterThan(0));
			result = hostService.deleteHost(host_id);
			assertThat(result, greaterThan(0));
			result = containerService.removeContainer(container_id);
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新查询数据库，确认应用、主机和容器已经删除 */
		try {
			sel_app = appService.findAppById(1,app_id);
			assertNull(sel_app);
			sel_host = hostService.loadHost(host_id);
			assertNull(sel_host);
			sel_container = new Container();
			sel_container.setConId(container_id);
			sel_container = containerService.getContainer(sel_container);
			assertNull(sel_container);
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
