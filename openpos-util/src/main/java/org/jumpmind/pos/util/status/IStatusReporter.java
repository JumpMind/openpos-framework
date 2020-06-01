package org.jumpmind.pos.util.status;

public interface IStatusReporter {
    StatusReport getStatus(IStatusManager statusManager);
}
