package com.cmbc.devops.manager;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Random;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cmbc.devops.entity.ClusterWithHostContainerNum;
import com.cmbc.devops.manager.ClusterManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
/* Junit按照字母顺序执行所有方法 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class ClusterManagerTest {
	
	@Autowired
	private ClusterManager clusterManager;

	/* 保存记录的ID信息 */
	private static String cluster_name = "junit_cluster7016559328";

	// @Before
	// public void init_ClusterManager() {
	// clusterManager = (ClusterManager) ctx.getBean("ClusterManager");
	// }

	@Test
	public void test_CheckName() {
		
		// boolean b_clustername = clusterManager.checkName(cluster_name);
	}

	@Test
	/* 测试创建ClusterManager记录接口 */
	public void test_ClusterManager() {
		/* 检查当前集群名称是否在用 */
		//ClusterWithHostContainerNum clu_hostcon = clusterManager.detail(63);
		//boolean b_clustername = clusterManager.checkName(cluster_name);
		// assertThat(b_clustername, equalTo(true));
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
