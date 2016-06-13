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
package com.once.crosscloud.cores.jdbc;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date 2016年6月8日
 *
 */
public class DruidDataSource extends com.alibaba.druid.pool.DruidDataSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5532980598047875226L;

	private static final Logger m_logger = Logger.getLogger(DruidDataSource.class);
	
	@Override
	public String getPassword() {
        try {
			return ConfigTools.decrypt(password);
		} catch (Exception e) {
			m_logger.error("Unsupported password.");
			return null;
		}
    }

	@Override
	public void init() throws SQLException {
		// TODO Auto-generated method stub
		super.init();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
	}
	
}
