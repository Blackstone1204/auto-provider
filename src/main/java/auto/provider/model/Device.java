package auto.provider.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import auto.provider.service.IAppiumServerService;
import auto.provider.service.IDeviceService;
import auto.provider.service.ISubTaskService;



/**
 * @author BlackStone
 *
 */
/**
 * @author BlackStone
 *
 */

public class Device implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2520974363685319369L;

	private static final Logger log=Logger.getLogger(Device.class);
	
	
    //AppiumTaskServiceImpl appiumService;
	ISubTaskService subTaskService;
	
    private String serial;
    
    private String md5;
    
    private String resolution;

    private Integer bpPort;
    
    private Integer port;
    
    private Integer viewPort;
    
    private Integer miniPort;
    
    private Integer touchPort;

    private String node;

    private String model;

    private String brand;

    private String version;
 
    private String sdk;

    private String imsi;

    private String imei;
    
    private String abi;
    
    private String url;

    private Integer hasGetRoot;

    private Date insertTime;

 
    


    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial == null ? null : serial.trim();
    }

    public Integer getBpPort() {
        return bpPort;
    }

    public void setBpPort(Integer bpPort) {
        this.bpPort = bpPort;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node == null ? null : node.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand == null ? null : brand.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi == null ? null : imsi.trim();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public Integer getHasGetRoot() {
        return hasGetRoot;
    }

    public void setHasGetRoot(Integer hasGetRoot) {
        this.hasGetRoot = hasGetRoot;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the resolution
	 */
	public String getResolution() {
		return resolution;
	}

	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	/**
	 * @return the viewPort
	 */
	public Integer getViewPort() {
		return viewPort;
	}

	/**
	 * @param viewPort the viewPort to set
	 */
	public void setViewPort(Integer viewPort) {
		this.viewPort = viewPort;
	}

	/**
	 * @return the miniPort
	 */
	public Integer getMiniPort() {
		return miniPort;
	}

	/**
	 * @param miniPort the miniPort to set
	 */
	public void setMiniPort(Integer miniPort) {
		this.miniPort = miniPort;
	}

	/**
	 * @return the abi
	 */
	public String getAbi() {
		return abi;
	}

	/**
	 * @param abi the abi to set
	 */
	public void setAbi(String abi) {
		this.abi = abi;
	}

	/**
	 * @return the sdk
	 */
	public String getSdk() {
		return sdk;
	}

	/**
	 * @param sdk the sdk to set
	 */
	public void setSdk(String sdk) {
		this.sdk = sdk;
	}

	/**
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}

	/**
	 * @param md5 the md5 to set
	 */
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	/**
	 * @return the touchPort
	 */
	public Integer getTouchPort() {
		return touchPort;
	}

	/**
	 * @param touchPort the touchPort to set
	 */
	public void setTouchPort(Integer touchPort) {
		this.touchPort = touchPort;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	
}