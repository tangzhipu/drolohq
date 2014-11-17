package ouc.drolo.action.orperator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.tribes.group.interceptors.OrderInterceptor;
import org.apache.commons.collections.OrderedMapIterator;

import net.sf.json.JSONObject;



import ouc.drolo.action.diaodu.Dispatcher;
import ouc.drolo.dao.OperatorDao;
import ouc.drolo.dao.OrderDao;
import ouc.drolo.domain.Order;

import wph.iframework.Action;
import wph.iframework.dao.db.Database;
import wph.iframework.push.staffPush;

public class repaidan extends Action {
	private String orderId;
	private String staffid;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getStaffid() {
		return staffid;
	}
	public void setStaffid(String staffid) {
		this.staffid = staffid;
	}
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Database dbDatabase=getDatabase();
		
		OrderDao dao=new OrderDao(dbDatabase);
		OperatorDao odaoDao=new OperatorDao(dbDatabase);
		int flag=dao.repaidan(orderId,staffid);
		
		int sid=Integer.parseInt(staffid);
		String 	pushId=dao.getPushId(sid);
		String  address=dao.getAddres(orderId);
		String 	status=odaoDao.getstaus(orderId);
		
		Dispatcher dispatcher = Dispatcher.getInstance();
		Order order=dispatcher.getOrder(orderId);
		
		if(Integer.parseInt(status)==30){
			System.out.println("ffff"+orderId);
			order.cancel();
		}
		dispatcher.remove(orderId);

		Order  order2=new Order();
		OperatorDao oDao=new OperatorDao(dbDatabase);
		String tel=oDao.getstaffphone(staffid);
		order2.setOrderId(order.getOrderId());
		order2.setStaffId(staffid);
		order2.setStaffphone(tel);
		order2.setLatitude(order.getLatitude());
		order2.setLongitude(order.getLongitude());
		order2.setAddress(order.getAddress());
		dispatcher.addnewOrder(order2);
	
	    order2.schedule();

		int flag1=oDao.getinfo(order,orderId);
	
		System.out.println("pushID："+pushId);
		//order.schedule();
		JSONObject js=new JSONObject();
		js.put("orderId", orderId);
		js.put("address",address);
		staffPush.tixing(pushId, js.toString());
		
		return flag+"";
	}


}
