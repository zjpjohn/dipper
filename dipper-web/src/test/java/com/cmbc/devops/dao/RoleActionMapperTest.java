package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.entity.RoleAction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RoleActionMapperTest {
	@Resource
	private RoleActionMapper roleActionMapper;

	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库RoleAction对象 */
		RoleAction ins_roleaction = new RoleAction();
		ins_roleaction.setRoleId(1);
		ins_roleaction.setActionId(12);

		/* 插入数据库记录中 */
		try {
			roleActionMapper.insert(ins_roleaction);
			/* 获取插入后的RoleAction对象ID信息 */
			int ins_roleactionId = ins_roleaction.getId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_roleactionId;
			/* 插入对象返回值大于0 */
			assertThat(ins_roleactionId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectByPrimaryKey() {
		try {
			RoleAction sel_roleaction = roleActionMapper.selectById(record_id);
			assertThat(sel_roleaction.getRoleId(), equalTo(1));
			assertThat(sel_roleaction.getActionId(), equalTo(12));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateByPrimaryKey() {
		RoleAction upd_roleaction = new RoleAction();
		upd_roleaction.setId(record_id);
		upd_roleaction.setRoleId(2);
		upd_roleaction.setActionId(23);

		try {
			int result = roleActionMapper.update(upd_roleaction);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的RoleAction对象 */
		try {
			upd_roleaction = roleActionMapper.selectById(record_id);
			assertThat(upd_roleaction.getRoleId(), equalTo(2));
			assertThat(upd_roleaction.getActionId(), equalTo(23));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteByPrimaryKey() {
		try {
			/* 首先查询，确认RoleAction对象存在数据库中 */
			RoleAction bef_ra = roleActionMapper.selectById(record_id);
			assertNotNull(bef_ra);

			/* 删除完成后，重新查询此RoleAction对象 */
			roleActionMapper.deleteById(record_id);
			RoleAction aft_ra = roleActionMapper.selectById(record_id);
			assertNull(aft_ra);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
