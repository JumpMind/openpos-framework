package org.jumpmind.pos.service;

public interface IConfigApplicator {

    public void applyAdditionalConfiguration(String deviceId, String startsWith, Object applyTo);
    
}
