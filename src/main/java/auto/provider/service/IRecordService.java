/**
 * 
 */
package auto.provider.service;

import java.util.List;

import auto.provider.model.Record;

/**
 * @author BlackStone
 *
 */
public interface IRecordService {
	
	public Record getCommandsByRecordId(int id);
	
	public void recordSave(Record record);
	
	public List<Record> mGetRecordHistory(String userName);

}
