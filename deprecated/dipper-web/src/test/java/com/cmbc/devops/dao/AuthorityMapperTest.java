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

import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.Authority;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AuthorityMapperTest {
	@Resource
	private AuthorityMapper authorityMapper;

	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库Authority对象 */

		Authority ins_authority = new Authority();
		ins_authority.setActionName("junit_test");
		ins_authority.setActionDesc("Junit Desc");
		ins_authority.setActionRelativeUrl("/junit/test/url");
		ins_authority.setActionType((byte) Type.AUTHORITY.CHILD.ordinal());
		ins_authority.setActionRemarks("Junit Remarks");
		ins_authority.setActionParentId(1);

		/* 插入数据库记录中 */
		try {
			authorityMapper.insert(ins_authority);
			/* 获取插入后的Authority对象ID信息 */
			int ins_authId = ins_authority.getActionId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_authId;
			/* 插入对象返回值大于0 */
			assertThat(ins_authId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectByPrimaryKey() {
		try {
			Authority sel_authority = authorityMapper.selectById(record_id);
			assertThat(sel_authority.getActionName(), equalTo("junit_test"));
			assertThat(sel_authority.getActionDesc(), equalTo("Junit Desc"));
			assertThat(sel_authority.getActionRelativeUrl(), equalTo("/junit/test/url"));
			assertThat(sel_authority.getActionType(), equalTo((byte) Type.AUTHORITY.CHILD.ordinal()));
			assertThat(sel_authority.getActionRemarks(), equalTo("Junit Remarks"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateByPrimaryKey() {
		/* 组装修改数据库的Authority对象 */
		Authority upd_authority = new Authority();
		upd_authority.setActionId(record_id);
		upd_authority.setActionName("update_junit_test");
		upd_authority.setActionDesc("Update Junit Desc");
		upd_authority.setActionRelativeUrl("/junit/test/url/update");
		upd_authority.setActionType((byte) Type.AUTHORITY.PARENT.ordinal());
		upd_authority.setActionRemarks("Update Junit Remarks");

		try {
			int result = authorityMapper.update(upd_authority);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的Authority对象 */
		try {
			upd_authority = authorityMapper.selectById(record_id);
			assertThat(upd_authority.getActionName(), equalTo("update_junit_test"));
			assertThat(upd_authority.getActionDesc(), equalTo("Update Junit Desc"));
			assertThat(upd_authority.getActionRelativeUrl(), equalTo("/junit/test/url/update"));
			assertThat(upd_authority.getActionType(), equalTo((byte) Type.AUTHORITY.PARENT.ordinal()));
			assertThat(upd_authority.getActionRemarks(), equalTo("Update Junit Remarks"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteByPrimaryKey() {
		try {
			/* 首先查询，确认Authority对象存在数据库中 */
			Authority bef_authority = authorityMapper.selectById(record_id);
			assertNotNull(bef_authority);

			/* 删除完成后，重新查询此Authority对象 */
			authorityMapper.deleteById(record_id);
			Authority aft_authority = authorityMapper.selectById(record_id);
			assertNull(aft_authority);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
