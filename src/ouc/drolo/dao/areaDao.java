package ouc.drolo.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import ouc.drolo.action.area.checkIf_Inarea;
import ouc.drolo.domain.area;
import wph.iframework.dao.Dao;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.Page;
import wph.iframework.dao.db.ResultSetCallback;

public class areaDao extends Dao {

	public areaDao(Database db) {
		super(db);
	}

	/**
	 * 获取匹配区域内的staffid
	 */
	public String getAreaId(String lon, String lan) {

		String []zhengarea=new String[20] ;
		String staffId=102 + "";
		
		int i = 0;
		HashMap<String, area> map = new HashMap<String, area>();
		String sqlString = "SELECT areaId ,LTlon,LTlan,LBlon,LBlan,RTlon,RTlan,RBlon,RBlan FROM area ";
		ResultSet rSet = db.executeQuery(sqlString);
		try {// 获取区域四个角对象
			while (rSet.next()) {
				ArrayList<Double> polygonLon = new ArrayList<Double>();
				ArrayList<Double> polygonLan = new ArrayList<Double>();

				String areaId = rSet.getString("areaId");
				
				String ltlon = rSet.getString("LTlon");
				polygonLon.add(Double.parseDouble(ltlon));
				String ltlan = rSet.getString("LTlan");
				polygonLan.add(Double.parseDouble(ltlan));
				
				String lblon = rSet.getString("LBlon");
				polygonLon.add(Double.parseDouble(lblon));
				String lblan = rSet.getString("LBlan");
				polygonLan.add(Double.parseDouble(lblan));
			
				String rblon = rSet.getString("RBlon");
				polygonLon.add(Double.parseDouble(rblon));
				String rblan = rSet.getString("RBlan");
				polygonLan.add(Double.parseDouble(rblan));
				String rtlon = rSet.getString("RTlon");
				polygonLon.add(Double.parseDouble(rtlon));
				String rtlan = rSet.getString("RTlan");
				polygonLan.add(Double.parseDouble(rtlan));
				
				checkIf_Inarea test = new checkIf_Inarea();
				boolean flag = test.isPointInPolygon(Double.parseDouble(lon),
						Double.parseDouble(lan), polygonLon, polygonLan);
				System.out.println("是否是嫌疑区域" +flag);
				
				if (flag) {
					zhengarea[i] = areaId;
					System.out.println("数组区域号" + zhengarea[i]);
					i++;
				} // end if
			} // end while
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int f = 0;
		for (int j = 0; j < zhengarea.length; j++) {
			String sqlString2 = "select staffId,lon1,lon2,lon3,lon4,lon5,lon6,lon7,lon8,"
					+ "lan1,lan2,lan3,lan4,lan5,lan6,lan7,lan8 from area where areaId='"
					+ zhengarea[j] + "'";
			ArrayList<Double> polygonLon = new ArrayList<Double>();
			ArrayList<Double> polygonLan = new ArrayList<Double>();
			ResultSet rSet2 = db.executeQuery(sqlString2);
			String lon1;
			try {
					if(rSet2.next()){
						String sid = rSet2.getString("staffId");
						lon1 = rSet2.getString("lon1");
						polygonLon.add(Double.parseDouble(lon1));
						String lan1 = rSet2.getString("lan1");
						polygonLan.add(Double.parseDouble(lan1));
						String lon2 = rSet2.getString("lon2");
						polygonLon.add(Double.parseDouble(lon2));
						String lan2 = rSet2.getString("lan2");
						polygonLan.add(Double.parseDouble(lan2));
						String lon3 = rSet2.getString("lon3");
						polygonLon.add(Double.parseDouble(lon3));
						String lan3 = rSet2.getString("lan3");
						polygonLan.add(Double.parseDouble(lan3));
						String lon4 = rSet2.getString("lon4");
						polygonLon.add(Double.parseDouble(lon4));
						String lan4 = rSet2.getString("lan4");
						polygonLan.add(Double.parseDouble(lan4));
						String lon5 = rSet2.getString("lon5");
						polygonLon.add(Double.parseDouble(lon5));
						String lan5 = rSet2.getString("lan5");
						polygonLan.add(Double.parseDouble(lan5));
						String lon6 = rSet2.getString("lon6");
						polygonLon.add(Double.parseDouble(lon6));
						String lan6 = rSet2.getString("lan6");
						polygonLan.add(Double.parseDouble(lan6));
						String lon7 = rSet2.getString("lon7");
						polygonLon.add(Double.parseDouble(lon7));
						String lan7 = rSet2.getString("lan7");
						polygonLan.add(Double.parseDouble(lan7));
						String lon8 = rSet2.getString("lon8");
						polygonLon.add(Double.parseDouble(lon8));
						String lan8 = rSet2.getString("lan8");
						polygonLan.add(Double.parseDouble(lan8));
						
						checkIf_Inarea test = new checkIf_Inarea();
					boolean flag = test.isPointInPolygon(Double.parseDouble(lon),
							Double.parseDouble(lan), polygonLon, polygonLan);
					
					if (flag) {
						System.out.println("有匹配的区域");
						staffId = sid;
						f = 1;
						break;
					}
				} //  end if

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		if (f == 0) {
		//	staffId = 102 + "";
			System.out.println("没有匹配区域，只好分给102");
		}
		return staffId;
	}

	
	/**
	 * 删除区域
	 * @param page
	 * @param leibie
	 * @param questcontext
	 * @return
	 */
	public int deleteInfo(String areaid) {
		String sqlString = "delete from area where areaId=?";
		List<Object> items = new ArrayList<Object>();
		items.add(areaid);

		int flag = db.executeUpdate(sqlString, items);
		System.out.println("flag" + flag);
		
		return flag;
	}

	/**
	 * 按查询条件查询区域信息
	 * @param page
	 * @param leibie
	 * @param questcontext
	 * @return
	 */
	public boolean Byquest(final Page<area> page, int leibie,
			String questcontext) {
		logger.debug("通过分页bean，获取area中全部信息" + page + "linker=" + db);
		if (page == null) {
			throw new NullPointerException();
		}

		if (db == null) {
			return false;
		}

		final List<area> list = new ArrayList<area>();

		// 构建 SQL
		StringBuffer sb = new StringBuffer();
		if (leibie == 1) {
			sb.append("select * from area where areaId='" + questcontext + "' ");
		} else if (leibie == 2) {
			sb.append("select * from area where areaName='" + questcontext+ "' ");
		} else {
			sb.append("select * from area where staffId='" + questcontext+ "' ");
		}
		String sql = sb.toString();

		String col = "id";
		int ret = db.executeQuery(sql, col, page, new ResultSetCallback() {
			@Override
			public int handle(ResultSet rs) throws SQLException {
				if (rs == null) {
					return -1;
				}

				try {
					while (rs.next()) {
						area ad = new area();
						page.setRowCount(rs.getInt("__count"));

						ad.setAreaId(rs.getString("areaId"));
						ad.setAreaName(rs.getString("areaName"));
						ad.setStaffId(rs.getString("staffId"));
						ad.setLon1(rs.getString("lon1"));
						ad.setLon2(rs.getString("lon2"));
						ad.setLon3(rs.getString("lon3"));
						ad.setLon4(rs.getString("lon4"));
						ad.setLon5(rs.getString("lon5"));
						ad.setLon6(rs.getString("lon6"));
						ad.setLon7(rs.getString("lon7"));
						ad.setLon8(rs.getString("lon8"));
						ad.setLan1(rs.getString("lan1"));
						ad.setLan2(rs.getString("lan2"));
						ad.setLan3(rs.getString("lan3"));
						ad.setLan4(rs.getString("lan4"));
						ad.setLan5(rs.getString("lan5"));
						ad.setLan6(rs.getString("lan6"));
						ad.setLan7(rs.getString("lan7"));
						ad.setLan8(rs.getString("lan8"));
						ad.setLTlan(rs.getString("LTlan"));
						ad.setLTlon(rs.getString("LTlon"));
						ad.setLBlan(rs.getString("LBlan"));
						ad.setLBlon(rs.getString("LBlon"));
						ad.setRTlan(rs.getString("RTlan"));
						ad.setRTlon(rs.getString("RTlon"));
						ad.setRBlan(rs.getString("RBlan"));
						ad.setRBlon(rs.getString("RBlon"));
						ad.setCreateTime(rs.getString("createTime"));
						list.add(ad);
					}
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return -1;
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				return 0;
			}
		});

		if (ret == -1) {
			return false;
		} else {
			page.setDataList(list);
			return true;
		}
	}

	
	/**
	 * 提交修改的区域信息
	 * @param page
	 * @return
	 */
	public int editAddArea(String areaid, String[] lon, String[] lan,
			String staffId, String oldstaffid, String areaName,String oldareaName) {

		Arrays.sort(lon.clone());
		List<String> s = Arrays.asList(lon);
		TreeSet<String> trees = new TreeSet<String>(s);
		String lonMax = trees.last();
		String lonMin = trees.first();
		List<String> a = Arrays.asList(lan);
		
		TreeSet<String> treea = new TreeSet<String>(a);
		String lanMax = treea.last();
		String lanMin = treea.first();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// 可以方便地修改日期格式
		Date now = new Date();
		String hehe = dateFormat.format(now);
		
		int flag = -1;
		if (!oldstaffid.equals(staffId)) {
			String sqlstaffid = "select staffId from area where staffId=?";
			PreparedStatement pstaffid = db.getPreparedStatement(sqlstaffid);

			try {
				pstaffid.setString(1, staffId);
				ResultSet rs2 = pstaffid.executeQuery();
				if (rs2.next()) {
					flag = 2;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (!oldareaName.equals(areaName)) {
			String sqlareaname = "select areaName from area where areaName=?";
			PreparedStatement pareaname = db.getPreparedStatement(sqlareaname);

			try {
				pareaname.setString(1, areaName);
				ResultSet rs3 = pareaname.executeQuery();
				if (rs3.next()) {
					flag = 3;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (flag == -1) {
			System.out.println("areaid"+areaid);

			String sqlinsert = "update area set areaName=?,staffId=?, lon1=?,lon2=?,lon3=?,lon4=?,lon5=?,lon6=?,lon7=?,lon8=?,"
					+ "lan1=?,lan2=?,lan3=?,lan4=?,lan5=?,lan6=?,lan7=?,lan8=?,"
					+ "LTlon=?,LTlan=?,LBlon=?,LBlan=?,RTlon=?,RTlan=?,RBlon=?,RBlan=?,createTime=? where areaId=?";

			List<Object> itemList = new ArrayList<Object>();
			itemList.add(areaName);
			itemList.add(staffId);
//			for(int j=0;j<8;j++){
//				System.out.println("lon"+lon[j]+"   ");
//				System.out.println("lan"+lan[j]+"   ");
//			}
			itemList.add(lon[0]);
			itemList.add(lon[1]);
			itemList.add(lon[2]);
			itemList.add(lon[3]);
			itemList.add(lon[4]);
			itemList.add(lon[5]);
			itemList.add(lon[6]);
			itemList.add(lon[7]);
			itemList.add(lan[0]);
			itemList.add(lan[1]);
			itemList.add(lan[2]);
			itemList.add(lan[3]);
			itemList.add(lan[4]);
			itemList.add(lan[5]);
			itemList.add(lan[6]);
			itemList.add(lan[7]);

			itemList.add(lonMin);
			itemList.add(lanMax);
			itemList.add(lonMin);
			itemList.add(lanMin);
			itemList.add(lonMax);
			itemList.add(lanMax);
			itemList.add(lonMax);
			itemList.add(lanMin);
			
			itemList.add(hehe);
			itemList.add(areaid);
			flag = db.executeUpdate(sqlinsert, itemList);
			System.out.println("flag:" + flag);

		}
		return flag;
	}

	public boolean area(final Page<area> page) {
		logger.debug("通过分页bean，获取area中全部信息" + page + "linker====" + db);
		if (page == null) {
			throw new NullPointerException();
		}

		if (db == null) {
			return false;
		}

		final List<area> list = new ArrayList<area>();

		// 构建 SQL
		StringBuffer sb = new StringBuffer();
		sb.append("select * from area ");
		String sql = sb.toString();

		String col = "id";
		int ret = db.executeQuery(sql, col, page, new ResultSetCallback() {
			@Override
			public int handle(ResultSet rs) throws SQLException {
				if (rs == null) {
					return -1;
				}

				try {
					while (rs.next()) {
						area ad = new area();
						page.setRowCount(rs.getInt("__count"));

						ad.setAreaId(rs.getString("areaId"));
						ad.setAreaName(rs.getString("areaName"));
						ad.setStaffId(rs.getString("staffId"));
						ad.setLon1(rs.getString("lon1"));
						ad.setLon2(rs.getString("lon2"));
						ad.setLon3(rs.getString("lon3"));
						ad.setLon4(rs.getString("lon4"));
						ad.setLon5(rs.getString("lon5"));
						ad.setLon6(rs.getString("lon6"));
						ad.setLon7(rs.getString("lon7"));
						ad.setLon8(rs.getString("lon8"));
						ad.setLan1(rs.getString("lan1"));
						ad.setLan2(rs.getString("lan2"));
						ad.setLan3(rs.getString("lan3"));
						ad.setLan4(rs.getString("lan4"));
						ad.setLan5(rs.getString("lan5"));
						ad.setLan6(rs.getString("lan6"));
						ad.setLan7(rs.getString("lan7"));
						ad.setLan8(rs.getString("lan8"));
						ad.setLTlan(rs.getString("LTlan"));
						ad.setLTlon(rs.getString("LTlon"));
						ad.setLBlan(rs.getString("LBlan"));
						ad.setLBlon(rs.getString("LBlon"));
						ad.setRTlan(rs.getString("RTlan"));
						ad.setRTlon(rs.getString("RTlon"));
						ad.setRBlan(rs.getString("RBlan"));
						ad.setRBlon(rs.getString("RBlon"));
						ad.setCreateTime(rs.getString("createTime"));
						list.add(ad);
					}
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return -1;
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				return 0;
			}
		});

		if (ret == -1) {
			return false;
		} else {
			page.setDataList(list);
			return true;
		}
	}

	public int getAllArea() {
		String sql = "select * from area ";
		ResultSet rSet = db.executeQuery(sql);

		try {
			while (rSet.next()) {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	/**
	 * 添加区域
	 * @param lon
	 * @param lan
	 * @param staffId
	 * @param areaName
	 * @param areaNum
	 * @return
	 */
	public int addArea(String[] lon, String[] lan, String staffId,
			String areaName, String areaNum) {
		Arrays.sort(lon.clone());
		List<String> s = Arrays.asList(lon);
		TreeSet<String> trees = new TreeSet<String>(s);
		String lonMax = trees.last();
		String lonMin = trees.first();
		
		List<String> a = Arrays.asList(lan);
		TreeSet<String> treea = new TreeSet<String>(a);
		String lanMax = treea.last();
		String lanMin = treea.first();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// 可以方便地修改日期格式
		Date now = new Date();
		String hehe = dateFormat.format(now);

		int flag = -1;

		String sqlStaffid = "SELECT staffId FROM area WHERE staffId='"+ staffId + "'";
		ResultSet rs1 = null;
		try {
			rs1 = db.executeQuery(sqlStaffid);
			if (rs1.next()){
				flag = 2;
			}
		}catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String sqlareaid = "select areaId from area where areaId=?";
		PreparedStatement pSareaid = db.getPreparedStatement(sqlareaid);
		
		String sqlareaName = "select areaName from area where areaName=?";
		PreparedStatement pSareaName = db.getPreparedStatement(sqlareaName);
		
		try {
			pSareaid.setString(1, areaNum);
			ResultSet rs2 = pSareaid.executeQuery();
			
			pSareaName.setString(1, areaName);
			ResultSet rs3 = pSareaName.executeQuery();

			if (rs2.next()) {
				flag = 3;
			}
			if (rs3.next()) {
				flag = 4;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (flag == -1) {
			String sqlinsert = "insert into area(areaId,areaName,staffId,"
					+ "lon1,lon2,lon3,lon4,lon5,lon6,lon7,lon8,"
					+ "lan1,lan2,lan3,lan4,lan5,lan6,lan7,lan8,"
					+ "LTlon,LTlan,LBlon,LBlan,RTlon,RTlan,RBlon,RBlan,createTime)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			List<Object> itemList = new ArrayList<Object>();
			itemList.add(areaNum);
			itemList.add(areaName);
			itemList.add(staffId);
			itemList.add(lon[0]);
			itemList.add(lon[1]);
			itemList.add(lon[2]);
			itemList.add(lon[3]);
			itemList.add(lon[4]);
			itemList.add(lon[5]);
			itemList.add(lon[6]);
			itemList.add(lon[7]);
			itemList.add(lan[0]);
			itemList.add(lan[1]);
			itemList.add(lan[2]);
			itemList.add(lan[3]);
			itemList.add(lan[4]);
			itemList.add(lan[5]);
			itemList.add(lan[6]);
			itemList.add(lan[7]);

			itemList.add(lonMin);
			itemList.add(lanMax);
			itemList.add(lonMin);
			itemList.add(lanMin);
			itemList.add(lonMax);
			itemList.add(lanMax);
			itemList.add(lonMax);
			itemList.add(lanMin);
			itemList.add(hehe);

			flag = db.executeUpdate(sqlinsert, itemList);
		}  //end if
		
		return flag;
	}

}
