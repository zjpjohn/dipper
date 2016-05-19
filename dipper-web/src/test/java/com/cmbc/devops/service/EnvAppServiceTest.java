package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Random;

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
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Env;
import com.cmbc.devops.entity.EnvApp;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class EnvAppServiceTest {
	@Autowired
	private EnvAppService envAppService;
	@Autowired
	private AppService appService;
	@Autowired
	private EnvService envService;

	/* 保存记录的ID信息 */
	private static Integer env_id = 0;
	private static Integer app_id = 0;

	@Before
	public void initEnvAppService() {

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

		/* 组装插入数据库Env对象 */
		Env ins_env = new Env();
		ins_env.setEnvName("junit_envname" + getRandom());
		ins_env.setEnvStatus((byte) Status.ENVIRONMENT.NORMAL.ordinal());
		ins_env.setEnvDesc("Junit Test Env Desc");
		ins_env.setEnvCreatetime(new Date());
		ins_env.setEnvCreator(1);

		try {
			int result = envService.add(ins_env);
			env_id = ins_env.getEnvId();
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 组装插入数据库EnvApp对象 */
		EnvApp ins_envapp = new EnvApp();
		ins_envapp.setAppId(app_id);
		ins_envapp.setEnvId(env_id);

		try {
			int result = envAppService.add(ins_envapp);
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试创建仓库记录接口 */
	public void test1_EnvAppService() {
		/** (1)测试通过应用Id查询所有对应EnvApp **/
		List<EnvApp> selea_list = null;
		try {
			selea_list = envAppService.listAllByAppId(app_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(selea_list.size(), greaterThan(0));

		/** (2)测试通过Env的Id查询所有对应EnvApp **/
		try {
			selea_list = envAppService.listAllByEnvId(env_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(selea_list.size(), greaterThan(0));
	}

	@After
	public void destroy_EnvAppService() {
		App sel_app = null;
		Env sel_env = null;
		try {
			sel_app = appService.findAppById(1,app_id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertNotNull(sel_app);

		try {
			sel_env = envService.find(env_id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertNotNull(sel_env);

		try {
			int result = appService.removeApp(app_id);
			assertThat(result, greaterThan(0));
			result = envService.remove(env_id);
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新查询数据库，确认应用和镜像已经删除 */
		try {
			sel_app = appService.findAppById(1,app_id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertNull(sel_app);

		try {
			sel_env = envService.find(env_id);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertNull(sel_env);
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
