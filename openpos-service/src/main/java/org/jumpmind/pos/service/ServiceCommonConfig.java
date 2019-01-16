package org.jumpmind.pos.service;

public class ServiceCommonConfig implements Cloneable {

    protected int httpTimeout;
    protected String url;

    public int getHttpTimeout() {
        return httpTimeout;
    }

    public void setHttpTimeout(int httpTimeout) {
        this.httpTimeout = httpTimeout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
