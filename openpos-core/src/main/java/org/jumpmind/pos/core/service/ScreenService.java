package org.jumpmind.pos.core.service;

import static org.jumpmind.pos.util.BoxLogging.HORIZONTAL_LINE;
import static org.jumpmind.pos.util.BoxLogging.LOWER_LEFT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.LOWER_RIGHT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.UPPER_LEFT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.UPPER_RIGHT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.VERITCAL_LINE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.flow.FlowException;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerFactory;
import org.jumpmind.pos.core.flow.SessionTimer;
import org.jumpmind.pos.core.model.ComboField;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormField;
import org.jumpmind.pos.core.model.FormListField;
import org.jumpmind.pos.core.model.IFormElement;
import org.jumpmind.pos.core.model.ToggleField;
import org.jumpmind.pos.core.model.annotations.FormButton;
import org.jumpmind.pos.core.model.annotations.FormTextField;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.core.screen.DialogProperties;
import org.jumpmind.pos.core.screen.DialogScreen;
import org.jumpmind.pos.core.screen.DynamicFormScreen;
import org.jumpmind.pos.core.screen.FormScreen;
import org.jumpmind.pos.core.screen.IHasForm;
import org.jumpmind.pos.core.screen.ScreenType;
import org.jumpmind.pos.util.LogFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("deprecation")
@CrossOrigin
@Controller
public class ScreenService implements IScreenService {

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger loggerGraphical = LoggerFactory.getLogger(getClass().getName() + ".graphical");

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    IStateManagerFactory stateManagerFactory;
    
    @Value("${org.jumpmind.pos.core.service.ScreenService.jsonIncludeNulls:true}")
    private boolean jsonIncludeNulls = true;
    
    @Autowired
    private LogFormatter logFormatter;

    int screenSequenceNumber = 0;

    private Map<String, Map<String, Screen>> lastScreenByAppIdByNodeId = new HashMap<>();

    private Map<String, Map<String, Screen>> lastDialogByAppIdByNodeId = new HashMap<>();
    
    @PostConstruct
    public void init() {
        if (!jsonIncludeNulls) {
            mapper.setSerializationInclusion(Include.NON_NULL);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "ping")
    @ResponseBody
    public String ping() {
        logger.info("Received a ping request");
        return "{ \"pong\": \"true\" }";
    }

    @RequestMapping(method = RequestMethod.GET, value = "app/{appId}/node/{nodeId}/{action}/{payload}")
    public void getAction(@PathVariable String appId, @PathVariable String nodeId, @PathVariable String action, @PathVariable String payload,
            HttpServletResponse resp) {
        logger.info("Received a request for {} {} {} {}", appId, nodeId, action, payload);
        IStateManager stateManager = stateManagerFactory.retrieve(appId, nodeId);
        if (stateManager != null) {
            logger.info("Calling stateManager.doAction with: {}", action);
            stateManager.doAction(new Action(action, payload));
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "api/app/{appId}/node/{nodeId}/control/{controlId}")
    @ResponseBody
    public String getComponentValues(@PathVariable String appId, @PathVariable String nodeId, @PathVariable String controlId, 
            @RequestParam(name="searchTerm", required=false) String searchTerm,
            @RequestParam(name="sizeLimit", defaultValue="1000") Integer sizeLimit
            ) {
        logger.info("Received a request to load component values for {} {} {}", appId, nodeId, controlId);
        String result = getComponentValues(appId, nodeId, controlId, getLastScreen(appId, nodeId), searchTerm, sizeLimit);
        if (result == null) {
            result = getComponentValues(appId, nodeId, controlId, getLastDialog(appId, nodeId), searchTerm, sizeLimit);
        }
        if (result == null) {
            result = "{}";
        }
        return result;
    }

    private String getComponentValues(String appId, String nodeId, String controlId, Screen screen, String searchTerm, Integer sizeLimit) {
        String result = null;
        if (screen instanceof DynamicFormScreen) {
            DynamicFormScreen dynamicScreen = (DynamicFormScreen) screen;
            IFormElement formElement = dynamicScreen.getForm().getFormElement(controlId);

            // TODO: Look at combining FormListField and ComboField or at least
            // inheriting off of each other.
            List<String> valueList = null;
            if (formElement instanceof FormListField) {
                valueList = ((FormListField) formElement).searchValues(searchTerm, sizeLimit);
            } else if (formElement instanceof ComboField) {
                valueList = ((ComboField) formElement).searchValues(searchTerm, sizeLimit);
            } else if (formElement instanceof ToggleField) {
                valueList = ((ToggleField) formElement).searchValues(searchTerm, sizeLimit);
            }
            if (valueList != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    mapper.writeValue(out, valueList);
                } catch (IOException e) {
                    throw new RuntimeException("Error while serializing the component values.", e);
                }
                result = new String(out.toByteArray());
                logger.info("Responding to request to load component values {} {} {} with {} values", appId, nodeId, controlId,
                        valueList.size());
            } else {
                logger.info("Unable to find the valueList for the requested component {} {} {}.", appId, nodeId, controlId);
            }
        }
        return result;
    }

    @MessageMapping("action/app/{appId}/node/{nodeId}")
    public void action(@DestinationVariable String appId, @DestinationVariable String nodeId, Action action) {
        IStateManager stateManager = stateManagerFactory.retrieve(appId, nodeId);
        if (stateManager != null) {
            if (SessionTimer.ACTION_KEEP_ALIVE.equals(action.getName())) {
                stateManager.keepAlive();
            } else {
                deserializeForm(appId, nodeId, action);

                logger.info("Received action from {}\n{}", nodeId, logFormatter.toJsonString(action));

                Screen lastDialog = removeLastDialog(appId, nodeId);
                if (lastDialog != null && ScreenType.Dialog.equals(lastDialog.getType())) {
                    logger.debug("Instructing node {} to clear dialog.", nodeId);
                    publishToClients(appId, nodeId, "{\"clearDialog\":true }");
                }

                try {
                    logger.debug("Posting action {}", action);
                    stateManager.doAction(action);
                } catch (Throwable ex) {
                    logger.error("Unexpected exception while processing action: " + action, ex);
                    DialogScreen errorDialog = new DialogScreen();
                    errorDialog.asDialog(new DialogProperties(true));
                    errorDialog.setIcon("error");
                    errorDialog.setTitle("Internal Server Error");
                    errorDialog.setMessage(Arrays
                            .asList("The application received an unexpected error. Please report to the appropriate technical personnel"));
                    showScreen(appId, nodeId, errorDialog);
                }
            }
        }
    }

    protected Screen removeLastDialog(String appId, String nodeId) {
        Map<String, Screen> lastScreenByNodeId = lastDialogByAppIdByNodeId.get(appId);
        if (lastScreenByNodeId != null) {
            return lastScreenByNodeId.remove(nodeId);
        } else {
            return null;
        }
    }

    @Override
    public Screen getLastDialog(String appId, String nodeId) {
        Map<String, Screen> lastScreenByNodeId = lastDialogByAppIdByNodeId.get(appId);
        if (lastScreenByNodeId != null) {
            return lastScreenByNodeId.get(nodeId);
        } else {
            return null;
        }
    }

    @Override
    public Screen getLastScreen(String appId, String nodeId) {
        Map<String, Screen> lastScreenByNodeId = lastScreenByAppIdByNodeId.get(appId);
        if (lastScreenByNodeId != null) {
            return lastScreenByNodeId.get(nodeId);
        } else {
            return null;
        }
    }

    @Override
    public void showScreen(String appId, String nodeId, Screen screen) {
        if (screen != null) {
            screen.setSequenceNumber(++this.screenSequenceNumber);
            try {
                applyAnnotations(screen);
                if (screen.isScreenOfType(ScreenType.Form) && !(screen instanceof FormScreen)) {
                    Form form = buildForm(screen);
                    screen.put("form", form);
                }
                logScreenTransition(nodeId, screen);
            } catch (Exception ex) {
                if (ex.toString().contains("org.jumpmind.pos.core.screen.ChangeScreen")) {
                    logger.error(
                            "Failed to write screen to JSON. Verify the screen type has been configured by calling setType() on the screen object.",
                            ex);
                } else {
                    logger.error("Failed to write screen to JSON", ex);
                }
            }
            publishToClients(appId, nodeId, screen);
            if (screen.getTemplate().isDialog()) {
                recordLastScreen(appId, nodeId, screen, lastDialogByAppIdByNodeId);
            } else {
                recordLastScreen(appId, nodeId, screen, lastScreenByAppIdByNodeId);
            }
        }
    }

    private void recordLastScreen(String appId, String nodeId, Screen screen,
            Map<String, Map<String, Screen>> lastScreenByAppIdByNodeId) {
        Map<String, Screen> lastScreenByNodeId = lastScreenByAppIdByNodeId.get(appId);
        if (lastScreenByNodeId == null) {
            lastScreenByNodeId = new HashMap<>();
            lastScreenByAppIdByNodeId.put(appId, lastScreenByNodeId);
        }
        lastScreenByNodeId.put(nodeId, screen);
    }

    protected void publishToClients(String appId, String nodeId, Object payload) {
        try {            
            StringBuilder topic = new StringBuilder(128);
            topic.append("/topic/app/").append(appId).append("/node/").append(nodeId);
            payload = payload instanceof String ? ((String)payload).getBytes("UTF-8") : mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload).getBytes("UTF-8");
            Message<?> message = MessageBuilder.withPayload(payload).build();
            this.template.send(topic.toString(), message);
        } catch (Exception ex) {
            throw new FlowException("Failed to serialize message for node: " + nodeId + " " + payload, ex);
        }
    }

    protected void deserializeForm(String appId, String nodeId, Action action) {
        if (hasForm(appId, nodeId)) {
            try {
                Form form = mapper.convertValue(action.getData(), Form.class);
                if (form != null) {
                    action.setData(form);
                }
            } catch (IllegalArgumentException ex) {
                // We should not assume a form will always be returned by
                // the DynamicFormScreen.
                // The barcode scanner can also return a value.
                // TODO: Allow serializing more than the form on an action.
            }
        }
    }

    private boolean hasForm(String appId, String nodeId) {
        Map<String, Screen> lastScreenByNodeId = lastDialogByAppIdByNodeId.get(appId);
        if (lastScreenByNodeId != null && lastScreenByNodeId.get(nodeId) != null) {
            return lastScreenByNodeId.get(nodeId) instanceof IHasForm;
        } else {
            lastScreenByNodeId = lastScreenByAppIdByNodeId.get(appId);
            if (lastScreenByNodeId != null) {
                Screen lastScreen = lastScreenByNodeId.get(nodeId);
                if (lastScreen != null && lastScreen instanceof IHasForm) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Form buildForm(Screen screen) {
        Form form = new Form();

        for (Field field : screen.getClass().getDeclaredFields()) {
            FormTextField textFieldAnnotation = field.getAnnotation(FormTextField.class);
            if (textFieldAnnotation != null) {
                FormField formField = new FormField();
                formField.setElementType(textFieldAnnotation.fieldElementType());
                formField.setInputType(textFieldAnnotation.fieldInputType());
                formField.setId(field.getName());
                formField.setLabel(textFieldAnnotation.label());
                formField.setPlaceholder(textFieldAnnotation.placeholder());
                formField.setPattern(textFieldAnnotation.pattern());
                formField.setValue(getFieldValueAsString(field, screen));
                formField.setRequired(textFieldAnnotation.required());
                form.addFormElement(formField);
            }
            FormButton buttonAnnotation = field.getAnnotation(FormButton.class);
            if (buttonAnnotation != null) {
                org.jumpmind.pos.core.model.FormButton button = new org.jumpmind.pos.core.model.FormButton();
                button.setLabel(buttonAnnotation.label());
                button.setButtonAction(getFieldValueAsString(field, screen));
                form.addFormElement(button);
            }
        }
        return form;
    }

    protected void applyAnnotations(Screen screen) {
        org.jumpmind.pos.core.model.annotations.Screen screenAnnotation = screen.getClass()
                .getAnnotation(org.jumpmind.pos.core.model.annotations.Screen.class);
        if (screenAnnotation != null) {
            screen.setName(screenAnnotation.name());
            screen.setType(screenAnnotation.type());
        }
    }

    @Override
    public void refresh(String appId, String nodeId) {
        Map<String, Screen> lastScreenByNodeId = lastScreenByAppIdByNodeId.get(appId);
        if (lastScreenByNodeId != null) {
            showScreen(appId, nodeId, lastScreenByNodeId.get(nodeId));
        }
    }

    protected void setFieldValue(Field field, Object target, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception ex) {
            throw new FlowException("Field to set value " + value + " for field " + field + " from target " + target, ex);
        }
    }

    protected String getFieldValueAsString(Field field, Object target) {
        try {
            field.setAccessible(true);
            Object value = field.get(target);
            if (value != null) {
                return String.valueOf(value);
            } else {
                return null;
            }

        } catch (Exception ex) {
            throw new FlowException("Field to get value for field " + field + " from target " + target, ex);
        }
    }

    protected void logScreenTransition(String nodeId, Screen screen) throws JsonProcessingException {
        if (loggerGraphical.isInfoEnabled()) {
            logger.info("Show screen on node \"" + nodeId + "\"\n" + drawBox(screen.getName(), screen.getType())
                    + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(screen));
        } else {
            logger.info("Show screen on node \"" + nodeId + "\"\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(screen));
        }
    }

    protected String drawBox(String name, String typeName) {
        String displayName = name != null ? name : null;
        String displayTypeName = "";
        
        if (!StringUtils.isEmpty(displayName)) {
            displayTypeName = typeName != null ? typeName : "screen";
            displayTypeName = "[" + displayTypeName + "]";
        } else {
            displayName = typeName != null ? typeName : "screen";
            displayName = "[" + displayName + "]";
        }
        
        int boxWidth = Math.max(Math.max(displayName.length() + 2, 28), displayTypeName.length() + 4);
        final int LINE_COUNT = 8;
        StringBuilder buff = new StringBuilder(256);
        for (int i = 0; i < LINE_COUNT; i++) {
            switch (i) {
                case 0:
                    buff.append(drawTop1(boxWidth));
                    break;
                case 1:
                    buff.append(drawTop2(boxWidth));
                    break;
                case 2:
                    buff.append(drawFillerLine(boxWidth));
                    break;
                case 3:
                    buff.append(drawTitleLine(boxWidth, displayName));
                    break;
                case 4:
                    buff.append(drawTypeLine(boxWidth, displayTypeName));
                    break;
                case 5:
                    buff.append(drawBottom1(boxWidth));
                    break;
                case 6:
                    buff.append(drawBottom2(boxWidth));
                    break;
            }
        }
        return buff.toString();
    }

    protected String drawTop1(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 2)).append(UPPER_RIGHT_CORNER);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTop2(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 4))
                .append(UPPER_RIGHT_CORNER + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawFillerLine(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + VERITCAL_LINE).append(StringUtils.repeat(' ', boxWidth - 4)).append(VERITCAL_LINE + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTitleLine(int boxWidth, String name) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + VERITCAL_LINE).append(StringUtils.center(name, boxWidth - 4)).append(VERITCAL_LINE + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTypeLine(int boxWidth, String typeName) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + VERITCAL_LINE).append(StringUtils.center(typeName, boxWidth - 4))
                .append(VERITCAL_LINE + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawBottom1(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 4))
                .append(LOWER_RIGHT_CORNER + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawBottom2(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 2)).append(LOWER_RIGHT_CORNER);
        buff.append("\r\n");
        return buff.toString();
    }

}
