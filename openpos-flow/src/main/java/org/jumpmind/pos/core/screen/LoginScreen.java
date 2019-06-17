package org.jumpmind.pos.core.screen;

import org.jumpmind.pos.core.model.FieldElementType;
import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.model.FormField;

public class LoginScreen extends DynamicFormScreen {

    private static final long serialVersionUID = 1L;

    private ActionItem changePasswordAction;
    private ActionItem forgotPasswordAction;

    public LoginScreen() {
        setScreenType(ScreenType.Login);

        FormField userIdField = new FormField("userId", null, FieldElementType.Input, FieldInputType.AlphanumericText, true, "");
        userIdField.setPlaceholder("User ID");
        getForm().addFormElement(userIdField);

        FormField passwordField = new FormField("password", null, FieldElementType.Input, FieldInputType.AlphanumericPassword, true, "");
        passwordField.setPlaceholder("Password");
        getForm().addFormElement(passwordField);

    }

    @Override
    public Screen asDialog() {
        super.asDialog();
        setSubmitButton(new ActionItem("Login"));
        return this;
    }
    
    @Override
    public Screen asDialog(DialogProperties dialogProperties) {
        super.asDialog(dialogProperties);
        setSubmitButton(new ActionItem("Login"));
        return this;
    }

    public ActionItem getChangePasswordAction() {
        return changePasswordAction;
    }

    public void setChangePasswordAction(ActionItem changePasswordAction) {
        this.changePasswordAction = changePasswordAction;
    }

    public ActionItem getForgotPasswordAction() {
        return forgotPasswordAction;
    }

    public void setForgotPasswordAction(ActionItem forgotPasswordAction) {
        this.forgotPasswordAction = forgotPasswordAction;
    }

}
