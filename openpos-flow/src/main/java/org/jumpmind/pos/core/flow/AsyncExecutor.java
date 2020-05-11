package org.jumpmind.pos.core.flow;

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

@Component
@Scope("device")
public class AsyncExecutor {
    private ThreadPoolTaskScheduler scheduler;
    private List<ScheduledFuture<?>> scheduledJobs;

    @In(scope = ScopeType.Device)
    private IStateManager stateManager;

    @Autowired
    private StateManagerContainer stateManagerContainer;

    @PostConstruct
    public void init() {
        this.scheduler = new ThreadPoolTaskScheduler();
        this.scheduler.setThreadNamePrefix("async-executor-task-");
        this.scheduler.setPoolSize(20);
        this.scheduler.initialize();
        scheduledJobs = new ArrayList<ScheduledFuture<?>>();
    }

    public <T, R> void execute(T request, Function<T, R> doWork, Consumer<R> handleResult, Consumer<Throwable> handleError) {
        scheduler.execute(() -> {
            try {
                stateManagerContainer.setCurrentStateManager(stateManager);
                R result = doWork.apply(request);
                handleResult.accept(result);
            } catch (Throwable throwable) {
                handleError.accept(throwable);
            }
        });
    }
}