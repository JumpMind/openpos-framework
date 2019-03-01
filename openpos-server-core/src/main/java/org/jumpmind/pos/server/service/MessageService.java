package org.jumpmind.pos.server.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jumpmind.pos.server.model.Action;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Value("${openpos.screenService.jsonIncludeNulls:true}")
    boolean jsonIncludeNulls = true;
    
    @Autowired(required=false)
    List<IActionListener> actionListeners;


    @PostConstruct
    public void init() {
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

    @MessageMapping("action/app/{appId}/node/{nodeId}")
    public void action(@DestinationVariable String appId, @DestinationVariable String nodeId, @Payload Action action, Message<?> message) {
        if (action.getType() == null) {
            throw new ServerException("Message/action must have a type. " + message);
        }        
        boolean handled = false;
        for (IActionListener actionListener : actionListeners) {
            if (action.getType() != null && actionListener.getRegisteredTypes() != null &&
                    actionListener.getRegisteredTypes().contains(action.getType())) {
                handled = true;
                actionListener.actionOccured(appId, nodeId, action);
            }
        }
        
        if (!handled) {
            throw new ServerException("Message/action was not handled by any action listeners. message=[" + 
                    message + "] actionListeners=[" + actionListeners + "]");
        }
    }

    @Override
    public void sendMessage(String appId, String nodeId, org.jumpmind.pos.util.model.Message message) {
        try {
            StringBuilder topic = new StringBuilder(128);
            topic.append("/topic/app/").append(appId).append("/node/").append(nodeId);
            byte[] json = messageToJson(message).getBytes("UTF-8");
            this.template.send(topic.toString(), MessageBuilder.withPayload(json).build());
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to publish message for node: " + nodeId + " " + message, ex);
        }
    }
    
    protected String messageToJson(org.jumpmind.pos.util.model.Message message) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
    }

}
