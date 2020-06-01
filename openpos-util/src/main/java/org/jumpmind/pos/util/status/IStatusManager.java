package org.jumpmind.pos.util.status;

public interface IStatusManager {

    public void reportStatus(StatusReport statusReport);

    public StatusReport getLastKnownStatus(String statusName);

    public StatusReport getRealTimeStatus(String statusName);
}
