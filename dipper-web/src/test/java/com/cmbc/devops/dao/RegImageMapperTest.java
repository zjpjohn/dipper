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

import com.cmbc.devops.entity.RegImage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RegImageMapperTest {
	@Resource
	private RegImageMapper regImageMapper;

	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库RegImage对象 */
		RegImage ins_regimg = new RegImage();
		ins_regimg.setRegistryId(1);
		ins_regimg.setImageId(12);

		/* 插入数据库记录中 */
		try {
			regImageMapper.insert(ins_regimg);
			/* 获取插入后的RegImage对象ID信息 */
			int ins_regimgId = ins_regimg.getId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_regimgId;
			/* 插入对象返回值大于0 */
			assertThat(ins_regimgId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectByPrimaryKey() {
		try {
			RegImage sel_regimg = regImageMapper.selectById(record_id);
			assertThat(sel_regimg.getRegistryId(), equalTo(1));
			assertThat(sel_regimg.getImageId(), equalTo(12));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateByPrimaryKey() {
		/* 组装修改数据库的RegImage对象 */
		RegImage upd_regimg = new RegImage();
		upd_regimg.setId(record_id);
		upd_regimg.setRegistryId(2);
		upd_regimg.setImageId(23);
		try {
			int result = regImageMapper.update(upd_regimg);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的RegImage对象 */
		try {
			upd_regimg = regImageMapper.selectById(record_id);
			assertThat(upd_regimg.getRegistryId(), equalTo(2));
			assertThat(upd_regimg.getImageId(), equalTo(23));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteCluster() {
		try {
			/* 首先查询，确认RegImage对象存在数据库中 */
			RegImage bef_regimg = regImageMapper.selectById(record_id);
			assertNotNull(bef_regimg);

			/* 删除完成后，重新查询此RegImage对象 */
			regImageMapper.deleteById(record_id);
			RegImage aft_regimg = regImageMapper.selectById(record_id);
			assertNull(aft_regimg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
