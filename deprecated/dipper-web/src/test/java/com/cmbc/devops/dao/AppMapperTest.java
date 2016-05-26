package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.App;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AppMapperTest {
	@Resource
	private AppMapper appMapper;

	private static int record_id = 0;

	@Test
	public void test1_Insert() {
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

		/* 插入数据库记录中 */
		try {
			appMapper.insert(ins_app);
			/* 获取插入后的App对象ID信息 */
			int ins_appId = ins_app.getAppId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_appId;
			/* 插入对象返回值大于0 */
			assertThat(ins_appId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_Select() {
		try {
			App sel_app = appMapper.select(record_id);
			assertThat(sel_app.getAppName(), equalTo("dm_cmbc"));
			assertThat(sel_app.getAppEnv(), equalTo("-v /var/log"));
			assertThat(sel_app.getAppProxy(), equalTo("192.3.166.166:10051"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_Update() {
		/* 组装修改数据库的App对象 */
		App upd_app = new App();
		upd_app.setAppId(record_id);
		upd_app.setAppName("update_dm_cmbc");
		upd_app.setAppEnv("-uv /var/log");
		upd_app.setAppProxy("192.3.166.166:10051");

		try {
			int result = appMapper.update(upd_app);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的App对象 */
		try {
			upd_app = appMapper.select(record_id);
			assertThat(upd_app.getAppName(), equalTo("update_dm_cmbc"));
			assertThat(upd_app.getAppEnv(), equalTo("-uv /var/log"));
			assertThat(upd_app.getAppProxy(), equalTo("192.3.166.166:10051"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_Delete() {
		try {
			/* 首先查询，确认App对象存在数据库中 */
			App bef_app = appMapper.select(record_id);
			assertNotNull(bef_app);

			/* 删除完成后，重新查询此App对象 */
			appMapper.delete(record_id);
			App aft_app = appMapper.select(record_id);
			assertNull(aft_app);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
