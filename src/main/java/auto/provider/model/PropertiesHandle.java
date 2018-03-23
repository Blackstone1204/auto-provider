package auto.provider.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;


/**
 * @author: iversoncl
 * @project: Neprotect
 * @Date: 2013-5-9
 */
public class PropertiesHandle {
	String classpath=this.getClass().getClassLoader().getResource("/").getPath();
		 
		 /**
			* @Description:读取配置文件信息
			* @param key
			* @return String
			* @author: iversoncl
			* @time:2015�?4�?12�? 下午8:18:28
		*/
		public  String readValue(String key) {
			
	
			
		  Properties props = new Properties();
		        try {
		        	
		        	String realPath=classpath+"config.properties";
		  
		           // System.out.println("propertieshandle :"+realPath);
		         	         
		         InputStream in = new BufferedInputStream (new FileInputStream(new File(realPath)));
		         props.load(in);
		         String value = props.getProperty (key);
		         //System.out.println("key: "+key+"value: "+value);
		 
		            return value;
		        } catch (Exception e) {
		        	
		   
		        	
		        	System.out.println(e.getMessage().toString());
		        
		         return null;
		        }
		 }
		 
		
}
