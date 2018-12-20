/**
 * 
 */
package auto.provider.service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import auto.provider.model.Mode;

/**
 * @author BlackStone
 *
 */
public interface IProjectionService {
	public String projectionInit(String serial);
	public String mGetWebUrl(String serial);
	public String actionExecution(String serial,String userName,String actionName,String args,String sw,boolean isPlayBack,boolean isRecording,Mode mode,String time);
	public void actionRecord(String serial,String userName,String command,String sw);
	public void stopRecord(String userName,String serial,String recordName,String tip,int method,String time);
	public List<String> getCurrentRecord(String userName,String serial);
	public boolean delUserRecordLast(String userName,String serial);
	public Map<String,Object> playback(String serial,String userName,String recordId,String sw,String time);
	
	//strict模式手动截屏
	public void screenshotByHand(String serial,String userName,boolean isPlayBack,boolean isRecording,Mode mode,String time);

}
