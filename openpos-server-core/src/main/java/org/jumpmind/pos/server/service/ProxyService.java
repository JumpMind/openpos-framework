package org.jumpmind.pos.server.service;

import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.model.ProxyMessage;
import org.jumpmind.pos.server.model.ProxyResponse;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class ProxyService implements IActionListener {

    public final static int MAX_REQUEST_RETENTION_MINS = 20;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IMessageService messageService;

    @Autowired
    ClientContext clientContext;

    private HashMap<UUID, ProxyResponseMapEntry> requestToResponseMap = new HashMap<>();

    @Override
    public Collection<String> getRegisteredTypes() {
        Set<String> types = new HashSet<>();
        types.add("proxy");
        return types;
    }

    public CompletableFuture<ProxyResponse> sendMessage( ProxyMessage message) {
        CompletableFuture<ProxyResponse> futureResponse = new CompletableFuture<>();
        this.requestToResponseMap.put(message.getMessageId(), new ProxyResponseMapEntry(futureResponse));

        messageService.sendMessage(clientContext.get("deviceId"), message);

        return futureResponse;
    }

    @Override
    public void actionOccurred(String deviceId, Action action) {
        ProxyResponse response = Action.convertActionData(action.getData(), ProxyResponse.class);

        ProxyResponseMapEntry responseEntry = this.requestToResponseMap.get(response.getMessageId());
        if (responseEntry != null) {
            this.requestToResponseMap.remove(response.getMessageId());
            logger.info("Invoking response callback for source proxy message id '{}'...", response.getMessageId().toString());
            responseEntry.getProxyResponseCallback().complete(response);
        } else {
            logger.warn("Callback handler not found or no longer exists for proxy message id '{}', response will be dropped.",
                    response.getMessageId().toString());
        }
    }

    /**
     * Clean up any requests that were not completed. Currently runs every 5
     * mins.
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    protected void cleanOrphanedRequests() {
        List<Entry<UUID, ProxyResponseMapEntry>> expiredRequests = this.requestToResponseMap.entrySet().stream()
                .filter(e -> e.getValue().getProxyResponseCallback().isDone()
                        || LocalTime.now().minusMinutes(MAX_REQUEST_RETENTION_MINS).compareTo(e.getValue().getTimeAdded()) >= 0)
                .collect(Collectors.toList());

        if (expiredRequests.size() > 0) {
            String expiredRequestKeys = expiredRequests.stream().map(e -> e.getKey().toString()).collect(Collectors.joining(", "));
            logger.debug("Removing {} orphaned proxy messages with IDs: {}", expiredRequests.size(), expiredRequestKeys);
            expiredRequests.forEach(entry -> {
                this.requestToResponseMap.remove(entry.getKey());
            });

            logger.info("Proxy Service cache cleaned, {} entr{} removed. New cache size: {}", expiredRequests.size(),
                    expiredRequests.size() > 1 ? "ies" : "y", this.requestToResponseMap.size());
        }
    }

    static class ProxyResponseMapEntry {
        CompletableFuture<ProxyResponse> futureResponse;
        LocalTime timeAdded;

        ProxyResponseMapEntry(CompletableFuture<ProxyResponse> callback) {
            this.futureResponse = callback;
            this.timeAdded = LocalTime.now();
        }

        public CompletableFuture<ProxyResponse> getProxyResponseCallback() {
            return futureResponse;
        }

        public void setProxyResponseCallback(CompletableFuture<ProxyResponse> deviceResponseCallback) {
            this.futureResponse = deviceResponseCallback;
        }

        public LocalTime getTimeAdded() {
            return timeAdded;
        }

        public void setTimeAdded(LocalTime timeAdded) {
            this.timeAdded = timeAdded;
        }
    }
}
