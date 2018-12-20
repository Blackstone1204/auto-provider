/**
 * 
 */
package auto.provider.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import auto.provider.dao.SubTaskMapper;
import auto.provider.dao.TaskMapper;
import auto.provider.model.SubTask;
import auto.provider.model.Task;
import auto.provider.service.ITaskService;

/**
 * @author BlackStone
 *
 */
public class TaskServiceImpl implements ITaskService{

	/* (non-Javadoc)
	 * @see auto.provider.service.ITaskService#add()
	 */
	private static Logger log=Logger.getLogger(TaskServiceImpl.class);
	@Autowired private TaskMapper taskMapper;
	@Autowired private SubTaskMapper subTaskMapper;
	public void submit(Task task) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年9月25日下午2:31:17
			 */
		taskMapper.insert(task);
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.ITaskService#setResultByTaskTag(java.lang.String)
	 */
	public void updateResult() {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年9月25日下午2:31:17
			 */
		
		log.debug("--批量更新已完成任务状态值-");
		List<Task> taskList=taskMapper.selectRun();
		
		log.debug("--正在运行中的任务个数 ="+taskList.size());
		int waitCount=0;
		int passCount=0;
		int failCount=0;
		int runCount=0;
		
		for(Task task:taskList){
			waitCount=0;
			passCount=0;
			failCount=0;
			runCount=0;
			
			String taskTag=task.getTaskTag();
			int scriptCount=task.getScriptName().split(";").length-1;
			int deviceCount=task.getDeviceSerial().split(";").length-1;
			int max=scriptCount;

			List<SubTask> subTaskList=subTaskMapper.selectByTaskTag(taskTag);
			log.info(String.format("%s任务子任务个数 =%s", taskTag,subTaskList.size()));
			for(SubTask subTask:subTaskList){
				log.info(String.format("%s子任务数据taskTag=%s serial=%s result=%s ",subTask.getSubTag(),subTask.getTaskTag(),subTask.getSerial(),subTask.getResult()));
				
				String result=subTask.getResult();
				if("pass".equals(result))passCount++;
				if("result".equals(result))failCount++;
				if("waiting".equals(result))waitCount++;
				if("running".equals(result))runCount++;
			}
			
			log.info(String.format("--任务%s max %s pass %s fail %s waiting %s running %s", taskTag,max,passCount,failCount,waitCount,runCount));
			
			if(waitCount==0&&runCount==0){
				String result=passCount==max?"pass":"fail";
				task.setResult(result);
				taskMapper.updateResult(task);
				log.info(String.format("--更新任务%s 的结果为 %s", taskTag,result));
				
			}else if(runCount>0){
				
			}

			
		}

	}
	/** 
	 * @ClassName: TaskServiceImpl 
	 * @Description: TODO(这里用一句话描述这个类的作用) 
	 * @date 2017年9月25日 下午2:30:41 
	 * 
	 */

	/* (non-Javadoc)
	 * @see auto.provider.service.ITaskService#findTaskByUser(java.lang.String)
	 */
	public List<Task> findTaskByUser(String userName) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年9月25日下午3:59:14
			 */
		return taskMapper.selectByUserName(userName);
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.ITaskService#findTaskByTaskTag(java.lang.String)
	 */
	public Task findTaskByTaskTag(String taskTag) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年10月25日下午2:57:47
			 */
		log.info("call findTaskByTaskTag method,param="+taskTag);
		return taskMapper.selectByPrimaryKey(taskTag);
	}
	
	
}
