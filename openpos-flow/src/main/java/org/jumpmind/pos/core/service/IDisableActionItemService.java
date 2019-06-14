package org.jumpmind.pos.core.service;

public interface IDisableActionItemService {
	
	boolean isActionDisabled( String appId, String deviceId, String action );
}
