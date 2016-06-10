/**
 * Copyright 2016-2016 Institute of Software, Chinese Academy of Sciences.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.once.cloudmix.spring.autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 10, 2016
 *
 */
public class BossTest extends TestCase {

	public void testBeanConfiguration() {
		String[] locations = { "classpath:beans.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(locations);
		BossBean boss = (BossBean) ctx.getBean("boss");
		assertEquals("京A20000", boss.toString());
	}
	
	public void testBeanAutowired() {
		String[] locations = { "classpath:autowired.xml" };
		ApplicationContext ctx = new ClassPathXmlApplicationContext(locations);
		BossAutowired boss = (BossAutowired) ctx.getBean("boss");
		assertEquals("京A40000", boss.toString());
	}
}
