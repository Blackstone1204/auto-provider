/**
 * 
 */
package auto.provider.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import auto.provider.dao.RecordMapper;
import auto.provider.model.Record;
import auto.provider.service.IRecordService;

/**
 * @author BlackStone
 *
 */


public class RecordServiceImpl implements IRecordService {
	
	@Autowired 
	private RecordMapper recordMapper;

	/* (non-Javadoc)
	 * @see auto.provider.service.IRecordService#getCommandsById(int)
	 */
	public Record getCommandsByRecordId(int recordId) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月1日下午5:32:41
			 */
		return recordMapper.selectByPrimaryKey(recordId);
	}

	/* (non-Javadoc)
	 * @see auto.provider.service.IRecordService#recordSave(auto.provider.model.Record)
	 */
	public void recordSave(Record record) {
			/**
			 * @Description:TODO
			 * @param 
			 * @author: BlackStone
			 * @time:2017年12月1日下午5:32:41
			 */
		recordMapper.insert(record);
	}
	
	public List<Record> mGetRecordHistory(String userName){
		return recordMapper.selectRecord(userName);
		
	}

}
