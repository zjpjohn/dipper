package com.cmbc.devops.manager;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class UserManagerTest {
	@Autowired
	private UserManager userManager;

	private static int record_id = 0;

	@Test
	public void test1_Insert() {
		// /* 组装插入数据库User对象 */
		// User ins_user = new User();
		// ins_user.setUserName("JunitUserName");
		// ins_user.setUserPass("123456");
		// ins_user.setUserMail("junit@sina.com.cn");
		// ins_user.setUserPhone("13589766132");
		// ins_user.setUserCompany("CMBC");
		// ins_user.setUserLevel(1);
		// ins_user.setUserStatus((byte) Status.USER.NORMAL.ordinal());
		// ins_user.setUserLoginStatus("login");
		// ins_user.setUserRoleid(1);
		// ins_user.setUserCreatedate(new Date());
		// ins_user.setUserCreator(1);
		// ins_user.setCreateUserName("zhangsan");
		// ins_user.setRoleString("manager");
		//
		// /* 插入数据库记录中 */
		// try {
		// Result ins_result = userManager.create(ins_user,
		// "//192.168.2.123:6600");
		// /* 获取插入后的集群对象ID信息 */
		// // int ins_cluId = ins_cluster.getClusterId();
		// /* 并且此记录为插入的主键值保存为全局变量 */
		// // record_id = ins_cluId;
		// /* 插入对象返回值大于0 */
		// assertThat(ins_result.isSuccess(), equalTo(true));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Test
	public void test2_SelectCluster() {
		// try {
		// Cluster sel_cluster = clusterMapper.selectCluster(record_id);
		// assertThat(sel_cluster.getClusterName(), equalTo("junit cluster"));
		// assertThat(sel_cluster.getClusterPort(), equalTo("3201"));
		// assertThat(sel_cluster.getManagePath(), equalTo("cluster_p3201"));
		// assertThat(sel_cluster.getClusterLogFile(), equalTo("clu_3201.log"));
		// assertThat(sel_cluster.getClusterDesc(), equalTo("junit
		// description"));
		// assertThat(sel_cluster.getMasteHostId(), equalTo(112));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Test
	public void test3_UpdateCluster() {
		// /* 组装修改数据库的Cluster对象 */
		// Cluster upd_cluster = new Cluster();
		// upd_cluster.setClusterId(record_id);
		// upd_cluster.setClusterName("update junit cluster");
		// upd_cluster.setClusterPort("3301");
		// upd_cluster.setManagePath("cluster_p3301");
		// upd_cluster.setClusterLogFile("clu_3301.log");
		// upd_cluster.setClusterDesc("update junit description");
		// upd_cluster.setMasteHostId(113);
		// try {
		// int result = clusterMapper.updateCluster(upd_cluster);
		// assertThat(1, equalTo(result));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// /* 重新连接数据库查询修改的集群对象 */
		// try {
		// upd_cluster = clusterMapper.selectCluster(record_id);
		// assertThat(upd_cluster.getClusterName(), equalTo("update junit
		// cluster"));
		// assertThat(upd_cluster.getClusterPort(), equalTo("3301"));
		// assertThat(upd_cluster.getManagePath(), equalTo("cluster_p3301"));
		// assertThat(upd_cluster.getClusterLogFile(), equalTo("clu_3301.log"));
		// assertThat(upd_cluster.getClusterDesc(), equalTo("update junit
		// description"));
		// assertThat(upd_cluster.getMasteHostId(), equalTo(113));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Test
	public void test4_DeleteCluster() {
		// try {
		// /* 首先查询，确认集群对象存在数据库中 */
		// Cluster bef_cluster = clusterMapper.selectCluster(record_id);
		// assertNotNull(bef_cluster);
		//
		// /* 删除完成后，重新查询此集群对象 */
		// clusterMapper.deleteCluster(record_id);
		// Cluster aft_cluster = clusterMapper.selectCluster(record_id);
		// assertNull(aft_cluster);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
}
