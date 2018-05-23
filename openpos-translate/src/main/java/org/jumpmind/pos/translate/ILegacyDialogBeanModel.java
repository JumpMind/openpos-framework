package org.jumpmind.pos.translate;

import java.util.Locale;

public interface ILegacyDialogBeanModel {
    enum DialogType {
        CONFIRMATION,
        CUSTOMER_CONFIRMATION,
        ERROR,
        RETRY_CONTINUE_CANCEL,
        RETRY_CONTINUE,
        RETRY_CANCEL,
        CONTINUE_CANCEL,
        RETRY,
        ACKNOWLEDGEMENT,
        INVALID_FIELD,
        SIGNATURE,
        NOW_LATER,
        UPDATE_CANCEL,
        YES_NO,
        NO_RESPONSE,
        MANUAL_CANCEL,
        CONTINUE,
        REGISTER_PINPAD,
        MASTER,
        SIGNATURERETRY,
        RETRY_OVERRIDE_CONTINUE,
        PRINT_CONTINUE,
        CANCEL_SUSPEND_CONTINUE,
        OK_CANCEL,
        LEASE_OK,
        NEWCARD_HANDENTER,
        SIGNATURE_TIFF,
        SIGNATURERETRY_TIFF,
        CANCEL_HANDENTER,
        RETRY_TENDER,
        PHONE_NOT_LINKED,
        YES_NO_PRINT;
    }
    
    enum ButtonType {
        BUTTON_OK,
        BUTTON_YES,
        BUTTON_NO,
        BUTTON_CONTINUE,
        BUTTON_RETRY,
        BUTTON_CANCEL,
        BUTTON_NOW,
        BUTTON_LATER,
        BUTTON_ADD,
        BUTTON_UPDATE,
        BUTTON_MANUAL,
        BUTTON_PINPAD,
        BUTTON_REGISTER, 
        BUTTON_ADD_PHONE, 
        BUTTON_HAND_ENTER, 
        BUTTON_LEASE, 
        BUTTON_NEW_CARD, 
        BUTTON_OVERRIDE, 
        BUTTON_PRINT, 
        BUTTON_SIGNUP, 
        BUTTON_SUSPEND, 
        BUTTON_TENDER        
    }
    
    String getResourceID();
    Locale getLocale();
    String[] getArgs();
    int getType();
    DialogType getDialogType();
    String[] getLetters();
    
    int toLegacyButtonType(ButtonType buttonType);
    
}
