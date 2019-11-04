package org.jumpmind.pos.management;

import org.jumpmind.pos.util.model.ProcessInfo;

/**
 * Queries the {@code status} service of a remote OpenPOS process to determine
 * its current state.
 */
public interface DeviceProcessStatusClient {

    public ProcessInfo getRemoteProcessStatus(String deviceId, int port);
}
