/**
 * 
 */
package auto.provider.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.stream.FileImageInputStream;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.provider.util.Md5Util;
import com.provider.util.PageUtil;
import com.provider.util.UpdateUI;

import auto.provider.dao.DeviceMapper;
import auto.provider.model.AdbConnector;
import auto.provider.model.Device;
import auto.provider.model.Globals;
import auto.provider.model.JedisUtil;
import auto.provider.model.RedisCache;
import auto.provider.model.SpringContextHolder;
import auto.provider.service.IDeviceService;
import auto.provider.service.IProjectionService;


/**
 * @author BlackStone
 *
 */
public class DeviceServiceImpl implements IDeviceService{
	
	private static Logger log=Logger.getLogger(DeviceServiceImpl.class);
	
	@Autowired
	private DeviceMapper deviceMapper;
	@Autowired
	private JedisUtil ju;
	@Autowired 
	private RedisCache rc;
	@Value("${call_address_port}")
	private String callAddressPort;
	@Value("${register_address}")
	private String registerAddress;
	
	@Value("${user_data_dir}")
	private String userDataDir;


	/* (non-Javadoc)
	 * @see com.auto.service.IDeviceService#findDeviceBySerial(java.lang.String)
	 */
	public Device findDeviceBySerial(String serial) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年8月12日下午6:05:58
			 */
		
		Device device=deviceMapper.selectByPrimaryKey(serial);
		
		return device;
		
		
	}

	
	
	private void addDeviceToRedis(String serial) throws IOException, InterruptedException{
		
		RedisCache redisCache=(RedisCache) SpringContextHolder.getBean("redisCache");
		Device device=new Device();
		String model = "";
		String resolution="";
		String brand="";
		String version="";
		String sdk="";
		String imsi="";
		String imei="";
		String abi="";
		int hasGetRoot=-1;
		
		String c=String.format("adb -s %s shell getprop", serial);
		//log.info(c);
		String r=AdbConnector.run(c);
		//log.info("==getprops=="+r);
		String[] sp=r.split("\n");
		
		for(String ss:sp){
			//log.info("=行信息="+ss);
			if(ss.contains("product.model")){
				model=ss.split(":")[1].trim().replace("[","").replace("]","");
				continue;
			
			}
			else if(ss.contains("product.brand")){
				brand=ss.split(":")[1].trim().replace("[","").replace("]","");
			    continue;
			}
			else if(ss.contains("build.version.release")){
				version=ss.split(":")[1].trim().replace("[","").replace("]","");
				continue;
			
			}
			else if(ss.contains("imei")){
				imei=ss.split(":")[1].trim().replace("[","").replace("]","");
				continue;
				
			}
		
		}
		
		String ip=InetAddress.getLocalHost().getHostAddress();
		String node=String.format("dubbo://%s:%s",ip,callAddressPort);

		log.info("==serial="+serial);
		log.info("==model=="+model);
		log.info("==brand=="+brand);
		log.info("==version=="+version);
		log.info("==imei=="+imei);
		log.info("==imsi=="+imsi);
		log.info("==node=="+ip);
		int count=deviceMapper.getDeviceCount();
		//int port=count==0?4724:deviceMapper.getMaxPort();
		//int bpPort=count==0?3000:deviceMapper.getMaxBpPort();
		String key=Globals.getAppiumPortGroup();
		log.info(String.format("key=%s port=%s bpPort=%s",key,key.split("|")[0],key.split("|")[1]));
		int port=Integer.valueOf(key.split(";")[0]);
		int bpPort=Integer.valueOf(key.split(";")[1]);
		int viewPort=Integer.valueOf(key.split(";")[2]);
		int miniPort=Integer.valueOf(key.split(";")[3]);
		int touchPort=Integer.valueOf(key.split(";")[4]);
		log.info(String.format("--port=%s bpPort=%s viewPort=%s miniPort=%s",port,bpPort,viewPort,miniPort));
        device.setSerial(serial);
        device.setMd5(Md5Util.enMd5(serial));
        resolution=getResolution(serial);
        abi=getABI(serial);
        sdk=getSdk(serial);
        
        log.info(String.format("--resolution=%s",resolution));
        device.setResolution(resolution);
		device.setModel(model);
		device.setBrand(brand);
		device.setVersion(version);
		device.setSdk(sdk);
		device.setImei(imei);
		device.setImsi(imsi);
		device.setNode(node);
		device.setPort(port);
		device.setBpPort(bpPort);
		device.setViewPort(viewPort);
		device.setMiniPort(miniPort);
		device.setTouchPort(touchPort);
		device.setAbi(abi);
        String url=String.format("http://%s:%s?n=%s&serial=%s",ip,viewPort,Md5Util.enMd5(serial),serial);
		device.setUrl(url);

		//
		redisCache.putCache(String.format("device_%s", serial),device);

	}
	//db数据同步 同步内容为 所有当前连接的设备 并且node信息正确
	//
	public void dbInfoSync(){
		//log.info("test");
		
		ProjectionServiceImpl projectionService=SpringContextHolder.getBean("projectionService");
		
		Set<String> keys=ju.getKeys("device_*");
		for(String k:keys){
			
			String serial=k.split("_")[1];
			Device device=deviceMapper.selectByPrimaryKey(serial);
			if(null==device){
				deviceMapper.insert((Device)rc.getCache(k));
				log.debug(String.format("=db设备信息同步完成 _insert serial=%s 开始设备projection初始化",serial));
				
				
				//新连入设备投影初始化
				projectionService.deviceProjectionInit(serial);
				
				//
				
				//触发 accessibilityService dump 更新UI层级文件
//				UpdateUI u=new UpdateUI(serial);
//				
//				new Thread(u).start();
				
				
			}
			else{
				deviceMapper.updateByPrimaryKey((Device) rc.getCache(k));	
				log.debug(String.format("=db设备信息同步完成_update serial=%s",serial));
			}
			
			
			//设备截屏操作

			String destLoc=userDataDir+File.separator+".."+File.separator+"basic-data"+File.separator+"jpeg"
					+File.separator+serial+".png";
			
			this.mSaveScreen(serial,destLoc);
			//

			
		}
		
		//不在redis中的 db进行删除
		List<Device> deviceList=deviceMapper.selectAll();
		for(Device d:deviceList){
			if(!keys.contains("device_"+d.getSerial()))deviceMapper.deleteByPrimaryKey(d.getSerial());
			
		}
		

		
		

		
	}
	public void redisInfoSync() throws Exception{
		

		Set<String> oldKeys=ju.getKeys("device_*");
		
	    List<String> newSerials=new ArrayList<String>();
		String cmd="adb devices";
		String res=AdbConnector.run(cmd);
		String[] lines=res.split("\n");
		
		//原来不在redis的增加
		//在的统一更新为当前节点信息
		for(String line:lines){
			if(line.contains("attached"))continue;
			if(line.contains("offline"))continue;
			if(line.contains("_mdb"))continue;
			if(line.contains("device")){				
				
				String serial=(line.split("\\s+"))[0];
				
				
				if(!oldKeys.contains("device_"+serial)){
					log.info(String.format("=redis设备信息同步 新增 key=device_%s",serial));
					this.addDeviceToRedis(serial);
					

				}else{
					
					
					Device d=rc.getCache("device_"+serial);
					log.debug("=redis设备信息同步 更新node");
					//String ip=InetAddress.getLocalHost().getHostAddress();
					String ip=registerAddress.split(":")[0];
					String node=String.format("dubbo://%s:%s",ip,callAddressPort);
					d.setNode(node);
					rc.putCache("device_"+serial,d);
				}
				
				newSerials.add(serial);
		
			}
			
		}
		//不在新信息里的同节点下的设备   从redis中去除
		
		for(String key:oldKeys){
			if(!newSerials.contains(key.split("_")[1])){
				String newIp=InetAddress.getLocalHost().getHostAddress();;
				Device device=rc.getCache(key);
				String oldNode=device.getNode();
				if(oldNode.contains(newIp)){
					
					Device target=rc.getCache(key);
					String portGroup=String.format("%s;%s;%s;%s;%s",target.getPort(),target.getBpPort(),target.getViewPort(),target.getMiniPort(),target.getTouchPort());
					log.info(String.format("--释放 %s",portGroup));
					
					Globals.freeAppiumGroup(portGroup);
					log.info(String.format("=redis设备信息同步 删除key=%s 当前节点 %s 中不包含",key,newIp));
					rc.delCache(key);
				}

			}
		
		}
		

		
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.auto.service.IDeviceService#getOnlineDevice()
	 */
	public List<Device> mGetOnlineDevice() {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年8月19日下午12:31:45
			 */
		List<Device> devices=new ArrayList<Device>();
		Set<String> set=ju.getKeys("device_*");
		for(String s:set){
			String serial=s.split("_")[1];
			Device d=deviceMapper.selectByPrimaryKey(serial);
			devices.add(d);
			
			
		}
		
		
		return devices;
	}
	/* (non-Javadoc)
	 * @see com.auto.service.IDeviceService#getHistoryDevice()
	 */
	public void mGetHistoryDevice() {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年8月19日下午12:31:45
			 */
	}


	//
	public void memInfoSync() {
		


	

	}
	
	
	
	
//获取sdk
	private static String getSdk(String serial){
		String abi="";
		String cmd=String.format("adb -s %s shell getprop ro.build.version.sdk",serial);
		log.info(String.format("-获取%s sdk命令%s",serial,cmd));
		try {
			abi=AdbConnector.run(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return abi;
	}
//获取abi	
	private static String getABI(String serial){
		String abi="";
		String cmd=String.format("adb -s %s shell getprop ro.product.cpu.abi",serial);
		log.info(String.format("-获取%s abi命令%s",serial,cmd));
		try {
			abi=AdbConnector.run(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return abi;
	}
	
	//判断是否黑屏
	private static boolean isPowerOff(String serial){
		String  isPowerOff="true";
		String cmd=String.format("adb -s %s  shell dumpsys window policy",serial);
		log.info(String.format("-获取%s poweroff命令%s",serial,cmd));
		  String str;
		try {
			str = AdbConnector.runWait(cmd);
			//log.info("power off返回内容 "+str);
		      String regex1="mScreenOnEarly=(.*?) mScreenOnFully=";
				
			  Pattern p1=Pattern.compile(regex1);
			  Matcher m1=p1.matcher(str);
			  while(m1.find()){
				  isPowerOff=m1.group(1);
				 
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isPowerOff.equals("true")?true:false;

		
	}

	//计算社设备分辨率
	private static String getResolution(String serial){
		String resolution="";
		String cmd=String.format("adb -s %s shell dumpsys window displays",serial);
		log.info(String.format("-获取%s 分辨率命令%s",serial,cmd));
		  String str;
		try {
			str = AdbConnector.run(cmd);
			log.info("分辨率命令返回内容 "+str);
		      String regex1="cur=(.*?) app=";
				
			  Pattern p1=Pattern.compile(regex1);
			  Matcher m1=p1.matcher(str);
			  while(m1.find()){
				  resolution=m1.group(1);
				 
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		return resolution;
		
	}



	/* (non-Javadoc)
	 * @see auto.provider.service.IDeviceService#mGetScreenShot(java.lang.String)
	 */
	public byte[] mGetScreenShot(String serial) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月5日下午1:37:44
			 */
		
		//log.info("herennnn");
		String loc=userDataDir+File.separator+".."+File.separator+"basic-data"+File.separator+"jpeg"+File.separator+serial+".png";

		byte[] data= image2byte(loc);
		
        log.info(String.format("获取设备屏幕图片[serial=%s size=%s]", serial,data.length));
		return data;
		
	}

	//图片到byte数组
    public  byte[] image2byte(String path){
	    byte[] data = null;
	    FileImageInputStream input = null;
	    File file=new File(path);
	    if(!file.exists())
	    	path=userDataDir+File.separator+".."+File.separator+"basic-data"+File.separator+"jpeg"+File.separator+"mr.png";
;
	    try {
	      input = new FileImageInputStream(new File(path));
	      ByteArrayOutputStream output = new ByteArrayOutputStream();
	      byte[] buf = new byte[1024];
	      int numBytesRead = 0;
	      while ((numBytesRead = input.read(buf)) != -1) {
	      output.write(buf, 0, numBytesRead);
	      }
	      data = output.toByteArray();
	      output.close();
	      input.close();
	    }
	    catch (FileNotFoundException ex1) {
	      ex1.printStackTrace();
	    }
	    catch (IOException ex1) {
	      ex1.printStackTrace();
	    }
	    return data;
	  }



	/* (non-Javadoc)
	 * @see auto.provider.service.IDeviceService#mSaveScreenShot()
	 */
	public void mSaveScreen(String serial,String destLoaction) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月5日下午5:12:43
			 */

		String screenshot_cmd=String.format("adb -s %s shell screencap -p /data/local/tmp/%s.png",serial, serial);
		String pull_cmd=String.format("adb -s %s pull /data/local/tmp/%s.png %s",serial,serial,destLoaction);
		try {
			AdbConnector.run(screenshot_cmd);
			AdbConnector.run(pull_cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	


}
