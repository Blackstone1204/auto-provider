package auto.provider.model;

import java.io.Serializable;

public class Record implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3961981469866147797L;

	private Integer recordId;

    private String recordName;

    private String tip;

    private String commandStr;

    private String userName;
    
    private String serial;
    
    private String screenshotLocation;
    
    private Integer method;

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName == null ? null : recordName.trim();
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip == null ? null : tip.trim();
    }

    public String getCommandStr() {
        return commandStr;
    }

    public void setCommandStr(String commandStr) {
        this.commandStr = commandStr == null ? null : commandStr.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

	/**
	 * @return the method
	 */
	public Integer getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(Integer method) {
		this.method = method;
	}



	/**
	 * @return the screenshotLocation
	 */
	public String getScreenshotLocation() {
		return screenshotLocation;
	}

	/**
	 * @param screenshotLocation the screenshotLocation to set
	 */
	public void setScreenshotLocation(String screenshotLocation) {
		this.screenshotLocation = screenshotLocation;
	}

	/**
	 * @return the serial
	 */
	public String getSerial() {
		return serial;
	}

	/**
	 * @param serial the serial to set
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}


}