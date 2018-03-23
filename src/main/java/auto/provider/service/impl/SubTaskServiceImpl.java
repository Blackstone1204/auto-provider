/**
 * 
 */
package auto.provider.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import auto.provider.dao.SubTaskMapper;
import auto.provider.model.SubTask;
import auto.provider.service.ISubTaskService;

/**
 * @author BlackStone
 *
 */
public class SubTaskServiceImpl implements ISubTaskService {

	/* (non-Javadoc)
	 * @see auto.provider.service.ISubTaskService#submit(auto.provider.model.SubTask)
	 */
	@Autowired SubTaskMapper subTaskMapper;
	public void submit(SubTask subTask) {
		/**
		 * @Description:TODO
		 * @param 
		 * @author: BlackStone
		 * @time:2017年10月16日上午11:21:14
		 */
		
		subTaskMapper.insert(subTask);
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.ISubTaskService#delSubTask(java.lang.String)
	 */
	public void delSubTask(String taskTag) {
		/**
		 * @Description:TODO
		 * @param 
		 * @author: BlackStone
		 * @time:2017年10月16日上午11:21:14
		 */
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.ISubTaskService#setResult(java.lang.String)
	 */
	public void setResult(SubTask subTask) {
		/**
		 * @Description:TODO
		 * @param 
		 * @author: BlackStone
		 * @time:2017年10月16日上午11:21:14
		 */
		subTaskMapper.updateResultBySubTag(subTask);
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.ISubTaskService#findByTaskTag(java.lang.String)
	 */
	public List<SubTask> findByTaskTag(String taskTag) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年10月16日下午12:03:52
			 */
		return subTaskMapper.selectByTaskTag(taskTag);
	}

	
}
