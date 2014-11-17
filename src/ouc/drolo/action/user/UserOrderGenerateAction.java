package ouc.drolo.action.user;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import ouc.drolo.action.diaodu.Dispatcher;
import ouc.drolo.dao.OperatorDao;
import ouc.drolo.dao.OrderDao;
import ouc.drolo.dao.UserDao;
import ouc.drolo.dao.areaDao;
import ouc.drolo.domain.Order;
import ouc.drolo.util.order.FileEveryDaySerialNumber;
import ouc.drolo.util.order.SerialNumber;
import wph.iframework.Action;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.SqlServer;
import wph.iframework.push.staffPush;

/**
 *   06-27  add  重复下单问题：-1 ：重复下单；
 * 	用户端 生成 订单
 * 
 * @author jeep
 * 
 */
public class UserOrderGenerateAction extends Action {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		SerialNumber serial = new FileEveryDaySerialNumber(4,"EveryDaySerialNumber.dat");
		String orderId = serial.getSerialNumber();  // 生成订单号

		String flag = "0";

		String userId = request.getParameter("userId");
		String takePhone = request.getParameter("takePhone"); // 收衣电话
		String address = request.getParameter("address");
		address = new String(address.getBytes("ISO-8859-1"), "UTF-8");
		// 经纬度
		String longitude = request.getParameter("longitude");
		String latitude = request.getParameter("latitude");
		String large = request.getParameter("large"); // 有无大件 默认 无

		String amount = request.getParameter("amount");
		String note = request.getParameter("note");
		note = new String(note.getBytes("ISO-8859-1"), "UTF-8");

		String userName = request.getParameter("name");
		userName = new String(userName.getBytes("ISO-8859-1"), "UTF-8");

		System.out.println("订单号 ： " + orderId + " 用户下单地址  : " + address
				+ "  用户下单姓名 :" + userName);

		Order order = new Order();
		order.setLarge(large);
		order.setAmount(amount);
		order.setNote(note);
		order.setTakePhone(takePhone);
		order.setAddress(address);
		order.setOrderId(orderId);
		order.setUserId(userId);
		order.setLongitude(longitude);
		order.setLatitude(latitude);
		order.setUserName(userName);
		
		Calendar c = Calendar.getInstance();
		int shi = c.get(Calendar.HOUR_OF_DAY);
		int fen = c.get(Calendar.MINUTE);
		Dispatcher dispatcher = Dispatcher.getInstance();
		Database db = null;
		try {
			db = new SqlServer();
			db.setAutoCommit(false);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		OrderDao orderDao = new OrderDao(db);
		OperatorDao operatorDao = new OperatorDao(db);

		UserDao uDao = new UserDao(db);
		boolean isCf = uDao.isCxOrder(order);
		if(isCf){ // 可下单
			if (shi < 20 && shi >= 7){
				order.schedule();
			}
			
			dispatcher.addnewOrder(order);
			areaDao dao=new areaDao(db);
			String staffId=dao.getAreaId(longitude, latitude);
			//String staffId = operatorDao.gettopone(longitude, latitude);
			
			if (staffId.equals("")) {
				flag = orderDao.shengchengOrder(order);
				order.setOrderId(flag);
				dispatcher.requests.add(order);
				System.out.println("放入队列了，队列里个数：" + dispatcher.requests.size());
			} else {
				System.out.println("cc：" );
				int staid = Integer.parseInt(staffId);
				String pushId = orderDao.getPushId(staid);
				flag = orderDao.generateOrder(order, staid);
				System.out.println("dd：" );
				String phoneString = operatorDao.getstaffphone(staffId);
				order.setStaffphone(phoneString);
				String addres = orderDao.getAddres(orderId);
				JSONObject js = new JSONObject();
				js.put("orderId", flag);
				js.put("address", addres);
				System.out.println("ee：" );
				staffPush.tixing(pushId, js.toString());
			}
		}else{  //重复下单
			flag = "-1" ;
		}
		
		try {
			db.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		return flag;
	}
}
