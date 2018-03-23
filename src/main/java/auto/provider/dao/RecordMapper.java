package auto.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import auto.provider.model.*;;



public interface RecordMapper {
   

    int insert(Record record);
    //获取最新指定条数历史记录 
    List<Record> selectRecord(String userName);
    //获取指定脚本
    Record selectByPrimaryKey(Integer recordId);



}