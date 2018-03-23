/**
 * 
 */
package auto.provider.service;

import java.io.IOException;
import java.util.List;

import org.springframework.context.ApplicationContext;

import auto.provider.model.Device;

/**
 * @author BlackStone
 *
 */
public interface IDeviceService {
	public Device findDeviceBySerial(String serial);
	
	public void redisInfoSync() throws Exception;
	
	public void dbInfoSync();
	
	public void memInfoSync();
	
	public List<Device> mGetOnlineDevice();
	
	public void mGetHistoryDevice();
	
	public void mSaveScreen(String serial,String destLoaction);
	
	public byte[] mGetScreenShot(String serial);

}
