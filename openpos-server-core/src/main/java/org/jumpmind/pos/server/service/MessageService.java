package org.jumpmind.pos.server.service;

import java.sql.Date;
import java.time.Instant;
import java.util.*;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.model.CachedMessage;
import org.jumpmind.pos.server.model.FetchMessage;
import org.jumpmind.pos.util.web.NotFoundException;
import org.jumpmind.pos.util.web.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@CrossOrigin
@Controller
public class MessageService implements IMessageService {

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger loggerGraphical = LoggerFactory.getLogger(getClass().getName() + ".graphical");

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    SimpMessagingTemplate template;

    @Value("${openpos.screens.jsonIncludeNulls:true}")
    boolean jsonIncludeNulls = true;

    @Value("${openpos.general.websocket.sendBufferSizeLimit:8192000}")
    int websocketSendBufferLimit;

    @Value("${openpos.general.message.cacheTimeout:300000}")
    int messageCacheTimeout;

    @Autowired(required=false)
    List<IActionListener> actionListeners;

    private Map<String, CachedMessage> cachedMessageMap;

    @PostConstruct
    public void init() {
        cachedMessageMap = Collections.synchronizedMap( new PassiveExpiringMap<>(messageCacheTimeout));

        if (!jsonIncludeNulls) {
            mapper.setSerializationInclusion(Include.NON_NULL);
        }
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "ping", produces="application/json")
    @ResponseBody
    public String ping() {
        logger.info("Received a ping request");
        return "{ \"pong\": \"true\" }";
    }

    @MessageMapping("action/device/{deviceId}")
    public void action(@DestinationVariable String deviceId, @Payload Action action, Message<?> message) {
        if (action.getType() == null) {
            throw new ServerException("Message/action must have a type. " + message);
        }        
        boolean handled = false;
        for (IActionListener actionListener : actionListeners) {
            if (action.getType() != null && actionListener.getRegisteredTypes() != null &&
                    actionListener.getRegisteredTypes().contains(action.getType())) {
                handled = true;
                actionListener.actionOccurred(deviceId, action);
            }
        }
        
        if (!handled) {
            throw new ServerException("Message/action was not handled by any action listeners. message=[" + 
                    message + "] actionListeners=[" + actionListeners + "]");
        }
    }

    @Override
    public void sendMessage(String deviceId, org.jumpmind.pos.util.model.Message message) {
        try {
            StringBuilder topic = new StringBuilder(128);
            topic.append("/topic/app/device/").append(deviceId);

            String jsonString = messageToJson(message);

            byte[] json = jsonString.getBytes("UTF-8");

            if( json.length <= websocketSendBufferLimit ){
                this.template.send(topic.toString(), MessageBuilder.withPayload(json).build());
            } else {
                String id = UUID.randomUUID().toString();
                String fetchMessageJson = messageToJson(FetchMessage.builder().messageIdToFetch(id).build());
                cachedMessageMap.put(id, CachedMessage.builder().message(message).cachedTime(Date.from(Instant.now())).build());
                this.template.send(topic.toString(), MessageBuilder.withPayload(fetchMessageJson.getBytes("UTF-8")).build());
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to publish message for deviceId: " + deviceId + " " + message, ex);
        }
    }

    @RequestMapping(method = RequestMethod.GET,  value = "api/app/device/{deviceId}/message/{id}")
    @ResponseBody
    public String getCachedMessage(@PathVariable("deviceId") String deviceId, @PathVariable("id") String id){

        try{
            if(cachedMessageMap.containsKey(id)){
                try {
                    org.jumpmind.pos.util.model.Message m = cachedMessageMap.get(id).getMessage();
                    cachedMessageMap.remove(id);
                    return messageToJson(m);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Failed to fetch cached message" + id, e);
                }
            } else {
                throw new NotFoundException();
            }
        } catch (Exception e){
            Action errorAction = new Action("GlobalError", e);
            errorAction.setType("Screen");
            action(deviceId, errorAction, null);
            throw e;
        }
    }
    
    protected String messageToJson(org.jumpmind.pos.util.model.Message message) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
    }

}
