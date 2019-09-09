package org.jumpmind.pos.core.ui.messagepart.builder;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.BaconStripPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;

public class Baconator {
	
	private UIMessage message;
	
	public Baconator(UIMessage message) {
		this.message = message;
	}
	
	public Baconator setHeaderText(String headerText) {
		BaconStripPart baconStrip = getBaconStrip();
		baconStrip.setHeaderText(headerText);
		return this;
	}
	
	public Baconator setHeaderIcon(String icon) {
		BaconStripPart baconStrip = getBaconStrip();
		baconStrip.setHeaderIcon(icon);
		return this;
	}
	
	public Baconator setDeviceId(String deviceId) {
		BaconStripPart baconStrip = getBaconStrip();
		baconStrip.setDeviceId(deviceId);
		return this;
	}
	
	public Baconator setOperatorText(String operatorText) {
		BaconStripPart baconStrip = getBaconStrip();
		baconStrip.setOperatorText(operatorText);
		return this;
	}
	
	public Baconator setBackButton(ActionItem backButton) {
		BaconStripPart baconStrip = getBaconStrip();
		baconStrip.setBackButton(backButton);
		return this;
	}
	
	private BaconStripPart getBaconStrip() {
		BaconStripPart baconStrip = (BaconStripPart) message.get(MessagePartConstants.BaconStrip);
		if (baconStrip == null) {
			baconStrip = new BaconStripPart();
			this.message.addMessagePart(MessagePartConstants.BaconStrip, baconStrip);
		}
		return baconStrip;
	}
	
}