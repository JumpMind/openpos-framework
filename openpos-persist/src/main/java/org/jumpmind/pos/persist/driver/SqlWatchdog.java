package org.jumpmind.pos.persist.driver;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.db.sql.LogSqlBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SqlWatchdog {

    public static final String THREAD_NAME = "Commerce-SqlWatchdog-Thread";

    private static java.util.Map<InProgressSqlKey, InProgressSqlStatement> inProgressSql = new ConcurrentHashMap<>();

    private static AtomicBoolean running = new AtomicBoolean(false);

    public static synchronized void start() {
        if (running.get()) {
            return;
        }
        running.set(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info(THREAD_NAME + " Started...");
                runLoop();
                log.info(THREAD_NAME + " Exiting...");
            }
        });

        thread.setName(THREAD_NAME);
        thread.setDaemon(true);
        log.info("Starting " + THREAD_NAME + "...");
        thread.start();
    }

    public static synchronized void stop() {
        running.set(false);
    }

    public static void sqlBegin(InProgressSqlKey key, InProgressSqlStatement sqlStatement) {
        inProgressSql.put(key, sqlStatement);
    }

    public static void sqlEnd(InProgressSqlKey key) {
        inProgressSql.remove(key);
    }

    protected static void runLoop() {
        LogSqlBuilder sqlBuilder = new LogSqlBuilder();

        long pollInterval = Long.parseLong(System.getProperty("jumpmind.commerce.SqlWatchDog.pollInterval", "20000"));
        long warnInterval = Long.parseLong(System.getProperty("jumpmind.commerce.SqlWatchDog.warnInterval", "60000"));

        while (running.get()) {
            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                log.debug("Thread interrupted.", e);
            }
            
            for (InProgressSqlStatement inProgressSqlStatement : inProgressSql.values()) {
                long now = System.currentTimeMillis();
                long timeSinceLastLogged = now - inProgressSqlStatement.getLastLoggedTime();

                if (timeSinceLastLogged >= warnInterval) {
                    inProgressSqlStatement.setLastLoggedTime(now);
                    String sql = sqlBuilder.buildDynamicSqlForLog(inProgressSqlStatement.getSql(), inProgressSqlStatement.getArgs().toArray(), null);
                    long elapsed = now - inProgressSqlStatement.getStartTime();
                    log.warn("SQL Still Running (" + elapsed + "ms.) on thread '" + inProgressSqlStatement.getThreadName() + "': " + sql.trim());
                }
            }
        }
    }
}
