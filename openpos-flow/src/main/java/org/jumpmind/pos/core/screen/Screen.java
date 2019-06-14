package org.jumpmind.pos.core.screen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.screenpart.BaconStripPart;
import org.jumpmind.pos.core.template.AbstractTemplate;
import org.jumpmind.pos.core.template.BlankWithBarTemplate;
import org.jumpmind.pos.core.ui.IHasBackButton;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.util.DefaultObjectMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Deprecated
public class Screen extends UIMessage implements IHasBackButton {

    private static final long serialVersionUID = 1L;

    /**
     * This was originally meant to be the title of a screen, but moving forward
     * we should use screen parts and set the title there (see {@link BaconStripPart}.
     */
    @Deprecated
    private String name;
    /**
     * This was originally meant to be an icon beside the title of a screen, but moving forward
     * we should use screen parts and set the icon there.
     */
    @Deprecated
    private String icon;

    @Deprecated
    private AbstractTemplate template = new BlankWithBarTemplate();


    private Map<String, String> trainingInstructions;

    public Screen(String name, String screenType) {
        this(name, screenType, name);
    }

    public Screen(String name, String screenType, String id) {
        super( screenType, id );
        this.name = name;
    }

    public Screen(){
        super();
    }

    // TODO i don't really like this method here
    public static <T> T convertActionData(Object actionData, Class<T> convertToInstanceOf) {
        return DefaultObjectMapper.build().convertValue(actionData, convertToInstanceOf);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBackButton(ActionItem backButton) {
        put("backButton", backButton);
    }

    public void setTemplate(AbstractTemplate template) {
        this.template = template;
    }

    public void setLogoutButton(ActionItem logoutButton) {
        put("logoutButton", logoutButton);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractTemplate> T getTemplate() {
        return (T) template;
    }


    @Deprecated
    public void setTheme(String theme) {
        this.put("theme", theme);
    }

    public void setReadOnly(boolean isReadOnly) {
        this.put("readOnly", isReadOnly);
    }

    public void setUseOnScreenKeyboard(boolean useOnScreenKeyboard) {
        this.put("useOnScreenKeyboard", useOnScreenKeyboard);
    }


    public void setRefreshAlways(boolean refreshAlways) {
        this.put("refreshAlways", refreshAlways);
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

    public void setSubtitle(String... list) {
        this.put("subtitle", Arrays.asList(list));
    }

    @JsonIgnore
    public void setSubtitle(List<String> list) {
        this.put("subtitle", list);
    }

    /**
     * Indicator for marking the screen as "Customer Facing", meaning that the
     * screen is intended for the customer to complete.
     * 
     * @param customerFacing
     *            <code>true</code> if the customer should use the screen.
     */
    public void setCustomerFacing(Boolean customerFacing) {
        put("customerFacing", customerFacing);
    }

    public void setInstructions(String instructions) {
        this.put("instructions", instructions);
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
