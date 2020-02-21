package org.jumpmind.pos.persist;

import org.springframework.core.env.Environment;


public interface ITagProvider {

    public String getTagValue(String deviceId, String businessUnitId, String tagName);

}
