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
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.ApplicationState;
import org.jumpmind.pos.core.flow.FlowException;
import org.jumpmind.pos.core.flow.IScreenInterceptor;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerFactory;
import org.jumpmind.pos.core.flow.SessionTimer;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.IDynamicListField;
import org.jumpmind.pos.core.model.IFormElement;
import org.jumpmind.pos.core.screen.IHasForm;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.core.screen.Toast;
import org.jumpmind.pos.core.util.LogFormatter;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IActionListener;
import org.jumpmind.pos.server.service.IMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@CrossOrigin
@Controller
public class ScreenService implements IScreenService, IActionListener {

    Logger logger = LoggerFactory.getLogger(getClass());
    Logger loggerGraphical = LoggerFactory.getLogger(getClass().getName() + ".graphical");

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    IStateManagerFactory stateManagerFactory;

    @Value("${openpos.ScreenService.jsonIncludeNulls:true}")
    boolean jsonIncludeNulls = true;

    @Autowired
    LogFormatter logFormatter;

    @Autowired
    IMessageService messageService;

    Queue<IScreenInterceptor> screenInterceptors = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        if (!jsonIncludeNulls) {
            mapper.setSerializationInclusion(Include.NON_NULL);
        }
    }

    @Autowired(required = false)
    public void setScreenInterceptors(List<IScreenInterceptor> screenInterceptors) {
        for (IScreenInterceptor screenInterceptor : screenInterceptors) {
            this.screenInterceptors.add(screenInterceptor);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "api/app/{appId}/node/{deviceId}/control/{controlId}")
    @ResponseBody
    public String getComponentValues(
            @PathVariable String appId,
            @PathVariable String deviceId,
            @PathVariable String controlId,
            @RequestParam(name = "searchTerm", required = false) String searchTerm,
            @RequestParam(name = "sizeLimit", defaultValue = "1000") Integer sizeLimit) {
        logger.info("Received a request to load component values for {} {} {}", appId, deviceId, controlId);
        String result = getComponentValues(appId, deviceId, controlId, getLastScreen(appId, deviceId), searchTerm, sizeLimit);
        if (result == null) {
            result = getComponentValues(appId, deviceId, controlId, getLastDialog(appId, deviceId), searchTerm, sizeLimit);
        }
        if (result == null) {
            result = "[]";
        }
        return result;
    }

    private String getComponentValues(String appId, String deviceId, String controlId, Screen screen, String searchTerm, Integer sizeLimit) {
        String result = null;
        if (screen instanceof IHasForm) {
            IHasForm dynamicScreen = (IHasForm) screen;
            IFormElement formElement = dynamicScreen.getForm().getFormElement(controlId);

            List<String> valueList = null;
            if (formElement instanceof IDynamicListField) {
                valueList = ((IDynamicListField) formElement).searchValues(searchTerm, sizeLimit);
            }

            if (valueList != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    mapper.writeValue(out, valueList);
                } catch (IOException e) {
                    throw new RuntimeException("Error while serializing the component values.", e);
                }
                result = new String(out.toByteArray());
                logger.info("Responding to request to load component values {} {} {} with {} values", appId, deviceId, controlId,
                        valueList.size());
            } else {
                logger.info("Unable to find the valueList for the requested component {} {} {}.", appId, deviceId, controlId);
            }
        }
        return result;
    }

    @Override
    public Collection<String> getRegisteredTypes() {
        return Arrays.asList(new String[] { "Screen", "KeepAlive" });
    }

    @Override
    public void actionOccured(String appId, String deviceId, Action action) {
        IStateManager stateManager = stateManagerFactory.retrieve(appId, deviceId);
        if (stateManager != null) {
            if (SessionTimer.ACTION_KEEP_ALIVE.equals(action.getName())) {
                stateManager.keepAlive();
            } else if( "Refresh".equals(action.getName())) {
            	Screen lastDialog = getLastDialog(appId, deviceId);
            	logger.info("Received Refresh action from {}", deviceId);
            	showScreen(appId, deviceId, getLastScreen(appId, deviceId));
            	showScreen(appId, deviceId, lastDialog);
            } else {
         
                deserializeForm(stateManager.getApplicationState(), action);

                logger.info("Received action from {}\n{}", deviceId, logFormatter.toJsonString(action));

                try {
                    logger.debug("Posting action {}", action);
                    stateManager.doAction(action);
                } catch (Throwable ex) {
                    logger.error(String.format("Unexpected exception while processing action from %s: %s", deviceId, action), ex);
                    messageService.sendMessage(appId, deviceId, Toast.createWarningToast("The application received an unexpected error. Please report to the appropriate technical personnel"));
                }
            }
        }
    }

    protected Screen removeLastDialog(String appId, String deviceId) {
        IStateManager stateManager = stateManagerFactory.retrieve(appId, deviceId);
        ApplicationState applicationState = stateManager.getApplicationState();
        if (applicationState != null && applicationState.getLastDialog() != null) {
            Screen lastDialog = applicationState.getLastDialog();
            applicationState.setLastDialog(null);
            return lastDialog;
        } else {
            return null;
        }
    }

    @Override
    public Screen getLastDialog(String appId, String deviceId) {
        IStateManager stateManager = stateManagerFactory.retrieve(appId, deviceId);
        ApplicationState applicationState = stateManager.getApplicationState();
        if (applicationState != null) {
            return applicationState.getLastDialog();
        } else {
            return null;
        }
    }

    @Override
    public Screen getLastScreen(String appId, String deviceId) {
        IStateManager stateManager = stateManagerFactory.retrieve(appId, deviceId);
        ApplicationState applicationState = stateManager.getApplicationState();
        if (applicationState != null) {
            return applicationState.getLastScreen();
        } else {
            return null;
        }
    }
    
    @Override
    public void showToast(String appId, String nodeId, Toast toast) {
        messageService.sendMessage(appId, nodeId, toast);
    }

    @Override
    public void showScreen(String appId, String deviceId, Screen screen) {
        IStateManager stateManager = stateManagerFactory.retrieve(appId, deviceId);
        if (screen != null && stateManager != null) {
            ApplicationState applicationState = stateManager.getApplicationState();
            screen.setSequenceNumber(applicationState.incrementAndScreenSequenceNumber());
            try {
                screen = interceptScreen(appId, deviceId, screen);
                logScreenTransition(deviceId, screen);
            } catch (Exception ex) {
                if (ex.toString().contains("org.jumpmind.pos.core.screen.ChangeScreen")) {
                    logger.error(
                            "Failed to write screen to JSON. Verify the screen type has been configured by calling setType() on the screen object.",
                            ex);
                } else {
                    logger.error("Failed to write screen to JSON", ex);
                }
            }
            if (!stateManager.areAllSessionsAuthenticated()) {
                logger.warn("Not sending screen because a session is attached that is not authenticated");
            } else if (!stateManager.areAllSessionsCompatible()) {
                logger.warn("Not sending screen because a session is attached that is not compatible");

            } else {
                messageService.sendMessage(appId, deviceId, screen);
            }
            if (screen.isDialog()) {
                applicationState.setLastDialog(screen);
            } else if (!screen.getScreenType().equals("NoOp")) {
                applicationState.setLastScreen(screen);
                applicationState.setLastDialog(null);
            }
        }
    }

    protected Screen interceptScreen(String appId, String deviceId, Screen screen) {
        if (this.screenInterceptors != null) {
            for (IScreenInterceptor screenInterceptor : screenInterceptors) {
                return screenInterceptor.intercept(appId, deviceId, screen);
            }
        }
        return screen;
    }

    protected void deserializeForm(ApplicationState applicationState, Action action) {
        if (hasForm(applicationState)) {
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

    protected boolean hasForm(ApplicationState applicationState) {
        if (applicationState.getLastDialog() != null) {
            return applicationState.getLastDialog() instanceof IHasForm;
        } else {
            return applicationState.getLastScreen() instanceof IHasForm;
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

    protected void logScreenTransition(String deviceId, Screen screen) throws JsonProcessingException {
        if (loggerGraphical.isInfoEnabled()) {
            logger.info("Show screen on node \"" + deviceId + "\"\n" + drawBox(screen.getName(), screen.getScreenType()));
        } else {
            logger.info("Show screen on node \"" + deviceId + "\"\n");
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
                    buff.append(drawTop1(boxWidth + 2));
                    break;
                case 1:
                    buff.append(drawTop2(boxWidth));
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
                    buff.append(drawBottom2(boxWidth + 2));
                    break;
            }
        }
        return buff.toString();
    }

    @Override
    public void addScreenInterceptor(IScreenInterceptor interceptor) {
        this.screenInterceptors.add(interceptor);
    }

    @Override
    public void removeScreenInterceptor(IScreenInterceptor interceptor) {
        this.screenInterceptors.remove(interceptor);
    }

    protected String drawTop1(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 2)).append(UPPER_RIGHT_CORNER);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTop2(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + UPPER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 4))
                .append(UPPER_RIGHT_CORNER + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawFillerLine(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + VERITCAL_LINE).append(StringUtils.repeat(' ', boxWidth - 4))
                .append(VERITCAL_LINE + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTitleLine(int boxWidth, String name) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + VERITCAL_LINE).append(StringUtils.center(name, boxWidth - 4))
                .append(VERITCAL_LINE + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawTypeLine(int boxWidth, String typeName) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + VERITCAL_LINE).append(StringUtils.center(typeName, boxWidth - 4))
                .append(VERITCAL_LINE + " " + VERITCAL_LINE);
        buff.append("\r\n");
        return buff.toString();
    }

    protected String drawBottom1(int boxWidth) {
        StringBuilder buff = new StringBuilder();
        buff.append(VERITCAL_LINE + " " + LOWER_LEFT_CORNER).append(StringUtils.repeat(HORIZONTAL_LINE, boxWidth - 4))
                .append(LOWER_RIGHT_CORNER + " " + VERITCAL_LINE);
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
