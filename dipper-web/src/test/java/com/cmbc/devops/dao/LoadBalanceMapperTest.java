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
import com.cmbc.devops.entity.LoadBalance;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class LoadBalanceMapperTest {
	@Resource
	private LoadBalanceMapper loadBalanceMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertCluster() {
		/* 组装插入数据库LoadBalance对象 */
		LoadBalance ins_loadbalance = new LoadBalance();
		ins_loadbalance.setLbName("junit_loadbalance");
		ins_loadbalance.setLbDesc("Junit LoadBalance Desc");
		ins_loadbalance.setLbMainHost(12);
		ins_loadbalance.setLbMainConf("/var/config");
		ins_loadbalance.setLbBackupHost(16);
		ins_loadbalance.setLbBackupConf("/var/config");
		ins_loadbalance.setLbCreatetime(new Date());
		ins_loadbalance.setLbCreator(1);
		ins_loadbalance.setLbStatus((byte) Status.LOADBALANCE.NORMAL.ordinal());

		/* 插入数据库记录中 */
		try {
			loadBalanceMapper.insertLoadBalance(ins_loadbalance);
			/* 获取插入后的LoadBalance对象ID信息 */
			int ins_lbId = ins_loadbalance.getLbId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_lbId;
			/* 插入对象返回值大于0 */
			assertThat(ins_lbId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectLoadBalance() {
		try {
			LoadBalance sel_loadbalance = loadBalanceMapper.selectLoadBalance(record_id);
			assertThat(sel_loadbalance.getLbName(), equalTo("junit_loadbalance"));
			assertThat(sel_loadbalance.getLbMainHost(), equalTo(12));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateLoadBalance() {
		/* 组装修改数据库的LoadBalance对象 */
		LoadBalance upd_loadbalance = new LoadBalance();
		upd_loadbalance.setLbId(record_id);
		upd_loadbalance.setLbName("junit_loadbalance_test");
		upd_loadbalance.setLbMainHost(19);

		try {
			int result = loadBalanceMapper.updateLoadBalance(upd_loadbalance);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的LoadBalance对象 */
		try {
			upd_loadbalance = loadBalanceMapper.selectLoadBalance(record_id);
			assertThat(upd_loadbalance.getLbName(), equalTo("junit_loadbalance_test"));
			assertThat(upd_loadbalance.getLbMainHost(), equalTo(19));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteLoadBalance() {
		try {
			/* 首先查询，确认LoadBalance对象存在数据库中 */
			LoadBalance bef_loadbalance = loadBalanceMapper.selectLoadBalance(record_id);
			assertNotNull(bef_loadbalance);

			/* 删除完成后，重新查询此LoadBalance对象 */
			String[] lb_array = new String[1];
			lb_array[0] = record_id + "";
			loadBalanceMapper.deleteLoadBalance(lb_array);
			LoadBalance aft_loadbalance = loadBalanceMapper.selectLoadBalance(record_id);
			assertNull(aft_loadbalance);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
