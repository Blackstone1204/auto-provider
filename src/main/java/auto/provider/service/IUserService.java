/**
 * 
 */
package auto.provider.service;

import auto.provider.model.User;

/**
 * @author BlackStone
 *
 */
public interface IUserService {
	public User findUserByUserName(String userName);
	public User findUserByUserNamePassword(String userName,String password);

}
