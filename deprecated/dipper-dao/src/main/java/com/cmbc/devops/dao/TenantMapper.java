package com.cmbc.devops.dao;

import java.util.List;
import com.cmbc.devops.entity.Tenant;

/**
 * date：2015年8月19日 下午4:43:06 project name：cmbc-devops-dao
 * 
 * @author mayh
 * @version 1.0
 * @since JDK 1.7.0_21 file name：RegistryMapper.java description：
 */
public interface TenantMapper {
	/**
	 * @author yangqinglin
	 * @return List<Tenant>
	 * @throws Exception
	 * @version 1.0 2016年3月30日 select all tenant entity from DB
	 */
	public List<Tenant> selectAll(Tenant tenant) throws Exception;

	/**
	 * @author mayh
	 * @return Tenant
	 * @throws Exception
	 * @version 1.0 2016年3月30日 select single tenant entity from DB by tenant
	 *          Id(primary key)
	 */
	public Tenant selectTenantById(Integer tenantId) throws Exception;

	/**
	 * @author yangqinglin
	 * @return int
	 * @throws Exception
	 * @version 1.0 2016年3月30日 logical delete tenant entity from DB
	 */
	public int deleteTenantById(Integer tenantId) throws Exception;

	/**
	 * @author yangqinglin
	 * @return int
	 * @throws Exception
	 * @version 1.0 2016年3月30日 insert tenant entity into DB
	 */
	public int insertTenant(Tenant tenant) throws Exception;

	/**
	 * @author yangqinglin
	 * @return int
	 * @throws Exception
	 * @version 1.0 2016年3月30日 update tenant entity to DB
	 */
	public int updateTenant(Tenant tenant) throws Exception;

	/**
	 * @author yangqinglin
	 * @return int
	 * @throws Exception
	 * @version 1.0 2016年3月30日 logical batch remove Tenant entity from DB
	 */
	public int changeStatus(List<Integer> list) throws Exception;
}