package com.cmbc.devops.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.constant.Status;
import com.cmbc.devops.entity.MonitorProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class MntrProxyMapperTest {
	@Resource
	private MntrProxyMapper mntrProxyMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertMntrProxy() {

		/* 组装插入数据库MntrProxy对象 */
		MonitorProxy ins_mp = new MonitorProxy();
		ins_mp.setMpName("Junit_mpname");
		ins_mp.setMpStatus((byte) Status.MONITOR_PROXY.NORMAL.ordinal());
		ins_mp.setMpIP("197.3.133.133");
		ins_mp.setMpPort(6635);
		ins_mp.setMpDesc("Junit Desc");
		ins_mp.setMpComment("Junit Comment");
		ins_mp.setMpCreator(1);
		ins_mp.setMpCreatetime(new Date());

		/* 插入数据库记录中 */
		try {
			mntrProxyMapper.insertMntrProxy(ins_mp);
			/* 获取插入后的MntrProxy对象ID信息 */
			int ins_mpId = ins_mp.getMpId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_mpId;
			/* 插入对象返回值大于0 */
			assertThat(ins_mpId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectMntrProxyById() {
		try {
			MonitorProxy sel_mp = mntrProxyMapper.selectMntrProxyById(record_id);
			assertThat(sel_mp.getMpName(), equalTo("Junit_mpname"));
			assertThat(sel_mp.getMpPort(), equalTo(6635));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateMntrProxy() {
		/* 组装修改数据库的MntrProxy对象 */
		MonitorProxy upd_mp = new MonitorProxy();
		upd_mp.setMpId(record_id);
		upd_mp.setMpName("Junit_mpname_update");
		upd_mp.setMpPort(7781);

		try {
			int result = mntrProxyMapper.updateMntrProxy(upd_mp);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的MntrProxy对象 */
		try {
			upd_mp = mntrProxyMapper.selectMntrProxyById(record_id);
			assertThat(upd_mp.getMpName(), equalTo("Junit_mpname_update"));
			assertThat(upd_mp.getMpPort(), equalTo(7781));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteCluster() {
		try {
			/* 首先查询，确认MntrProxy对象存在数据库中 */
			MonitorProxy bef_mp = mntrProxyMapper.selectMntrProxyById(record_id);
			assertNotNull(bef_mp);

			/* 删除完成后，重新查询此MntrProxy对象 */
			ArrayList<Integer> mntrid_list = new ArrayList<Integer>();
			mntrid_list.add(record_id);
			mntrProxyMapper.deleteMntrProxy(mntrid_list);

			MonitorProxy aft_mp = mntrProxyMapper.selectMntrProxyById(record_id);
			assertNull(aft_mp);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
