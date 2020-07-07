package org.jumpmind.pos.util.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

abstract public class AbstractStartupTask implements ApplicationListener<ApplicationReadyEvent> {

    final protected Logger logger = LoggerFactory.getLogger(getClass());

    private Exception taskException = null;
    
    @Override
    final public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("{} is executing ...", getClass().getSimpleName());
        try {
            doTask();
        } catch (Exception e) {
            this.taskException = e;
            String message = "Failed to execute " + getClass().getName();
            if (isFailOnExceptionEnabled(event.getApplicationContext().getEnvironment())) {
                throw new RuntimeException(message, e);
            }
            logger.error(message, e);
        } finally {
            logger.info("{} is complete", getClass().getSimpleName());
        }
    }

    protected boolean isFailOnExceptionEnabled(Environment env) {
        return Boolean.parseBoolean(env.getProperty("openpos.general.failStartupOnModuleLoadFailure", "false"));
    }

    public boolean hasException() {
        return this.taskException != null;
    }
    
    public Exception getTaskException() {
        return taskException;
    }
    
    protected abstract void doTask() throws Exception;
}