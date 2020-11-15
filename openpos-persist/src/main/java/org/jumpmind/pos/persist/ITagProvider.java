package org.jumpmind.pos.persist;


public interface ITagProvider {

    public String getTagValue(String deviceId, String appId, String tagName, String businessUnitId);

}
