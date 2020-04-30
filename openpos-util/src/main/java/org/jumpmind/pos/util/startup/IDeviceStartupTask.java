package org.jumpmind.pos.util.startup;

public interface IDeviceStartupTask {

    void onDeviceStartup(String deviceId, String appId);

    void doTask(String deviceId, String appId) throws Exception;

    boolean hasException();

    Exception getTaskException();

}
