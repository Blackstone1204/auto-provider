/**
 * 
 */
package auto.provider.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import auto.provider.model.AdbConnector;
import auto.provider.model.Device;
import auto.provider.model.SpringContextHolder;
import auto.provider.service.IAppiumAsynService;
import auto.provider.service.IDeviceService;

/**
 * @author BlackStone
 *
 */
public class AppiumAsynServiceImpl implements IAppiumAsynService{

	/* (non-Javadoc)
	 * @see auto.provider.service.IAppiumAsynService#startServer(java.lang.String)
	 */
	
	
	private static final Logger log=Logger.getLogger( AppiumAsynServiceImpl.class);
	@Value("${user_data_dir}")private String userDataDir;
	@Value("${appium_dir}") String appiumDir;
	@Value("${win_start_command}")private String winStartCommand;
	
	@Value("${kill_command}")String killCommand;
	@Value("${query_command}") String queryCommand;
	@Value("${position_num}") String positionNum;
	
	
	public String apkSave(byte[] bytes, String userName)throws IllegalStateException, IOException {
		log.info("==[开始保存收到的apk]==");
	    log.info("recv byte size="+bytes.length);
	    log.info("recv content "+new String(bytes));

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date now=new Date();
		String fileName=sdf.format(now)+".apk";
//
		File f=new File(userDataDir+File.separator+userName);
		if(!f.exists())f.mkdir();

		try {
			FileOutputStream out = new FileOutputStream(userDataDir+File.separator+userName+File.separator+"appium"+File.separator+fileName);

			log.info("--apk字节流"+new String(bytes));
			out.write(bytes,0,bytes.length);
			
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("==[成功保存收到的apk]==");

		return "upload complete !";
	}	
	
	
	public void startServer(String serial) {
		ApplicationContext ctx=SpringContextHolder.getApplicationContext();
		IDeviceService deviceService=(IDeviceService) ctx.getBean("deviceService");
		Device device=deviceService.findDeviceBySerial(serial);
		if(null==device){
			log.info(String.format("--启动appium server失败 serial=%s设备没连接或库数据没同步", serial));
			return;
		}
		String node=winStartCommand+" "+appiumDir+File.separator+"node.exe";
		
		String mainjs=appiumDir +File.separator+"node_modules"+File.separator+"appium"+File.separator+"lib"+File.separator+"server"+File.separator+"main.js";
	
		String host=getIp(device.getNode());

		int port=device.getPort();
		int bp=device.getBpPort();
		String userial=serial;
		
		String command=String.format("%s %s --address %s --port %s -bp %s -U %s --platform-name Android --platform-version 23 --automation-name Appium", node,mainjs,host,port,bp,userial);
		
		log.info("==[启动appium server]==");
		log.info(command);
		try {
			AdbConnector.run(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("==[appium server成功启动]==");
	

	}

	/* (non-Javadoc)
	 * @see auto.provider.service.IAppiumAsynService#stopServer(java.lang.String)
	 */
	public void stopServer(String serial) {
		
		log.info("==[开始停止appium server]==");
	    ApplicationContext ctx=SpringContextHolder.getApplicationContext();
	    IDeviceService deviceService=(IDeviceService)ctx.getBean("deviceService");
		Device device=deviceService.findDeviceBySerial(serial);
		int port =device.getPort();
		
		String command=String.format(queryCommand,port);
		//log.info(command);
		try {
			String result=AdbConnector.run(command);
			//log.info("result "+result);
			String[] lines=result.split("\n");
			for(String line:lines){
				if(line.contains(String.valueOf(port))){
					//log.info(line);
					int processNum=Integer.valueOf(line.split("\\s+")[Integer.parseInt(positionNum)]);
					
					log.info(String.format("--查找server 进程号为  %s 尝试停止-- ", processNum));
					
					command=String.format(killCommand, processNum);
					AdbConnector.run(command);
					
					
					
				}
			}
			
			
	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("==[appium server已停止]==");

	}
	
	
	private static String getIp(String node){
		  String ip="";
		  Pattern p=Pattern.compile("dubbo://(.*?):");
		  Matcher m=p.matcher(node);
		  while(m.find()){
			  ip=m.group(1);
		  }
		  return ip;
		
	}

}
