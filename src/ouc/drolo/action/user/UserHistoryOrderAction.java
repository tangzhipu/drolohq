package ouc.drolo.action.user;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ouc.drolo.dao.UserDao;
import wph.iframework.Action;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.SqlServer;

/**
 *14/07/07  用户查询历史订单 (状态  37 的)
 *	修改：0804  作废订单(状态29)加入历史订单中
 *
 * @author jeep
 *
 */
public class UserHistoryOrderAction extends Action{
	private static Log logger = LogFactory.getLog(UserHistoryOrderAction.class);

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String res = "0";
		int userId = Integer.parseInt(request.getParameter("userId"));
		Database dbDatabase = null;
		
		try {
			dbDatabase =  new SqlServer();
			UserDao uDao = new UserDao(dbDatabase);
			res = uDao.findHistoryOrder(userId);
			
			logger.debug("用户编号 :"+userId+"  用户查询历史订单结果:"+res);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			dbDatabase.close();
		}
		
		return res;  
	}
	
}
