/**
 * 
 */
package auto.provider.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author BlackStone
 *
 */
public class AdbConnector {
	/** 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @ClassName: AdbConnector 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年8月15日 下午3:01:02 
	 * 
	 */
	static Logger log=Logger.getLogger(AdbConnector.class);
	
	public static String run(String command) throws IOException, InterruptedException{
		//log.info("jer"+command);
		BufferedReader bd=null;
		String rest ="";
		Process p=Runtime.getRuntime().exec(command);
		//p.waitFor();

		bd=new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while((line=bd.readLine())!=null){
			//log.info(line);
			rest+=line+"\n";
		}
		
		if(null!=p)p.destroy();
		if(null!=bd)bd.close();
		
		return rest;
		
		
	}
	
	public static String runWait(String command) throws IOException, InterruptedException{
		BufferedReader bd=null;
		String rest ="";
		Process p=Runtime.getRuntime().exec(command);
		
		p.waitFor();
	

		bd=new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while((line=bd.readLine())!=null){
			//log.info(line);
			rest+=line+"\n";
		}
		
		if(null!=p)p.destroy();
		if(null!=bd)bd.close();
		
		
		return rest;
	}
}
