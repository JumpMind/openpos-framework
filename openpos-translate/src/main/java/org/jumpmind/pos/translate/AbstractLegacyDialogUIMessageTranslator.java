package org.jumpmind.pos.translate;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Optional;
import java.util.Properties;

import org.jumpmind.pos.core.screen.DialogScreen;
import org.jumpmind.pos.core.ui.message.DialogUIMessage;
import org.jumpmind.pos.core.screen.ActionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLegacyDialogUIMessageTranslator extends AbstractLegacyUIMessageTranslator<DialogUIMessage> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Maximum number of lines OrPOS supports on a dialog */
    public static final int MAX_DIALOG_LINES = 17; // Defined in OrPOS DialogBean.MAX_LINES, but protected there

    public AbstractLegacyDialogUIMessageTranslator(ILegacyScreen headlessScreen, Class<DialogUIMessage> screenClass) {
        super(headlessScreen, screenClass);
    }
    
    public AbstractLegacyDialogUIMessageTranslator(ILegacyScreen headlessScreen, Class<DialogUIMessage> screenClass, String appId, Properties properties) {
    		super(headlessScreen, screenClass, appId, properties);
    }

    protected void buildMainContent() {
        super.buildMainContent();
        ILegacyAssignmentSpec assignmentSpec = this.getLegacyAssignmentSpec();
        logger.trace("The work panel bean spec name was {}", assignmentSpec.getBeanSpecName());

        ILegacyBeanSpec spec = this.getLegacyPOSBeanService().getLegacyBeanSpec(this.getLegacyScreen(), assignmentSpec.getBeanSpecName());
        ILegacyDialogBeanModel dialogModel = this.legacyPOSBeanService.getLegacyDialogBeanModel(this.getLegacyScreen());
        if (dialogModel != null) {
            Properties resourceBundle = this.legacyPOSBeanService.getLegacyResourceBundleUtil().getGroupText(spec.getSpecName() + "." + dialogModel.getResourceID(),
                    new String[] { legacyScreen.getResourceBundleFilename() }, dialogModel.getLocale());

            screen.setTitle(resourceBundle.getProperty(dialogModel.getResourceID() + ".title"));

            buildDialogMessageLines(dialogModel, resourceBundle);

            configureTypeAndButtons(dialogModel);

        }
    }

    protected void buildDialogMessageLines(ILegacyDialogBeanModel dialogModel, Properties resourceBundle) {
        boolean found = false;
        String lineKey = String.format("%s.line", dialogModel.getResourceID());
        if ( resourceBundle.containsKey(lineKey)) {
            String message = this.toFormattedString(resourceBundle, lineKey, dialogModel.getArgs());
            String[] messageLines = message.split("\\n");
            for (String messageLine : messageLines) {
                this.screen.getMessage().add(messageLine);
                found = true;
            }
        } else {
            for (int i = 1; i <= MAX_DIALOG_LINES; i++) {
                lineKey = String.format("%s.line%d", dialogModel.getResourceID(), i);
                if (resourceBundle.containsKey(lineKey)) {
                    this.screen.getMessage().add(toFormattedString(resourceBundle, lineKey, dialogModel.getArgs()));
                    found = true;
                }
            }
        }
        // Display arg text if no other text found.
        if (!found) {
            String[] text = dialogModel.getArgs();
            for (String t : text) {
                this.screen.getMessage().add(t);
            }
        }
    }

    protected String getResourceID() {
        return this.legacyScreen.getDialogResourceId();
    }
    
    abstract protected void configureTypeAndButtons(ILegacyDialogBeanModel dialogModel);
    
    protected String addDialogButton(String action, ILegacyDialogBeanModel dialogModel) {
        return addDialogButton(action, action, Optional.empty(), dialogModel);
    }

    /**
     * 
     * @param name
     * @param action
     * @param orposButtonIndexOpt OrPOS sometimes overrides the default letter for a given button by passing in the letter override
     * on the dialog model. If the letter has been overridden with another value, that value is available on the dialog model via
     * a letters array. To access the letter value, one must use the button index values provided in DialogScreensIfc constants.
     * This parameter needs to be one of the BUTTON constant values from the DialogScreensIfc class. 
     * @param dialogModel
     */
    protected String addDialogButton(String name, String action, Optional<Integer> orposButtonIndexOpt, ILegacyDialogBeanModel dialogModel) {
        String letter = action;
        String[] letters = dialogModel.getLetters();
        if (letters != null && letters.length > 0) {
            if (orposButtonIndexOpt.filter(idx -> idx >= 0 && idx < letters.length).isPresent() 
                    && isNotBlank(letters[orposButtonIndexOpt.get()])) {
                letter = letters[orposButtonIndexOpt.get()];
            } /* prior code -- I don't think this will work, see com.extendyourstore.pos.ui.beans.DialogBean, line 603 
            else if ( isNotBlank(letters[0]) ){
                letter = dialogModel.getLetters()[0];
            }*/
        }
        this.screen.getButtons().add(new ActionItem(translateButtonTitle(name, action), letter, true));
        return letter;
    }
    
    protected String translateButtonTitle(String name, String action) {
        if (name.equals("Enter..")) {
            return "Ok";
        } else {
            return name;
        }
    }

}
