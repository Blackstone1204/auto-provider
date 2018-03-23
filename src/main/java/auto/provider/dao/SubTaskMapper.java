package auto.provider.dao;

import java.util.List;

import auto.provider.model.SubTask;


public interface SubTaskMapper {
	

    int deleteBySubTag(String subTag);
    int insert(SubTask record);
    List<SubTask> selectByTaskTag(String taskTag);
    int updateResultBySubTag(SubTask subTask);
}