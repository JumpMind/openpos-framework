package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.core.template.AbstractTemplate;
import org.jumpmind.pos.core.template.BlankWithBarTemplate;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Screen implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Put properties in this map if they are optional. When not set, they don't
     * show up in the json which means less overhead.
     */
    private Map<String, Object> optionalProperties = new HashMap<String, Object>();

    private String name;
    private String type;
    private String icon;
    private AbstractTemplate template = new BlankWithBarTemplate();
    private String locale;
    private int sessionTimeoutMillis;

    public Screen() {
    }

    public Screen(String name, String type) {
        this.type = type;
        this.name = name;
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
        this.template.setDialog(true);
        if (dialogProperties != null) {
            this.template.setDialogProperties(dialogProperties);
        }
        return this;
    }

    // TODO i don't really like this method here
    public static <T> T convertActionData(Object actionData, Class<T> convertToInstanceOf) {
        return new ObjectMapper().convertValue(actionData, convertToInstanceOf);
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.optionalProperties;
    }

    @JsonAnySetter
    public void put(String name, Object value) {
        this.optionalProperties.put(name, value);
    }

    public boolean contains(String name) {
        return this.optionalProperties.containsKey(name);
    }

    public Object get(String name) {
        return optionalProperties.get(name);
    }

    public void clearAdditionalProperties() {
        this.optionalProperties.clear();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public boolean isScreenOfType(String type) {
        return this.type == type;
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
        this.optionalProperties.put("theme", theme);
    }

    public void setReadOnly(boolean isReadOnly) {
        this.optionalProperties.put("readOnly", isReadOnly);
    }

    public void setUseOnScreenKeyboard(boolean useOnScreenKeyboard) {
        this.optionalProperties.put("useOnScreenKeyboard", useOnScreenKeyboard);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setRefreshAlways(boolean refreshAlways) {
        this.optionalProperties.put("refreshAlways", refreshAlways);
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
    


}
