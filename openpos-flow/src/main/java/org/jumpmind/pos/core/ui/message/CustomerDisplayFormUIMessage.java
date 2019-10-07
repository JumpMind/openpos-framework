package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.ui.*;
import org.jumpmind.pos.core.ui.messagepart.ProgressBarPart;

import java.util.ArrayList;
import java.util.List;

public class CustomerDisplayFormUIMessage extends UIMessage implements IHasForm {

    private static final long serialVersionUID = 1L;

    private Form form = new Form();

    private ActionItem submitButton;

    private List<String> alternateSubmitActions = new ArrayList<String>();

    private String imageUrl;

    private ProgressBarPart progressBar;

    public CustomerDisplayFormUIMessage() {
        setScreenType(UIMessageType.CUSTOMER_DISPLAY_FORM);
        ActionItem submitButton = new ActionItem("Next", "Next", IconType.Forward);
        submitButton.setKeybind(KeyConstants.KEY_ENTER);
        setSubmitButton(submitButton);
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public ActionItem getSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(ActionItem submitButton) {
        this.submitButton = submitButton;
    }

    public List<String> getAlternateSubmitActions() {
        return alternateSubmitActions;
    }

    public void setAlternateSubmitActions(List<String> alternateSubmitActions) {
        this.alternateSubmitActions = alternateSubmitActions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ProgressBarPart getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBarPart progressBar) {
        this.progressBar = progressBar;
    }

}
