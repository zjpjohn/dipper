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

import com.cmbc.devops.entity.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class UserRoleMapperTest {
	@Resource
	private UserRoleMapper userRoleMapper;

	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库UserRole对象 */
		UserRole ins_userrole = new UserRole();
		ins_userrole.setUserId(12);
		ins_userrole.setRoleId(23);

		/* 插入数据库记录中 */
		try {
			userRoleMapper.insert(ins_userrole);
			/* 获取插入后的UserRole对象ID信息 */
			int ins_urId = ins_userrole.getId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_urId;
			/* 插入对象返回值大于0 */
			assertThat(ins_urId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectByPrimaryKey() {
		try {
			UserRole sel_userrole = userRoleMapper.selectByPrimaryKey(record_id);
			assertThat(sel_userrole.getUserId(), equalTo(12));
			assertThat(sel_userrole.getRoleId(), equalTo(23));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateByPrimaryKey() {
		/* 组装修改数据库的UserRole对象 */
		UserRole upd_userrole = new UserRole();
		upd_userrole.setId(record_id);
		upd_userrole.setUserId(21);
		upd_userrole.setRoleId(32);

		try {
			int result = userRoleMapper.updateByPrimaryKey(upd_userrole);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的UserRole对象 */
		try {
			upd_userrole = userRoleMapper.selectByPrimaryKey(record_id);
			assertThat(upd_userrole.getUserId(), equalTo(21));
			assertThat(upd_userrole.getRoleId(), equalTo(32));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteByPrimaryKey() {
		try {
			/* 首先查询，确认UserRole对象存在数据库中 */
			UserRole bef_userrole = userRoleMapper.selectByPrimaryKey(record_id);
			assertNotNull(bef_userrole);

			/* 删除完成后，重新查询此UserRole对象 */
			userRoleMapper.deleteByPrimaryKey(record_id);

			UserRole aft_userrole = userRoleMapper.selectByPrimaryKey(record_id);
			assertNull(aft_userrole);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
