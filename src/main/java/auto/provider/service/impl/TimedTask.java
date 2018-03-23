package auto.provider.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import auto.provider.model.SpringContextHolder;
import auto.provider.model.Task;
import auto.provider.service.IDeviceService;
import auto.provider.service.ITaskService;
import auto.provider.service.ITimedTask;


@Component  //import org.springframework.stereotype.Component;  
public class TimedTask  implements ITimedTask { 
	
	private static Logger log=Logger.getLogger(TimedTask.class);
      @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次  
      public void redisSync(){  
            //System.out.println("进入测试");  
            

			try {
	            
				ApplicationContext ctx=SpringContextHolder.getApplicationContext();
	            IDeviceService deviceService=(IDeviceService) ctx.getBean("deviceService");
				deviceService.redisInfoSync();
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
      }

	/* (non-Javadoc)
	 * @see auto.provider.service.ITimedTask#dbSync()
	 */
   @Scheduled(cron="0/10 * *  * * ? ") 
	public void dbSync() {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年9月1日下午1:58:47
			 */
		try {
			ApplicationContext ctx=SpringContextHolder.getApplicationContext();
	        IDeviceService deviceService=(IDeviceService) ctx.getBean("deviceService");
			deviceService.dbInfoSync();
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}  
    
    //分析测试结果
    @Scheduled(cron="0/2 * *  * * ? ")   //每30秒执行一次  
    public void taskSync(){
 		ITaskService taskService=SpringContextHolder.getBean("taskService");

 		taskService.updateResult();
    }
    

}  