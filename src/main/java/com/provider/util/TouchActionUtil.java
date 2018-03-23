/**
 * 
 */
package com.provider.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import auto.provider.model.SpringContextHolder;
import auto.provider.service.IDeviceService;

/**
 * @author BlackStone
 *
 */
public class TouchActionUtil {
	/** 
	 * @ClassName: ActionUtil 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年11月21日 下午2:42:18 
	 * 
	 */
	
	
	private static Logger log=Logger.getLogger(TouchActionUtil.class);
	
	private static Map<String,OutputStream> map=new HashMap<String,OutputStream>();
	
	public static Map<String,String[]> cmdMap=new HashMap<String,String[]>();
	
	private static String[] touchChain={"d 0 %s %s 50\n","c\n","u 0\n","c\n"};
	private static String[] longTouchChain={"d 0 %s %s 50\n","c\n","2000","u 0\n","c\n"};
	private static String[] dragChain={"d 0 %s %s 50\n","c\n","m 0 %s %s 50\n","c\n","u 0\n","c\n"};
	
	
	static{
		cmdMap.put("touch",touchChain);
		cmdMap.put("longtouch", longTouchChain);
		cmdMap.put("drag", dragChain);
	}


	public static OutputStream getO(String serial){
		
		OutputStream outputStream=map.get(serial);
		
		if(null==outputStream){
			Socket socket=null;
			try {
				IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
			    //String ip=InetAddress.getLocalHost().getHostAddress();
			    String ip="localhost";
			    int touchPort=deviceService.findDeviceBySerial(serial).getTouchPort();
				socket=new Socket(ip,touchPort);
				
			    log.info(String.format("建立socket连接 ip=%s port=%s", ip,touchPort));
				outputStream =socket.getOutputStream();
				
				map.put(serial, outputStream);
				log.info(String.format("存取device[%s]输出流对象%s",serial,outputStream));

				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				getO(serial);
				e.printStackTrace();
				
			}
			
	
		}
		
		//log.info(String.format("获取设备%s 的输出流为 %s", serial,outputStream));
		return outputStream;
		
	}
	
	public static void perform(String serial,String actionName,String args,String sw){
		
		IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		String res=deviceService.findDeviceBySerial(serial).getResolution();
		
		int dwidth=Integer.parseInt(res.split("x")[0])>Integer.parseInt(res.split("x")[1])?Integer.parseInt(res.split("x")[1]):Integer.parseInt(res.split("x")[0]);
        
		double bs=dwidth/Integer.parseInt(sw);
		OutputStream os=getO(serial);
		String[] as=args.split(";");
		//String[] as2=new String[as.length];
		
		//坐标做整数处理
		for(int i=0;i<as.length;i++){
			
			if(i==0)continue;
			
			double d=Double.valueOf(as[i])*Double.valueOf(bs);
			//log.info(String.format("修该前%s %s",i,as[i]));
			if(String.valueOf(d).contains(".")){
				as[i]=String.valueOf(d).split("\\.")[0];
			}
			
			//log.info(String.format("修改后%s %s",i,as[i]));
		}
		

		log.info(String.format("device[%s]获取输出流对象%s", serial,os));
		
		String[] commands =cmdMap.get(actionName);
		
		log.info(String.format("%s %s", actionName,args));
		
		if(actionName.equals("touch")){
			String cmd1=String.format(commands[0],as[1],as[2]);
			String cmd2=commands[1];
			String cmd3=commands[2];
			String cmd4=commands[3];
			
			//log.info(String.format("touch:[%s,%s,%s,%s]", cmd1,cmd2,cmd3,cmd4));
			try {
				
				log.info(String.format("执行 %s,len=%s", cmd1,cmd1.length()));
				os.write(cmd1.getBytes());
				os.flush();
				log.info(String.format("执行 %s,len=%s", cmd2,cmd2.length()));
				os.write(cmd2.getBytes());
				os.flush();
				log.info(String.format("执行 %s,len=%s", cmd3,cmd3.length()));
				os.write(cmd3.getBytes());
				os.flush();
				log.info(String.format("执行 %s,len=%s", cmd4,cmd4.length()));
				os.write(cmd4.getBytes());
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(actionName.equals("drag")){
			
			String cmd1=String.format(commands[0],as[1],as[2]);
			String cmd2=commands[1];
			String cmd3=String.format(commands[2],as[3],as[4]);
			String cmd4=commands[3];
			String cmd5=commands[4];
			String cmd6=commands[5];
			
			
			try {
				log.info(String.format("执行 %s,len=%s", cmd1,cmd1.length()));
				os.write(cmd1.getBytes());
				os.flush();
				
				log.info(String.format("执行 %s,len=%s", cmd2,cmd2.length()));
				os.write(cmd2.getBytes());
				os.flush();
				
				log.info(String.format("执行 %s,len=%s", cmd3,cmd3.length()));
				os.write(cmd3.getBytes());
				os.flush();
				
				log.info(String.format("执行 %s,len=%s", cmd4,cmd4.length()));
				os.write(cmd4.getBytes());
				os.flush();
				
				log.info(String.format("执行 %s,len=%s", cmd5,cmd5.length()));
				os.write(cmd5.getBytes());
				os.flush();
				
				log.info(String.format("执行 %s,len=%s", cmd6,cmd6.length()));
				os.write(cmd6.getBytes());
				os.flush();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}else if(actionName.equals("longtouch")){
			
			String cmd1=String.format(commands[0],as[1],as[2]);
			String cmd2=commands[1];
			String cmd3=commands[2];
			String cmd4=commands[3];
			String cmd5=commands[4];
			
			//log.info(String.format("touch:[%s,%s,%s,%s]", cmd1,cmd2,cmd3,cmd4));
			try {
				
				log.info(String.format("执行 %s,len=%s", cmd1,cmd1.length()));
				os.write(cmd1.getBytes());
				os.flush();
				log.info(String.format("执行 %s,len=%s", cmd2,cmd2.length()));
				os.write(cmd2.getBytes());
				os.flush();
//				log.info(String.format("执行 %s,len=%s", cmd3,cmd3.length()));
//				os.write(cmd3.getBytes());
//				os.flush();
				
				Thread.sleep(Long.valueOf(cmd3));
				
				log.info(String.format("执行 %s,len=%s", cmd4,cmd4.length()));
				os.write(cmd4.getBytes());
				os.flush();
				log.info(String.format("执行 %s,len=%s", cmd5,cmd5.length()));
				os.write(cmd5.getBytes());
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

}

class Tx implements Runnable{
	private static Logger log=Logger.getLogger(Tx.class);
	private String actionName;
	private String serial;
	private String args;
	
	public Tx(String actionName,String serial,String args){
		this.actionName=actionName;
		this.serial=serial;
		this.args=args;
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年11月29日上午10:06:43
			 */
		

	}
	
}



