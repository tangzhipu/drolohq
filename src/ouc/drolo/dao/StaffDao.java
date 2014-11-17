package ouc.drolo.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONStringer;

import ouc.drolo.util.CloseResource;
import wph.iframework.dao.Dao;
import wph.iframework.dao.db.Database;
import wph.iframework.utils.Md5Utils;

public class StaffDao extends Dao {

	public StaffDao(Database db) {
		super(db);
	}
	
	/**
	 * 物流人员登录 
	 * @param equipNum 
	 * @param staff
	 * @return 1:成功；2：密码不对 ；3 ：工号不存在
	 */
	public String login(String staffId,String password,String pushId,String longitude,String latitude, String equipNum){
		String sql = "SELECT * FROM _staff WHERE staffId = ? AND if_live= 1";//存在 ：1
		PreparedStatement ps = db.getPreparedStatement(sql);
		ResultSet rs = null;
		
		String setPush = "UPDATE _staff SET online=1,push_id=?,longitude=?,latitude=? WHERE staffId=?";
		PreparedStatement pushPs = db.getPreparedStatement(setPush);
		String sqlPsw = "";
		String flag = "-1";
		String staffPhone = "";
		try {
			
			ps.setString(1, staffId); 
			rs = ps.executeQuery();  
			 
			if(rs.next()){
				sqlPsw = rs.getString("password");
				staffPhone = rs.getString("tel");
				if(Md5Utils.encode(password).equals(sqlPsw)&&equipNum.equals(rs.getString("equipNum"))){
//					flag = "1" ;  //登陆成功
					flag = staffPhone; //登陆成功，返回物流电话
					pushPs.setString(1, pushId);
					pushPs.setString(2, longitude);
					pushPs.setString(3, latitude);
					pushPs.setString(4, staffId);
					int push = pushPs.executeUpdate();
					if(push==0){
						flag ="-3" ;  //更新失败  
					}
					pushPs.close();
				}else {    
					flag = "2";   //密码不对      
				}
			}else{                       
				flag = "3";   //工号不存在
			}
			pushPs.close();
			
		} catch (SQLException e) {
			System.out.println(e.getStackTrace());
			 return "-2" ;
		}finally{
			CloseResource.close(ps, rs);
		}
		return flag;
	}
	
	/**
	 * 检查客户端版本更新
	 * 
	 * @param version
	 * @param type
	 * @return
	 */
	public String checkUpdateInfo(String version, int type,int role) {
		String flag = "";
		String sql = "SELECT apk_version FROM _apk_version WHERE apk_type='"+ type + "'and role='"+role+"' "
				+ "and current_apk='true'";
		ResultSet rs = null;
		String[] now = version.split("[.]");
		String version1 = "";
		try {
			rs = db.executeQuery(sql);
			if (rs.next()) {
				version1 = rs.getString("apk_version");
				
				logger.debug("User's version： " + version+"--- Server's version  :"+version1);
				String[] latest = version1.split("[.]");
				int i = 0;
				while (i < latest.length) {
					if (Integer.parseInt(now[i].trim()) < Integer.parseInt(latest[i].trim())) {
						flag = "1";// 不是最新版本
						break;
					} else if (Integer.parseInt(now[i].trim()) == Integer
							.parseInt(latest[i].trim())) {
						i++;
						if (i == latest.length) {
							flag = "0";// 是最新版本
						}
					} else {
						flag = "0";// 是最新版本
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * 物流人员 退出登陆
	 * @param staff
	 * @return
	 */
	public String logout(String staffId) {
		String sql = "UPDATE _staff SET online=0 WHERE staffid=? ";
		PreparedStatement ps = db.getPreparedStatement(sql);
		String flag = "";
		try {
			ps.setString(1, staffId);
			int flg = ps.executeUpdate();
			
			if(flg==1){
					flag = "1" ;
				}else {    
					flag = "-1";// 退出失败
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/** v0925
	 *  查询     送衣订单  
	 * @param staffId
	 * @return
	 */
	public String findSendOrder(String staffId) {
		String result = "[]";
		ResultSet rs = null;
		JSONStringer json = new JSONStringer();  
		String sql = "select userName,takePhone,runNo,orderId,address from _order  "
						+ "where staffId=? and (status = '10' or status='34')";

		PreparedStatement ps = db.getPreparedStatement(sql);
		try {
			
/*			//------  手动 更新  订单状态  32 ->34    -----------------------------
			String usql ="	update _order set status=34 where status=32  and runNo in "
					+ "(select bill_no from laundry.dbo.cloth_info "
						+ "group by bill_no having min(state)>=10)";
			PreparedStatement ups = null;
			ups = db.getPreparedStatement(usql);
			
			int flg = ups.executeUpdate();
			
			ups.close();
			System.err.println("更新结果  :       " + flg); 
			//-----------------------------------
*/			
			ps.setString(1, staffId);
			rs = ps.executeQuery();
			 
			if(rs.next()){
				json.array();
				json.object().key("runNo").value(rs.getString("runNo")).
				key("userName").value(rs.getString("userName")).
				key("takePhone").value(rs.getString("takePhone")).
				key("orderId").value(rs.getString("orderId")).
				key("address").value(rs.getString("address")).
				endObject();
				while(rs.next()){
					json.object().key("runNo").value(rs.getString("runNo")).
					key("userName").value(rs.getString("userName")).
					key("takePhone").value(rs.getString("takePhone")).
					key("orderId").value(rs.getString("orderId")).
					key("address").value(rs.getString("address")).
					endObject();
				}
				json.endArray();
				result = json.toString();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			CloseResource.close(ps, rs); 
		}
		return result;
	}
	
	/**    06-15
	 * 送衣订单   
	 * @param staffId
	 * @return
	 */
	
	public String sendOrder(String staffId){
		String sql ="	update _order set status=34 where status=32  and runNo in "
				+ "(select bill_no from laundry.dbo.cloth_info "
					+ "group by bill_no having min(state)>=10)";
		PreparedStatement ps = null;
		int flg = 0 ;
		
		try {
			ps = db.getPreparedStatement(sql);
			flg = ps.executeUpdate();
			
			System.err.println("xxxxxxxxxxx  " + flg); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return  flg+"";
	}
	
	
	
	/**
	 * 物流人员 查询  送衣 订单 详情 
	 * @param orderId
	 * @return
	 */
	public String findSendOrderXq(String orderId) {
		ResultSet rs = null;
		JSONStringer json = new JSONStringer();  
		String sql = "SELECT clothesName,serviceName,id ,note  FROM _myclothes WHERE orderId=?";
		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			ps.setString(1, orderId);
			rs = ps.executeQuery();
			if(rs.next()){
				json.array();
				json.object().key("clothesName").value(rs.getString("clothesName")).
				key("serviceName").value(rs.getString("serviceName")).
				key("id").value(rs.getString("id")).
				key("note").value(rs.getString("note")).
				endObject();
				while(rs.next()){
					json.object().key("clothesName").value(rs.getString("clothesName")).
					key("serviceName").value(rs.getString("serviceName")).
					key("id").value(rs.getString("id")).
					key("note").value(rs.getString("note")).
					endObject();
				}
				json.endArray();
				return json.toString();
			}else{
				return "[]";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "[]";
	}

	/**
	 * 物流人员  上传 经纬度 
	 * @param staffId
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public String changeGps(String staffId, String longitude, String latitude) {
		String sql = "UPDATE _staff SET longitude=?,latitude=? WHERE staffId=? ";
		PreparedStatement ps = db.getPreparedStatement(sql);
		String flag = "";
		try {
			ps.setString(1, longitude);
			ps.setString(2, latitude);
			ps.setString(3, staffId);
			
			int flg = ps.executeUpdate();
			
			if(flg==1){
					flag = "1" ;  // 更新 成功
				}else {    
					flag = "-1";// 失败
			}
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 物流人员 查询 他所有的 订单记录 
	 * @param staffId
	 * @return
	 */
	public String allOrders(String staffId) {
		ResultSet rs = null;
		JSONStringer json = new JSONStringer();  
		String sql = "SELECT count(*) num, bill_no,custom_tel,custom_name  FROM laundry.dbo.cloth_info "
				+ "WHERE employee=? AND state =10 GROUP BY bill_no,custom_tel,custom_name";

		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			ps.setString(1, staffId);
			rs = ps.executeQuery();
			 
			if(rs.next()){
			
				json.array();
				json.object().key("runNo").value(rs.getString("bill_no")).
				key("userName").value(rs.getString("custom_name")).
				key("userTel").value(rs.getString("custom_tel")).
				key("number").value(rs.getString("num")).
				endObject();
				while(rs.next()){
		
				json.object().key("runNo").value(rs.getString("bill_no")).
				key("userName").value(rs.getString("custom_name")).
				key("userTel").value(rs.getString("custom_tel")).
				key("number").value(rs.getString("num")).
				endObject();
			}
				json.endArray();
				return json.toString();
			}else{
				return "[]";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "[]";
	}

	/**
	 * 物流人员 查询  所有 送衣  完成      订单记录  
	 * @param staffId
	 * @param month 
	 * @return
	 */
	public String allSendOrders(String staffId, String month) {
		int sendNumber = -1 ;
		ResultSet rs = null;
		JSONStringer json = new JSONStringer();   
		String sql = "SELECT orderId,runNo FROM _order WHERE staffId=? AND status BETWEEN '34' AND '37' AND convert(varchar(7), sendTime , 120)=?";

		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			sendNumber = sendOrderNumber(staffId);
			
			ps.setString(1, staffId);
			ps.setString(2, month); 
			rs = ps.executeQuery();
			 
			if(rs.next()){
			
				json.array();
				json.object().key("runNo").value(rs.getString("runNo")).
				key("orderId").value(rs.getString("orderId")).
				endObject();
				while(rs.next()){
				json.object().key("runNo").value(rs.getString("runNo")).
				key("orderId").value(rs.getString("orderId")).
				endObject();
			}
				json.object().key("sendNumber").value(sendNumber).endObject();
				json.endArray();
				return json.toString();
			}else{
				return "[]";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "[]";
	}
	
	/**
	 *  获取 物流人员 送衣 的订单 总数 
	 * @param staffId
	 * @return
	 */
	public int sendOrderNumber(String staffId){
		ResultSet rs = null ;
		int sendNumber = -1;
		String sql ="SELECT COUNT(*) AS numbers  FROM _order  WHERE staffId=? AND status>34 GROUP BY staffId";
		PreparedStatement ps = db.getPreparedStatement(sql);
		try {
			ps.setString(1, staffId);
			rs = ps.executeQuery();
			if(rs.next()){
				sendNumber = rs.getInt("numbers");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return sendNumber;
	}

	/**
	 * 物流人员 获取  所有 取衣 订单 记录 
	 * @param staffId
	 * @param month 
	 * @return
	 */
	public String allTakeOrders(String staffId, String month) { 
		int takeNumber = -1 ;
		ResultSet rs = null;
		JSONStringer json = new JSONStringer();  
		String sql = "SELECT runNo,orderId  FROM _financial WHERE staffId=? AND CONVERT(varchar(7), payTime , 120) = ?";

		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			takeNumber = takeOrderNumber(staffId);
			
			ps.setString(1, staffId);
			ps.setString(2, month); 
			
			rs = ps.executeQuery();
			 
			if(rs.next()){
			
				json.array();
				json.object().key("runNo").value(rs.getString("runNo")).
				key("orderId").value(rs.getString("orderId")).
				endObject();
				while(rs.next()){
				json.object().key("runNo").value(rs.getString("runNo")).
				key("orderId").value(rs.getString("orderId")).
				endObject();
			}
				json.object().key("takeNumber").value(takeNumber).endObject();
				json.endArray();
				return json.toString();
			}else{
				return "[]";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "[]";
	}
	
	/**
	 *  获取 物流人员 取衣 的订单 总数 
	 * @param staffId
	 * @return
	 */
	public int takeOrderNumber(String staffId){
		ResultSet rs = null ;
		int takeNumber = -1;
		String sql ="SELECT COUNT(*) AS numbers  FROM _order  WHERE staffId=? AND status='32' GROUP BY staffId";
		PreparedStatement ps = db.getPreparedStatement(sql);
		try {
			ps.setString(1, staffId);
			rs = ps.executeQuery();
			if(rs.next()){
				takeNumber = rs.getInt("numbers");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(ps!=null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return takeNumber;
	}

	/**
	 * 物流人员  完成 送衣  订单
	 * @param staffId
	 * @param orderId
	 * @param sendTime 
	 * @return
	 */
	public String sendFinish(String staffId, String orderId, String sendTime) {
		String sql = "UPDATE _order SET status = 35 ,sendTime=? WHERE staffId=? AND orderId=? ";
		PreparedStatement ps = db.getPreparedStatement(sql); 
		String flag = "";
		try {
			ps.setString(1, sendTime); 
			ps.setString(2, staffId);
			ps.setString(3, orderId);
			int flg = ps.executeUpdate();
			
			// -------将送衣记录 插入到 表 sendfinish 中  0613
			String send ="INSERT INTO sendfinish(orderId,staffId,sendtime) VALUES(?,?,GETDATE())";
			PreparedStatement sendps = db.getPreparedStatement(send);
			sendps.setString(1, orderId);
			sendps.setString(2,staffId);
			int res =sendps.executeUpdate();
			
			
			if(flg==1 && res == 1){
					flag = "1" ;  // 送衣 成功
			}else {    
					flag = "-1";// 失败
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 物流人员 下单  0603
	 * @param staffId
	 * @param takePhone
	 * @param address
	 * @param userName
	 * @param note
	 * @param orderId 
	 * @return
	 */
	public String generateOrder(String staffId, String takePhone, 
			String address, String userName, String note, String orderId) {
		String flag = "-1";
		
		try {
			String sql = "insert into _order(orderId,address,takephone,userName,staffId,note,status,generateTime)"
					+ "values(?,?,?,?,?,?,31,getdate())";
			
			PreparedStatement ps = db.getPreparedStatement(sql);
			ps.setString(1, orderId);
			ps.setString(2, address);
			ps.setString(3, takePhone);
			ps.setString(4, userName);
			ps.setString(5, staffId);
			ps.setString(6, note);
			
			int flg = ps.executeUpdate();
			if(flg!=0){
				flag = orderId;
			}
			
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return "-2" ;
		}
		
		return flag;
	}

	/**
	 * 作废 订单  0612
	 * @param staffId
	 * @param orderId
	 * @return
	 */
	public String zfOrder(String staffId, String orderId) {
		PreparedStatement ps = null;
		String sql = "UPDATE _order  SET status='29' WHERE staffId=? AND orderId=?";
		String res = "-1";
		
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, staffId);
			ps.setString(2, orderId);
			
			int ret = ps.executeUpdate();
			if(ret == 1){
				res = "1";
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/*  14/08/05
	 * 物流人员修改订单地址 
	 */
	public String updateAddress(String orderId, String address) {
		String res = "-1";
		String sql = "UPDATE _order SET address=? WHERE orderId=?";
		PreparedStatement ps = null;
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, address);
			ps.setString(2, orderId);
			int ret = ps.executeUpdate();
			if(ret==1){
				res = "1";
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			res = "-2";
		}finally{
			CloseResource.close(ps, null); 
		}
		
		return res;
	}
	
}