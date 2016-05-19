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
import com.cmbc.devops.entity.ConPort;
import com.cmbc.devops.entity.Container;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ConportServiceTest {
	@Autowired
	private ConportService conportService;
	@Autowired
	private ContainerService containerService;

	/* 保存记录的ID信息 */
	private static Integer container_id = 0;
	private static Integer app_id = 12;
	private static Integer host_id = 23;

	@Before
	public void initConportService() {

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

		/* 组装插入数据库ConPort对象 */
		ConPort ins_conport = new ConPort();
		ins_conport.setContainerId(container_id);
		ins_conport.setConIp("192.3.155.155");
		ins_conport.setPubPort("5501");
		ins_conport.setPriPort("6607");

		try {
			int result = conportService.addConports(ins_conport);
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试创建仓库记录接口 */
	public void test_ConportService() {
		/** (1)测试查询容器下的所有ConPort对象 **/
		try {
			List<ConPort> conport_list = conportService.listConPorts(container_id);
			assertThat(conport_list.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** (1)测试应用的ID所有ConPort对象 **/
		try {
			List<ConPort> conport_list = conportService.listConPortsByAppId(app_id);
			assertThat(conport_list.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@After
	public void destroyConportService() {
		/** (1)首先判断容器和Conport对象在数据库中存在 **/
		Container sel_container = new Container();
		sel_container.setConId(container_id);
		try {
			sel_container = containerService.getContainer(sel_container);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertThat(sel_container.getConId(), equalTo(container_id));

		try {
			List<ConPort> conport_list = conportService.listConPorts(container_id);
			assertThat(conport_list.size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** (2)删除ConPort和容器 **/
		try {
			int result = containerService.removeContainer(container_id);
			assertThat(result, equalTo(1));

			String[] container_ids = new String[1];
			container_ids[0] = container_id + "";
			result = conportService.removeConports(container_ids);
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** (3)判断ConPort和容器删除成功 **/
		sel_container = new Container();
		sel_container.setConId(container_id);
		try {
			sel_container = containerService.getContainer(sel_container);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertNull(sel_container);

		try {
			List<ConPort> conport_list = conportService.listConPorts(container_id);
			assertThat(conport_list.size(), equalTo(0));
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
