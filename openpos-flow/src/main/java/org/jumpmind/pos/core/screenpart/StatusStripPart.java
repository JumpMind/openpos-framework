package org.jumpmind.pos.core.screenpart;

import java.io.Serializable;
import java.util.Date;

import org.jumpmind.pos.core.model.SystemStatus;

/**
 * @deprecated Use {@link org.jumpmind.pos.core.ui.messagepart.StatusStripPart} instead
 *
 */
@Deprecated
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
