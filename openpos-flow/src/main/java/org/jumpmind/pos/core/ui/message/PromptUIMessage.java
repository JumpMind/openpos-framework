package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.model.KeyboardType;
import org.jumpmind.pos.core.model.Validator;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

import java.math.BigDecimal;
import java.util.*;

public class PromptUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_ALPHANUMERICTEXT = "ALPHANUMERICTEXT";
    public static final String TYPE_ALPHANUMERICPASSWORD = "ALPHANUMERICPASSWORD";
    public static final String TYPE_PHONE = "PHONE";
    public static final String TYPE_CURRENCYTEXT = "CURRENCYTEXT";
    public static final String TYPE_NUMERICTEXT = "NUMERICTEXT";
    public static final String TYPE_DATE = "DATE";
    public static final String TYPE_NO_YEAR_DATE = "NOYEARDATE";
    public static final String TYPE_ONOFF = "ONOFF";
    public static final String TYPE_EMAIL = "EMAIL";
    public static final String TYPE_MONEY = "MONEY";

    private String promptIcon;
    private String placeholderText;
    private String hintText;
    private String instructions;
    private String responseText;
    private boolean editable = true;
    private FieldInputType responseType;
    private ActionItem actionButton = null;
    private List<ActionItem> otherActions;
    private Integer minLength;
    private Integer maxLength;
    private String comments = "";
    private boolean showComments = false;
    private List<String> validationPatterns;
    private boolean scanEnabled;
    private BigDecimal min;
    private BigDecimal max;
    private String imageUrl;
    private boolean autoFocus = true;

    public PromptUIMessage() {
        this.setScreenType(UIMessageType.PROMPT);
    }
    
    public void setMax(BigDecimal max) {
        this.max = max;
    }
    
    public BigDecimal getMax() {
        return max;
    }
    
    public void setMin(BigDecimal min) {
        this.min = min;
    }
    
    public BigDecimal getMin() {
        return min;
    }

    public String getPromptIcon() {
        return promptIcon;
    }

    public void setPromptIcon(String promptIcon) {
        this.promptIcon = promptIcon;
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getResponseText() {
        return  responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public FieldInputType getResponseType() {
        return responseType;
    }

    public void setResponseType(FieldInputType responseType) {
        this.responseType = responseType;
    }

    public ActionItem getActionButton() {
        return actionButton;
    }

    public void setActionButton(ActionItem actionButton) {
        this.actionButton = actionButton;
    }

    public List<ActionItem> getOtherActions() {
        return otherActions;
    }

    public void setOtherActions(List<ActionItem> otherActions) {
        this.otherActions = otherActions;
    }

    public boolean getAutoFocus() {
        return autoFocus;
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    public void addOtherAction(ActionItem action) {
        if (this.otherActions == null) {
            this.otherActions = new ArrayList<>();
        }
        this.otherActions.add(action);
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isShowComments() {
        return showComments;
    }

    public void setShowComments(boolean showComments) {
        this.showComments = showComments;
    }
    
    public void setValidationPatterns(List<String> validationPatterns) {
        this.validationPatterns = validationPatterns;
    }
    
    public List<String> getValidationPatterns() {
        return validationPatterns;
    }

    public void addValidationPattern(String pattern) {
        if (this.validationPatterns == null) {
            this.validationPatterns = new ArrayList<>();
        }
        this.validationPatterns.add(pattern);
    }

    public boolean isScanEnabled() {
        return scanEnabled;
    }

    public void setScanEnabled(boolean scanEnabled) {
        this.scanEnabled = scanEnabled;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setKeyboardPreference(KeyboardType keyboardPreference) {
        this.put("keyboardPreference", keyboardPreference);
    }

    public void setValidators(Set<Validator> validators) {
        this.put("validators", validators);
    }

    public void addValidators(Validator ...validators) {
        if (validators != null && validators.length > 0) {
            if (! this.contains("validators")) {
                this.put("validators", new HashSet<Validator>());
            }
            @SuppressWarnings("unchecked")
            Set<Validator> theValidators = (Set<Validator>) this.get("validators");
            theValidators.addAll(Arrays.asList(validators));
        }
    }

    public void setValidationMessages( Map<String,String> messages) {
        this.put("validationMessages", messages);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getValidationMessages() {
        if(any().containsKey("validationMessages")) {
            return (Map<String, String>) any().get("validationMessages");
        }
        return null;
    }

    public void addValidationMessage( String validatorName, String message) {
        if( getValidationMessages() == null) {
            setValidationMessages(new HashMap<String, String>());
        }
        getValidationMessages().put(validatorName, message);
    }
}
