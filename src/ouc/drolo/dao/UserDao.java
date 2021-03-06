package ouc.drolo.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.json.util.JSONStringer;
import ouc.drolo.domain.Order;
import ouc.drolo.domain.User;
import ouc.drolo.util.CloseResource;
import wph.iframework.dao.Dao;
import wph.iframework.dao.db.Database;
import wph.iframework.utils.Md5Utils;

public class UserDao extends Dao{

	
	public UserDao(Database db) {
		super(db);
	}

	
	/**
	 *  判断 在数据库 中 有没有 这个手机号
	 * @param tel
	 * @return
	 */
	public String isTel(String tel){
		String flag = "";
		String sql = "select * from _users where phone=? AND password IS NOT NULL ";
		PreparedStatement ps = db.getPreparedStatement(sql);
		ResultSet rs = null;
		try {
			ps.setString(1, tel);
			rs = ps.executeQuery();
			if(rs.next()){
				flag = "1";//号码存在
			}else{
				flag = "-1";  // 不存在
			}	
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return "-2";
		}finally{
			CloseResource.close(ps, rs); 
		}
		return flag;
	}
	
	
	/**  14/07/05   注册第一步  ：手机号、验证码，验证 手机号
	 * 用户注册
	 * @param user
	 * @return   
	 */
	public String registerFirst(String phone) { 
		
		String flag = "0";
		String sql = "INSERT INTO _users (phone,zctime) VALUES (?,GETDATE())";
		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			ps.setString(1, phone);
			int res = ps.executeUpdate();
			if(1==res){
				flag = "1";
			}
			ps.close();			
		}catch (Exception e) {
			System.out.println(e.getStackTrace());
			 flag = "-2";
		}
		return flag;
	}
	
	
	/**
	 * 用户注册 第二步  提交 手机号、姓名、密码 
	 * @param user
	 * @return  1:注册成功；2：注册失败;
	 */
	public String register(String phone, String password,String name,String inviteCode,String invite) { 
		
		String flag = "-1";
		String sql = "	INSERT INTO _users(name,password,phone,inviteCode,inviteCodeOther,zctime) VALUES(?,?,?,?,?,GETDATE())";
		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			ps.setString(1, name);
			ps.setString(2, Md5Utils.encode(password));
			ps.setString(3, phone);
			ps.setString(4, inviteCode);
			ps.setString(5, invite);
			
			int res = ps.executeUpdate();
			if(res==1){
				flag = "1";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			CloseResource.close(ps, null);
		} 
		return flag;
	}

	/*
	 * 获取 用户 id 
	 */
	public String getUid(String name,String phone){
		String ret = "-1";
		String sql = "SELECT id FROM  _users WHERE phone=?";
		PreparedStatement ps =null;
		ResultSet rs = null ;
		
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, phone);
			rs = ps.executeQuery();
			if(rs.next()){
				ret = rs.getString("id");
			}
			
			logger.debug("获取用户ID返回结果:"+ret);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			CloseResource.close(ps, rs); 
		}
		
		return ret;
	}
	
	/**
	 * 用户登录
	 * @param user
	 * @return String 成功登录：用户id ;密码不对："userId", "-1"；用户不存在："userId", "-2"
	 */
	public String login(User user) {
		String userId ="-2";
		String phone = user.getPhone();
		String sql = "SELECT id,password FROM _users WHERE phone = ? ";
		String password = "";
		ResultSet rs = null ;
		PreparedStatement ps = null;
		
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, phone); 
			rs = ps.executeQuery();
			if(rs.next()){
				password = rs.getString("password");
				if(Md5Utils.encode(user.getPassword()).equals(password)){//登陆成功，
						userId = rs.getString("id");
				}else {    
					userId = "-1";//密码不对      
				}
			}else{                       
				userId = "-2"; //用户不存在
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "-3";
		}finally{
			CloseResource.close(ps, rs); 
		}
		return userId;
	}



	/**
	 * 用户 根据 订单号 查询 订单下 所有衣物名称 和状态
	 * @param orderId
	 * @return
	 */
	public String findClothesByOrderId(String orderId) {
		ResultSet rs = null;
		JSONStringer json = new JSONStringer();  
		
		String runSql ="SELECT runNo FROM _order WHERE orderId=?";
		PreparedStatement runPs = db.getPreparedStatement(runSql);
		
		String sql = "SELECT clothesName,status FROM _myclothes WHERE orderId = ?";
		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			runPs.setString(1, orderId);
			ResultSet runRs = runPs.executeQuery();
			if(runRs.next()){  // 工厂系统 有 流水号  （衣物  已经 进入 到工厂） ，此时 查询的是 工厂系统里  的衣物 信息表 
				String infoSql ="SELECT cloth,state FROM laundry.dbo.cloth_info WHERE bill_no=?";
				PreparedStatement infoPs = db.getPreparedStatement(infoSql);
				infoPs.setString(1,runRs.getString("runNo"));
				ResultSet infoRs = infoPs.executeQuery();
				if(infoRs.next()){
					json.array();
					json.object().key("clothesName").value(infoRs.getString("cloth")).
					key("status").value(infoRs.getString("state").trim()).endObject();
					while(infoRs.next()){
						json.object().key("clothesName").value(infoRs.getString("cloth")).
						key("status").value(infoRs.getString("state").trim()).endObject();
					}
					json.endArray();
					return json.toString();
				}else{
					return "[]";
				}
				
			}else{
				ps.setString(1, orderId);
				rs = ps.executeQuery(); 
				if(rs.next()){
					json.array();
					json.object().key("clothesName").value(rs.getString("clothesName")).
					key("status").value(rs.getString("status").trim()).endObject();
					while(rs.next()){
						json.object().key("clothesName").value(rs.getString("clothesName")).
						key("status").value(rs.getString("status").trim()).endObject();
					}
					json.endArray();
					return json.toString();
				}else{
					return "[]";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			CloseResource.close(ps, rs); 
		}
		return "[]";
	}

	/**
	 * 用户 修改 密码 
	 * @param tel
	 * @param psw
	 * @return
	 */
	public String updatePsw(String tel, String psw) {
		
		String flag = "-1";
		String sql = " UPDATE _users SET password = ? WHERE  phone = ? ";
		
		PreparedStatement ps = db.getPreparedStatement(sql);
		try {
			ps.setString(1, Md5Utils.encode(psw));
			ps.setString(2, tel);
			
			 int flg = ps.executeUpdate();
			 if(flg!=0){
				 flag = "1" ;  //修改成功
			 }
			ps.close();
						
		} catch (Exception e) { 
			System.out.println(e.getStackTrace());
			return "-2"; 
		}
		return flag;
	}

	/**
	 * 用户   评价
	 * @param orderId
	 * @param wlfw
	 * @param xypz
	 * @param pjxq
	 * @return
	 */
	public String judge(String orderId, String wlfw, String xypz, String pjxq) {
		
		String flag ="-1";
		String sql = "INSERT INTO _judgment(orderId,wlfw,xypz,pjxq)VALUES(?,?,?,?)";
		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			ps.setString(1, orderId); 
			ps.setString(2, wlfw);
			ps.setString(3, xypz);
			ps.setString(4, pjxq);
			int flg = ps.executeUpdate();
			
			int update = updateStatus(orderId);  // 修改订单状态为  37 
			
			if(flg == 1&& update == 1){
				flag ="1" ;  // 评价成功 
			}else{
				flag = "-1"; //评价 失败 
			}
			ps.close();
						
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return "-2";
		}
		return flag;
	}

	/**
	 * 用户 评价 订单 后 修改  订单状态  为 37
	 * @param orderId
	 * @return
	 */
	public int updateStatus(String orderId){
		int  flag = -1 ;
		String sql = "UPDATE _order SET status='37' WHERE orderId=?";
		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			ps.setString(1, orderId); 
			int flg = ps.executeUpdate();
			
			if(flg == 1){
				flag =1 ;  // 确认 订单 成功 
			}else{
				flag = -1; //失败 
			}
			ps.close();
						
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return -2;
		}
		return flag;
	}
	
	/**
	 * 用户   确认 收取订单
	 * @param orderId
	 * @return
	 */
	public String confirmOrder(String orderId) {
		
		String flag ="-1";
		String sql = "UPDATE _order SET status='36' WHERE orderId=?";
		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			ps.setString(1, orderId); 
			int flg = ps.executeUpdate();
			
			if(flg == 1){
				flag ="1" ;  // 确认 订单 成功 
			}else{
				flag = "-1"; //失败 
			}
			ps.close();
						
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			return "-2";
		}
		return flag;
	}

	/**
	 * 判断  用户 手机号 是否  已经 注册过 
	 * @param phone
	 * @return
	 */
	public String isRegister(String phone) {
		String flag ="-1";
		String sql = "SELECT * FROM _users WHERE phone = ? ";
		PreparedStatement ps = null; 
		ResultSet rs = null;
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, phone); 
			rs = ps.executeQuery();
			if(rs.next()){
				flag ="-1";
			}else{
				flag = "1";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			CloseResource.close(ps, rs);
		}
		return flag;
	}

	/**   06 - 18
	 *  判断  userId 是否存在
	 * @param userId
	 * @return
	 */
	public String isUserId(String userId) {
		String flag ="-1";
		String sql = "SELECT * FROM _users WHERE id = ? ";
		PreparedStatement ps = null; 
		ResultSet rs = null;
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, userId); 
			rs = ps.executeQuery();
			if(rs.next()){
				flag ="1";
			}else{
				flag = "-1";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			CloseResource.close(ps, rs);
		}
		return flag;
	}
	
	/**  0627
	 * 判断是否重复 下单
	 * @param order
	 * @return
	 */
	public boolean isCxOrder(Order order){
		boolean flag = true ;
		String sql = "SELECT * FROM _order WHERE address=? AND takePhone=? AND (status=30 or status=41)";
		ResultSet rs = null;
		PreparedStatement ps = db.getPreparedStatement(sql);
		
		try {
			ps.setString(1, order.getAddress());
			ps.setString(2, order.getTakePhone());
			rs = ps.executeQuery();
			
			if(rs.next()){
				flag = false;   //重复下单
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			 CloseResource.close(ps, rs);
		}
		return flag ;
	}

	/*  14/07/07
	 * 查询某个用户的当前订单信息
	 */
	@Deprecated
	public String findCurrentOrder(int userId) {
		int number = 1;
		ResultSet rs = null;
		JSONStringer json = new JSONStringer();
		String sql = "SELECT orderId,status,runNo FROM _order WHERE userId = ? AND status<>37  AND status<>29 order by generateTime desc";
		PreparedStatement ps = db.getPreparedStatement(sql);
		try {
			ps.setInt(1, userId);
			rs = ps.executeQuery();
			if (rs.next()) {
				json.array();
				if (isWashing(rs.getString("runNo")) == 1) {
					json.object().key("number").value(number)
							.key("orderId").value(rs.getString("orderId"))
							.key("status").value("33").endObject();
				} else {
					json.object().key("number").value(number)
							.key("orderId").value(rs.getString("orderId"))
							.key("status").value(rs.getString("status"))
							.endObject();
				}
				while (rs.next()) {
					number++;
					if (isWashing(rs.getString("runNo")) == 1) { // 工厂 洗涤中，返回状态
																	// ：33
						json.object().key("number").value(number)
							.key("orderId").value(rs.getString("orderId"))
							.key("status").value("33").endObject();
					} else { // 不在洗涤中，则查询 应用端 订单记录 的状态信息
						json.object().key("number").value(number)
							.key("orderId").value(rs.getString("orderId"))
							.key("status").value(rs.getString("status")).endObject();
					}
				}

				json.endArray();
				return json.toString();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseResource.close(ps, rs); 
		}
		
		return "[]";
	}
	
	
	/**
	 * 判断 该 订单 是否在工厂 洗涤中
	 * @param runNo
	 * @return 1： 洗涤中 ;-1 : 已经完成 ，该查询 手机端 订单信息
	 */
	private int isWashing(String runNo) {
		int iswash = -1;
		ResultSet rs = null;

		String sql = "SELECT bill_no ,MIN(state) AS state FROM laundry.dbo.cloth_info WHERE  bill_no=? GROUP BY bill_no ";
		PreparedStatement ps = db.getPreparedStatement(sql);
		try {
			ps.setString(1, runNo);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("state") < 10) {
					iswash = 1; // 洗涤中
				} else {
					iswash = -1;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			CloseResource.close(ps, rs);
		}
		return iswash; 
	}


	/* 14/07/07
	 * 查询 历史订单
	 */
	public String findHistoryOrder(int userId) {
		int number = 1;
		ResultSet rs = null;
		JSONStringer json = new JSONStringer();
		String sql = "SELECT orderId,status,runNo FROM _order WHERE userId = ? "
				+ "AND ( status=37 OR status=29 ) AND isDel=1 "
					+ " order by generateTime desc";
		PreparedStatement ps = db.getPreparedStatement(sql);
		try {
			ps.setInt(1, userId);
			rs = ps.executeQuery();
			if (rs.next()) {
				json.array();
				json.object().key("number").value(number)
					.key("orderId").value(rs.getString("orderId"))
					.key("status").value(rs.getString("status"))
					.endObject();
				while (rs.next()){
					number++;
					json.object().key("number").value(number)
						.key("orderId").value(rs.getString("orderId"))
						.key("status").value(rs.getString("status")).endObject();
				}

				json.endArray();
				return json.toString();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseResource.close(ps, rs); 
		}
		
		return "[]";
	}


	/*
	 * 删除订单
	 */
	public String delOrder(String orderId) {
		String res = "-1";
		String sql ="UPDATE _order SET isDel=0 WHERE orderId=?";
		PreparedStatement ps = null;
		
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, orderId);
			int ret = ps.executeUpdate();
			if(ret != 0){
				res ="1";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			res = "-2";
		}finally{
			CloseResource.close(ps, null);
		}
		
		return res;
	}


	/**
	 * 查询用户信息
	 * @param uid
	 * @return
	 */
	public String findName(int uid) {
		JSONStringer json = new JSONStringer();
		
		String sql = "SELECT  * FROM _users WHERE id=?";
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		
		try {
			ps = db.getPreparedStatement(sql);
			ps.setInt(1, uid);
			rs = ps.executeQuery();
			if(rs.next()){
				json.object().key("name").value(rs.getString("name"))
				.key("sex").value(rs.getString("sex")==null?"":rs.getString("sex")) 
				.key("birthday").value(rs.getString("birthday")==null?"":rs.getString("birthday"))
				.key("headimages").value(rs.getString("headimages")==null?"":rs.getString("headimages"))
				.key("inviteCode").value(rs.getString("inviteCode")==null?"":rs.getString("inviteCode"))
				.endObject();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			CloseResource.close(ps, rs); 
		}
		
		return json.toString();
	}

	/**
	 * 更新用户信息
	 * @param uid
	 * @param sex
	 * @param birthday
	 * @param headimages
	 * @return
	 */
	public String updateUserInfo(String uid, String sex, String birthday,
			String headimages) {
		String flag ="-1";
		String sql = "UPDATE _users SET sex=? ,birthday=?,headimages=? WHERE id=?";
		PreparedStatement ps = null;
		
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, sex);
			ps.setString(2, birthday);
			ps.setString(3, headimages);
			ps.setString(4, uid);
			
			int ret = ps.executeUpdate();
			if(ret==1){
				flag = "1";
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			CloseResource.close(ps, null); 
		}
		
		
		return flag;
	}


	public String getActivityInfo() {
		String sql = "SELECT * FROM activity ORDER BY act_end DESC";
		JSONStringer json = new JSONStringer();
		PreparedStatement ps = null; 
		ResultSet rs = null;
		
		try {
			ps = db.getPreparedStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				json.array();
				json.object().key("name").value(rs.getString("act_name"))
				.key("image").value(rs.getString("act_image"))
				.key("html").value(rs.getString("act_imagexq"))
				.key("start").value(rs.getString("act_start")).key("end").value(rs.getString("act_end"))
				.key("isend").value(rs.getString("act_isend")).endObject();
			} // end if 
			
			while(rs.next()){
				json.object().key("name").value(rs.getString("act_name"))
				.key("image").value(rs.getString("act_image"))
				.key("html").value(rs.getString("act_imagexq"))
				.key("start").value(rs.getString("act_start")).key("end").value(rs.getString("act_end"))
				.key("isend").value(rs.getString("act_isend")).endObject();
			}
			json.endArray();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			CloseResource.close(ps, rs); 
		}
		
		return json.toString();
	}


	/**
	 * 用户添加意见反馈
	 * @param uid
	 * @param sgst
	 * @return
	 */
	public String addSgstFeedBack(String uid, String sgst) {
		String flag = "-1";
		String sql = "INSERT INTO feedback VALUES(?,?,GETDATE())";
		PreparedStatement ps = null;
		
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, uid);
			ps.setString(2, sgst);
			int ret = ps.executeUpdate();
			if(ret==1){
				flag = "1";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			CloseResource.close(ps, null); 
		}
		
		return flag;
	}


	
	public String getAddressNum(String uid) {
		String num = "0";
		String sql = "SELECT COUNT(*) AS num FROM _users_address WHERE userId=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = db.getPreparedStatement(sql);
			ps.setString(1, uid);
			rs = ps.executeQuery();
			if(rs.next()){
				num = rs.getString("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return num; 
	}
	
}
