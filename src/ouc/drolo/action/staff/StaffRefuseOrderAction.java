package ouc.drolo.action.staff;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import ouc.drolo.action.diaodu.Dispatcher;
import ouc.drolo.dao.OrderDao;
import ouc.drolo.domain.Order;
import wph.iframework.Action;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.SqlServer;
import wph.iframework.push.OperatorPush;

/**
 * 物流人员  拒单
 * @author jeep
 *
 */
public class StaffRefuseOrderAction extends Action {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String orderId = request.getParameter("orderId");
		String staffId = request.getParameter("staffId");
		String refuse = request.getParameter("refuse"); // 拒单理由
		refuse = new String(refuse.getBytes("ISO-8859-1"), "UTF-8");
		
		int isOrder = -1 ;
		String flag = "" ;
		String result;
		String lon="";
		String lan="";
		try {
			Database dbDatabase =  new SqlServer();
			dbDatabase.setAutoCommit(false);
			OrderDao orderDao = new OrderDao(dbDatabase);
			
			isOrder = orderDao.isStaffOrder(staffId, orderId); // 判断是否 是 分配该 物流人员的 订单
			if(isOrder == 1){
				Dispatcher dispatcher = Dispatcher.getInstance();
				Order order=dispatcher.getOrder(orderId);
				order.cancel();
				result= orderDao.refuseOrder(orderId,staffId,refuse);
				
				JSONObject ls = JSONObject.fromObject(result);
				flag=ls.getString("flag");
				lon=ls.getString("lon");
				lan=ls.getString("lan");
			}else{
				return "-1";
			}

			dbDatabase.commit();
			dbDatabase.close();
			System.out.println("物流人员   ： "+staffId+"        物流人员拒单理由 : "+refuse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String orid="\""+orderId+"\"";

		
		JSONObject json = new JSONObject();
		 json.put("ordrId", orderId);
		 json.put("reason",refuse );
		 json.put("lon", lon);
		 json.put("lan", lan);
		 json.put("staffid", staffId);
		 json.put("oid",orid );
		 String oid=1+"";
		 OperatorPush.jiuIfAccept(oid, json.toString());
		return flag;
	}

}
