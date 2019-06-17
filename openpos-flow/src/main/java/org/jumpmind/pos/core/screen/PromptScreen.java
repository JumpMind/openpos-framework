package org.jumpmind.pos.core.screen;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.model.KeyboardType;
import org.jumpmind.pos.core.model.Validator;

public class PromptScreen extends Screen implements IPromptScreen {

    private static final long serialVersionUID = 1L;
    
    private String promptIcon;
    private String placeholderText;
    private String text;
    private String responseText;
    private boolean editable = true;
    private String responseType;
    private Integer minLength;
    private Integer maxLength;
    private String action = "Next";
    private ActionItem actionButton = null;
    private String comments = "";
    private boolean showComments = false;
    private List<ActionItem> otherActions;
    private String pattern;
    private Boolean scanEnabled;
    

    public PromptScreen() {
        setScreenType(ScreenType.Prompt);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getResponseText() {
        return responseText;
    }

    @Override
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(FieldInputType responseType) {
        this.responseType = responseType.name();
    }
    
    @Override
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    @Override
    public Integer getMinLength() {
        return minLength;
    }

    @Override
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    @Override
    public Integer getMaxLength() {
        return maxLength;
    }

    @Override
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public String getPromptIcon() {
        return promptIcon;
    }

    @Override
    public void setPromptIcon(String promptIcon) {
        this.promptIcon = promptIcon;
    }

    @Override
    public String getPlaceholderText() {
        return placeholderText;
    }

    @Override
    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }

    public ActionItem getActionButton() {
        return actionButton;
    }

    public void setActionButton(ActionItem actionButton) {
        this.actionButton = actionButton;
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
	
	public List<ActionItem> getOtherActions() {
        return otherActions;
    }
	
	public void setOtherActions(List<ActionItem> otherActions) {
        this.otherActions = otherActions;
    }
	
	public void addOtherAction(ActionItem action) {
	    if (this.otherActions == null) {
	        this.otherActions = new ArrayList<>();
	    }
	    this.otherActions.add(action);
	}

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public String getPattern() {
        return this.pattern;
    }

    public void setKeyboardPreference(KeyboardType keyboardPreference) {
        this.put("keyboardPreference", keyboardPreference);
    }

    public Boolean getScanEnabled() {
        return scanEnabled;
    }

    public void setScanEnabled(Boolean scanEnabled) {
        this.scanEnabled = scanEnabled;
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
    
}
