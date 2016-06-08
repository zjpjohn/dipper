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
package com.once.crosscloud.jdbc;

import com.alibaba.druid.filter.config.ConfigTools;

import junit.framework.TestCase;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date 2016年6月8日
 *
 */
public class DruidDataSourceTest extends TestCase {

	public static final String ORIGINAL_PASSWORD = "onceas";
	
	public static final String CRYPTOGRAHIC_PASSWORD = "gn6/zEFs4raa8TKgHGsWBfpr5EdqQkA6sTvKQeNXLkEAxaqOvzC8ocpmtYH8H7P/TTioNk5EQvfmTFgc8X8ueg==";
	
	public void testDecrypt() throws Exception {
		String password = ConfigTools.decrypt(CRYPTOGRAHIC_PASSWORD);
		assertEquals(password, ORIGINAL_PASSWORD);
	}
	
	public void testEncrypt() throws Exception {
		String password = ConfigTools.encrypt(ORIGINAL_PASSWORD);
		assertEquals(password, CRYPTOGRAHIC_PASSWORD);
	}
	
}
