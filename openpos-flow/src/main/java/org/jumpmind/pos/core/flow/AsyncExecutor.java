package org.jumpmind.pos.core.flow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
@Scope("device")
public class AsyncExecutor {
    private ThreadPoolTaskScheduler scheduler;
    private List<ScheduledFuture<?>> scheduledJobs;
    private boolean cancelled;

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @Autowired
    StateManagerContainer stateManagerContainer;

    Runnable beforeCancel;

    @PostConstruct
    public void init() {
        this.scheduler = new ThreadPoolTaskScheduler();
        this.scheduler.setThreadNamePrefix("async-executor-task-");
        this.scheduler.setPoolSize(20);
        this.scheduler.initialize();
        scheduledJobs = new ArrayList<ScheduledFuture<?>>();
    }

    synchronized public void cancel() {
        cancelled = true;
        if (beforeCancel != null) {
            this.beforeCancel.run();
        }
    }

    synchronized public <T, R> void execute(T request, Function<T, R> doWork, Consumer<R> handleResult, Consumer<Throwable> handleError) {
       execute(request, doWork, handleResult, handleError, r-> log.info("The executor returned but the results are being ignored because it was cancelled.  The ignored results were {}", r), null);
    }

    synchronized public <T, R> void execute(T request, Function<T, R> doWork, Consumer<R> handleResult, Consumer<Throwable> handleError, Consumer<R> handleCancel, Runnable beforeCancel) {
        this.beforeCancel = beforeCancel;
        cancelled = false;
        scheduler.execute(() -> {
            try {
                stateManagerContainer.setCurrentStateManager(stateManager);
                R result = doWork.apply(request);
                if (!cancelled) {
                    handleResult.accept(result);
                } else if (handleCancel != null) {
                    handleCancel.accept(result);
                }
            } catch (Throwable throwable) {
                if (!cancelled) {
                    handleError.accept(throwable);
                } else if (handleCancel != null) {
                    handleCancel.accept(null);
                }
            }
        });
    }
}