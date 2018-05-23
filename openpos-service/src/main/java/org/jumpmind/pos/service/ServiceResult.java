package org.jumpmind.pos.service;

import org.apache.commons.lang3.StringUtils;

public class ServiceResult {
    
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_NOT_FOUND = "NOT_FOUND";
    
    private String resultStatus = "";
    private String resultMessage = "";
    private Object extension;
    
    public boolean isSuccess() {
        return StringUtils.equals(resultStatus, RESULT_SUCCESS);
    }
    
    public String getResultStatus() {
        return resultStatus;
    }
    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }
    public String getResultMessage() {
        return resultMessage;
    }
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
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
