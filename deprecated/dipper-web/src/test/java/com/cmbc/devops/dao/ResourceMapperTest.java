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
import com.cmbc.devops.entity.DkResource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ResourceMapperTest {
	@Resource
	private ResourceMapper resourceMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertResource() {
		/* 组装插入数据库DkResource对象 */
		DkResource ins_res = new DkResource();
		ins_res.setResName("JunitResName");
		ins_res.setResStatus((byte) Status.RESOURCE.NORMAL.ordinal());
		ins_res.setResCPU(30);
		ins_res.setResMEM(512);
		ins_res.setResBLKIO(772);
		ins_res.setResDesc("Junit Desc");
		ins_res.setResComment("Junit Comment");
		ins_res.setResCreator(1);
		ins_res.setResCreatetime(new Date());

		/* 插入数据库记录中 */
		try {
			resourceMapper.insertResource(ins_res);
			/* 获取插入后的Resource对象ID信息 */
			int ins_resId = ins_res.getResId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_resId;
			/* 插入对象返回值大于0 */
			assertThat(ins_resId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectResource() {
		try {
			DkResource sel_resource = resourceMapper.selectResource(record_id);
			assertThat(sel_resource.getResName(), equalTo("JunitResName"));
			assertThat(sel_resource.getResCPU(), equalTo(30));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateResource() {
		/* 组装修改数据库的Resource对象 */
		DkResource upd_resource = new DkResource();
		upd_resource.setResId(record_id);
		upd_resource.setResName("JunitResNameUpdate");
		upd_resource.setResCPU(66);

		try {
			int result = resourceMapper.updateResource(upd_resource);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的Resource对象 */
		try {
			upd_resource = resourceMapper.selectResource(record_id);
			assertThat(upd_resource.getResName(), equalTo("JunitResNameUpdate"));
			assertThat(upd_resource.getResCPU(), equalTo(66));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteResource() {
		try {
			/* 首先查询，确认Resource对象存在数据库中 */
			DkResource bef_resource = resourceMapper.selectResource(record_id);
			assertNotNull(bef_resource);

			/* 删除完成后，重新Resource此集群对象 */
			resourceMapper.deleteResource(record_id);
			DkResource aft_resource = resourceMapper.selectResource(record_id);
			assertNull(aft_resource);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
