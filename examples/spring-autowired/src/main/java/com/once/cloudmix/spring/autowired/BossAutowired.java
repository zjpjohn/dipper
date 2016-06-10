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


import org.springframework.beans.factory.annotation.Autowired;

import com.once.cloudmix.spring.autowired.beans.Car;

/**
 * @author wuheng(wuheng@otcaix.iscas.ac.cn)
 * @date   Jun 10, 2016
 *
 * @ref beans.xml
 * 
 * @see refresh function in class org.springframework.context.support.AbstractApplicationContext
 * 
 * 1. obtainFreshBeanFactory: parse beans.xml
 * 2. finishBeanFactoryInitialization: AutowiredAnnotationBeanPostProcessor.postProcessPropertyValues
 * 
 */
public class BossAutowired {

	@Autowired
	private Car car;

	@Override
	public String toString() {
		return car.toString();
	}
	
}
