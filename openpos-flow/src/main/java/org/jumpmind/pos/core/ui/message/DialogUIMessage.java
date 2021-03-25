package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.DialogProperties;
import org.jumpmind.pos.core.ui.data.Line;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.DialogHeaderPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;
import org.jumpmind.pos.core.ui.messagepart.PromptButtonRowPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;
    private String additionalStyleName;
    private List<String> message = new ArrayList<>();

    private List<Line> messageLines = new ArrayList<>();

    private PromptButtonRowPart promptButtonRow = new PromptButtonRowPart();

    public DialogUIMessage() {
        this(null);
    }

    public DialogUIMessage(DialogProperties dialogProperties) {
        setScreenType(UIMessageType.DIALOG);
        this.asDialog(dialogProperties);
    }

    public DialogUIMessage(String title, ActionItem button) {
        this();
        DialogHeaderPart dialogHeader = new DialogHeaderPart();
        dialogHeader.setHeaderText(title);
        addMessagePart(MessagePartConstants.DialogHeader, dialogHeader);
        this.addButton(button);
    }

    // Intentionally omitted getter for title so Jackson won't add title
    // attribute to serialized JSON
    public void setTitle(String title) {
        DialogHeaderPart dialogHeader = getDialogHeaderPart();
        dialogHeader.setHeaderText(title);
    }

    public void setIcon(String icon) {
        DialogHeaderPart dialogHeader = getDialogHeaderPart();
        dialogHeader.setHeaderIcon(icon);
    }


    public DialogHeaderPart getDialogHeaderPart() {
        DialogHeaderPart dialogHeader = (DialogHeaderPart) get(MessagePartConstants.DialogHeader);
        if (dialogHeader == null) {
            dialogHeader = new DialogHeaderPart();
            addMessagePart(MessagePartConstants.DialogHeader, dialogHeader);
        }
        return dialogHeader;
    }

    public void setButtons(List<ActionItem> buttons) {
        if (buttons != null && buttons.size() > 0) {
            promptButtonRow.setPrimaryButton(buttons.get(0));

            for (int b = 1; b < buttons.size(); b++) {
                promptButtonRow.addSecondaryButton(buttons.get(b));
            }
        }
    }

    public void addButton(ActionItem button) {
        if (promptButtonRow.getPrimaryButton() == null) {
            promptButtonRow.setPrimaryButton(button);
        } else {
            promptButtonRow.addSecondaryButton(button);
        }
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(String... messages) {
        this.setMessage(Arrays.asList(messages));
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public DialogUIMessage addMessage(String message) {
        this.message.add(message);
        return this;
    }

    public List<Line> getMessageLines() {
        return messageLines;
    }

    public void setMessageLines(List<Line> messageLines) {
        this.messageLines = messageLines;
    }

    public DialogProperties getDialogProperties() {
        return (DialogProperties) get("dialogProperties");
    }

    public DialogUIMessage addMessageLine(Line line) {
        this.messageLines.add(line);
        return this;
    }

    public PromptButtonRowPart getPromptButtonRow() {
        return promptButtonRow;
    }

    public void setPromptButtonRow(PromptButtonRowPart promptButtonRow) {
        this.promptButtonRow = promptButtonRow;
    }

    public String getAdditionalStyleName() {
        return additionalStyleName;
    }

    public void setAdditionalStyleName(String additionalStyleName) {
        this.additionalStyleName = additionalStyleName;
    }
}
