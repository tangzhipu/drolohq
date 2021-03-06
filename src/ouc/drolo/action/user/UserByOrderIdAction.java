package ouc.drolo.action.user;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ouc.drolo.dao.OrderDao;
import wph.iframework.Action;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.SqlServer;

/**
 *  根据 订单号 查询 订单详细信息  
 *      已被物流 接单 则显示 物流人员 电话
 * @author jeep
 *
 */
public class UserByOrderIdAction extends Action{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String orderInfo = "";
		String orderId = request.getParameter("orderId");
		try {
			Database dbDatabase =  new SqlServer();
			OrderDao orderDao = new OrderDao(dbDatabase);
			orderInfo = orderDao.findOrderByOrderId(orderId); 
			
			System.err.println("查询 订单 结果     :   "+orderInfo); 
			dbDatabase.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderInfo;
	}
	
}
