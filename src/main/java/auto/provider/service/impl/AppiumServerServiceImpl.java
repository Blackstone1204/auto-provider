/**
 * 
 */
package auto.provider.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import auto.provider.model.AdbConnector;
import auto.provider.model.Device;
import auto.provider.model.SpringContextHolder;
import auto.provider.service.IAppiumServerService;
import auto.provider.service.IDeviceService;

/**
 * @author BlackStone
 *
 */
public class AppiumServerServiceImpl implements IAppiumServerService {
	/** 
	 * @ClassName: AppiumUtil 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年8月25日 下午2:47:53 
	 * 
	 */
	
	private static final Logger log=Logger.getLogger(AppiumServerServiceImpl.class);
	
	private boolean state=false;
	
    @Value("${appium_dir}") String appiumDir;
	
	@Value("${sdk_dir}") String sdkDir;

	@Value("${user_data_dir}")private String userDataDir;
	@Value("${win_start_command}")private String winStartCommand;
	
	@Value("${kill_command}")String killCommand;
	@Value("${query_command}") String queryCommand;
	@Value("${position_num}") String positionNum;
	
  
	//static param
	private String taskTag;
	private String subTaskTag;
	private String userName;
	private String scriptName;
	private String serial;
	private String apkName;
	private String method;
	
	public void runTest(String taskTag,String subTaskTag, String userName,String scriptName,String serial,String apkName,String method){
		this.taskTag=taskTag;
		this.subTaskTag=subTaskTag;
		this.userName=userName;
		this.scriptName=scriptName;
		this.serial=serial;
		this.apkName=apkName;
		this.method=method;
		
		//
		printParam();
		

	}
	
	 

	//文件上传
	
	public String apkSave(byte[] bytes,String userName) throws IllegalStateException, IOException {
	    
		log.info("==[开始保存收到的apk]==");

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date now=new Date();
		String fileName=sdf.format(now)+".apk";
//
		File f=new File(userDataDir+File.separator+userName);
		if(!f.exists())f.mkdir();

		try {
			FileOutputStream out = new FileOutputStream(userDataDir+File.separator+userName+File.separator+fileName);
			int n = -1;
//			byte[] b = new byte[10240];
//			log.debug("==inpustream "+in.toString());
//			while((n=in.read(b)) != -1){
//				//System.out.println(new String(b,0,n,"utf-8"));
//				out.write(b, 0, n);
//				
//			}
			System.out.println("--apk字节流"+new String(bytes));
			out.write(bytes);
			out.close();
			out.flush();
			//in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("==[成功保存收到的apk]==");
		this.setState(true);
		return "upload complete !";
	}

	
	
	/* (non-Javadoc)
	 * @see auto.provider.service.IDeviceService#installApk(java.lang.String)
	 */
	public void installApk(String userName,String serial) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年9月15日下午6:53:01
			 */
		log.info("--开始安装apk--");

		String apkPath=this.getLastApkPath(userName);
		String cmd=String.format("adb -s %s install  -r %s",serial,apkPath);
		log.info(String.format("--安装命令 %s", cmd));
		try {
			String back=AdbConnector.run(cmd);
			log.info("--安装结果 "+back);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("--安装apk结束--");
	}
	private String getLastApkPath(String userName){
		File userNameDic=new File(userDataDir+File.separator+userName);
		String lastName="";
		for(File apk:userNameDic.listFiles()){
			String name=apk.getName();
			if(name.compareTo(lastName)>0)lastName=name;
			
		}
		
		return userDataDir+File.separator+userName+File.separator+lastName;
		
		
		
	}
	
	//启动appium server
	public  void startServer(String serial){
		
		ApplicationContext ctx=SpringContextHolder.getApplicationContext();
		IDeviceService deviceService=(IDeviceService) ctx.getBean("deviceService");
		Device device=deviceService.findDeviceBySerial(serial);
		
		String node=winStartCommand+" "+appiumDir+File.separator+"node.exe";
		
		String mainjs=appiumDir +File.separator+"node_modules"+File.separator+"appium"+File.separator+"lib"+File.separator+"server"+File.separator+"main.js";
	
		String host="127.0.0.1";
		
		int port=device.getPort();
		int bp=device.getBpPort();
		String userial=serial;
		
		String command=String.format("%s %s --address %s --port %s -bp %s -U %s --platform-name Android --platform-version 23 --automation-name Appium", node,mainjs,host,port,bp,userial);
		
		log.info("==启动appium server==");
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
	
	
	}
	
	
	
	//脚本执行完成后 停止server
	
	public  void stopServer(String serial){
		
		log.info("==停止appium server==");
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
		
		log.info("==appium server已停止==");
		
	}
	
	public void gather(){
		
	}
	
	private void printParam(){
		log.info("--运行参数--");
		log.info(String.format("--killCommand %s--", killCommand));
		log.info(String.format("--queryComman %s--", queryCommand));
		log.info(String.format("--positionNum %s--", positionNum));
		log.info(String.format("--appium %s",appiumDir));
		log.info(String.format("--sdk %s",sdkDir));
		log.info(String.format("--userDir %s",userDataDir));
		
	    log.info(String.format("-Basic参数-"));
	    log.info("--taskTag "+taskTag);
	    log.info("--subTakTag "+subTaskTag);
	    log.info("--userName "+userName);
	    log.info("--scriptName "+scriptName.toString());
	    log.info("--serial "+serial.toString());
	    log.info("--apkName "+apkName);
	    log.info("--method "+method);
	}



	/**
	 * @return the state
	 */
	public boolean isState() {
		return state;
	}



	/**
	 * @param state the state to set
	 */
	public void setState(boolean state) {
		this.state = state;
	}



	/* (non-Javadoc)
	 * @see auto.provider.service.IAppiumServerService#getLastApk(java.lang.String)
	 */
	public File getLastApk(String userName) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年9月16日下午12:51:25
			 */
		
		File userNameDir=new File(userDataDir+File.separator+userName);
		log.info("-userNameDir="+userNameDir.getAbsolutePath());
		String userLastestApkName="";
		for(File apk:userNameDir.listFiles()){
			if(apk.getName().compareTo(userLastestApkName)>0)
				userLastestApkName=apk.getName();
			
		}
		
		return new File(userDataDir+File.separator+userName+File.separator+userLastestApkName);
		

	}






}
