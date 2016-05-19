package com.cmbc.devops.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmbc.devops.dao.ConPortMapper;
import com.cmbc.devops.entity.ConPort;
import com.cmbc.devops.service.ConportService;

/**  
 * date：2015年8月31日 下午2:18:11  
 * project name：cmbc-devops-service  
 * @author langzi  
 * @version 1.0   
 * @since JDK 1.7.0_21  
 * file name：ConportServiceImpl.java  
 * description：  
 */
@Component
public class ConportServiceImpl implements ConportService {
	
	@Autowired
	private ConPortMapper conPortMapper;
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.service.ConportService#listConPorts(java.lang.Integer)
	 */
	@Override
	public List<ConPort> listConPorts(Integer containerId) throws Exception {
		return conPortMapper.selectConport(containerId);
	}
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.service.ConportService#listConPortsByAppId(java.lang.Integer)
	 */
	@Override
	public List<ConPort> listConPortsByAppId(Integer appId) throws Exception {
		return conPortMapper.selectConportByAppId(appId);
	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.service.ConportService#addConports(com.cmbc.devops.entity.ConPort)
	 */
	@Override
	public int addConports(ConPort port) throws Exception {
		return conPortMapper.insertConport(port);
	}
	
	/* (non-Javadoc)
	 * @see com.cmbc.devops.service.ConportService#updateConports(com.cmbc.devops.entity.ConPort)
	 */
	@Override
	public int updateConports(ConPort port) throws Exception {
		return conPortMapper.updateConport(port);
	}

	/* (non-Javadoc)
	 * @see com.cmbc.devops.service.ConportService#removeConports(java.lang.String[])
	 */
	@Override
	public int removeConports(String[] containerIds) throws Exception {
		return conPortMapper.deleteConport(containerIds);
	}

}
