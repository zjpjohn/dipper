package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
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
import com.cmbc.devops.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class UserMapperTest {
	@Resource
	private UserMapper userMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertUser() {
		/* 组装插入数据库User对象 */
		User ins_user = new User();
		ins_user.setUserName("JunitUserName");
		ins_user.setUserPass("123456");
		ins_user.setUserMail("junit@sina.com.cn");
		ins_user.setUserPhone("13589766132");
		ins_user.setUserCompany("CMBC");
		ins_user.setUserLevel(1);
		ins_user.setUserStatus((byte) Status.USER.NORMAL.ordinal());
		ins_user.setUserLoginStatus("login");
		ins_user.setUserRoleid(1);
		ins_user.setUserCreatedate(new Date());
		ins_user.setUserCreator(1);
		ins_user.setCreateUserName("zhangsan");
		ins_user.setRoleString("manager");

		/* 插入数据库记录中 */
		try {
			userMapper.insertUser(ins_user);
			/* 获取插入后的User对象ID信息 */
			int ins_userId = ins_user.getUserId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_userId;
			/* 插入对象返回值大于0 */
			assertThat(ins_userId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectUser() {
		try {
			User sel_user = new User();
			sel_user.setUserId(record_id);
			sel_user = userMapper.selectUser(sel_user);
			assertThat(sel_user.getUserName(), equalTo("JunitUserName"));
			assertThat(sel_user.getUserStatus(), equalTo((byte) Status.USER.NORMAL.ordinal()));
			assertThat(sel_user.getUserCreator(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateUser() {
		/* 组装修改数据库的User对象 */
		User upd_user = new User();
		upd_user.setUserId(record_id);
		upd_user.setUserName("JunitUserNameUpdate");
		upd_user.setUserPhone("61450079");

		try {
			int result = userMapper.updateUser(upd_user);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的User对象 */
		try {
			User sel_user = new User();
			sel_user.setUserId(record_id);
			sel_user = userMapper.selectUser(sel_user);

			assertThat(sel_user.getUserName(), equalTo("JunitUserNameUpdate"));
			assertThat(sel_user.getUserPhone(), equalTo("61450079"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteUser() {
		try {
			/* 首先查询，确认User对象存在数据库中 */
			User bef_user = new User();
			bef_user.setUserId(record_id);
			bef_user = userMapper.selectUser(bef_user);
			assertNotNull(bef_user);

			/* 删除完成后，重新查询此User对象 */
			userMapper.deleteUser(record_id);

			User aft_user = new User();
			aft_user.setUserId(record_id);
			aft_user = userMapper.selectUser(aft_user);
			assertThat(aft_user.getUserStatus(), equalTo((byte) Status.USER.DELETE.ordinal()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
