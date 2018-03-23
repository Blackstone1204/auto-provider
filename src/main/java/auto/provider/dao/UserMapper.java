package auto.provider.dao;

import org.apache.ibatis.annotations.Param;

import auto.provider.model.User;


public interface UserMapper {
    int deleteByPrimaryKey(String userName);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String userName);
    
    User selectByUserNamePassword(@Param("userName") String userName,@Param("password") String password);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}