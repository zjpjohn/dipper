package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RoleMapperTest {
	@Resource
	private RoleMapper roleMapper;

	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库Role对象 */
		Role ins_role = new Role();
		ins_role.setRoleName("JunitRoleName");
		ins_role.setRoleDesc("Junit Role Desc");
		ins_role.setRoleRemarks((byte) 1);
		ins_role.setRoleStatus((byte) Status.ROLE.NORMAL.ordinal());

		/* 插入数据库记录中 */
		try {
			roleMapper.insert(ins_role);
			/* 获取插入后的Role对象ID信息 */
			int ins_roleId = ins_role.getRoleId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_roleId;
			/* 插入对象返回值大于0 */
			assertThat(ins_roleId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectByPrimaryKey() {
		try {
			Role sel_role = roleMapper.selectRoleById(record_id);
			assertThat(sel_role.getRoleName(), equalTo("JunitRoleName"));
			assertThat(sel_role.getRoleStatus(), equalTo((byte) Status.ROLE.NORMAL.ordinal()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateByPrimaryKey() {
		/* 组装修改数据库的Role对象 */
		Role upd_role = new Role();
		upd_role.setRoleId(record_id);
		upd_role.setRoleName("JunitRoleNameUpdate");
		upd_role.setRoleDesc("Junit Role Desc Update");
		upd_role.setRoleRemarks((byte) 1);
		upd_role.setRoleStatus((byte) Status.ROLE.NORMAL.ordinal());

		try {
			int result = roleMapper.update(upd_role);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的Role对象 */
		try {
			upd_role = roleMapper.selectRoleById(record_id);
			assertThat(upd_role.getRoleName(), equalTo("JunitRoleNameUpdate"));
			assertThat(upd_role.getRoleDesc(), equalTo("Junit Role Desc Update"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteByPrimaryKey() {
		try {
			/* 首先查询，确认Role对象存在数据库中 */
			Role bef_role = roleMapper.selectRoleById(record_id);
			assertNotNull(bef_role);

			/* 删除完成后，重新查询此Role对象 */
			roleMapper.deleteById(record_id);

			Role aft_role = roleMapper.selectRoleById(record_id);
			assertThat(aft_role.getRoleStatus(), equalTo((byte) Status.ROLE.DELETE.ordinal()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
