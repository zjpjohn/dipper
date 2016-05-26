package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.entity.Log;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class LogMapperTest {
	@Resource
	private LogMapper logMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertLog() {
		/* 组装插入数据库Log对象 */
		Log ins_log = new Log();
		ins_log.setLogObject("Junit LogObject");
		ins_log.setLogAction("Junit LogAction");
		ins_log.setLogResult("Junit LogResult");
		ins_log.setUserId(1);
		ins_log.setUserIp("197.3.155.129");
		ins_log.setLogCreatetime(new Date());
		ins_log.setBeginTime(new Date());
		ins_log.setEndTime(new Date());
		ins_log.setLogDetail("Junit LogDetail");

		/* 插入数据库记录中 */
		try {
			logMapper.insertLog(ins_log);
			/* 获取插入后的Log对象ID信息 */
			int ins_logId = ins_log.getLogId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_logId;
			/* 插入对象返回值大于0 */
			assertThat(ins_logId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectAll() {
		try {
			Log sel_log = new Log();
			sel_log.setLogId(record_id);
			List<Log> log_list = logMapper.selectAll(sel_log);
			assertThat(log_list.size(), greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateLog() {
		/* 组装修改数据库的Log对象 */
		Log upd_log = new Log();
		upd_log.setLogId(record_id);
		upd_log.setUserIp("197.3.155.130");

		try {
			int result = logMapper.updateLog(upd_log);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteCluster() {
		try {
			/* 首先查询，确认Log对象存在数据库中 */
			Log sel_log = new Log();
			sel_log.setLogId(record_id);
			List<Log> bef_loglist = logMapper.selectAll(sel_log);
			int bef_logsize = bef_loglist.size();

			/* 删除完成后，重新查询此Log对象 */
			logMapper.deleteLog(record_id);
			sel_log.setLogId(record_id);
			List<Log> aft_loglist = logMapper.selectAll(sel_log);
			int aft_logsize = aft_loglist.size();
			assertThat(aft_logsize, lessThan(bef_logsize));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
