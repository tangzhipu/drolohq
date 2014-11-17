package ouc.drolo.action.diaodu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONObject;
import ouc.drolo.dao.OperatorDao;
import ouc.drolo.dao.OrderDao;
import ouc.drolo.domain.Order;
import ouc.drolo.domain.Staff;
import ouc.drolo.domain.orperator;
import wph.iframework.MyTimer;
import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.SqlServer;
import wph.iframework.push.staffPush;

public class Dispatcher{
	
    private static final Dispatcher INSTANCE = new Dispatcher();
    
    public static Dispatcher getInstance(){
        return INSTANCE;
    }
    
    private static final Timer timer = new Timer();
    
    private final operators  operators;
    private final Staffs  staffs;
    private final Orders  Orders;
    public final Queue<Order> requests;//wan8点之后放入队列
    
    private Dispatcher(){
    	 operators = new operators();
    	 staffs= new Staffs();
    	 Orders=new Orders();
    	 requests = new LinkedList<Order>();
    }
    
    /**
     * @param id
     * @return
     */
  
    /**
     * 当物流人员下线时调用
     * 
     * @param rid
     * @return
     */
    public synchronized String  onStaffLogout(String rid){
    	
        if (staffs.contains(rid)){
        	 staffs.remove(rid);
        }
        
        return rid;
    }
    
    /**
     * 开机后重启服务器
     */
    public  synchronized void initOrders(){
   
    	String sqlString="select distinct _order.orderId,_order.longitude,_order.latitude,_order.address ,_staff.tel,_staff.staffId,_order.userId ,_order.status from _order,_staff "
    				+ "	where (_order.status=30 or _order.status=41 or _order.status=40)  and _staff.staffId=_order.staffId and _staff.if_live=1 ";
    	Database db = null;
  		
		try {
			db = new SqlServer();
			db.setAutoCommit(false);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		OperatorDao odaoDao=new OperatorDao(db);
    	ResultSet rSet=db.executeQuery(sqlString);
    	try {
			while(rSet.next()){
				Order order=new Order();
				order.setAddress(rSet.getString("address"));
			
				order.setOrderId(rSet.getString("orderId"));
		
				order.setUserId(rSet.getString("userId"));
				order.setLongitude(rSet.getString("longitude"));
				order.setLatitude(rSet.getString("latitude"));
				order.setStaffId(rSet.getString("staffId"));
				order.setStaffphone(rSet.getString("tel"));
		
				addnewOrder(order);
				if(Integer.parseInt(rSet.getString("status"))==30)
				{order.schedule();
				System.out.println("启动定时器");
				}
				//Orders.add(rSet.getString("orderId"), order);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			db.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close();
		System.out.println("重启后订单放入内存,个数有："+Orders.mapNum());
    }
    
    /**
     * 物流人员的对外接口
     * 
     * @return
     */
    public Staffs getstaffs()
    {
        return staffs;
    }
    
    /**
     * 订单们的对外接口
     * @return
     */
    public Orders getOrders()
    {
        return Orders;
    }
    
    
    /**
     * 订单们的对外接口
     * 
     * @return
     */
    
    public Order getOrder(String id){  
    	Order order=null;
    	if(Orders.contains(id)){
    		order=Orders.getOrder(id);
    	}
    	
    	return order;
    }
    
    
    /**
     * 订单们的对外接口
     * 
     * @return
     */
    public void remove(String id)
    {  
    	Orders.remove(id);
     
    }
    
    /**
     * 当接线员上线时调用
     * 
     * @param oid
     * @return
     */
  public synchronized int onOperatorOnline(String oid)
    {
        if (operators.contains(oid))
        {
            // 提示已经在线
            return 1;
        }
        else
        {
            // 加载接线员信息
        	orperator o = new orperator(oid);
            //o.cleanCount();
            o.free();
            operators.add(o);
            return 2;           // 提示在线成功
        }
           
    }
  
  /**
   * 当接线员离线时调用
   * 
   * @param oid
   * @return
   */
  public synchronized int onOperatorOffline(String oid)
  {
      // 获取接线员
	  orperator o = operators.get(oid);
      
      if (null == o)
      {
          // 提示已经离线
          return 1;
      }
      else
      {
          o.setStatus(0);
         
              operators.remove(oid);
              // 提示离线成功
              return 2;
      }
  }
  /**
   * 开启调度
   */
  public synchronized void startup()
  {
      // 每分钟清理一次没用的定时器任务
      timer.schedule(new TimerTask()
      {
          
          @Override
          public void run()
          {
              timer.purge();
          }
      }, 0, 60 * 1000);
      
      MyTimer t = new MyTimer(7,00, 00);
    	
  	  TimerTask task = new TimerTask() { 
  	       
  	        public void run() {
  	        	while(!requests.isEmpty()){
  	        		Database db = null;
  	        		Order o =requests.poll();
  	        		try {
						db = new SqlServer();
						db.setAutoCommit(false);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					o.schedule();
					addnewOrder(o);
  	  			OrderDao orderDao = new OrderDao(db); 
  	  			
  	  		//int staid=Integer.parseInt(staffid);
  	  			OperatorDao operatorDao=new OperatorDao(db);
  	  			//System.out.println("staid："+staid);
  	  			String staffId=operatorDao.gettopone(o.getLongitude(), o.getLatitude());
  	  			System.out.println("定时器再分配staid"+staffId);
  	  			int staid=Integer.parseInt(staffId);
  	  				String pushId=orderDao.getPushId(staid);
  	  				
  	  			System.out.println("定时器再分配pushid"+pushId);
  	  	     int  flag = orderDao.repaidan(o.getOrderId(),staffId);
  	  	     
  	  		System.out.println("flag"+flag);
  	  			System.out.print("定时器执行了");
  	  				
  	  				String phoneString=operatorDao.getstaffphone(staffId);
  	  				o.setStaffphone(phoneString);
  	  				String addres=orderDao.getAddres(o.getOrderId());
  	  				JSONObject js=new JSONObject();
  	  				js.put("orderId", o.getStaffId());
  	  				js.put("address", addres);
  	  				 staffPush.tixing(pushId, js.toString());
  	  				 
  	  			try {
					db.commit();
					db.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  	  			
  	        	}
  	        }
  	    };
        t.setTask(task);
                       
        t.startup();
  }
  
  public static Timer getTimer()
  {
      return timer;
  }
    /**
     * 当配送员登陆时调用
     * 
     * @param rid
     * @param map
     * @return
     */
    public synchronized void onStaffLogin(String  staffid, Map<String, Object> map)
    {
       Staff staff = null;
       System.out .println("刚登陆的配送员id是："+staffid);
        if (staffs.contains(staffid))
        {
           staff = staffs.remove(staffid);
        }
        else
        {
            staff = new Staff(staffid);
        }
        staff.setPush_id((String) map.get("pushId"));
        staff.setName((String) map.get("name"));
        staff.setTel((String) map.get("phone"));
        staff.setLongitude((String)map.get("longitude"));
        staff.setLatitude((String)map.get("latitude"));
        staff.setStatus(1);
        staffs.add(staffid,staff);
    }
    
    
    /**
     * 当有新的订单时
     * @param rid
     * @param map
     * @return
     */
    public synchronized void addnewOrder(Order Order){
        Orders.add(Order.getOrderId(), Order);
    }
    
    /**
     * 当订单被接受时
     * @param rid
     * @return
     */
    public synchronized void orderAccept(Order Order){
    	Orders.remove(Order.getOrderId());
       
    }
}
