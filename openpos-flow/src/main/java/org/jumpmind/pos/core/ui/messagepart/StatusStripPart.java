package org.jumpmind.pos.core.ui.messagepart;

import org.jumpmind.pos.core.model.SystemStatus;

import java.io.Serializable;
import java.util.Date;

public class StatusStripPart implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String storeNumber;
    private String registerNumber;
    private SystemStatus systemStatus;
    private Date timestampBegin;
    
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	public String getRegisterNumber() {
		return registerNumber;
	}
	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}
	public SystemStatus getSystemStatus() {
		return systemStatus;
	}
	public void setSystemStatus(SystemStatus systemStatus) {
		this.systemStatus = systemStatus;
	}
	public Date getTimestampBegin() {
		return timestampBegin;
	}
	public void setTimestampBegin(Date timestampBegin) {
		this.timestampBegin = timestampBegin;
	}
}
