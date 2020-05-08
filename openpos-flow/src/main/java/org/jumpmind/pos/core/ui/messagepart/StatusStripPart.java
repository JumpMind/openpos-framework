package org.jumpmind.pos.core.ui.messagepart;

import lombok.Data;
import org.jumpmind.pos.core.model.SystemStatus;

import java.io.Serializable;
import java.util.Date;

@Data
public class StatusStripPart implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String storeNumber;
    private String registerNumber;
    private SystemStatus systemStatus;
    private Date timestampBegin;
    private String transactionNumber;

}
