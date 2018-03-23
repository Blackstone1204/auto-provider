package auto.provider.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.provider.util.ImageUtil;
import com.provider.util.RecordUtil;
import com.provider.util.TouchActionUtil;
import com.provider.util.AdbActionUtil;

import auto.provider.model.AdbConnector;
import auto.provider.model.Device;
import auto.provider.model.Mode;
import auto.provider.model.Record;
import auto.provider.model.SpringContextHolder;
import auto.provider.service.IDeviceService;
import auto.provider.service.IProjectionService;
import auto.provider.service.IRecordService;

/**
 * @author BlackStone
 * 同步调用
 */
public class ProjectionServiceImpl implements IProjectionService{

	/* (non-Javadoc)
	 * @see auto.provider.service.IProjectionService#projectionInit(java.lang.String)
	 */
	
	
	private static Logger log=Logger.getLogger(ProjectionServiceImpl.class);
	private static List<String> touchSupportList=new ArrayList<String>();
	
	static{
		touchSupportList.add("touch");
		touchSupportList.add("longtouch");
		touchSupportList.add("drag");
	}
	
	//@Autowired
	private RecordUtil  ru;
	
	@Value("${node_dir}") private String nodeDir;
	@Value("${tool_dir}") private String toolDir;
	@Value("${user_data_dir}") private String userDataDir;
	@Value("${kill_command}")String killCommand;
	@Value("${query_command}") String queryCommand;
	@Value("${position_num}") String positionNum;

	
	/**
	 * 初始化完成 返回可以访问的URL
	 */
	public String projectionInit(String serial) {
		
	
		return  mGetWebUrl(serial);
	
			

	}

	
	public void deviceProjectionInit(String serial){
		
		log.info(String.format("开始设备投影初始化操作 [serial=%s]",serial));

		if(!hasFile(serial))pushFile(serial);
		else log.info("已有相关服务文件");
		
		grantPermission(serial);
		
		adbForward(serial);
		
	    startTouchService(serial);
		
		startMiniService(serial);
		
		startDemo(serial);
		
		log.info(String.format("完成设备投影初始化操作 [serial=%s]",serial));
		
	
		
	}
	
	public String mGetWebUrl(String serial){
		IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		String ip=""; 
		try {
			ip=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int port=deviceService.findDeviceBySerial(serial).getViewPort();
		String wsName=deviceService.findDeviceBySerial(serial).getMd5();
		
		String url=String.format("http://%s:%s?n=%s&serial=%s",ip,port,wsName,serial );
		
		log.info("weburl="+url);
		return url;
	}
	
/**
 * 触屏动作模拟
 */
	public void actionExecution(String serial,String userName,String actionName,String args,String sw,boolean isPlayBack,boolean isRecording,Mode mode,String time){
		
		if(isPlayBack){
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//截图
		doScreenshot(serial,userName,isPlayBack,isRecording,mode,mode1->true,time);
		
		boolean isTouchSupport=touchSupportList.toString().contains(actionName);
		
		if(isTouchSupport)
			TouchActionUtil.perform(serial, actionName, args,sw);
		else 
			AdbActionUtil.perform(serial, actionName, args,sw);
		
		

	}
	
	private void doScreenshot(String serial,String userName,boolean isPlayBack,boolean isRecording,Mode mode,Predicate<Mode> p,String time){
		

		//
	
		if(isRecording&&p.test(mode)){
			log.info(String.format("模式不匹配 略过截图操作 当前mode=%s",mode.getType()));
			return;
		}
		
		log.info(String.format("准备截图 isRecording=%s isPlayBack=%s mode=%s", isRecording,isPlayBack,mode.getType()));
		if(!isRecording&&!isPlayBack)return;
		
		int step=RecordUtil.getStep(time);;

		File dir;
		String destLocation=userDataDir+File.separator+userName+File.separator+"pointer";
		dir=new File(destLocation);
		if(!dir.exists())dir.mkdir();
		
		if(isRecording&!isPlayBack){
			destLocation+=File.separator+"record";
			dir=new File(destLocation);
			if(!dir.exists()){
				dir.mkdir();
				log.info(String.format("创建 录制目录record %s",destLocation));
			}
			
		}
		
		if(isPlayBack&!isRecording){
			
			destLocation+=File.separator+"playback";
			dir=new File(destLocation);
			if(!dir.exists()){
				dir.mkdir();
				log.info(String.format("创建回放playback子目录 %s",destLocation));
			}
			
			destLocation+=File.separator+serial;
			dir=new File(destLocation);
			if(!dir.exists()){
				dir.mkdir();
				log.info(String.format("创建回放 serial子目录  %s",destLocation));
			}

		}

		
		destLocation+=File.separator+time;
		dir=new File(destLocation);
		if(!dir.exists()){
			dir.mkdir();
			log.info(String.format("创建回放时间 子目录 %s",destLocation));
		}
		destLocation+=File.separator+step+".png";
		
		
		//点击产生图片 服务端保存    easy模式除外
		IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		deviceService.mSaveScreen(serial, destLocation);
	}
/**
 * 
 * 检查是否拥有so文件
 */
	private boolean hasFile(String serial){
		log.info("检查是否已有相关文件 serial="+serial);
		
        boolean flag=false;
		String location="/data/local/tmp";
		String cmd=String.format("adb -s %s shell ls %s",serial,location);
		String result="";
		
		try {
			result=AdbConnector.run(cmd);
			if(result.contains("minicap.so")&&result.contains("minicap")&&result.contains("minitouch"))flag=true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			if(flag)
				log.info(String.format("设备 %s已有相关文件", serial));
				
			
		}
		
		return false;
		
	}
/**
 * 
 *push合适的so文件
 */
	private void pushFile(String serial){

		IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		String abi=deviceService.findDeviceBySerial(serial).getAbi();
		String sdk=deviceService.findDeviceBySerial(serial).getSdk();
		sdk=sdk.replaceAll("[^0-9]","");
		sdk=Integer.valueOf(sdk)>22?"M":sdk;
		
		String minicapLoaction=toolDir+File.separator+"minicap-file"+File.separator+"bin"+File.separator+abi+File.separator+"minicap";
		String minicapsoLocaton=toolDir+File.separator+"minicap-file"+File.separator+"shared"+File.separator+"android-"+sdk+File.separator+abi+File.separator+"minicap.so";
		String minitouchLocation=toolDir+File.separator+"touch-file"+File.separator+abi+File.separator+"minitouch";
		
		String cmd1=String.format("adb -s %s push %s %s",serial,minicapLoaction,"/data/local/tmp");
		log.info(cmd1);
		String cmd2=String.format("adb -s %s push %s %s",serial,minicapsoLocaton,"/data/local/tmp");
		log.info(cmd2);
		String cmd3=String.format("adb -s %s push %s %s",serial,minitouchLocation,"/data/local/tmp");
		log.info(cmd3);
		

		try {
			AdbConnector.runWait(cmd1);
			AdbConnector.runWait(cmd2);
			AdbConnector.runWait(cmd3);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			log.info(String.format("向 %s /data/local/tmp 推送文件完成 ", serial));
		}
		
	}
	
/**
 * 
 * 端口映射
 */
	private void adbForward(String serial){
		
		wait(1);
		
		IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		int miniPort=deviceService.findDeviceBySerial(serial).getMiniPort();
		String wsName=deviceService.findDeviceBySerial(serial).getMd5();
		
		int touchPort=deviceService.findDeviceBySerial(serial).getTouchPort();
		
		String cmd=String.format("adb -s %s forward tcp:%s localabstract:a_%s",serial,miniPort,wsName);
		log.info(cmd);
		
		String cmd2=String.format("adb -s %s forward tcp:%s localabstract:b_%s",serial,touchPort,wsName);
		log.info(cmd2);
		try {
			AdbConnector.runWait(cmd);
			AdbConnector.runWait(cmd2);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			log.info(String.format("设备 %s端口映射完成 ", serial));
		}
		
	}
	
	
	private void grantPermission(String serial){

		String cmd=String.format("adb -s %s shell chmod 777 /data/local/tmp/minicap ",serial);
		log.info(cmd);
		
		String cmd2=String.format("adb -s %s shell chmod 777 /data/local/tmp/minitouch ",serial);
		log.info(cmd2);
		try {
			AdbConnector.runWait(cmd);
			AdbConnector.runWait(cmd2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			log.info(String.format("设备%s授权完成 ",serial));
		}
		
	}

/**
 * 
 * 启动minicap服务
 */
	private void startMiniService(String serial){
		
		//wait(2);
		
		//Allocating
		log.info(String.format("启动minicap服务 [serial=%s]",serial));
		IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		String resolution=deviceService.findDeviceBySerial(serial).getResolution();
		String wsName=deviceService.findDeviceBySerial(serial).getMd5();
		//log.info("resolution="+resolution);
		String cmd=String.format("cmd.exe /c start adb -s %s shell LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P %s@%s/0 -n a_%s  -S", serial,resolution,resolution,wsName);
		log.info(cmd);
		

			
		Thread t=new Thread(new T(cmd));
		t.setDaemon(true);
		t.start();
		
	
		



	}
	
	private void startTouchService(String serial){
		
		//wait(2);
        //detected on
		log.info("启动touch服务 serial="+serial);
		IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		String wsName=deviceService.findDeviceBySerial(serial).getMd5();
		
		String cmd2=String.format("cmd.exe /c start adb -s %s shell  /data/local/tmp/minitouch -n b_%s", serial,wsName);
		log.info(cmd2);
		
		
		Thread t=new Thread(new T(cmd2));
		t.setDaemon(true);
		t.start();
		
	   
		

	}
	
	private void startDemo(String serial){
		//wait(4);
		
		log.info(String.format("启动demo serial=%s",serial));
		
//		while(true){
//			log.info("等待服务启动 serial="+serial);
//		}
		IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		int viewPort=deviceService.findDeviceBySerial(serial).getViewPort();
		int miniPort=deviceService.findDeviceBySerial(serial).getMiniPort();
		String node=nodeDir+File.separator+"node";
		String appjs=toolDir+File.separator+"minicap-web"+File.separator+"example"+File.separator+"app.js";
		String cmd=String.format("cmd.exe /c start %s %s %s %s",node,appjs,viewPort,miniPort);
		log.info(cmd);
		
		
		Thread t=new Thread(new T(cmd));
		t.setDaemon(true);
		t.start();
		


		
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.IProjectionService#playback(java.lang.String, java.lang.String)
	 */
	public Map<String,Object> playback(String serial,String userName,String recordId,String sw,String time) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月1日下午6:10:33
			 */
		
		log.info(String.format("回放脚本 %s %s ",serial,recordId));

		ru=new RecordUtil();
		return ru.playback(serial,userName,Integer.valueOf(recordId), sw,time,userDataDir);
		
		
		

	}


	/* (non-Javadoc)
	 * @see auto.provider.service.IProjectionService#actionRecord()
	 */
	public void actionRecord(String serial,String userName,String command) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月4日下午2:19:06
			 */
		

		new RecordUtil().actionRecord(userName, command);

		

	}

	/* (non-Javadoc)
	 * @see auto.provider.service.IProjectionService#stopRecord(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void stopRecord(String userName, String serial,String recordName, String tip,int method,String time) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月4日下午2:25:29
			 */
		

		String screenshotLocation=userDataDir+File.separator+userName+File.separator+"pointer"
				+File.separator+"record"+File.separator+time;
		new RecordUtil().stop(userName, recordName, tip,method,screenshotLocation);
	}
	
	private void killProcesses(String serial){

	    IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
		Device device=deviceService.findDeviceBySerial(serial);
		int p1=device.getMiniPort();
		int p2=device.getTouchPort();
		int p3=device.getViewPort();
		closePort(p1);
		closePort(p2);
		closePort(p3);

		
	}
	
	public void closePort(int port){
		String command=String.format(queryCommand,port);
		//log.info(command);
		try {
			String result=AdbConnector.run(command);
			//log.info("result "+result);
			String[] lines=result.split("\n");
			for(String line:lines){
				if(line.contains(String.valueOf(port))&&line.contains("LISTENING")){
					
					log.info(line);
					int processNum=Integer.valueOf(line.split("\\s+")[Integer.parseInt(positionNum)]);
					
					log.info(String.format("--查找 进程号为  %s 尝试停止-- ", processNum));
					
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
		
	}
	
	
	
	private void wait(int m){
		
		try {
			TimeUnit.SECONDS.sleep(m);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}


	/* (non-Javadoc)
	 * @see auto.provider.service.IProjectionService#screenshotByHand(java.lang.String, java.lang.String, boolean, boolean, java.lang.String, java.lang.String)
	 */
	public void screenshotByHand(String serial, String userName,
			boolean isPlayBack, boolean isRecording, Mode mode, String time) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2018年3月20日下午3:40:58
			 */
		
		this.doScreenshot(serial, userName, isPlayBack, isRecording, mode,mode1->false, time);
	}

	

}



class T implements Runnable{

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	private String cmd;
	public T(String cmd){
		this.cmd=cmd;
		
	}
	public void run() {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年11月18日上午11:03:18
			 */
		
		try {
		 // AdbConnector.run(cmd);
		  Runtime.getRuntime().exec(cmd).waitFor();
//			String command=String.format("java -jar E:/projection-start/projection-start.jar  -cmd %s",cmd);
//			
//			System.out.println("jar run "+command);
//			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		
		}
		
	}
	
	
	
	
}


