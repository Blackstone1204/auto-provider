/**
 * 
 */
package com.provider.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import auto.provider.dao.RecordMapper;
import auto.provider.model.Device;
import auto.provider.model.Mode;
import auto.provider.model.Record;
import auto.provider.model.SpringContextHolder;
import auto.provider.service.IDeviceService;
import auto.provider.service.IProjectionService;
import auto.provider.service.IRecordService;

/**
 * @author BlackStone
 *
 */
public class RecordUtil {
	/** 
	 * @ClassName: RecordUtil 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年11月30日 上午11:49:36 
	 * 
	 */
	private static Logger log=Logger.getLogger(RecordUtil.class);
	//private static Map<String,String> statusMap=new HashMap<String, String>();
	private static Map<String,List<String>> recordMap=new HashMap<String, List<String>>();
	private static Map<String,Integer> stepMap=new HashMap<String, Integer>();
	
	
	
	//录制控制
	public void stop(String userName,String recordName,String tip,int method,String screenshotLocation){
		//statusMap.put(userName,"0");
		
		//保存脚本
		IRecordService recordService=SpringContextHolder.getBean("recordService");
		Record r=new Record();
		String str="";
		List<String> commands=this.getCommands(userName);
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
	public void actionRecord(String userName,String command){
		
		List<String> current= recordMap.get(userName)==null?new ArrayList<String>():recordMap.get(userName);
		current.add(command);
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
	
	
	//脚本回放
	public Map<String,Object> playback(String serial,String userName,int recordId,String sw,String time,String userDir){
	
		//执行.record脚本

		IDeviceService ds=SpringContextHolder.getBean("deviceService");
		IRecordService recordService=SpringContextHolder.getBean("recordService");
		IProjectionService projectionService=SpringContextHolder.getBean("projectionService");
		String commandStr=recordService.getCommandsByRecordId(recordId).getCommandStr();
		
		String[] commands=commandStr.split(",");

		for(String command:commands){
			
			if(command.length()==0)continue;
			
			//log.info(String.format("commmand=%s",command));
			
			String[] as=command.split(";");
			int len=as.length;
			String actionName=as[0];
			String args="";
			
			for(int k=1;k<len;k++){
				//log.info(String.format("index=%s v=%s",k,as[k]));
				args+=";"+as[k];
				
			}
			
			log.info(String.format("开始回放脚本[serial=%s actionName=%s args=%s sw=%s]",serial, actionName, args, sw));
			
			projectionService.actionExecution(serial,userName,actionName, args, sw,true,false,new Mode("no easy and strict"),time);
			
			
		}
		
		
	//分析结果
		Map<String,Object> resultMap=new HashMap<String,Object>();
		Record record=recordService.getCommandsByRecordId(recordId);
		String fromLoc=record.getScreenshotLocation();
		String toLoc=userDir+File.separator+userName+File.separator+"pointer"+File.separator+"playback"+File.separator+serial+File.separator+time;
		
		boolean flag=this.getPlayBackResult(fromLoc, toLoc);
		log.info(String.format("回放结果分析[结果=%s 比较 %s %s]",flag,fromLoc,toLoc));
		
		Device device=ds.findDeviceBySerial(serial);
		
		String model=device==null?"无法获取":device.getModel();
		
		
		resultMap.put("result",flag);
		resultMap.put("model", model);
		
		return resultMap;
	
		

		
		
		
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
	
	
}
