package org.jumpmind.pos.util.model;

import java.util.ArrayList;

public class ServiceResult {

    private Object extension;
    private java.util.List<ServiceVisit> serviceVisits = new ArrayList<>();

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

    public void setServiceVisits(java.util.List<ServiceVisit> serviceVisits) {
        this.serviceVisits = serviceVisits;
    }

    public java.util.List<ServiceVisit> getServiceVisits() {
        return serviceVisits;
    }

}
