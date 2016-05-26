package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.User;
import com.cmbc.devops.entity.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class UserServiceTest {
	@Autowired
	private UserService userService;
	/* 保存记录的ID信息 */
	private static int record_id = 0;

	@Test
	/* 测试创建用户功能接口 */
	public void test1_Create() {
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
			int ins_result = userService.create(ins_user);
			/* 获取插入后的集群对象ID信息 */
			int ins_userId = ins_user.getUserId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_userId;
			/* 插入对象返回值大于0 */
			assertThat(ins_result, equalTo(1));
			assertThat(ins_userId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试获取的那个用户对象的方法 */
	public void test2_GetUser() {
		try {
			User sel_user = new User();
			sel_user.setUserId(record_id);
			sel_user = userService.getUser(sel_user);
			assertThat(sel_user.getUserName(), equalTo("JunitUserName"));
			assertThat(sel_user.getUserPass(), equalTo("123456"));
			assertThat(sel_user.getUserMail(), equalTo("junit@sina.com.cn"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateUserRole() {
		/* 组装修改数据库的UserRole对象 */
		UserRole upd_userrole = new UserRole();
		upd_userrole.setRoleId(1);
		upd_userrole.setUserId(record_id);

		try {
			int result = userService.updateUserRole(upd_userrole);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	/* 测试激活当前添加的用户 */
	public void test4_Active() {
		try {
			int result = userService.active(record_id);
			assertThat(result, equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		User sel_user = new User();
		sel_user.setUserId(record_id);
		try {
			sel_user = userService.getUser(sel_user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* 用户状态判断用户状态是否已经激活 */
		assertThat(sel_user.getUserStatus(), equalTo((byte) 1));
	}
}
