/**
 * 
 */
package com.provider.util;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import auto.provider.dao.RecordMapper;
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
 *
 */

@Component
public class RecordUtil {
	/** 
	 * @ClassName: RecordUtil 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年11月30日 上午11:49:36 
	 * 
	 */

	public static String[] black= {"cn.fingard.rhfappapi.app:id/iv_bank_icon"};
	
	private PageUtil pu;
	private static Logger log=Logger.getLogger(RecordUtil.class);
	//private static Map<String,String> statusMap=new HashMap<String, String>();
	private static Map<String,List<String>> recordMap=new HashMap<String, List<String>>();
	private static Map<String,Integer> stepMap=new HashMap<String, Integer>();
	
	public RecordUtil(PageUtil pu) {
		this.pu=pu;

		
	}
	

	
	
	
	
	//录制控制
	public void stop(String userName,String recordName,String tip,int method,String screenshotLocation){
		//statusMap.put(userName,"0");
		
		//保存脚本
		IRecordService recordService=SpringContextHolder.getBean("recordService");
		Record r=new Record();
		String str="";
		List<String> commands=this.getCommands(userName);
		
		if(null==commands) {
			log.warn("录入的步骤数=0 直接返回不如库...");
			return;
		}
		for(String command:commands){
			str+=command+",";
			
		}
		
		r.setRecordName(recordName);
		r.setTip(tip);
		r.setUserName(userName);
		r.setCommandStr(str);
		
		r.setMethod(method);
		r.setScreenshotLocation(screenshotLocation);
		
		recordService.recordSave(r);
		log.info(String.format("保存录制结果完成[username=%s recordname=%s tip=%s command=%s saveLocation=%s]", userName,recordName,tip,str,screenshotLocation));

		//
		recordReset(userName);
	
		
	}
//	public  void start(String userName){
//		
//		
//		statusMap.put(userName,"1");
//		
//	}
//	public static boolean isRecording(String userName){
//		
//		return statusMap.get(userName)!=null&&statusMap.get(userName).equals("1")?true:false;
//		
//		
//	}
	
	
	//录制命令保存
	public void  actionRecord(String userName,String command,String serial,String sw){

		//
		List<String> current= recordMap.get(userName)==null?new ArrayList<String>():recordMap.get(userName);
		//特殊处理wait指令
		
		String actionName=command.split(";")[0];
		
		List<String> transform=new ArrayList<>();
		transform.add("touch");
		transform.add("longtouch");
		transform.add("wait");
		
		if(transform.contains(actionName)) {
				
			IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
			String res=deviceService.findDeviceBySerial(serial).getResolution();
			
			int dwidth=Integer.parseInt(res.split("x")[0])>Integer.parseInt(res.split("x")[1])?Integer.parseInt(res.split("x")[1]):Integer.parseInt(res.split("x")[0]);
	        
			
			int x=Integer.parseInt(command.split(";")[1]);
			int y=Integer.parseInt(command.split(";")[2]);
			
			double nX=(double)dwidth*(double)x/Double.parseDouble(sw);
			double nY=(double)dwidth*(double)y/Double.parseDouble(sw);
		    
			log.info("解析web端所传坐标 =>"+x+"x"+y+"@"+nX+"x"+nY);
			MyPoint point=new MyPoint(nX,nY);
			
			if(null==pu) {
				log.error("pu对象空  录制略过该步骤=>"+command);
				return;
			}
			
			String xpath=pu.searchElementXPathByPoint(point);
			
	
			String packageName=pu.getPackageName();
			String md5=pu.getPageMd5();
			String express=String.format("%s_%s_%s",packageName,md5,xpath);
			
			log.info(String.format("解析结果..xpath=%s",xpath));
			
			command=actionName+";"+express;
			//xpath唯一指向性验证
			if(pu.isUniqueXpath(xpath)) {
				log.warn(String.format("xpath指向不唯一 xpath=%s",xpath));
			}
			//touch longtouch事件做click校验
			if(actionName.contains("touch")&&!pu.xpathCheckClickable(xpath))
			{
				//command="";
				log.warn("touch事件 xpath clickable规则验证失败 ..");
			}
			
		}
		
		
		if(command.length()>0)current.add(command);
		
		recordMap.put(userName, current);

	}
	
	//获取录制脚本
	public List<String> getCommands(String userName){
		
		return recordMap.get(userName);
		
	}
	
	
	
	//重置
	public void recordReset(String userName){
		recordMap.remove(userName);
		
	}

	
	public boolean delUserRecordLast(String userName) {
		boolean f=false;
		List<String> lst=this.getCommands(userName);
		String cmd=lst.get(lst.size()-1);
		log.info("用户 "+userName+"删除步骤=>"+cmd);
		lst.remove(lst.size()-1);
		recordMap.put(userName, lst);
		return true;
		
	}
	
	//脚本回放
	public Map<String,Object> playback(String serial,String userName,int recordId,String sw,String time){
	
		//执行.record脚本
		
		long start=System.currentTimeMillis();

		IDeviceService ds=SpringContextHolder.getBean("deviceService");
		IRecordService recordService=SpringContextHolder.getBean("recordService");
		IProjectionService projectionService=SpringContextHolder.getBean("projectionService");
		String commandStr=recordService.getCommandsByRecordId(recordId).getCommandStr();
		
		log.info(String.format("开始回放脚本 id=%s 设备号=%s 动作=%s  sw=%s..",recordId,serial, "playback", sw));
		
		String msg=projectionService.actionExecution(serial,userName,"playback",commandStr, sw,true,false,new Mode("not easy and strict"),time);
	
		long end=System.currentTimeMillis();
		double spend=(double)(end-start)/1000.0;
		
		log.info("录制退出 花费 "+spend+" seconds");
		
	//分析结果
		Map<String,Object> resultMap=new HashMap<String,Object>();
//		Record record=recordService.getCommandsByRecordId(recordId);
//		String fromLoc=record.getScreenshotLocation();
//		String toLoc=userDir+File.separator+userName+File.separator+"pointer"+File.separator+"playback"+File.separator+serial+File.separator+time;
//		
//		boolean flag=this.getPlayBackResult(fromLoc, toLoc);
//		log.info(String.format("回放结果分析[结果=%s 比较 %s %s]",flag,fromLoc,toLoc));
		
		Device device=ds.findDeviceBySerial(serial);
		String model=device==null?"无法获取":device.getModel();
		
//		resultMap.put("result",flag);
		resultMap.put("result",msg.contains("正常退出")?true:false);
		resultMap.put("model", model);
		resultMap.put("msg",msg);
		resultMap.put("spend",spend);
	
		return resultMap;
	
		

		
		
		
	}
	
	public void confirmUIOK(int cnt) {
		while(cnt>0) {
			pu.rePullUI();
			
		}
		
	}
	
	public boolean getPlayBackResult(String fromLoc,String toLoc){
		boolean result=true;
		List<String> alist=this.getHashList(fromLoc);
		List<String> blist=this.getHashList(toLoc);
		
		int dt=10;
		ImageUtil p=new ImageUtil();
		

		boolean isLenEqual=alist.size()==blist.size()?true:false;
		
		if(!isLenEqual){
			log.info(String.format("录制回放图片张数不一致  from=%s,to=%s", fromLoc,toLoc));
			//Easy模式情况
			if(blist.containsAll(alist))return true;
			else return false;
		
            
		}
		
		//Strict模式 和Easy 图片张数相等处理
		
		for(int i=0;i<alist.size();i++){
			int d=p.distance(alist.get(i),blist.get(i));
			if(d>dt){
				result=false;
				break;
			}
		
			
		}
		
		return result;
		
		
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.IProjectionService#getHashList(java.lang.String)
	 */
	private List<String> getHashList(String location) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月8日下午1:46:55
			 */
		
		List<String> list=new ArrayList<String>();


		
		File dir=new File(location);
		
		if(dir.exists()&&dir.isDirectory()){
			
		  int size=dir.listFiles().length;
		  if(size<1){
			  log.info(String.format("获取图片感知指纹失败[目录下没图片 loc=%s]", location));
		  }
		  
		  for(int i=1;i<size+1;i++){
			  

		        try {
		        	
				    String where=location+File.separator+String.format("%s.png", i);
			        ImageUtil p = new ImageUtil();
					String hash= p.getHash(new FileInputStream(new File(where)));
					list.add(hash);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  
		  }
			
		}else{
			log.info(String.format("获取图片感知指纹失败[loc=%s]",location));
		}
		
		return list;
		
	}
	
   public static int getStep(String time){
	   
	   int step=-1;
	  if( stepMap.get(time)==null)
		  step=1;
	  else
		  step=stepMap.get(time);
	  
	  stepMap.put(time, step+1);
	  return step;
			   
	   
   }
	
	//
	private static String getTimeStr(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date now=new Date();
		String timeStr=sdf.format(now);
		return timeStr;
	}
	
	public static List<String> getBlack(){
		return Arrays.asList(black);
	}
	
}
