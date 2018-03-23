/**
 * 
 */
package auto.provider.service;

import java.io.IOException;

/**
 * @author BlackStone
 *
 */
public interface IAppiumAsynService {
	public String apkSave(byte[] bytes,String userName) throws IllegalStateException, IOException;
	public  void startServer(String serial);
	public  void stopServer(String serial);

}
