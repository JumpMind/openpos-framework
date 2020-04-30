package org.jumpmind.pos.util.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractDeviceStartupTask implements IDeviceStartupTask {

    final protected Logger logger = LoggerFactory.getLogger(getClass());

    private Exception taskException = null;

    @Override
    public void onDeviceStartup(String deviceId, String appId) {
        logger.info("{} is executing for deviceId={} and appId={}", getClass().getSimpleName(), deviceId, appId);
        try {
            doTask(deviceId, appId);
        } catch (Exception e) {
            this.taskException = e;
            logger.error("Failed to execute " + getClass().getName(), e);
        } finally {
            logger.info("{} is complete", getClass().getSimpleName());
        }
    }

    @Override
    public boolean hasException() {
        return this.taskException != null;
    }

    @Override
    public Exception getTaskException() {
        return taskException;
    }

    @Override
    public abstract void doTask(String deviceId, String appId) throws Exception;
}
