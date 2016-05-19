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
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.Parameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ParameterMapperTest {
	@Resource
	private ParameterMapper parameterMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertParameter() {

		/* 组装插入数据库Parameter对象 */
		Parameter int_param = new Parameter();
		int_param.setParamName("Junit ParamName");
		int_param.setParamValue("Junit ParamValue");
		int_param.setParamConnector((byte) 1);
		int_param.setParamType((byte) Type.PARAMETER.INT.ordinal());
		int_param.setParamStatus((byte) Status.PARAMETER.ACTIVATE.ordinal());
		int_param.setParamReusable((byte) 0);
		int_param.setParamMutex("Junit Mutex");
		int_param.setParamDesc("Junit Desc");
		int_param.setParamComment("Junit Comment");
		int_param.setParamCreatetime(new Date());
		int_param.setParamCreator(1);

		/* 插入数据库记录中 */
		try {
			parameterMapper.insertParameter(int_param);
			/* 获取插入后的Parameter对象ID信息 */
			int ins_paramId = int_param.getParamId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_paramId;
			/* 插入对象返回值大于0 */
			assertThat(ins_paramId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectParamById() {
		try {
			Parameter sel_param = parameterMapper.selectParamById(record_id);
			assertThat(sel_param.getParamName(), equalTo("Junit ParamName"));
			assertThat(sel_param.getParamType(), equalTo((byte) Type.PARAMETER.INT.ordinal()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateParameter() {
		/* 组装修改数据库的Parameter对象 */
		Parameter upd_param = new Parameter();
		upd_param.setParamId(record_id);
		upd_param.setParamName("Junit ParamName Update");
		upd_param.setParamType((byte) Type.PARAMETER.MAP.ordinal());

		try {
			int result = parameterMapper.updateParameter(upd_param);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的Parameter对象 */
		try {
			upd_param = parameterMapper.selectParamById(record_id);
			assertThat(upd_param.getParamName(), equalTo("Junit ParamName Update"));
			assertThat(upd_param.getParamType(), equalTo((byte) Type.PARAMETER.MAP.ordinal()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteCluster() {
		try {
			/* 首先查询，确认集群Parameter存在数据库中 */
			Parameter bef_param = parameterMapper.selectParamById(record_id);
			assertNotNull(bef_param);

			/* 删除完成后，重新查询此Parameter对象 */
			parameterMapper.deleteParameter(record_id);
			Parameter aft_param = parameterMapper.selectParamById(record_id);
			assertNull(aft_param);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
