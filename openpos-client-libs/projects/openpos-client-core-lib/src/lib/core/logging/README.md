# Client Logging

The client implements a plugable logging framework that uses the console log, info, warn, error, and debug methods as its API. When the logger is disabled through configuration the log entries are output to the browsers console. When enabled the console methods are intercepted and redirected to the loggers provided.

Console intercepting is enabled through the following client configuration set.

```yaml
    console-interceptor:
        enable: true
```

## Server Logger

The server logger is a logging plugin provided in the core that will buffer up log entries and send the batches to an endpoint on OpenposServer. The timespan in milliseconds for the buffer is set through the following client configuration set.

```yaml
    server-logger:
        logBufferTime: 1000

```

After the entries reach the server the AppId, DeviceId, and timestamp recorded on the client are put into SLF4J context map making them available to use in the pattern layout of a log appender.

Here is an example using Log4J

```xml
    <appender name="CLIENT"
              class="org.jumpmind.pos.util.OpenposRollingFileAppender">
        <param name="File" value="build/clientlog.log" />
        <param name="Append" value="true" />
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%X{timestamp} deviceId: %X{deviceId}  appId: %X{appId} %p [%t] %m%n" />
        </layout>
    </appender>
```

The `timestamp` output to the client log can optionally be formatted by providing a value in the server configuration for the `openpos.clientLogCollector.timestampFormat` property. Here is an example configuration:
```yaml
openpos:
  clientLogCollector:
    timestampFormat: yyyy-MM-dd HH:mm:ss,SSS
```

## Providing additional loggers

Additional loggers can be provided by implementing [ILogger](logger.interface) and provide it using the token `LOGGERS` token.

```typescript
        { provide: LOGGERS, useExisting: ServerLogger, multi: true }
```

## Bypassing the Console Interceptor

There may be times when you want to by pass the console interceptor. For example logging from a logger plugin could result in looping. In these cases use the [ConsoleInterceptorBypassService](console-interceptor-bypass.service.ts) and your messages will be output to the non-intercepted console methods.
