/**
 * 
 */
package auto.provider.service.impl;

import java.io.Serializable;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auto.provider.dao.UserMapper;
import auto.provider.model.User;
import auto.provider.service.IUserService;

/**
 * @author BlackStone
 *
 */

public class UserServiceImpl implements IUserService,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8892501113190456672L;

	Logger log=Logger.getLogger(this.getClass().getName());
	/* (non-Javadoc)
	 * @see com.auto.service.IUserService#findUserById(int)
	 */ 
	
	@Autowired
	private UserMapper userMapper;

	/* (non-Javadoc)
	 * @see com.auto.service.IUserService#findUserByUserName(int)
	 */
	public User findUserByUserName(String userName) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年8月17日下午1:26:01
			 */
		User user=userMapper.selectByPrimaryKey(userName);
		
		//log.info("user:"+user.getUserName()+"--"+user.getPassword());
		return user;
	}
	
	public User findUserByUserNamePassword(String userName,String password){
	
		User user=userMapper.selectByUserNamePassword(userName, password);
		return user;
	}
	


}
