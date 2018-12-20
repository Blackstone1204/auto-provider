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
import com.provider.util.PageUtil;
import com.provider.util.RecordUtil;
import com.provider.util.TouchActionUtil;
import com.provider.util.AdbActionUtil;

import auto.provider.model.AdbConnector;
import auto.provider.model.Device;
import auto.provider.model.Mode;
import auto.provider.model.MyPoint;
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
		touchSupportList.add("key_back");
		
	}
	
	//@Autowired
	private RecordUtil  ru;
	
	//@Autowired 
	private PageUtil pu;
	
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
	public String actionExecution(String serial,String userName,String actionName,String args,String sw,boolean isPlayBack,boolean isRecording,Mode mode,String time){
		
		//
		
		String msg="等待执行结果..";
	



		if(isPlayBack) {
		
			String[] commands=args.split(",");

			for(String command:commands){
				
				//log.info("数据库取得的指令 "+command);
				
				if(command.length()==0)continue;
				
				//log.info(String.format("commmand=%s",command));
				
				String[] as=command.split(";");
				int len=as.length;
				actionName=as[0];
				String args0="";
				
				if("drag".equals(actionName)||"input".equals(actionName)||"wait".equals(actionName)) {
					
					for(int k=1;k<len;k++){
						//log.info(String.format("index=%s v=%s",k,as[k]));
						args0+=";"+as[k];
						
					}

					//touch系列
				}else if(actionName.contains("key_")) {
					
				}
				else {
					String[] s=as[1].split("_");
				
					String tzm=s[1];//页面特征码
					String xpath=s[2];
					int l=s.length;
					
					if(l>3) {
						for(int i=3;i<l;i++) {
							
							log.info("xpath连接 "+s[i]);
							xpath+="_"+s[i];
						}
						
						log.info("len>3生成新的xpath=>"+xpath);
						
					}
					
					//回放解析ui前 先pull
			
					MyPoint p=pu.getElementPointByXpath(tzm,xpath);
					
					if("000".equals(p.getExt())) {
						log.error("xpath解析成坐标失败 结束本次回放...");
						return p.getExt2();
					}
					args0+=";"+p.getX()+";"+p.getY();
					
				}
				
				//单条指令回放
				String b=this.actionPerform(serial, actionName, args0,true,false, sw);
				
				//处理wait指令结果回调
				if(b.contains("wait超时"))
					return b;
				
	
			}
			
			log.info("结束本次回放 正常退出...");
			msg="结束本次回放 正常退出...";
			return msg;
			
		}
		else if(isRecording) {
			
			//
			String command=actionName+args;
			log.info("录制中 接受web端指令=>"+command);
			RecordUtil ru=new RecordUtil(pu);

			ru.actionRecord(userName, command,serial,sw);
			this.actionPerform(serial, actionName, args, false,true, sw);
	
		}else {
			
			this.actionPerform(serial, actionName, args, false,false, sw);
			
			
		}
		
		return msg;


	}
	
	private void doScreenshot(String serial,String userName,boolean isPlayBack,boolean isRecording,Mode mode,String time){
		//非录制模式和回放模式时不做截屏处理
		if(!isRecording&&!isPlayBack)return;

		//easy录屏模式不做截屏处理
	
		if(isRecording&&"easy".equals(mode.getType())){
			log.info(String.format("Easy模式 略过截图操作 当前mode=%s",mode.getType()));
			return;
		}
		
		log.info(String.format("准备截图 isRecording=%s isPlayBack=%s mode=%s", isRecording,isPlayBack,mode.getType()));

		//构建pointer坐标目录
		int step=RecordUtil.getStep(time);;

		File dir;
		String destLocation=userDataDir+File.separator+userName+File.separator+"pointer";
		dir=new File(destLocation);
		if(!dir.exists())dir.mkdir();
		
		
		//录制模式为strict 时 创建目录
		if(isRecording&&!isPlayBack&&"strict".equals(mode.getType())){
			destLocation+=File.separator+"record";
			dir=new File(destLocation);
			if(!dir.exists()){
				dir.mkdir();
				log.info(String.format("创建 录制目录record %s",destLocation));
			}
			
		}
		//easy手动截屏触发
		
		if(isRecording&&!isPlayBack&&"easy_by_hand".equals(mode.getType())){
			destLocation+=File.separator+"record";
			dir=new File(destLocation);
			if(!dir.exists()){
				dir.mkdir();
				log.info(String.format("创建 录制目录record %s",destLocation));
			}
			
		}
		
		
		//构造回放截图目录
		
		if(isPlayBack&&!isRecording){
			
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
		log.info("\n\n#################################################\n");
		log.info("#################################################\n");
		log.info(String.format("开始回放脚本设备号=%s 脚本id=%s ",serial,recordId));
		
		pu=new PageUtil(userDataDir,serial);
		
		//pu.rePullUI();

		ru=new RecordUtil(pu);
		return ru.playback(serial,userName,Integer.valueOf(recordId), sw,time);
		
		
		

	}


	/* (non-Javadoc)
	 * @see auto.provider.service.IProjectionService#actionRecord()
	 */
	public void actionRecord(String serial,String userName,String command,String sw) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月4日下午2:19:06
			 */
		

		new RecordUtil(pu).actionRecord(userName, command,serial,sw);

		

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
		new RecordUtil(pu).stop(userName, recordName, tip,method,screenshotLocation);
	}
	
	public List<String> getCurrentRecord(String userName,String serial){
		
		
		pu=new PageUtil(userDataDir,serial);
		List<String> list=new RecordUtil(pu).getCommands(userName);
		if(list!=null)
			log.info("records=>"+list.toString());
		return list;
		
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
		
		this.doScreenshot(serial, userName, isPlayBack, isRecording, mode, time);
	}
	
	
	
	public String actionPerform(String serial,String actionName,String args,boolean isPlayBack,boolean isRecording,String sw) {
		
		if(isPlayBack) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		//录制状态碰到wait指令  这时直接返回
		
		if("wait".equals(actionName)&&isRecording) {
			
			return "";
		}
		
		//回放状态 碰到wait指令 需执行
		if("wait".equals(actionName)&&isPlayBack) {
			
			//log.info("传参 args="+args);
			String express=args.split(";")[1];
			
			log.info("wait组件 express="+express);
			
			boolean g=pu.waitElement(express, serial, userDataDir);
			int index=express.indexOf("//node");
			String xpath=express.substring(index);
			
			return g?"":"wait超时 xpath="+xpath;
		}
		
		boolean isTouchSupport=touchSupportList.toString().contains(actionName);
		
		if(isTouchSupport) {
			TouchActionUtil.perform(serial, actionName, args,sw,isPlayBack);
			
			
		}
			
		else 
			AdbActionUtil.perform(serial, actionName, args,sw,isPlayBack);
		
		//进行校准
		
	    PageUtil.proof(serial,500);
	    
	    //冲啦页面
	    
		pu=new PageUtil(userDataDir,serial);
		pu.rePullUI();
		
		
		return "";
		
	}



	public boolean delUserRecordLast(String userName, String serial) {
		// TODO Auto-generated method stub
		pu=new PageUtil(userDataDir,serial);
		return new RecordUtil(pu).delUserRecordLast(userName);
		

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


