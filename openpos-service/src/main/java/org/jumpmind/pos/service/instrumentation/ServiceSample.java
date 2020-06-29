package org.jumpmind.pos.service.instrumentation;

import java.util.Date;

import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@TableDef(name = "service_sample", description="This table records statistics about service calls within openpos.",
        primaryKey = "sampleId")
public class ServiceSample extends AbstractModel {

    @ColumnDef
    String sampleId; // TODO need sequence generator for deviceId+sequence.
    @ColumnDef
    String installationId;
    @ColumnDef
    String hostname;
    @ColumnDef
    String serviceName;
    @ColumnDef
    String servicePath;    
    @ColumnDef
    String serviceType;
    @ColumnDef
    String serviceResult;
    @ColumnDef
    Date startTime;
    @ColumnDef
    Date endTime;
    @ColumnDef
    long durationMs;
    @ColumnDef
    boolean errorFlag;
    @ColumnDef
    String errorSummary;    
    
    public String getSampleId() {
        return sampleId;
    }
    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }
    public String getInstallationId() {
        return installationId;
    }
    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getServicePath() {
        return servicePath;
    }
    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }
    public String getServiceType() {
        return serviceType;
    }
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    public String getServiceResult() {
        return serviceResult;
    }
    public void setServiceResult(String serviceResult) {
        this.serviceResult = serviceResult;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public long getDurationMs() {
        return durationMs;
    }
    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }
    public boolean isErrorFlag() {
        return errorFlag;
    }
    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }
    public String getErrorSummary() {
        return errorSummary;
    }
    public void setErrorSummary(String errorSummary) {
        this.errorSummary = errorSummary;
    }
    
    
}
