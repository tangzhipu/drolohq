package ouc.drolo.action.staff;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ouc.drolo.dao.OrderDao;
import ouc.drolo.domain.Pay;
import wph.iframework.Action;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.SqlServer;

/**
 * 非 充值卡 付款
 * 付款： -3： 已经付款； -1： 付款失败； 1 ： 付款成功  
 * @author jeep
 *
 */
public class StaffPayAction extends Action {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String staffId = request.getParameter("staffId");
		String orderId = request.getParameter("orderId");
		String runNo = request.getParameter("runNo"); 
		runNo = new String(runNo.getBytes("ISO-8859-1"), "UTF-8");
		
		String payDate = request.getParameter("payDate");  //付款日期
		String totalPrice = request.getParameter("totalPrice");// 总金额
		String lastPrice = request.getParameter("lastPrice");  //实际 收取 金额
		
		String  type = request.getParameter("type");  // 付款方式
		String  money = request.getParameter("money");  // 金额
		
		String discount = request.getParameter("discount"); //折扣率
		
		String clothesNum = request.getParameter("num");  //衣物件数
		String userTel = request.getParameter("userTel");  //用户电话
		
		String flag = "1";
		
		Pay  orderPay = new Pay(staffId, runNo, orderId, payDate, lastPrice, totalPrice,discount,clothesNum,userTel) ;
		
		Database db=null;
		String str="-1";
			try {
				 db=new SqlServer();
				 db.setAutoCommit(false);
				 
				 OrderDao oDao = new OrderDao(db);
				 String isPay = oDao.findOrderState(orderId);
				 if(isPay.equals("1")){
					 flag = oDao.orderPay(orderPay, type, money);
					 
					 if(flag.equals("1")){
						  str = oDao.updateOrderState(orderId,runNo);
						  System.err.println(runNo+"      付款 订单号 ：　"+ orderId);
					 }	
				 }else{
					 str="-3" ; // 已经付款 
				 } 
				 db.commit(); 
				 db.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return str; 
		
	}

}
