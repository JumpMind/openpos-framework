package org.jumpmind.pos.server.service;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import java.util.List;

@Data
@ConfigurationProperties("openpos.server.http-requests.logging")
public class RestRequestLoggingFilter extends AbstractRequestLoggingFilter {

    Logger log = LoggerFactory.getLogger(getClass());

    List<String> include;

    public RestRequestLoggingFilter() {
        setIncludeClientInfo(true);
        setIncludeQueryString(true);
        setBeforeMessagePrefix("\n*****************************************\n  SERVICE REQUEST:");
        setBeforeMessageSuffix("\n*****************************************");
        setIncludePayload(true);
        setMaxPayloadLength(10000);
        setIncludeHeaders(true);
        setAfterMessagePrefix("\n*****************************************\n  SERVICE RESULT: ");
        setAfterMessageSuffix("\n*****************************************");
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        if (log(request)) {
            log.info(message);
        }
    }

    protected boolean log(HttpServletRequest request) {
        return include != null && include.stream().anyMatch((p)->request.getRequestURI().contains(p));
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        if (log(request)) {
            log.info(message);
        }
    }

}
