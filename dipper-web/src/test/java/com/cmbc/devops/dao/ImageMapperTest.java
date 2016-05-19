package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.Image;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ImageMapperTest {
	@Resource
	private ImageMapper imageMapper;
	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		/* 组装插入数据库Image对象 */
		Image ins_image = new Image();
		ins_image.setImageUuid(UUID.randomUUID().toString());
		ins_image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
		ins_image.setImageName("junit_test");
		ins_image.setImageTag("latest");
		ins_image.setImageSize("123456");
		ins_image.setImageType("APP");
		ins_image.setImageDesc("Junit Test Desc");
		ins_image.setAppId(12);
		ins_image.setImagePort("7709");
		ins_image.setImageCreatetime(new Date());
		ins_image.setImageCreator(1);

		/* 插入数据库记录中 */
		try {
			imageMapper.insertImage(ins_image);
			/* 获取插入后的Image对象ID信息 */
			int ins_imgId = ins_image.getImageId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_imgId;
			/* 插入对象返回值大于0 */
			assertThat(ins_imgId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectByPrimaryKey() {
		try {
			Image sel_image = imageMapper.selectByPrimaryKey(record_id);
			assertThat(sel_image.getImageDesc(), equalTo("Junit Test Desc"));
			assertThat(sel_image.getAppId(), equalTo(12));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateImage() {
		/* 组装修改数据库的Image对象 */
		Image upd_image = new Image();
		upd_image.setImageId(record_id);
		upd_image.setImageStatus((byte) Status.IMAGE.MAKED.ordinal());
		upd_image.setImageDesc("Junit Test Desc Update");
		upd_image.setAppId(16);

		try {
			int result = imageMapper.updateImage(upd_image);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的Image对象 */
		try {
			upd_image = imageMapper.selectByPrimaryKey(record_id);
			assertThat(upd_image.getImageDesc(), equalTo("Junit Test Desc Update"));
			assertThat(upd_image.getAppId(), equalTo(16));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteCluster() {
		try {
			/* 首先查询，确认Image对象存在数据库中 */
			Image bef_image = imageMapper.selectByPrimaryKey(record_id);
			assertNotNull(bef_image);

			/* 删除完成后，重新查询此Image对象 */
			imageMapper.deleteImage(record_id);
			Image aft_image = imageMapper.selectByPrimaryKey(record_id);
			assertNull(aft_image);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
