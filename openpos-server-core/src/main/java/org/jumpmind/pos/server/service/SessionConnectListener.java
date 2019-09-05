package org.jumpmind.pos.server.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.server.config.MessageUtils;
import org.jumpmind.pos.server.config.PersonalizationParameter;
import org.jumpmind.pos.server.config.PersonalizationParameters;
import org.jumpmind.pos.util.BoxLogging;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import com.fasterxml.jackson.core.type.TypeReference;

@Component("serverCoreSessionConnectListener")
public class SessionConnectListener implements ApplicationListener<SessionConnectEvent>, MessageUtils {

    Logger log = LoggerFactory.getLogger(getClass());

    Map<String, Boolean> sessionAuthenticated = Collections.synchronizedMap(new HashMap<>());

    Map<String, Boolean> sessionCompatible = Collections.synchronizedMap(new HashMap<>());

    Map<String, Map<String, String>> sessionPersonalizationResults = Collections.synchronizedMap(new HashMap<>());

    Map<String, Map<String, Object>> sessionQueryParamsMap = Collections.synchronizedMap(new HashMap<>());

    @Value("${openpos.auth.token:#{null}}")
    String serverAuthToken;

    @Value("${openpos.compatibility.version:#{null}}")
    String serverCompatibilityVersion;

    @Autowired(required = false)
    PersonalizationParameters personalizationParameters;

    public void onApplicationEvent(SessionConnectEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        String authToken = getHeader(event.getMessage(), "authToken");
        String clientVersion = getHeader(event.getMessage(), "version");
        log.info(BoxLogging.box("Session Connected " + sessionId) + "\n" + clientVersion + "\n");
        String compatibilityVersion = getHeader(event.getMessage(), COMPATIBILITY_VERSION_HEADER);
        String queryParams = getHeader(event.getMessage(), QUERY_PARAMS_HEADER);
        sessionQueryParamsMap.put(sessionId, toQueryParams(queryParams));
        sessionAuthenticated.put(sessionId, serverAuthToken == null || serverAuthToken.equals(authToken));
        if (serverAuthToken != null && ! serverAuthToken.equals(authToken)) {
            String clientAuthTokenValueIfNull = authToken == null || "".equals(authToken) || "undefined".equals(authToken) ? String.format(" (value is: '%s')", authToken) : "";
            this.log.warn("Client auth token{} does not match server auth token, client connection will be rejected.", clientAuthTokenValueIfNull);
        };
        sessionCompatible.put(sessionId, serverCompatibilityVersion == null || serverCompatibilityVersion.equals(compatibilityVersion));

        setPersonalizationResults(sessionId, event);
    }

    private void setPersonalizationResults(String sessionId, SessionConnectEvent event) {
        if (personalizationParameters != null && personalizationParameters.getParameters() != null) {
            Map<String, String> personalizationResults = new HashMap<>();
            for (PersonalizationParameter param : personalizationParameters.getParameters()) {
                String prop = param.getProperty();
                String value = getHeader(event.getMessage(), prop);
                personalizationResults.put(prop, value);
            }
            sessionPersonalizationResults.put(sessionId, personalizationResults);
        }
    }

    private Map<String, Object> toQueryParams(String json) {
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        try {
            return DefaultObjectMapper.build().readValue(json, typeRef);
        } catch (Exception e) {
            log.error("Failed to parse query params", e);
            return Collections.emptyMap();
        }
    }

    public boolean isSessionAuthenticated(String sessionId) {
        return this.sessionAuthenticated.get(sessionId) != null && this.sessionAuthenticated.get(sessionId);
    }

    public boolean isSessionCompatible(String sessionId) {
        return this.sessionCompatible.get(sessionId) != null && this.sessionCompatible.get(sessionId);
    }

    public Map<String, String> getPersonalizationResults(String sessionId) {
        return sessionPersonalizationResults.get(sessionId);
    }

    public Map<String, Object> getQueryParams(String sessionId) {
        return sessionQueryParamsMap.get(sessionId);
    }

    public void removeSession(String sessionId) {
        this.sessionAuthenticated.remove(sessionId);
        this.sessionCompatible.remove(sessionId);
    }

}