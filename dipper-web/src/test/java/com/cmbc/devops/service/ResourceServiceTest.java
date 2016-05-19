package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.DkResource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ResourceServiceTest {
	@Autowired
	private ResourceService resourceService;
	/* 保存记录的ID信息 */
	private static int record_id = 0;

	@Test
	/* 测试创建用户功能接口 */
	public void test1_CreateResource() {
		/* 组装插入数据库DkResource对象 */
		DkResource ins_dkres = new DkResource();
		ins_dkres.setResName("DkResName");
		ins_dkres.setResStatus((byte) Status.RESOURCE.NORMAL.ordinal());
		ins_dkres.setResCPU(30);
		ins_dkres.setResMEM(512);
		ins_dkres.setResBLKIO(772);
		ins_dkres.setResDesc("Junit Desc");
		ins_dkres.setResComment("Junit Comment");
		ins_dkres.setResCreator(1);
		ins_dkres.setResCreatetime(new Date());

		/* 插入数据库记录中 */
		try {
			int ins_result = resourceService.createResource(ins_dkres);
			/* 获取插入后的集群对象ID信息 */
			int ins_resId = ins_dkres.getResId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_resId;
			/* 插入对象返回值大于0 */
			assertThat(ins_result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试获取资源对象的JsonObject对象的方法 */
	public void test2_GetResJsonById() {
		try {
			JSONObject res_json = resourceService.getResJsonById(record_id);
			/* 判定获取json对象内容 */
			assertThat(res_json.getString("resName"), equalTo("DkResName"));
			assertThat(res_json.getInteger("resCPU"), equalTo(30));
			assertThat(res_json.getString("resDesc"), equalTo("Junit Desc"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_GetAllResource() {
		/* 获取全部资源的json数组 */
		try {
			JSONArray res_jsonarray = resourceService.getAllResource();
			assertThat(res_jsonarray.size(), greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试通过资源ID列表获取全部 */
	public void test4_SelectAllViaIds() {
		try {
			ArrayList<Integer> resid_list = new ArrayList<Integer>();
			resid_list.add(record_id);
			List<DkResource> res_list = resourceService.selectAllViaIds(resid_list);
			assertThat(res_list.size(), greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
