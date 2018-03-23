/**
 * 
 */
package auto.provider.service;

import java.util.List;

import auto.provider.model.Task;

/**
 * @author BlackStone
 *
 */
public interface ITaskService {
   //提交测试任务
	public void submit(Task task);
	//更新测试结果
	public void updateResult();
	//查询用户测试任务
	public List<Task> findTaskByUser(String userName);
	//
	public Task findTaskByTaskTag(String taskTag);

}
