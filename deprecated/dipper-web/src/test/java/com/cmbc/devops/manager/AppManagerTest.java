package com.cmbc.devops.manager;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

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
import com.cmbc.devops.bean.Result;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.ClusterWithHostContainerNum;
import com.cmbc.devops.model.ClusterModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AppManagerTest {

	@Resource
	private AppManager appManager;

	/* 保存记录的ID信息 */
	private static String cluster_name = "junit_cluster7016559328";
	private static Integer host_id = 83;
	private static Integer cluster_id = 0;

	@Test
	public void test_CheckName() {
		/* 组装插入数据库App对象 */
		App ins_app = new App();
		ins_app.setEnvIds("1,2,3");
		ins_app.setClusterIds("11,12");
		ins_app.setClusterNames("cluster11,cluster12");
		ins_app.setAppName("dm_cmbc");
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

		Result result = appManager.addApp(ins_app);
		assertThat(result.isSuccess(), equalTo(true));
	}

	@Test
	/* 测试创建ClusterManager记录接口 */
	public void test_ClusterManager() {
		/* 检查当前集群名称是否在用 */
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
