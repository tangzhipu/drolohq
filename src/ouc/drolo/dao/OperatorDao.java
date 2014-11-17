package ouc.drolo.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.util.JSONStringer;
import ouc.drolo.domain.Order;
import ouc.drolo.domain.Staff;
import ouc.drolo.util.MapUtils;
import wph.iframework.dao.Dao;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.Page;
import wph.iframework.dao.db.ResultSetCallback;
import wph.iframework.utils.Md5Utils;

/**
 * 订单调度 
 *
 */
public class OperatorDao extends Dao {
	
	public OperatorDao(Database db) {
		super(db);
	}

	/**
	 * 获取订单状态
	 */
	public String getstaus(String oid) {
		String sqlString = "SELECT status FROM _order WHERE orderId='" + oid+ "'";
		ResultSet rSet = db.executeQuery(sqlString);
		String status = "";
		try {
			rSet.next();
			status = rSet.getString("status");
			rSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return status;
	}
	
	/**
	 * 获取订单的经纬度
	 */
	public int getinfo(final Order order, String orderId) {
		String sqlString = "SELECT longitude,latitude FROM _order WHERE orderId=?";
		List<Object> items = new ArrayList<Object>();
		items.add(orderId);
		int flag = db.executeQuery(sqlString, items, new ResultSetCallback() {

			@Override
			public int handle(ResultSet rs) throws SQLException {
				if (rs.next()) {
					order.setLongitude(rs.getString("longitude"));
					order.setLatitude(rs.getString("latitude"));
					return 1;
				}
				return 0;
			}
		});
		return flag;
	}
	
	/**
	 * 获取staffid
	 */
	public  String getstaffid(String oid){
		String  sqlString="SELECT staffId FROM _order WHERE orderId='"+oid+"'";
		ResultSet rSet=db.executeQuery(sqlString);
		String sid="";
		
		try {
			if(rSet.next()){
				sid=rSet.getString("staffId");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return sid;
	}
	
	
	/**
	 * 添加回访
	 */
	public int huifangupdate(String orderid, int xiyidu, int songyidu) {
		String sqlString = "INSERT INTO huifang(wlfw,xypz,orderid) VALUES(?,?,?) ";
		
		List<Object> list = new ArrayList<Object>();
		list.add(songyidu);
		list.add(xiyidu);
		list.add(orderid);
		int flag;
		int f1 = db.executeUpdate(sqlString, list);
		
		String sql = "UPDATE _order SET if_huifang=1 WHERE orderId = ?";
		PreparedStatement ps = db.getPreparedStatement(sql);
		int f2=0;
		try {
			ps.setString(1, orderid);
			f2 = ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		if(f2==1&&f1==1) {
			flag=1;
		}else{
			flag=-1;
		}
		
		return flag;
	}

	/**
	 * 获取用户电话
	 */
	public String NULL(String s) {
		String aString;
		if (s == null) {
			aString = "";
		} else
			aString = s;

		return aString;
	}

	public int getuserPh(String orderid, Order order) {
		String sql = "SELECT takePhone,userName,address,amount  FROM _order WHERE orderId='"+ orderid + "'";
		ResultSet rSet = db.executeQuery(sql);

		int flag = 0;
		try {
			while (rSet.next()) {
				String tel = NULL(rSet.getString("takePhone"));
				order.setTakePhone(tel);
				String username = NULL(rSet.getString("userName"));
				order.setUserName(username);
				String address = NULL(rSet.getString("address"));
				order.setAddress(address);
				String amount = NULL(rSet.getString("amount"));
				order.setAmount(amount);
				
				flag = 1;
			}
			rSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
			flag = 0;
		}

		return flag;
	}
	
	public boolean dingdanorder(final Page<Order> page)
	{
	   // logger.debug("通过分页bean，获取ad中全部信息" + page + "linker====" + db);
	    if (page == null){
	        throw new NullPointerException();
	    }
	    
	    if (db == null){
	        return false;
	    }
	    
	    final List<Order> list = new ArrayList<Order>();
	    
	    // 构建 SQL
	    StringBuffer sb = new StringBuffer();
	    sb.append("SELECT userName,address,id, orderId,takePhone,staffId,status,generateTIme FROM _order ");
	    String sql = sb.toString();
	   
	    String col = "generateTIme";
	    int ret = db.executeQuery1(sql, col, page, new ResultSetCallback()
	    { 
	        @Override
	        public int handle(ResultSet rs) throws SQLException{
	            if (rs == null){
	                return -1;
	            }
	            
	            try{
	                while (rs.next()){
	                	Order ad = new Order();
	                    page.setRowCount(rs.getInt("__count"));
	                    ad.setAddress(rs.getString("address"));
	                    ad.setUserName(rs.getString("userName"));
	                    ad.setOrderId(rs.getString("orderId"));
	                    ad.setTakePhone(rs.getString("takePhone"));
	                    ad.setStaffId(rs.getString("staffId"));
	                    String sta;
	                    switch (Integer.parseInt(rs.getString("status"))) {
						case 30:
							sta="已下单";
							break;
						case 31:
							sta="已接单";
							break;
						case 32:
							sta="已取衣";
							break;
						case 33:case 2: case 4: case 6:case 8:
							sta = "洗涤中";
							break;
						case 10:
						case 34:
							sta="物流送衣中";
							break;
						case 35:
							sta="待确认";
							break;
						case 36:
							sta="待评价";
							break;
						case 37:
							sta="已评价";
							break;						
						case 41:
							sta="物流超时未响应";
							break;
						case 40:
							sta="拒单";
							break;
						case 45:
							sta="物流已下班";
							break;
						case 29:
							sta="订单已取消";
							break;
						default:
							sta="其他";
							break;
						}
	                    ad.setStatus(sta);
	                    ad.setGenerateTime(rs.getString("generateTIme"));
	                    
	                    list.add(ad);
	                }
	            }catch (SQLException e){
	                e.printStackTrace();
	                return -1;
	            }finally{
	                if (rs != null){
	                    try{
	                        rs.close();
	                    }catch (SQLException e){
	                        e.printStackTrace();
	                    }
	                }
	            }
	            return 0;
	        }
	    });
	    
	    if (ret == -1){
	        return false;
	    }else{
	        page.setDataList(list);
	        return true;
	    }
	}
	
	
	/**
	 * 按条件获取订单
	 */
	public boolean orderByquest(final Page<Order> page,int leibie,String questcontext  ){
		boolean flag=false;
		if (page == null){
	        throw new NullPointerException();
	    }
	    
	    if (db == null){
	        return false;
	    }
	    
	    final List<Order> list = new ArrayList<Order>();
	    String sql;
	    String col;
	    StringBuffer sb = new StringBuffer();

	    if(leibie==1){
	    	sb.append("select id, orderId,takePhone,staffId,status,generateTIme from _order where staffId='"+questcontext+"'");
	    	sql = sb.toString();
	    	col = "id";
	    } else  if(leibie==2){
	    	sb.append("select id, orderId,takePhone,staffId,status,generateTIme from _order where takePhone='"+questcontext+"'");
	    	sql = sb.toString();
	    	col = "id";
	    }else{
	    	sb.append("select id, orderId,takePhone,staffId,status,generateTIme from _order where orderId='"+questcontext+"'");
	  	    sql = sb.toString();
	  	    col = "id";
	    }
		
	    int ret = db.executeQuery(sql, col, page, new ResultSetCallback()
	    {
	        @Override
	        public int handle(ResultSet rs) throws SQLException
	        {
	            if (rs == null)
	            {
	                return -1;
	            }
	            
	            try
	            {
	                while (rs.next())
	                {Order ad = new Order();
	                    page.setRowCount(rs.getInt("__count"));
	                  
	                 
	                    ad.setOrderId(rs.getString("orderId"));
	                    ad.setTakePhone(rs.getString("takePhone"));
	                    ad.setStaffId(rs.getString("staffId"));
	                    String sta;
	                    switch (Integer.parseInt(rs.getString("status"))) {
						case 30:
							sta="已下单";
							break;
						case 31:
							sta="已接单";
							break;
						case 32:
							sta="已取衣";
							break;
						case 33:case 2: case 4: case 6:case 8:
							sta = "洗涤中";
							break;
						case 10:
						case 34:
							sta="物流送衣中";
							break;
						case 35:
							sta="待确认";
							break;
						case 36:
							sta="待评价";
							break;
						case 37:
							sta="已评价";
							break;
						case 29:
							sta="订单已取消";
							break;
						case 40:
							sta="物流拒单";
							break;	
						case 41:
							sta="超时未响应";
							break;	
						case 45:
							sta="物流下班订单";
							break;	
						default:
							sta="其他";
							break;
						}
	                    ad.setStatus(sta);
	                    ad.setGenerateTime(rs.getString("generateTIme"));
	                    
	                    list.add(ad);
	                }
	            }
	            catch (SQLException e)
	            {
	                e.printStackTrace();
	                return -1;
	            }
	            finally
	            {
	                if (rs != null)
	                {
	                    try
	                    {
	                        rs.close();
	                    }
	                    catch (SQLException e)
	                    {
	                        e.printStackTrace();
	                    }
	                }
	            }
	            return 0;
	        }
	    });
	    
	    if (ret == -1)
	    {
	        return false;
	    }
	    else
	    {
	        page.setDataList(list);
	        return true;
	    }
	}
	
	/**
	 * 未响应该状态
	 */
	public int weixiangying(String orderId) {
		int flag;
		String sql = "UPDATE _order SET status= 41 WHERE orderId = '"+orderId+"'";
		flag=db.executeUpdate(sql);
	
		return flag ;
	}

	/**
	 * 该状态
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public String gaizhuangtai(String oid) {
		String sql = "update _order set status=34 where orderId='" + oid + "'";
		int flag = db.executeUpdate(sql);
		return flag + "";

	}
	
	/**
	 * 重新派单获取订单信息
	 */
	//public int getinfo(Order order,String orderId){
		// json.put("ordrId", orderId);		
		// json.put("phone",get Staffphone());
		/// json.put("stafid", StaffId);
		// json.put("lon", longitude);
		// json.put("lan", latitude);
		//String sql="select phone,staf ";
	//}

	/**
	 * 获取最近的N个配送员
	 */
	public synchronized List<Staff> getTopN(String longi, String lant, int n) {
		
		String sql = "SELECT longitude,staffId,latitude,push_id FROM _staff WHERE if_live=1 AND online=1";
		List<Car> sortedList = new ArrayList<Car>();
		List<Staff> stafflist = new ArrayList<Staff>();
		double lon = Double.parseDouble(longi);
		double lan = Double.parseDouble(lant);
		ResultSet rSet = db.executeQuery(sql);
		try {
			while (rSet.next()) {
				//System.out.println("在线物流人员有" + rSet.getString("staffId"));
				Car c = new Car();
				c.id = rSet.getString("staffId");// id代表pushid
				c.distance = MapUtils.getDistance(lon, lan,
						Double.parseDouble(rSet.getString("longitude")),
						Double.parseDouble(rSet.getString("latitude")));
				sortedList.add(c);
			}
			rSet.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (sortedList.size() > 0) {
			Car[] sorted = new Car[sortedList.size()];
			sortedList.toArray(sorted);
			Arrays.sort(sorted);
			for (int j = 0; j < sorted.length && j < n; j++) {
				String sqlString = "select push_id,name,longitude,latitude,tel from _staff where staffId='"
						+ sorted[j].id + "' and if_live=1";
				ResultSet rSet2 = db.executeQuery(sqlString);
				
				try {
					while (rSet2.next()) {
						Staff staff = new Staff();
						staff.setStaffId(sorted[j].id);
						staff.setPush_id(rSet2.getString("push_id"));
						staff.setName(rSet2.getString("name"));
						staff.setLongitude(rSet2.getString("longitude"));
						staff.setLatitude(rSet2.getString("latitude"));
						staff.setTel(rSet2.getString("tel"));
						stafflist.add(staff);
					}
					rSet2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}
		 System.out.println("dao得大小是：          "+stafflist.size());
		return stafflist;
	}
	
	/**
	 * 获取一个物流人员
	 * @param longi
	 * @param lant
	 * @return
	 */
	public String gettopone(String longi, String lant) {
		String sql = "select longitude,staffId,latitude,push_id from _staff where if_live=1 and online=1";
		List<Car> sortedList = new ArrayList<Car>();

		double lon = Double.parseDouble(longi);
		double lan = Double.parseDouble(lant);
		ResultSet rSet = db.executeQuery(sql);
		try {
			while (rSet.next()) {
				System.out.println("在线物流人员有" + rSet.getString("staffId"));
				Car c = new Car();
				c.id = rSet.getString("staffId");// id代表pushid
				c.distance = MapUtils.getDistance(lon, lan,
						Double.parseDouble(rSet.getString("longitude")),
						Double.parseDouble(rSet.getString("latitude")));
				sortedList.add(c);
			}
			rSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sortedList.size() > 0) {
			Car[] sorted = new Car[sortedList.size()];
			sortedList.toArray(sorted);
			Arrays.sort(sorted);
			for(int i=0;i<sortedList.size();i++){
			System.out.println("距离："+i+"是"+sorted[i].distance);}
			System.out.println("要分物流人员id   " + sorted[0].id);
			return sorted[0].id;
		}

		return "";
	}

	class Car implements Comparable<Car> {
		public String id;
		public long distance;

		@Override
		public int compareTo(Car o) {
			if (this.distance < o.distance) {
				return -1;
			} else if (this.distance > o.distance) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * 获取staffid的电话
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public String getstaffphone(String sid) {
		String sqlString = "select tel from _staff where staffId='" + sid + "'and if_live=1" ;
		ResultSet rSet = db.executeQuery(sqlString);
		String phString = "没查到呜呜";
		try {
			rSet.next();
			phString = rSet.getString("tel");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return phString;
	}
	
	/**
	 * 回访
	 * @param oid
	 * @return
	 */
	public String initHuifang(int oid){
		final JSONStringer json = new JSONStringer();
		String sql = "select userName,takePhone ,address,orderId,amount,userId from _order where operatorId=? and (status=35 or status=36 or status=37) and if_huifang=0";
		List<Object> items = new ArrayList<Object>();
		items.add(oid);
		json.array();
		int flag = db.executeQuery(sql, items, new ResultSetCallback() {
	
			public int handle(ResultSet rs) throws SQLException {
				try {
					while (rs.next()) {
						String tel = rs.getString("takePhone").trim();
						String add = rs.getString("address").replace('\n',' ').trim();
						String orderId = rs.getString("orderId");
						String amount = rs.getString("amount");
						int userId = rs.getInt("userId");
						String username = rs.getString("userName").trim();
					
						json.object()
						.key("tel").value(tel)
						.key("username").value(username)
						.key("add").value(add)
						.key("orderId").value(orderId)
						.key("amount").value(amount).endObject();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				return 1;
			}
		});
		json.endArray();
		return json.toString();
	}

	/**
	 * 初始化订单列表
	 * @param oid
	 * @return
	 */
	public String initOrderList(int oid) {
		final JSONStringer json = new JSONStringer();
		String sql = "select refuse,takePhone ,status,longitude,latitude,orderId,staffId ,operatorId from _order where operatorId=? and (status=40 or status=41)";
		List<Object> items = new ArrayList<Object>();
		items.add(oid);
		
		json.array();
		int flag = db.executeQuery(sql, items, new ResultSetCallback() {

			public int handle(ResultSet rs) throws SQLException {
				while (rs.next()) {
					String orderid = rs.getString("orderId");
					String status = rs.getString("status");
					String lon = rs.getString("longitude");
					String lat = rs.getString("latitude");
					String staffId = rs.getString("staffId");
					
					String sql2 = "select tel from _staff where staffId='"+ staffId + "'  and if_live=1 ";
					ResultSet rs2 = db.executeQuery(sql2);
					rs2.next();
					String tel = rs2.getString("tel");
					
					String zhuangtai = "";
					String leibei = "";
					String reason = "";
					if (status.equals("40")) {
						zhuangtai = "拒单";
						leibei = "1";
						reason = rs.getString("refuse");
					} else {
						zhuangtai = "未响应再派";
						leibei = "2";
						reason = tel;
					}

					json.object().key("tel").value(tel)
					.key("reason").value(reason)
					.key("orderid").value(orderid)
					.key("zhuangtai").value(zhuangtai)
					.key("leibie").value(leibei).key("lon").value(lon)
					.key("lan").value(lat).key("sid").value(staffId)
					.endObject();
				}

				return 1;
			}
		});
		json.endArray();
		return json.toString();
	}

	/**
	 * 配送员登陆
	 * @param username
	 * @param password
	 * @return
	 */
	public int login(String username, String password) {
		String sql = "SELECT pid, password FROM orperator where username=?";
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("pid", 0);

		PreparedStatement ps = db.getPreparedStatement(sql);
		ResultSet rs = null;
		try {
			ps.setString(1, username);
			rs = ps.executeQuery();
			if (rs.next()) {
				map.put("password", rs.getString("password").trim());
				if (Md5Utils.encode(password).equals((String) map.get("password"))) {
					map.put("pid", rs.getInt("pid"));
				} else { 
					map.put("pid", 2);
				}
			} else {
				map.put("pid", -1);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}

		return (Integer) map.get("pid");
	}		
	
}