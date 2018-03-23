package auto.provider.dao;

import java.util.List;

import auto.provider.model.Task;

public interface TaskMapper {

    
    List<Task> selectByUserName(String userName);
    
    Task selectByPrimaryKey(String taskTag);
    
    int insert(Task record);
    
    int updateResult(Task task);
    
    List<Task> selectRun();
}