/**
 * 
 */
package auto.provider.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author BlackStone
 *
 */
public interface IAppiumServerService {
	public String apkSave(byte[] bytes,String userName) throws IllegalStateException, IOException;
	public File getLastApk(String userName);
	public void installApk(String userName,String serial);
	public  void startServer(String serial);
	public  void stopServer(String serial);
	public void gather();
}
