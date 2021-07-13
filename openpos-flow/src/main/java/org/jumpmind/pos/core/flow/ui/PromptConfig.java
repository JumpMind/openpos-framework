package org.jumpmind.pos.core.flow.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.DialogProperties;

public class PromptConfig {

    private String name;
    private String promptText;
    private String backAction = "Back";
    private FieldInputType promptType = FieldInputType.AlphanumericText;
    private String icon;
    private String placeholder;
    private String responseText;
    private List<String> validationPatterns;
    private Map<String, String> validationMessages;
    private BigDecimal min;
    private BigDecimal max;
    private DialogProperties dialogProperties;
    private ActionItem actionMenuItem = new ActionItem("Next", "Next", true);
    private List<ActionItem> otherActions = new ArrayList<>();
    private String id;
    private boolean allowScan;
    private boolean editable = true;
    private Integer maxLength;
    private Integer minLength;


    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public PromptConfig named(String name) {
        this.name = name;
        return this;
    }
    
    public void setMax(BigDecimal max) {
        this.max = max;
    }
    
    public BigDecimal getMax() {
        return max;
    }
    
    public PromptConfig max(BigDecimal max) {
        this.max = max;
        return this;
    }
    
    public void setMin(BigDecimal min) {
        this.min = min;
    }
    
    public BigDecimal getMin() {
        return min;
    }
    
    public PromptConfig min(BigDecimal min) {
        this.min = min;
        return this;
    }
    
    public List<String> getValidationPatterns() {
        return validationPatterns;
    }
    
    public PromptConfig validationPattern(String expression) {
        if (this.validationPatterns == null) {
            this.validationPatterns = new ArrayList<>();
        }
        this.validationPatterns.add(expression);
        return this;
    }
    
    public PromptConfig validationPatterns(List<String> patterns) {
        if (this.validationPatterns == null) {
            this.validationPatterns = new ArrayList<>();
        }
        this.validationPatterns.addAll(patterns);
        return this;
    }

    public Map<String, String> getValidationMessages() {
        return validationMessages;
    }

    public PromptConfig validationMessage(String validatorName, String message) {
        if(this.validationMessages == null) {
            this.validationMessages = new HashMap<>();
        }
        this.validationMessages.put(validatorName, message);
        return this;
    }

    public PromptConfig validationMessages(Map<String, String> validationMessages) {
        if(this.validationMessages == null) {
            this.validationMessages = new HashMap<>();
        }
        this.validationMessages.putAll(validationMessages);
        return this;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseText() {
        return responseText;
    }

    public PromptConfig minLength(Integer minLength) {
        this.minLength = minLength;
        return this;
    }

    public PromptConfig maxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public PromptConfig responseText(String responseText) {
        this.responseText = responseText;
        return this;
    }

    public PromptConfig promptText(String promptText) {
        this.promptText = promptText;
        return this;
    }

    public PromptConfig backAction(String backAction) {
        this.backAction = backAction;
        return this;
    }

    public PromptConfig icon(String icon) {
        this.icon = icon;
        return this;
    }

    public PromptConfig placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public PromptConfig dialogProperties(DialogProperties dialogProperties) {
        this.dialogProperties = dialogProperties;
        return this;
    }

    public PromptConfig promptType(FieldInputType promptType) {
        this.promptType = promptType;
        return this;
    }

    public PromptConfig action(String title, String actionName) {
        this.actionMenuItem = new ActionItem(title, actionName, true);
        return this;
    }

    public PromptConfig action(String title, String actionName, boolean sensitive) {
        this.actionMenuItem = new ActionItem(title, actionName, true, sensitive);
        return this;
    }

    public PromptConfig actionMenuItem(ActionItem actionMenuItem) {
        this.actionMenuItem = actionMenuItem;
        return this;
    }

    public PromptConfig asDialog() {
        DialogProperties props = new DialogProperties();
        props.setMinWidth("50%");
        props.setRestoreFocus(false);
        return dialogProperties(props);
    }

    public PromptConfig addOtherAction(ActionItem option) {
        otherActions.add(option);
        return this;
    }

    public PromptConfig allowScan(boolean allow){
        this.allowScan = allow;
        return this;
    }

    public boolean getAllowScan() {
        return this.allowScan;
    }

    public String getName() {
        return name;
    }

    public String getPromptText() {
        return promptText;
    }

    public String getBackAction() {
        return backAction;
    }

    public FieldInputType getPromptType() {
        return promptType;
    }

    public String getIcon() {
        return icon;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public DialogProperties getDialogProperties() {
        return dialogProperties;
    }

    public ActionItem getActionMenuItem() {
        return actionMenuItem;
    }

    public List<ActionItem> getOtherActions() {
        return otherActions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PromptConfig id(String id) {
        this.setId(id);
        return this;
    }

    public PromptConfig editable(boolean editable) {
        setEditable(editable);
        return this;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return this.editable;
    }
}
