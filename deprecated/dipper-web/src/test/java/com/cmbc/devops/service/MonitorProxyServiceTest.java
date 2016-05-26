package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Random;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.entity.MonitorProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MonitorProxyServiceTest {
	@Autowired
	private MonitorProxyService monitorProxyService;
	/* 保存记录的ID信息 */
	private static String mp_name = "";
	/* 保存新增监控代理的ID */
	private static Integer record_id = 0;

	@Test
	/* 测试创建仓库记录接口 */
	public void test1_Create() {
		/* 组装插入数据库MntrProxy JSONObject对象 */
		JSONObject ins_mpjson = new JSONObject();
		mp_name = "Junit_mpname" + getRandom();
		ins_mpjson.put("mpName", mp_name);
		ins_mpjson.put("mpIP", "197.3.133.133");
		ins_mpjson.put("mpPort", 6635);
		ins_mpjson.put("mpDesc", "Junit Desc");
		ins_mpjson.put("mpComment", "Junit Comment");
		ins_mpjson.put("userId", 1);

		int result = 0;
		try {
			result = monitorProxyService.createMonitorProxy(ins_mpjson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* MntrProxy记录创建成功 */
		assertThat(result, greaterThan(0));
	}

	@Test
	/* 测试通过通过监控代理名称获取参数对象的方法 */
	public void test2_GetMonitorProxyByName() {
		MonitorProxy sel_mp = null;
		try {
			sel_mp = monitorProxyService.getMonitorProxyByName(mp_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		record_id = sel_mp.getMpId();
		assertThat(sel_mp.getMpName(), equalTo(mp_name));
		assertThat(sel_mp.getMpIP(), equalTo("197.3.133.133"));
		assertThat(sel_mp.getMpPort(), equalTo(6635));
	}

	@Test
	public void test3_ListAll() {
		/* 获取全部监控代理的JSON数组信息 */
		try {
			MonitorProxy sel_mp = new MonitorProxy();
			JSONArray allmp_array = monitorProxyService.listAll(1, sel_mp);
			assertThat(allmp_array.size(), greaterThan(0));
			for (int count = 0, size = allmp_array.size(); count < size; count++) {
				JSONObject single_json = allmp_array.getJSONObject(count);
				assertNotNull(single_json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 清空新创建的监控代理对象 */
	public void test4_DeleteMultiMP() {
		/* 获取新建的监控代理对象 */
		ArrayList<Integer> del_list = new ArrayList<Integer>();
		del_list.add(record_id);
		try {
			int result = monitorProxyService.deleteMultiMP(del_list);
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新查询数据库，监控代理是否已经被删除 */
		MonitorProxy sel_mp = null;
		try {
			sel_mp = monitorProxyService.getMonitorProxyByName(mp_name);
			assertNull(sel_mp);
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
