package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
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
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.Parameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ParameterServiceTest {
	@Autowired
	private ParameterService parameterService;
	/* 保存记录的ID信息 */
	private static String param_name = "";

	@Test
	/* 测试创建仓库记录接口 */
	public void test1_Create() {

		/* 组装插入数据库Parameter对象 */
		Parameter ins_param = new Parameter();
		/* 组装参数名称 */
		param_name = "Junit" + getRandom();
		ins_param.setParamName(param_name);
		ins_param.setParamValue("Junit ParamValue");
		ins_param.setParamConnector((byte) 1);
		ins_param.setParamType((byte) Type.PARAMETER.INT.ordinal());
		ins_param.setParamStatus((byte) Status.PARAMETER.ACTIVATE.ordinal());
		ins_param.setParamReusable((byte) 0);
		ins_param.setParamMutex("Junit Mutex");
		ins_param.setParamDesc("Junit Desc");
		ins_param.setParamComment("Junit Comment");
		ins_param.setParamCreatetime(new Date());
		ins_param.setParamCreator(1);

		int result = parameterService.create(ins_param);
		/* Parameter记录创建成功 */
		assertThat(result, greaterThan(0));
	}

	@Test
	/* 测试通过通过参数名称获取参数对象的方法 */
	public void test2_SelectParamByName() {
		Parameter sel_param = null;
		try {
			sel_param = parameterService.selectParamByName(1,param_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* 获取列表 */
		assertThat(sel_param.getParamName(), equalTo(param_name));
		assertThat(sel_param.getParamValue(), equalTo("Junit ParamValue"));
		assertThat(sel_param.getParamType(), equalTo((byte) Type.PARAMETER.INT.ordinal()));
		assertThat(sel_param.getParamDesc(), equalTo("Junit Desc"));
	}

	@Test
	public void test3_AllParam() {
		/* 获取全部参数的JSON数组信息 */
		try {
			JSONArray allparam_array = parameterService.allParam(1);
			assertThat(allparam_array.size(), greaterThan(0));
			for (int count = 0, size = allparam_array.size(); count < size; count++) {
				JSONObject single_json = allparam_array.getJSONObject(count);
				assertNotNull(single_json);
			}
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
