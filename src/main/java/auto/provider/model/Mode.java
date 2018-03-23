package auto.provider.model;

import java.io.Serializable;

public class Mode implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5425712150357912875L;
	String type;
	
	public Mode(String type){
		this.type=type;
	}
	
	public String getType(){
		return type;
	}
	
}