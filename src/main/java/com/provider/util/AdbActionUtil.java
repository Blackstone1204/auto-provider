/**
 * 
 */
package com.provider.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import auto.provider.model.AdbConnector;
import auto.provider.model.MyPoint;
import auto.provider.model.SpringContextHolder;
import auto.provider.service.IDeviceService;

/**
 * @author BlackStone
 *
 */
public class AdbActionUtil {
	/** 
	 * @ClassName: AdbActionUtil 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年11月23日 上午9:52:16 
	 * 
	 */
	
	private static Logger log=Logger.getLogger(AdbActionUtil.class);
	
	static Map<String,String> map=new HashMap<String,String>();
	
	static Map<String,String> filterMap=new HashMap<String,String>();
	
	static{
		map.put("touch","adb -s %s shell input tap");
		map.put("longtouch","adb -s %s shell input swipe");
		map.put("input","adb -s %s shell input text");
		map.put("drag","adb -s %s shell input swipe");
		map.put("home","adb -s %s shell input keyevent 3");
		map.put("back","adb -s %s shell input keyevent 4");
		map.put("menu","adb -s %s shell input keyevent 82");
		
	}
	
	static{
		filterMap.put("home","");
		filterMap.put("back","");
		filterMap.put("menu","");
	}
	
	public static void perform(String serial,String actionName,String args,String sw,boolean isPlayback){
		try {
			IDeviceService deviceService=SpringContextHolder.getBean("deviceService");
			String res=deviceService.findDeviceBySerial(serial).getResolution();
			
			int dwidth=Integer.parseInt(res.split("x")[0])>Integer.parseInt(res.split("x")[1])?Integer.parseInt(res.split("x")[1]):Integer.parseInt(res.split("x")[0]);
	        
			double bs=(double)dwidth/(double)Integer.parseInt(sw);
			
			
			String[] as=args.split(";");
			int count=args.split(";").length;
			
			//
			
			String f=map.get(actionName);		

			
			f=String.format(f,serial);
			
			Set<String> set=filterMap.keySet();
			//过滤掉键盘事件
			if(!set.contains(actionName)){
				
				//过滤文本输入事件
				if(!"input".equals(actionName)){
					
					for(int k=1;k<count;k++){	
						
						//非回放状态 坐标需要映射放大
						//回放状态 无需放大
						String v=isPlayback?as[k]:String.valueOf(Double.valueOf(as[k])*(double)dwidth/Double.parseDouble(sw));
						f=f+" "+v;
				
					}

					
				}else{
					for(int k=1;k<count;k++){		
						f=f+" "+as[k];
				
					}
				}

				
			}
			

			
		    log.info(String.format("执行动作 %s", f));
			AdbConnector.run(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static int getParamCount(String fstr){
		int count=0;
	    char chars[]=fstr.toCharArray();
		for(char c:chars){
			if("%".toCharArray()[0]==c){
				count++;
			}
			
		}
		
		return count;
		
	}
}
