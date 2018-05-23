package org.jumpmind.pos.core.screen;

import org.jumpmind.pos.core.model.FieldElementType;
import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.model.FormField;

public class LoginScreen extends DynamicFormScreen {

    private static final long serialVersionUID = 1L;

    private MenuItem changePasswordAction;
    private MenuItem forgotPasswordAction;

    public LoginScreen() {
        setType(ScreenType.Login);

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
        setSubmitAction("Login");
        return this;
    }
    
    @Override
    public Screen asDialog(DialogProperties dialogProperties) {
        super.asDialog(dialogProperties);
        setSubmitAction("Login");
        return this;
    }

    public MenuItem getChangePasswordAction() {
        return changePasswordAction;
    }

    public void setChangePasswordAction(MenuItem changePasswordAction) {
        this.changePasswordAction = changePasswordAction;
    }

    public MenuItem getForgotPasswordAction() {
        return forgotPasswordAction;
    }

    public void setForgotPasswordAction(MenuItem forgotPasswordAction) {
        this.forgotPasswordAction = forgotPasswordAction;
    }

}
