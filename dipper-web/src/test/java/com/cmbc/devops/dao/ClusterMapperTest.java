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
import com.cmbc.devops.constant.Type;
import com.cmbc.devops.entity.Cluster;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ClusterMapperTest {
	@Resource
	private ClusterMapper clusterMapper;

	private static int record_id = 0;

	@Test
	public void test1_InsertCluster() {
		/* 组装插入数据库Cluster对象 */
		Cluster ins_cluster = new Cluster();
		ins_cluster.setClusterUuid(UUID.randomUUID().toString());
		ins_cluster.setClusterName("junit cluster");
		ins_cluster.setClusterType((byte) Type.CLUSTER.DOCKER.ordinal());
		ins_cluster.setClusterStatus((byte) Status.CLUSTER.NORMAL.ordinal());
		ins_cluster.setClusterPort("3201");
		ins_cluster.setManagePath("cluster_p3201");
		ins_cluster.setClusterLogFile("clu_3201.log");
		ins_cluster.setClusterDesc("junit description");
		ins_cluster.setMasteHostId(112);
		ins_cluster.setClusterCreatetime(new Date());
		ins_cluster.setClusterCreator(1);

		/* 插入数据库记录中 */
		try {
			clusterMapper.insertCluster(ins_cluster);
			/* 获取插入后的集群对象ID信息 */
			int ins_cluId = ins_cluster.getClusterId();
			/* 并且此记录为插入的主键值保存为全局变量 */
			record_id = ins_cluId;
			/* 插入对象返回值大于0 */
			assertThat(ins_cluId, greaterThan(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2_SelectCluster() {
		try {
			Cluster sel_cluster = clusterMapper.selectCluster(record_id);
			assertThat(sel_cluster.getClusterName(), equalTo("junit cluster"));
			assertThat(sel_cluster.getClusterPort(), equalTo("3201"));
			assertThat(sel_cluster.getManagePath(), equalTo("cluster_p3201"));
			assertThat(sel_cluster.getClusterLogFile(), equalTo("clu_3201.log"));
			assertThat(sel_cluster.getClusterDesc(), equalTo("junit description"));
			assertThat(sel_cluster.getMasteHostId(), equalTo(112));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3_UpdateCluster() {
		/* 组装修改数据库的Cluster对象 */
		Cluster upd_cluster = new Cluster();
		upd_cluster.setClusterId(record_id);
		upd_cluster.setClusterName("update junit cluster");
		upd_cluster.setClusterPort("3301");
		upd_cluster.setManagePath("cluster_p3301");
		upd_cluster.setClusterLogFile("clu_3301.log");
		upd_cluster.setClusterDesc("update junit description");
		upd_cluster.setMasteHostId(113);
		try {
			int result = clusterMapper.updateCluster(upd_cluster);
			assertThat(1, equalTo(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* 重新连接数据库查询修改的集群对象 */
		try {
			upd_cluster = clusterMapper.selectCluster(record_id);
			assertThat(upd_cluster.getClusterName(), equalTo("update junit cluster"));
			assertThat(upd_cluster.getClusterPort(), equalTo("3301"));
			assertThat(upd_cluster.getManagePath(), equalTo("cluster_p3301"));
			assertThat(upd_cluster.getClusterLogFile(), equalTo("clu_3301.log"));
			assertThat(upd_cluster.getClusterDesc(), equalTo("update junit description"));
			assertThat(upd_cluster.getMasteHostId(), equalTo(113));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4_DeleteCluster() {
		try {
			/* 首先查询，确认集群对象存在数据库中 */
			Cluster bef_cluster = clusterMapper.selectCluster(record_id);
			assertNotNull(bef_cluster);

			/* 删除完成后，重新查询此集群对象 */
			clusterMapper.deleteCluster(record_id);
			Cluster aft_cluster = clusterMapper.selectCluster(record_id);
			assertNull(aft_cluster);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
