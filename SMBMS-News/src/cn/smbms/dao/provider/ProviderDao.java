package cn.smbms.dao.provider;

import java.sql.Connection;
import java.util.List;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.Role;

public interface ProviderDao {
	
	/**
	 * 增加供应商
	 * @param connection
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public int add(Connection connection,Provider provider)throws Exception;


	/**
	 * 通过供应商名称、编码获取供应商列表-模糊查询-providerList
	 * @param connection
	 * @param proName
	 * @return
	 * @throws Exception
	 */
	public List<Provider> getProviderList(Connection connection,String proName,String proCode,int currentPageNo,int pageSize)throws Exception;
	
	/**
	 * 通过条件查询-用户表记录数
	 * @param connection
	 * @param userName
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	public int getUserCount(Connection connection,String proName,String proCode)throws Exception;
	
	/**
	 * 通过proId删除Provider
	 * @param delId
	 * @return
	 * @throws Exception
	 */
	public int deleteProviderById(Connection connection,String delId)throws Exception; 
	
	
	/**
	 * 通过proId获取Provider
	 * @param connection
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Provider getProviderById(Connection connection,String id)throws Exception; 
	
	/**
	 * 修改用户信息
	 * @param connection
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int modify(Connection connection,Provider provider)throws Exception;
	/**
	 * 查询Provider供应商名
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public List<Provider> getProviderName(Connection connection)throws Exception;
}