package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.cmbc.devops.entity.Registry;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RegistryServiceTest {
	@Autowired
	private RegistryService registryService;
	/* 保存记录的ID信息 */
	private static int record_id = 0;
	private static int host_id = 12;

	@Test
	/* 测试创建仓库记录接口 */
	public void test1_CreateRegistry() {
		/* 组装插入数据库Registry对象 */
		JSONObject ins_registry = new JSONObject();
		ins_registry.put("registryName", "JunitRegiName");
		ins_registry.put("registryDesc", "Junit Test Registry Desc");
		ins_registry.put("registryPort", 2308);
		ins_registry.put("userId", 1);
		ins_registry.put("hostId", host_id);

		int result = registryService.createRegistry(ins_registry);
		/* 仓库记录创建成功 */
		assertThat(result, greaterThan(0));
	}

	@Test
	/* 测试通过主机ID获取全部仓库列表的方法 */
	public void test2_ListRegistrysByHostId() {
		List<Registry> registry_list = null;
		try {
			registry_list = registryService.listRegistrysByHostId(host_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* 获取列表 */
		assertThat(registry_list.size(), greaterThan(0));
	}

	@Test
	public void test3_GetRegistry() {
		/* 获取全部资源的json数组 */
		try {
			Registry sel_registry = new Registry();
			sel_registry.setRegistryId(record_id);
			JSONObject regi_json = registryService.getRegistry(sel_registry);

			assertThat(regi_json.getString("registryName"), equalTo("JunitRegiName"));
			assertThat(regi_json.getString("registryDesc"), equalTo("Junit Test Registry Desc"));
			assertThat(regi_json.getInteger("registryPort"), equalTo(2308));
			assertThat(regi_json.getInteger("hostId"), equalTo(host_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
