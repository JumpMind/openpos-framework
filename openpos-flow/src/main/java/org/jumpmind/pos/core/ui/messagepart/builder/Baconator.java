package org.jumpmind.pos.core.ui.messagepart.builder;

import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.BaconStripPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;

public class Baconator {
	
	private UIMessage message;
	
	public Baconator(UIMessage message) {
		this.message = message;
	}
	
	public Baconator setHeaderText(String headerText) {
		BaconStripPart baconStrip = (BaconStripPart) message.get(MessagePartConstants.BaconStrip);
		if (baconStrip != null) {
			baconStrip.setHeaderText(headerText);
		} else {
			baconStrip = new BaconStripPart();
			baconStrip.setHeaderText(headerText);
			this.message.addMessagePart(MessagePartConstants.BaconStrip, baconStrip);
		}
		return this;
	}
	
	public Baconator setHeaderIcon(String icon) {
		BaconStripPart baconStrip = (BaconStripPart) message.get(MessagePartConstants.BaconStrip);
		if (baconStrip != null) {
			baconStrip.setHeaderIcon(icon);
		} else {
			baconStrip = new BaconStripPart();
			baconStrip.setHeaderIcon(icon);
			this.message.addMessagePart(MessagePartConstants.BaconStrip, baconStrip);
		}
		return this;
	}
	
}