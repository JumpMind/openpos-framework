package org.jumpmind.pos.translate;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.core.screen.IPromptScreen;

public abstract class AbstractPromptScreenTranslator<T extends Screen> extends AbstractLegacyScreenTranslator<T> {

	public AbstractPromptScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass) {
	    this(legacyScreen, screenClass, null, null);
	}
	
    public AbstractPromptScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass, String appId, Properties properties) {
        super(legacyScreen, screenClass, appId, properties);
        if (!IPromptScreen.class.isAssignableFrom(screenClass)) {
            throw new IllegalArgumentException(String.format("screenClass %s must be assignable from %s", screenClass.getSimpleName(),
                    IPromptScreen.class.getSimpleName()));
        }
    }

    protected void configureScreenResponseField() {
        ILegacyUIModel baseModel = legacyPOSBeanService.getLegacyUIModel(legacyScreen);
        if (baseModel != null) {
            ILegacyAssignmentSpec promptResponseSpec = legacyPOSBeanService.getLegacyAssignmentSpec(legacyScreen, PROMPT_RESPONSE_PANEL_KEY);
            ILegacyPromptAndResponseModel promptAndResponseBeanModel = legacyPOSBeanService.getLegacyPromptAndResponseModel(legacyScreen);
            IPromptScreen promptScreen = getPromptScreen();

            String formattedPromptText = getPromptText(baseModel, promptResponseSpec, legacyScreen.getResourceBundleFilename()).orElse(null);

            String responseFieldType = getResponseFieldType(promptResponseSpec);

            String enterDataValue = promptResponseSpec.getPropertyValue("enterData");
            boolean enterData = "true".equals(enterDataValue != null ? enterDataValue.toLowerCase() : "false");

            String minLength = getSpecPropertyValue(promptResponseSpec, "minLength", promptAndResponseBeanModel.getMinLength());
            String maxLength = getSpecPropertyValue(promptResponseSpec, "maxLength", promptAndResponseBeanModel.getMaxLength());

            if (isNotBlank(formattedPromptText)) {
                String text = formattedPromptText
                        .replace("Enter number and press Next", "")
                        .replace(" and press Next", "")
                        .replace(" and press next", "")
                        .replace(", then press Next", "");
                if (text.endsWith(".")) {
                    text = text.substring(0, text.length() - 1);
                }
                promptScreen.setText(text);
            }

            if (enterData) {
                promptScreen.setResponseText(promptAndResponseBeanModel.getResponseText());
                promptScreen.setEditable(true);
                // If the reponseType has already been set before getting here, don't override it.
                if(StringUtils.isEmpty(promptScreen.getResponseType()) ) {
                    promptScreen.setResponseType(responseFieldType);
                }
                
                if (promptAndResponseBeanModel.getMinLength() != null) {
                    promptScreen.setMinLength(Integer.parseInt(minLength));
                }
                if (promptAndResponseBeanModel.getMaxLength() != null) {
                    promptScreen.setMaxLength(Integer.parseInt(maxLength));
                }
            } else {
                promptScreen.setEditable(false);
            }

            if (IPromptScreen.TYPE_ALPHANUMERICPASSWORD.equals(promptScreen.getResponseType())) {
                promptScreen.setPromptIcon("lock");
            } else if( IPromptScreen.TYPE_PHONE.equals(promptScreen.getResponseType())) {
            		promptScreen.setPromptIcon("phone");
            } else if(FieldInputType.Money.name().equalsIgnoreCase(promptScreen.getResponseType())) {
                // TODO: I'd like to use cash icon here, but needs to have adjustment to styling for case when using
                // a local icon.  just use default icon for now
                // promptScreen.setPromptIcon(new LocalIcon("cash-multiple").getName());
                promptScreen.setPromptIcon("question_answer");
            }
            else {
                promptScreen.setPromptIcon("question_answer");
            }
        }

    }

    protected Optional<String> getPromptText(ILegacyUIModel uiModel, ILegacyAssignmentSpec promptResponsePanel,
            String resourceBundleFilename) {
        Optional<String> optPromptText = Optional.empty();
        try {
          
            ILegacyPromptAndResponseModel promptAndResponseModel = this.legacyPOSBeanService.getLegacyPromptAndResponseModel(legacyScreen);
            
            String formattedPromptText = promptAndResponseModel.getPromptText();
            
            if (isBlank(formattedPromptText)) {
                String promptTextTag = promptResponsePanel.getPropertyValue("promptTextTag");            
                String resourceText = this.legacyPOSBeanService.getLegacyUIUtilities().retrieveText(promptResponsePanel.getBeanSpecName(), resourceBundleFilename, promptTextTag);
                formattedPromptText = toFormattedString(resourceText, promptAndResponseModel != null ? promptAndResponseModel.getArguments() : null);
            }

            optPromptText = Optional.ofNullable(formattedPromptText);
        } catch (Exception ex) {
            logger.error("Failed to get promptText for {}", uiModel.getModel().getClass());
        }
        return optPromptText;
    }

    protected String getResponseFieldType(ILegacyAssignmentSpec promptResponseSpec) {
        String responseFieldType = getSpecPropertyValue(promptResponseSpec, "responseField", null);
        if (responseFieldType != null && responseFieldType.contains("Field")) {
            responseFieldType = responseFieldType.substring(responseFieldType.lastIndexOf(".") + 1, responseFieldType.indexOf("Field"))
                    .toUpperCase();
        } else {
            responseFieldType = IPromptScreen.TYPE_ALPHANUMERICTEXT;
        }

        return responseFieldType;
    }

    protected void handleNextAction(ITranslationManagerSubscriber subscriber, TranslationManagerServer tmServer, Action action, Form formResults) {
        String prompt = (action != null && action.getData() != null) ? action.getData().toString() : null;
        if (isNotBlank(prompt)) {
            setScreenResponseText(action.toDataString());
            tmServer.sendAction(action.getName());
        } else {
            super.handleAction(subscriber, tmServer, action, formResults);
        }
    }
    
    @Override
    public void handleAction(ITranslationManagerSubscriber subscriber, TranslationManagerServer tmServer, Action action, Form formResults) {
        if ("Next".equalsIgnoreCase(action.getName())) {
            handleNextAction(subscriber, tmServer, action, formResults);
        } else {
            super.handleAction(subscriber, tmServer, action, formResults);
        }
    }

    protected IPromptScreen getPromptScreen() {
        return (IPromptScreen) getScreen();
    }

    protected void setScreenResponseText(String responseText) {
        legacyPOSBeanService.getLegacyPromptAndResponseModel(legacyScreen).setResponseText(responseText);
    }

}
