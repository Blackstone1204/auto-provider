/**
 * 
 */
package auto.provider.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import auto.provider.model.SubTask;

/**
 * @author BlackStone
 *
 */
public interface ISubTaskService {
//提交子任务	
public void submit(SubTask subTask);
//根据父任务id删除子任务
public void delSubTask(String taskTag);
//更新测试结果
public void setResult(SubTask subTask);
//根据父任务查询子任务
public List<SubTask> findByTaskTag(String taskTag);

}
