package com.cmbc.devops.webservice.impl;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cmbc.devops.webservice.ApplicationClientBuilder;

/**  
 * date：2015年11月6日 上午11:48:15  
 * project name：cmbc-devops-core  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ApplicationServiceClientImpl.java  
 * description：  
 */
@Component("applicationClinetBuilder")
public class ApplicationClientBuilderImpl implements ApplicationClientBuilder {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationClientBuilder.class);
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.webservice.ApplicationClientBuilder#createClient(java.lang.String, java.lang.String)
	 */
	@Override
	public Client createClient(String ip, String port, String method, boolean firstCreated){
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance(); 
		String wsUrl = httpUrlBuilder(ip, port)+method;
		LOGGER.info(wsUrl);
	    Client client;
		try {
			client = dcf.createClient(wsUrl);
			LOGGER.info(client);
		    LOGGER.info("Client Created");
		    return client;
		} catch (Exception e) {
			LOGGER.error("Get client", e);
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.webservice.ApplicationClientBuilder#invoke(org.apache.cxf.endpoint.Client, java.lang.String)
	 */
	@Override
	public Object[] invoke(Client client, String methodName) throws Exception{
		LOGGER.info("Method name: "+ methodName);
		return client.invoke(methodName);
	}


	/* (non-Javadoc)
	 * @see com.cmbc.devops.webservice.ApplicationClientBuilder#destroyClinet(org.apache.cxf.endpoint.Client)
	 */
	@Override
	public void destroyClinet(Client client){
		LOGGER.info("Client destroyed");
		client.destroy();
	}
	
	/**
	 * @author langzi
	 * @param ip
	 * @param port
	 * @return
	 * @version 1.0
	 * 2015年11月16日
	 */
	private String httpUrlBuilder(String ip, String port){
		return "http://"+ip+":"+port;
	}

}
