package auto.provider.dao;

import java.util.List;

import auto.provider.model.Device;


public interface DeviceMapper {
    int deleteByPrimaryKey(String serial);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(String serial);
    
    List<Device> selectAll();

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);
    
    int getMaxPort();
    
    int getMaxBpPort();
    
    int getDeviceCount();
}