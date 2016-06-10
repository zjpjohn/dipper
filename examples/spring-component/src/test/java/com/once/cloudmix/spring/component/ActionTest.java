package com.once.cloudmix.spring.component;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class ActionTest 
    extends TestCase
{
    public void testApp()
    {
    	 BeanFactory factory = new ClassPathXmlApplicationContext("beans.xml");
         Action action = factory.getBean("action", Action.class);
         action.todo();
    }
}
