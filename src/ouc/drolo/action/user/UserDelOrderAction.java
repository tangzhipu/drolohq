package ouc.drolo.action.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ouc.drolo.dao.UserDao;
import wph.iframework.Action;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.SqlServer;

/** 14/08/05
 *  删除   订单，将 isDel 由 1 -> 0 
 * @author jeep
 *
 */
public class UserDelOrderAction extends Action{
	private static Log logger = LogFactory.getLog(UserDelOrderAction.class);

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String result = "0";
		String orderId = request.getParameter("orderId");
		
		Database db = null;
		try {
			db =  new SqlServer();
			UserDao uDao = new UserDao(db);
			result = uDao.delOrder(orderId);
			
			logger.debug("用户删除订单号 ： "+orderId+"  结果："+result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.close();
		}
		return result;
	}
	
}
