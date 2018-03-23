package auto.provider.model;

import java.io.Serializable;
import java.util.Date;

public class SubTask implements Serializable{
    private String subTag;

    private String taskTag;

    private String serial;
    
    private String model;
    
    private String scriptName;

    private String result;

    private String deviceLog;

    private String cpuData;

    private String memoryData;

    private Date startTime;

    private Date endTime;

    public String getSubTag() {
        return subTag;
    }

    public void setSubTag(String subTag) {
        this.subTag = subTag == null ? null : subTag.trim();
    }

    public String getTaskTag() {
        return taskTag;
    }

    public void setTaskTag(String taskTag) {
        this.taskTag = taskTag == null ? null : taskTag.trim();
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial == null ? null : serial.trim();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    public String getDeviceLog() {
        return deviceLog;
    }

    public void setDeviceLog(String deviceLog) {
        this.deviceLog = deviceLog == null ? null : deviceLog.trim();
    }

    public String getCpuData() {
        return cpuData;
    }

    public void setCpuData(String cpuData) {
        this.cpuData = cpuData == null ? null : cpuData.trim();
    }

    public String getMemoryData() {
        return memoryData;
    }

    public void setMemoryData(String memoryData) {
        this.memoryData = memoryData == null ? null : memoryData.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

	/**
	 * @return the scriptName
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * @param scriptName the scriptName to set
	 */
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
}