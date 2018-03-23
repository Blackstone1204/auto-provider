package auto.provider.model;

import java.io.Serializable;
import java.util.Date;


public class Task implements Serializable {
    private String taskTag;

    private String taskName;

    private String userName;
    
    private String taskDir;

    private String deviceSerial;
    
    private String model;
    
    private String uploadName;

    private String scriptName;

    private String result;

    private String apkName;
    
    private String apkName2;
    
    private String pkgName;
    
    private String method;

    private Date submitTime;

    public String getTaskTag() {
        return taskTag;
    }

    public void setTaskTag(String taskTag) {
        this.taskTag = taskTag == null ? null : taskTag.trim();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial == null ? null : deviceSerial.trim();
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName == null ? null : scriptName.trim();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName == null ? null : apkName.trim();
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the taskDir
	 */
	public String getTaskDir() {
		return taskDir;
	}

	/**
	 * @param taskDir the taskDir to set
	 */
	public void setTaskDir(String taskDir) {
		this.taskDir = taskDir;
	}

	/**
	 * @return the uploadName
	 */
	public String getUploadName() {
		return uploadName;
	}

	/**
	 * @param uploadName the uploadName to set
	 */
	public void setUploadName(String uploadName) {
		this.uploadName = uploadName;
	}

	/**
	 * @return the apkName2
	 */
	public String getApkName2() {
		return apkName2;
	}

	/**
	 * @param apkName2 the apkName2 to set
	 */
	public void setApkName2(String apkName2) {
		this.apkName2 = apkName2;
	}

	/**
	 * @return the pkgName
	 */
	public String getPkgName() {
		return pkgName;
	}

	/**
	 * @param pkgName the pkgName to set
	 */
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
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