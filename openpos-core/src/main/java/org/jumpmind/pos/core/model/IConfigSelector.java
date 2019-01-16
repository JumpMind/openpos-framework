package org.jumpmind.pos.core.model;

public interface IConfigSelector {

    public ClientConfiguration getClientConfig(String brandId, String deviceType);

    public String getTheme(String brandId);

}
