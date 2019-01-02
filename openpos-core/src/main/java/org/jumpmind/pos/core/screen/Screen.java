package org.jumpmind.pos.core.screen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.core.template.AbstractTemplate;
import org.jumpmind.pos.core.template.BlankWithBarTemplate;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.util.model.Message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Screen extends Message {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String screenType;
    private String icon;
    private AbstractTemplate template = new BlankWithBarTemplate();
    private String locale;
    private int sessionTimeoutMillis;
    private Action sessionTimeoutAction;
    private Map<String, String> trainingInstructions;

    public Screen() {
        this.setType(MessageType.Screen);
    }
    
    public Screen(String name, String screenType) {
        this(name, screenType, name);
    }

    public Screen(String name, String screenType, String id) {
        this();
        this.screenType = screenType;
        this.name = name;
        this.id = id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public boolean isDialog() {
        String type = getType();
        return type != null && type.equals(MessageType.Dialog);
    }

    /**
     * Allows this screen content to be displayed in a Dialog on the client
     * side.
     */
    public Screen asDialog() {
        return this.asDialog(null);
    }

    /**
     * Allows this screen content to be displayed in a Dialog on the client
     * side.
     * 
     * @param dialogProperties
     *            Additional properties that can control dialog behavior and
     *            rendering on the server side.
     */
    public Screen asDialog(DialogProperties dialogProperties) {
        this.setType(MessageType.Dialog);
        if (dialogProperties != null) {
            this.setDialogProperties(dialogProperties);
        }
        return this;
    }
    
    public void setDialogProperties(DialogProperties dialogProperties) {
        this.put("dialogProperties", dialogProperties);
    }

    // TODO i don't really like this method here
    public static <T> T convertActionData(Object actionData, Class<T> convertToInstanceOf) {
        return new ObjectMapper().convertValue(actionData, convertToInstanceOf);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setScreenType(String type) {
        this.screenType = type;
    }

    public String getScreenType() {
        return this.screenType;
    }

    public boolean isScreenOfType(String type) {
        return this.screenType == type;
    }

    public void setBackButton(MenuItem backButton) {
        put("backButton", backButton);
    }
    
    public void setLogoutButton(MenuItem logoutButton) {
        put("logoutButton", logoutButton);
    }

    public void setTemplate(AbstractTemplate template) {
        this.template = template;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractTemplate> T getTemplate() {
        return (T)template;
    }

    public void setSequenceNumber(int sequenceNumber) {
        put("sequenceNumber", sequenceNumber);
    }

    public void setTheme(String theme) {
        this.put("theme", theme);
    }

    public void setReadOnly(boolean isReadOnly) {
        this.put("readOnly", isReadOnly);
    }

    public void setUseOnScreenKeyboard(boolean useOnScreenKeyboard) {
        this.put("useOnScreenKeyboard", useOnScreenKeyboard);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setRefreshAlways(boolean refreshAlways) {
        this.put("refreshAlways", refreshAlways);
    }

    public int getSessionTimeoutMillis() {
        return sessionTimeoutMillis;
    }

    public void setSessionTimeoutMillis(int sessionTimeoutMillis) {
        this.sessionTimeoutMillis = sessionTimeoutMillis;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setPrompt(String prompt) {
        this.put("prompt", prompt);
    }
    
    public void setSubtitle(String ... list) {
        this.put("subtitle", Arrays.asList(list));
    }
    
    @JsonIgnore
    public void setSubtitle(List<String> list) {
    	this.put("subtitle", list);
    }
    
    /**
     * Indicator for marking the screen as "Customer Facing", meaning that the screen is intended for the customer to 
     * complete.
     * @param customerFacing <code>true</code> if the customer should use the screen.
     */
    public void setCustomerFacing(Boolean customerFacing) {
        put("customerFacing", customerFacing);
    }        
    
    public void setInstructions(String instructions) {
        this.put("instructions", instructions);
    }

    public Action getSessionTimeoutAction() {
        return sessionTimeoutAction;
    }

    public void setSessionTimeoutAction(Action sessionTimeoutAction) {
        this.sessionTimeoutAction = sessionTimeoutAction;
    }

    public Map<String, String> getTrainingInstructions() {
        return trainingInstructions;
    }

    public void setTrainingInstructions(Map<String, String> trainingInstructions) {
        this.trainingInstructions = trainingInstructions;
    }
    
    public void addTrainingInstuctions(String key, String instructions) {
        if (this.trainingInstructions == null) {
            this.trainingInstructions = new HashMap<String, String>();
        }
        this.trainingInstructions.put(key, instructions);
    }
    
}
