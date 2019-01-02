package org.jumpmind.pos.core.flow.ui;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.screen.DialogProperties;
import org.jumpmind.pos.core.screen.IPromptScreen;
import org.jumpmind.pos.core.screen.MenuItem;

public class PromptConfig {

    private String name;
    private String promptText;
    private String backAction = "Back";
    private String promptType = IPromptScreen.TYPE_ALPHANUMERICTEXT;
    private String icon;
    private String placeholder;
    private DialogProperties dialogProperties;
    private MenuItem actionMenuItem = new MenuItem("Next", "Next", true);
    private List<MenuItem> otherActions = new ArrayList<>();

    public PromptConfig named(String name) {
        this.name = name;
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

    public PromptConfig promptType(String promptType) {
        this.promptType = promptType;
        return this;
    }

    public PromptConfig action(String title, String actionName) {
        this.actionMenuItem = new MenuItem(title, actionName, true);
        return this;
    }

    public PromptConfig action(String title, String actionName, boolean sensitive) {
        this.actionMenuItem = new MenuItem(title, actionName, true, sensitive);
        return this;
    }

    public PromptConfig actionMenuItem(MenuItem actionMenuItem) {
        this.actionMenuItem = actionMenuItem;
        return this;
    }

    public PromptConfig addOtherAction(MenuItem option) {
        otherActions.add(option);
        return this;
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

    public String getPromptType() {
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

    public MenuItem getActionMenuItem() {
        return actionMenuItem;
    }

    public List<MenuItem> getOtherActions() {
        return otherActions;
    }

}
