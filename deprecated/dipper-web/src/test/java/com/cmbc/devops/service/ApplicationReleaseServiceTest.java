package com.cmbc.devops.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONArray;
import com.cmbc.devops.bean.GridBean;
import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.App;
import com.cmbc.devops.entity.Image;
import com.cmbc.devops.model.ImageModel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ApplicationReleaseServiceTest {
	@Autowired
	private ImageService imageService;
	@Autowired
	private AppService appService;

	/* 保存记录的ID信息 */
	private static int image_id = 0;
	private static int app_id = 0;
	private static int envId = 0;

	@Before
	public void initAppImage() {
		/* 向数据库中插入应用对象App */
		/* 组装插入数据库App对象 */
		App ins_app = new App();
		ins_app.setEnvIds("1,2,3");
		ins_app.setClusterIds("11,12");
		ins_app.setClusterNames("cluster11,cluster12");
		ins_app.setAppName("dm_cmbc" + getRandom());
		ins_app.setAppStatus((byte) Status.APP_STATUS.NORMAL.ordinal());
		ins_app.setAppCpu(2);
		ins_app.setAppMem(512);
		ins_app.setAppPortMap(true);
		ins_app.setAppPubPort(1234);
		ins_app.setAppPriPort(5678);
		ins_app.setAppEnv("-v /var/log");
		ins_app.setEnvNames("-v,-t,-i");
		ins_app.setBalanceId(16);
		ins_app.setAppVolumn("/temp/runapper:/var");
		ins_app.setAppHealth((byte) 1);
		ins_app.setAppMonitor((byte) 1);
		ins_app.setAppCommand("cd;");
		ins_app.setAppUrl("/junit/dm_cmbc");
		ins_app.setAppProxy("192.3.166.166:10051");
		ins_app.setAppGrayPolicy("grey_policy");
		ins_app.setAppDesc("test desc");
		ins_app.setAppCreatetime(new Date());
		ins_app.setAppCreator(1);

		try {
			int result = appService.addApp(ins_app);
			app_id = ins_app.getAppId();
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 向数据库中插入镜像对象Image */
		/* 组装插入数据库Image对象 */
		Image ins_image = new Image();
		ins_image.setImageUuid(UUID.randomUUID().toString());
		ins_image.setImageStatus((byte) Status.IMAGE.NORMAL.ordinal());
		ins_image.setImageName("junit_image" + getRandom());
		ins_image.setImageTag("latest");
		ins_image.setImageSize("123456");
		ins_image.setImageType("APP");
		ins_image.setImageDesc("Junit Test Desc");
		ins_image.setAppId(app_id);
		ins_image.setImagePort("7709");
		ins_image.setImageCreatetime(new Date());
		ins_image.setImageCreator(1);

		try {
			int result = imageService.create(ins_image);
			image_id = ins_image.getImageId();
			/* Image记录创建成功 */
			assertThat(result, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	/* 测试创建仓库记录接口 */
	public void test1_ImageService() {
		/** (1)测试查询所有激活状态下的镜像 **/
		ArrayList<Image> image_list = new ArrayList<Image>();
		try {
			Image sel_img = new Image();
			image_list = (ArrayList<Image>) imageService.selectActiveAllImages(sel_img);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(image_list.size(), greaterThan(0));

		/** (2)通过应用的ID获取全部相关的镜像 **/
		try {
			
			GridBean image_bean = imageService.getImageListByappIdAndEnvId(1,1,1, 10, app_id, envId);
			assertThat(image_bean.getRows().size(), equalTo(1));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** (3)查询所有的镜像列表 **/
		ImageModel img_mdl = new ImageModel();
		try {
			JSONArray all_images = imageService.imageAllList(1,1, img_mdl);
			assertNotNull(all_images);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@After
	public void destroyAppImage() {
		try {
			int result = appService.removeApp(app_id);
			assertThat(result, greaterThan(0));
			boolean del_img = imageService.delete(image_id);
			assertTrue(del_img);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* 重新查询数据库，确认应用和镜像已经删除 */
		try {
			App sel_app = appService.findAppById(1,app_id);
			Image sel_img = imageService.loadImage(1,image_id);
			assertNull(sel_app);
			assertNull(sel_img);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* 生成10为随机数字符串 */
	private static Random random = new Random();

	private static String getRandom() {
		long num = Math.abs(random.nextLong() % 10000000000L);
		String s = String.valueOf(num);
		for (int i = 0; i < 10 - s.length(); i++) {
			s = "0" + s;
		}
		return s;
	}

}
