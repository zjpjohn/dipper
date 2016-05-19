package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.entity.ConPort;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ConPortMapperTest {
	@Resource
	private ConPortMapper conPortMapper;

	private static int record_id = 0;
	private static int container_id = 16;

	@Test
	public void test1_InsertConport() {
		/* 组装插入数据库ConPort对象 */
		ConPort ins_conport = new ConPort();
		ins_conport.setContainerId(container_id);
		ins_conport.setConIp("192.3.155.155");
		ins_conport.setPubPort("5501");
		ins_conport.setPriPort("6607");

		/* 插入数据库记录中 */
		try {
			conPortMapper.insertConport(ins_conport);
			/* 获取插入后的ConPort对象ID信息 */
			int ins_conportId = ins_conport.getId();
			/* 保留对象全局ID */
			record_id = ins_conportId;
			/* 插入对象返回值大于0 */
			assertThat(ins_conportId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectConport() {
		try {
			List<ConPort> conport_list = conPortMapper.selectConport(container_id);
			assertThat(conport_list.size(), greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateConport() {
		/* 组装修改数据库的ConPort对象 */
		ConPort upd_conport = new ConPort();
		upd_conport.setId(record_id);
		upd_conport.setContainerId(18);
		container_id = 18;
		upd_conport.setConIp("197.3.156.156");
		upd_conport.setPubPort("5502");
		upd_conport.setPriPort("6608");

		try {
			int result = conPortMapper.updateConport(upd_conport);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的ConPort对象 */
		try {
			List<ConPort> sel_conport_list = conPortMapper.selectConport(container_id);
			assertThat(sel_conport_list.size(), greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteConport() {
		try {
			/* 删除完成后，重新查询此ConPort对象 */
			String[] conport_ids = new String[1];
			conport_ids[0] = container_id + "";
			int result = conPortMapper.deleteConport(conport_ids);
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
