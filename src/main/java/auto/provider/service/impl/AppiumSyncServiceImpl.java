/**
 * 
 */
package auto.provider.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import auto.provider.model.AdbConnector;
import auto.provider.service.IAppiumSyncService;

/**
 * @author BlackStone
 *
 */
public class AppiumSyncServiceImpl implements IAppiumSyncService{

	/* (non-Javadoc)
	 * @see auto.provider.service.IAppiumSyncService#apkSave(byte[], java.lang.String)
	 */
	private static final Logger log=Logger.getLogger(AppiumSyncServiceImpl.class);
	
	@Value("${user_data_dir}")private String userDataDir;

	/* (non-Javadoc)
	 * @see auto.provider.service.IAppiumSyncService#getLastApk(java.lang.String)
	 */
	public File getLastApk(String userName) {

		this.dirCheck(userName);
		
		File userNameDir=new File(userDataDir+File.separator+userName+File.separator+"appium");
		if(!userNameDir.exists())log.info("-userNameDir="+userDataDir+File.separator+userName);
		String userLastestApkName="";
		for(File apk:userNameDir.listFiles()){
			if(apk.getName().compareTo(userLastestApkName)>0)
				userLastestApkName=apk.getName();
			
		}
		
		return new File(userDataDir+File.separator+userName+File.separator+"appium"+File.separator+userLastestApkName);
		

	}
	


	/* (non-Javadoc)
	 * @see auto.provider.service.IAppiumSyncService#installApk(java.lang.String, java.lang.String)
	 */
	public void installApk(String userName, String serial) {
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
	
		File userNameDic=new File(userDataDir+File.separator+userName+File.separator+"appium");
		String lastName="";
		for(File apk:userNameDic.listFiles()){
			String name=apk.getName();
			if(name.compareTo(lastName)>0)lastName=name;
			
		}
		
		return userDataDir+File.separator+userName+File.separator+"appium"+File.separator+lastName;
		
		
		
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.IAppiumSyncService#gather()
	 */
	public void gather() {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年9月21日下午5:13:51
			 */
	}
	
	private void dirCheck(String userName){
		File target=new File(userDataDir);
		if(!target.exists())target.mkdir();
		target=new File(userDataDir+File.separator+userName);
		if(!target.exists())target.mkdir();
		target=new File(userDataDir+File.separator+userName+File.separator+"appium");
		if(!target.exists())target.mkdir();
		
		
		
	}

}
