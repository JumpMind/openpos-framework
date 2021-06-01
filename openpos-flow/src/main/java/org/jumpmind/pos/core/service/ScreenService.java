package org.jumpmind.pos.core.service;

import static org.jumpmind.pos.util.BoxLogging.HORIZONTAL_LINE;
import static org.jumpmind.pos.util.BoxLogging.LOWER_LEFT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.LOWER_RIGHT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.UPPER_LEFT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.UPPER_RIGHT_CORNER;
import static org.jumpmind.pos.util.BoxLogging.VERITCAL_LINE;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.content.ContentProviderService;
import org.jumpmind.pos.core.error.IErrorHandler;
import org.jumpmind.pos.core.flow.ApplicationState;
import org.jumpmind.pos.core.flow.FlowException;
import org.jumpmind.pos.core.flow.IMessageInterceptor;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.jumpmind.pos.core.flow.SessionTimer;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.IDynamicListField;
import org.jumpmind.pos.core.model.IFormElement;
import org.jumpmind.pos.core.ui.*;
import org.jumpmind.pos.core.ui.data.UIDataMessageProvider;
import org.jumpmind.pos.core.util.LogFormatter;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IActionListener;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.jumpmind.pos.util.web.MimeTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    ObjectMapper mapper = DefaultObjectMapper.defaultObjectMapper();

    @Autowired
    IStateManagerContainer stateManagerContainer;

    @Value("${openpos.screens.jsonIncludeNulls:true}")
    boolean jsonIncludeNulls = true;

    @Value("${openpos.ui.content.maxage:null}")
    String contentMaxAge;

    @Autowired
    LogFormatter logFormatter;

    @Autowired
    IMessageService messageService;

    @Autowired
    UIDataMessageProviderService uiDataMessageProviderService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    IErrorHandler errorHandler;

    @PostConstruct
    public void init() {
        if (!jsonIncludeNulls) {
            mapper.setSerializationInclusion(Include.NON_NULL);
        }
    }
    
    @SuppressWarnings("deprecation")
    @RequestMapping(method = RequestMethod.GET, value = "api/appId/{appId}/deviceId/{deviceId}/content")
    public void getImageAsByteArray(
            HttpServletResponse response,
            @PathVariable String appId,
            @PathVariable String deviceId,
            @RequestParam(name = "contentPath") String contentPath,
            @RequestParam(name = "provider") String provider) throws IOException {

        logger.debug("Received a request for content: {}", contentPath);

        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        if (stateManager != null) {
            String contentType = MimeTypeUtil.getContentType(contentPath);
            response.setContentType(contentType);

            stateManagerContainer.setCurrentStateManager(stateManager);

            if(MimeTypeUtil.isContentTypeAudio(contentType)) {
                // Required by browsers to allow starting audio at arbitrary time
                response.setHeader("Accept-Ranges", "bytes");
            }

            if(StringUtils.isNotEmpty(contentMaxAge)){
                response.setHeader("Cache-Control", "max-age=" + contentMaxAge);
            }

            ContentProviderService contentProviderService = applicationContext.getBean(ContentProviderService.class);
            InputStream in = contentProviderService.getContentInputStream(contentPath, provider);
            ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream();

            if (in != null) {
                try {
                    int byteCount = IOUtils.copy(in, tempOutputStream);

                    if(byteCount > -1) {
                        // Required by browsers to allow starting audio at arbitrary time
                        response.setContentLength(byteCount);
                    }

                    tempOutputStream.writeTo(response.getOutputStream());
                } finally {
                    IOUtils.closeQuietly(in);
                }
            } else {
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
            stateManagerContainer.setCurrentStateManager(null);
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
        String result = getComponentValues(appId, deviceId, controlId, getLastScreen(deviceId), searchTerm, sizeLimit);
        if (result == null) {
            result = getComponentValues(appId, deviceId, controlId, getLastDialog(deviceId), searchTerm, sizeLimit);
        }
        if (result == null) {
            result = "[]";
        }
        return result;
    }

    private String getComponentValues(
            String appId,
            String deviceId,
            String controlId,
            UIMessage screen,
            String searchTerm,
            Integer sizeLimit) {
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
    public void actionOccurred(String deviceId, Action action) {
        logger.trace("actionOccurred -> deviceId: {}, action: {}", deviceId, action != null ? action.getName() : null);
        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        if (stateManager != null) {
            try {
                String appId = stateManager.getAppId();
                stateManagerContainer.setCurrentStateManager(stateManager);
                if (SessionTimer.ACTION_KEEP_ALIVE.equals(action.getName())) {
                    stateManager.keepAlive();
                } else if ("Refresh".equals(action.getName())) {
                    UIMessage lastDialog = getLastDialog(deviceId);
                    logger.info("Received Refresh action from {}", deviceId);
                    showScreen(deviceId, getLastScreen(deviceId));
                    showScreen(deviceId, lastDialog);
                } else if ( uiDataMessageProviderService.handleAction(action, stateManager.getApplicationState())){
                    logger.info("Action handled by UIMessageDataProvider from {}\n{}", deviceId, logFormatter.toJsonString(action));
                } else {

                    deserializeForm(stateManager.getApplicationState(), action);

                    if (logger.isInfoEnabled()) {
                        logger.info("Received action from {}\n{}", deviceId, logFormatter.toJsonString(action));
                    }

                    try {
                        logger.debug("Posting action {}", action);
                        stateManager.doAction(action);
                    } catch (Throwable ex) {
                        if( errorHandler != null){
                            errorHandler.handleError(stateManager, ex);
                        } else {
                            logger.error(String.format("Unexpected exception while processing action from %s: %s", deviceId, action), ex);
                            messageService.sendMessage(deviceId, Toast.createWarningToast(
                                    "The application received an unexpected error. Please report to the appropriate technical personnel"));
                        }
                    }
                }
            } finally {
                stateManagerContainer.setCurrentStateManager(null);
            }
        }
    }

    protected UIMessage removeLastDialog(String deviceId) {
        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        ApplicationState applicationState = stateManager != null ? stateManager.getApplicationState() : null;
        if (applicationState != null && applicationState.getLastDialog() != null) {
            UIMessage lastDialog = applicationState.getLastDialog();
            applicationState.setLastDialog(null);
            return lastDialog;
        } else {
            return null;
        }
    }

    @Override
    public UIMessage getLastDialog(String deviceId) {
        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        ApplicationState applicationState = stateManager != null ? stateManager.getApplicationState() : null;
        if (applicationState != null) {
            return applicationState.getLastDialog();
        } else {
            return null;
        }
    }

    @Override
    public UIMessage getLastScreen(String deviceId) {
        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        ApplicationState applicationState = stateManager != null ? stateManager.getApplicationState() : null;
        if (applicationState != null) {
            return applicationState.getLastScreen();
        } else {
            return null;
        }
    }
    
    @Override
    public UIMessage getLastPreInterceptedScreen(String deviceId) {
        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        ApplicationState applicationState = stateManager != null ? stateManager.getApplicationState() : null;
        if (applicationState != null) {
            return applicationState.getLastPreInterceptedScreen();
        } else {
            return null;
        }
    }
    
    @Override
    public UIMessage getLastPreInterceptedDialog(String deviceId) {
        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        ApplicationState applicationState = stateManager != null ? stateManager.getApplicationState() : null;
        if (applicationState != null) {
            return applicationState.getLastPreInterceptedDialog();
        } else {
            return null;
        }
    }

    @Override
    public void showToast(String deviceId, Toast toast) {
        interceptToast(deviceId, toast);
        messageService.sendMessage(deviceId, toast);
    }

    @Override
    public void closeToast(String deviceId, CloseToast toast) {
        interceptCloseToast(deviceId, toast);
        messageService.sendMessage(deviceId, toast);
    }

    @Override
    public void showScreen(String deviceId, UIMessage screen) {
        showScreen(deviceId, screen, null);
    }

    @Override
    public void showScreen(String deviceId, UIMessage screen, Map<String, UIDataMessageProvider<?>> uiDataMessageProviders) {
        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        if (screen != null && stateManager != null) {
            ApplicationState applicationState = stateManager.getApplicationState();
            screen.setSequenceNumber(applicationState.incrementAndScreenSequenceNumber());

            UIMessage preInterceptedScreen = null;
            try {
                preInterceptedScreen = SerializationUtils.clone(screen);
                interceptScreen(deviceId, screen);
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
                uiDataMessageProviderService.updateProviders(applicationState, uiDataMessageProviders);
                messageService.sendMessage(deviceId, screen);
            }

            if (screen.isDialog()) {
                applicationState.setLastDialog(screen);
                applicationState.setLastPreInterceptedDialog(preInterceptedScreen);
            } else if (!screen.getScreenType().equals("NoOp")) {
                applicationState.setLastScreen(screen);
                applicationState.setLastPreInterceptedScreen(preInterceptedScreen);
                applicationState.setLastDialog(null);
                applicationState.setLastPreInterceptedDialog(null);
            }
        }
    }

    protected void interceptToast(String deviceId, Toast toast) {
        String[] toastInterceptorBeanNames = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(IMessageInterceptor.class, Toast.class));

        if (toastInterceptorBeanNames != null) {
            for (String beanName: toastInterceptorBeanNames) {
                @SuppressWarnings("unchecked")
                IMessageInterceptor<Toast> toastInterceptor =  (IMessageInterceptor<Toast>) applicationContext.getBean(beanName);
                toastInterceptor.intercept(deviceId, toast);
            }
        }
    }

    protected void interceptCloseToast(String deviceId, CloseToast closeToast) {
        String[] closeToastInterceptorBeanNames = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(IMessageInterceptor.class, CloseToast.class));

        for (String beanName: closeToastInterceptorBeanNames) {
            @SuppressWarnings("unchecked")
            IMessageInterceptor<CloseToast> toastInterceptor =  (IMessageInterceptor<CloseToast>) applicationContext.getBean(beanName);
            toastInterceptor.intercept(deviceId, closeToast);
        }
    }
    
    protected void interceptScreen(String deviceId, UIMessage screen) {
        String[] screenInterceptorBeanNames = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(IMessageInterceptor.class, UIMessage.class));

        if (screenInterceptorBeanNames != null) {
            for (String beanName: screenInterceptorBeanNames) {
                @SuppressWarnings("unchecked")
                IMessageInterceptor<UIMessage> screenInterceptor =  (IMessageInterceptor<UIMessage>) applicationContext.getBean(beanName);
                screenInterceptor.intercept(deviceId, screen);
                
            }
        }
    }

    protected void deserializeForm(ApplicationState applicationState, Action action) {
        if (hasForm(applicationState)) {
            try {
                Form form = mapper.convertValue(action.getData(), Form.class);
                
                if (form != null) {
                    // Sometimes Jackson convertValue method will produce an empty 
                    // Form object even if the given action data doesn't even resemble a form!
                    if (Form.isAssignableFrom(action.getData())) {
                        action.setData(form);
                    } else {
                        logger.trace("Given action data is not actually a form, is instance of {}", 
                            action.getData() != null ? action.getData().getClass().getName() : "?");
                    }
                }
            } catch (IllegalArgumentException ex) {
                logger.debug(ex.getMessage(), ex);
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

    protected void logScreenTransition(String deviceId, UIMessage screen) throws JsonProcessingException {
        if (loggerGraphical.isInfoEnabled()) {
            logger.info("Show screen on device \"" + deviceId + "\" (" + screen.getClass().getName() + ")\n"
                    + drawBox(screen.getId(), screen.getScreenType()));
        } else {
            logger.info("Show screen on device \"" + deviceId + "\"(\" + screen.getClass().getName() + \")\n");
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

        int boxWidth = Math.max(Math.max(displayName.length() + 2, 50), displayTypeName.length() + 4);
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
