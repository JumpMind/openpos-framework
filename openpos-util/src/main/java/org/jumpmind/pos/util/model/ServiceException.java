package org.jumpmind.pos.util.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ServiceException extends RuntimeException {

    java.util.List<ServiceVisit> serviceVisits = new ArrayList<>();

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
