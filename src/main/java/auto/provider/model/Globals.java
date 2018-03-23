/**
 * 
 */
package auto.provider.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import auto.provider.service.impl.ProjectionServiceImpl;

/**
 * @author BlackStone
 *
 */
public class Globals {
	/** 
	 * @ClassName: Globals 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年10月23日 上午9:29:05 
	 * 
	 */
	private static Logger log=Logger.getLogger(Globals.class);
	private static Hashtable<String,String> usedMap=new Hashtable<String,String>();
	private static Vector<String> portList=new Vector<String>();
	
	static{
		//每个节点上初始化12个端口组
		portList.add("4724;3000;9002;1717;1111");
		portList.add("4725;3001;9003;1718;1112");
		portList.add("4726;3002;9004;1719;1113");
		portList.add("4727;3003;9005;1720;1114");
		portList.add("4728;3004;9006;1721;1115");
		portList.add("4729;3005;9007;1722;1116");
		portList.add("4730;3006;9008;1723;1117");
		portList.add("4731;3007;9009;1724;1118");
		portList.add("4732;3008;9010;1725;1119");
		portList.add("4733;3009;9011;1726;1120");
		portList.add("4734;3010;9012;1727;1121");
		portList.add("4735;3011;9013;1728;1122");
		
	}
	
	public static String getAppiumPortGroup(){
		printUsed();
		
		for(String key:portList){
			
			if(null==usedMap.get(key)){
				
				usedMap.put(key,"1");
				log.info("可用端口组 "+key);
				
				return key;
			}
			
		}
		return getAppiumPortGroup();
		
	}
	
	public static void freeAppiumGroup(String key){
		usedMap.remove(key);
		log.info("释放端口组 "+key);
		
		//projection 为长连接需自己关闭
		ProjectionServiceImpl projectionService=SpringContextHolder.getBean("projectionService");
		
		String[] projectionPortGroup=key.split(";");
		
		for(int i=2;i<5;i++){
			projectionService.closePort(Integer.valueOf(projectionPortGroup[i]));
		}
		
		
		
	}
	
	
	public static void printUsed(){
		
		Set<String> keys=usedMap.keySet();
		String str="";
		for(String key:keys){
			str+=key+" ";
		  
			
		}
		log.info("已用的group "+str);
		
	}
}
