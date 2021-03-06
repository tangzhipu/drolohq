package wph.iframework.startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 配置文件
 */
public final class Config
{
    private final static Log logger               = LogFactory.getLog(Config.class);
    // 内网--开始
/*  public static String     TEMP_PATH            = "http://211.64.154.157:8080/tire/client/";               // 用于存放临时文件的目录
                                                                                                              // UploadClient.java
    public static String     BROKER_URL           = "tcp://211.64.154.170:1883";                             // 推送服务器地址
    public static String     GPS_BROKER_URL       = "tcp://211.64.154.170:1883";                             // GPS推送服务器地址
    public static String     IM_ONLINE_BROKER_URL = "tcp://211.64.154.170:1883";
    
    public static boolean    IOS_HELPER           = false;                                                   // ios证书，指定是正式证书还是测试证书
    public static String     PHOTO_PATH           = "E:\\workspace\\tire\\WebContent\\images\\RescuePhoto\\"; // 救援拍照
*/                                                                                                              // --内网结束
    //**********************************************************
    
    public static String     TEMP_PATH            = "http://58.59.27.210:8433/drolo/client/" ;
    
//   public static String     TEMP_PATH            = "http://www.xiyiapp.com/drolo/client/";               // 用于存放临时文件的目录
    
    //**********************************************************
      //外网--开始 
    // UploadClient.java
//	public static String     BROKER_URL           = "tcp://58.59.27.210:1883";                             // 推送服务器地址
//	public static String     GPS_BROKER_URL       = "tcp://58.59.27.210:1883";                             // GPS推送服务器地址
//	public static String     IM_ONLINE_BROKER_URL = "tcp://58.59.27.210:1883";
//	
	
	public static String     BROKER_URL           = "tcp://115.28.243.144:1883";                             // 推送服务器地址
	public static String     GPS_BROKER_URL       = "tcp://115.28.243.144:1883";                             // GPS推送服务器地址
	public static String     IM_ONLINE_BROKER_URL = "tcp://115.28.243.144:1883";
	
	public static boolean    IOS_HELPER           = true;                                                   // ios证书，指定是正式证书还是测试证书
//	public static String     PHOTO_PATH           = "D:\\workspace\\drolo\\WebContent\\"; // 救援拍照

	
   static
    {
        configure();
    }
    
    private Config()
    {
    }
    
    public static void configure()
    {
        logger.debug("----------------初始化配置文件中--------------------");
        
//        logger.debug("---------------初始化配置文件成功-------------------");
    }
    
}
