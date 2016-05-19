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

import com.cmbc.devops.entity.ClusterApp;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ClusterAppMapperTest {
	@Resource
	private ClusterAppMapper clusterAppMapper;

	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库ClusterApp对象 */
		ClusterApp ins_clusterapp = new ClusterApp();
		ins_clusterapp.setAppId(12);
		ins_clusterapp.setClusterId(36);

		/* 插入数据库记录中 */
		try {
			clusterAppMapper.insert(ins_clusterapp);
			/* 获取插入后的ClusterApp对象ID信息 */
			int ins_cluappId = ins_clusterapp.getId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_cluappId;
			/* 插入对象返回值大于0 */
			assertThat(ins_cluappId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectByID() {
		try {
			ClusterApp sel_cluapp = clusterAppMapper.selectById(record_id);
			assertThat(sel_cluapp.getAppId(), equalTo(12));
			assertThat(sel_cluapp.getClusterId(), equalTo(36));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateByID() {
		/* 组装修改数据库的ClusterApp对象 */
		ClusterApp upd_cluapp = new ClusterApp();
		upd_cluapp.setId(record_id);
		upd_cluapp.setAppId(24);
		upd_cluapp.setClusterId(30);
		try {
			int result = clusterAppMapper.updateByID(upd_cluapp);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的ClusterApp对象 */
		try {
			upd_cluapp = clusterAppMapper.selectById(record_id);
			assertThat(upd_cluapp.getAppId(), equalTo(24));
			assertThat(upd_cluapp.getClusterId(), equalTo(30));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteByID() {
		try {
			/* 首先查询，确认集群对象存在数据库中 */
			ClusterApp bef_cluapp = clusterAppMapper.selectById(record_id);
			assertNotNull(bef_cluapp);

			/* 删除完成后，重新查询此集群对象 */
			clusterAppMapper.deleteByID(record_id);
			ClusterApp aft_cluapp = clusterAppMapper.selectById(record_id);
			assertNull(aft_cluapp);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
