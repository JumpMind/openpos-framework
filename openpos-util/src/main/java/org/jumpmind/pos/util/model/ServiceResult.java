package org.jumpmind.pos.util.model;

public class ServiceResult {

    private Object extension;

    @SuppressWarnings("unchecked")
    public <T> T ext() {
        return (T) extension;
    }

    @SuppressWarnings("unchecked")
    public <T> T ext(Class<T> type) {
        return (T) extension;
    }

    public Object getExtension() {
        return extension;
    }

    public void setExtension(Object extension) {
        this.extension = extension;
    }

}
