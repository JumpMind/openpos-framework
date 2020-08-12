package org.jumpmind.pos.util.logging;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.*;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class OpenposPatternLayoutEncoder extends PatternLayoutEncoder {

    private int historySize = 1024;

    private Map<String, String> loggedEventKeys;

    public OpenposPatternLayoutEncoder() {
        this.loggedEventKeys = new LinkedHashMap<String, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return (size() >= historySize);
            }
        };
    }

    @Override
    public byte[] headerBytes() {
        loggedEventKeys.clear();
        return super.headerBytes();
    }

    public byte[] encode(ILoggingEvent event) {
        if (event.getThrowableProxy() != null) {
            String key = toKey(event);
            if (loggedEventKeys.containsKey(key)) {
                event = supressStackTrace(event, key);
            } else {
                event = appendKey(event, key);
                loggedEventKeys.put(key, null);
            }
        }

        return super.encode(event);
    }

    protected long getThrowableHash(IThrowableProxy throwable) throws UnsupportedEncodingException {
        CRC32 crc = new CRC32();
        String stackTraceString = ThrowableProxyUtil.asString(throwable);
        crc.update(stackTraceString.getBytes("UTF8"));
        return crc.getValue();
    }

    protected ILoggingEvent appendKey(ILoggingEvent event, String key) {
        String message = getMessageWithKey(event, key, ".init");
        ILoggingEvent eventClone = cloneLoggingEvent(event, message, (ThrowableProxy) event.getThrowableProxy());
        return eventClone;
    }

    protected ILoggingEvent supressStackTrace(ILoggingEvent event, String key) {
        String message = getMessageWithKey(event, key);
        ILoggingEvent eventClone = cloneLoggingEvent(event, message, null);
        return eventClone;
    }

    protected ILoggingEvent cloneLoggingEvent(ILoggingEvent event, String message, ThrowableProxy throwableProxy) {
        LoggingEvent clonedEvent = new LoggingEvent();
        clonedEvent.setMessage(message);
        clonedEvent.setThrowableProxy(throwableProxy);
        clonedEvent.setLevel(event.getLevel());
        clonedEvent.setArgumentArray(event.getArgumentArray());
        clonedEvent.setLoggerName(event.getLoggerName());
        clonedEvent.setMarker(event.getMarker());
        clonedEvent.setTimeStamp(event.getTimeStamp());
        clonedEvent.setCallerData(event.getCallerData());
        clonedEvent.setMDCPropertyMap(event.getMDCPropertyMap());
        clonedEvent.setThreadName(event.getThreadName());
        return clonedEvent;
    }

    protected String getMessageWithKey(ILoggingEvent event, String key) {
        return getMessageWithKey(event, key, null);
    }

    protected String getMessageWithKey(ILoggingEvent event, String key, String prefix) {
        StringBuilder buff = new StringBuilder(128);
        if (event.getMessage() != null) {
            buff.append(event.getMessage()).append(" ");
        }
        buff.append("StackTraceKey");
        if (prefix != null) {
            buff.append(prefix);
        }
        buff.append(" [").append(key).append("]");
        return buff.toString();
    }

    protected String toKey(ILoggingEvent event) {
        try {
            StringBuilder buff = new StringBuilder(128);

            IThrowableProxy throwable = event.getThrowableProxy();
            buff.append(throwable.getClassName());

            StackTraceElementProxy[] stackTraceElements = throwable.getStackTraceElementProxyArray();

            if (stackTraceElements == null || stackTraceElements.length == 0) {
                buff.append("-jvm-optimized");
            }
            buff.append(":");
            buff.append(getThrowableHash(throwable));
            return buff.toString();
        } catch (Exception ex) {
            System.err.println("Failed to hash stack trace for " + ex.toString());
            ex.printStackTrace(System.err);
            return null;
        }
    }
}
