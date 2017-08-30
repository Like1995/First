package cn.smbms.service.provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.bill.BillDao;
import cn.smbms.dao.bill.BillDaoImpl;
import cn.smbms.dao.provider.ProviderDao;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.Role;
@Service
public class ProviderServiceImpl implements ProviderService {
	@Resource
	private ProviderDao providerDao;
	@Resource
	private BillDao billDao;
	
	/*public ProviderServiceImpl(){
		billDao = new BillDaoImpl();
	}*/
	public boolean add(Provider provider) {
		boolean flag = false;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);//开启JDBC事务管理
			if(providerDao.add(connection,provider) > 0)
				flag = true;
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.out.println("rollback==================");
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}
	/**
	 * 模糊查询
	 */
	public List<Provider> getProviderList(String proName,String proCode,int currentPageNo,int pageSize) {
		Connection connection = null;
		List<Provider> list = null;
		connection = BaseDao.getConnection();
		try {
			list = providerDao.getProviderList(connection, proCode,
					proName, currentPageNo, pageSize);
			if (list != null) {
				return list;
			}
		} catch (Exception e) { 
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return null;
	}

	/**
	 * 业务：根据ID删除供应商表的数据之前，需要先去订单表里进行查询操作
	 * 若订单表中无该供应商的订单数据，则可以删除
	 * 若有该供应商的订单数据，则不可以删除
	 * 返回值billCount
	 * 1> billCount == 0  删除---1 成功 （0） 2 不成功 （-1）
	 * 2> billCount > 0    不能删除 查询成功（0）查询不成功（-1）
	 * 
	 * ---判断
	 * 如果billCount = -1 失败
	 * 若billCount >= 0 成功
	 */
	public int deleteProviderById(String delId) {
		Connection connection = null;
		int billCount = -1;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);
			billCount = billDao.getBillCountByProviderId(connection,delId);
			if(billCount == 0){
				providerDao.deleteProviderById(connection, delId);
			}
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
			billCount = -1;
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return billCount;
	}

	public Provider getProviderById(String id) {
		Provider provider = null;
		Connection connection = null;
		try{
			connection = BaseDao.getConnection();
			provider = providerDao.getProviderById(connection, id);
		}catch (Exception e) {
			e.printStackTrace();
			provider = null;
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return provider;
	}

	public boolean modify(Provider provider) {
		Connection connection = null;
		boolean flag = false;
		try {
			connection = BaseDao.getConnection();
			if(providerDao.modify(connection,provider) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}
	/**
	 * 查询有多少个数据
	 */
	public int getProviderCount(String queryProcode, String queryProname) {
		Connection connection = null;
		connection = BaseDao.getConnection();
		int count = 0;
		try {
			count = providerDao.getUserCount(connection, queryProname, queryProcode);
			if (count != 0) {
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return 0;
		
	}
	public List<Provider> getNameList() {
		Connection connection = null;
		List<Provider> providerList = null;
		try {
			connection = BaseDao.getConnection();
			providerList = providerDao.getProviderName(connection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return providerList;
	}

}
