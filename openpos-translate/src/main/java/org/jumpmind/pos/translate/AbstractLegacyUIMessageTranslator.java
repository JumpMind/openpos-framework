package org.jumpmind.pos.translate;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.ModeConstants;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.message.DynamicFormUIMessage;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class AbstractLegacyUIMessageTranslator<T extends UIMessage> extends AbstractUIMessageTranslator<T>
        implements ILegacyBeanAccessor {
    public final static String LOCAL_NAV_PANEL_KEY = "LocalNavigationPanel";
    public final static String GLOBAL_NAV_PANEL_KEY = "GlobalNavigationPanel";
    public final static String PROMPT_RESPONSE_PANEL_KEY = "PromptAndResponsePanel";
    public final static String WORK_PANEL_KEY = "WorkPanel";
    public final static String STATUS_PANEL_KEY = "StatusPanel";

    public final static String STATUS_BAR_USER_TEXT_PROP = "ui.statusbar.user";
    public final static String STATUS_BAR_USER_TEXT_FIRST_LAST_NAME = "firstLastName";

    private ILegacyUtilityManager legacyUtilityManager;

    protected ILegacyPOSBeanService legacyPOSBeanService;
    protected ILegacyStoreProperties legacyStoreProperties;

    protected IUIActionOverrider actionOverrider;

    private boolean cancelAsBack;

    public AbstractLegacyUIMessageTranslator(ILegacyScreen legacyScreen, Class<T> screenClass) {
        super(legacyScreen, screenClass);
    }

    public AbstractLegacyUIMessageTranslator(ILegacyScreen legacyScreen, Class<T> screenClass, String appId, Properties properties) {
        super(legacyScreen, screenClass);
        this.appId = appId;
        this.properties = properties;
    }

    protected boolean isPOS() {
        return ModeConstants.POS.equals(appId);
    }

    protected boolean isSelfCheckout() {
        return ModeConstants.SELFCHECKOUT.equals(appId);
    }

    protected boolean isCustomerDisplay() {
        return ModeConstants.CUSTOMERDISPLY.equals(appId);
    }

    @Override
    public void setLegacyPOSBeanService(ILegacyPOSBeanService beanService) {
        this.legacyPOSBeanService = beanService;
    }

    @Override
    public ILegacyPOSBeanService getLegacyPOSBeanService() {
        return this.legacyPOSBeanService;
    }

    @Override
    public void setLegacyStoreProperties(ILegacyStoreProperties legacyStoreProperties) {
        this.legacyStoreProperties = legacyStoreProperties;
    }

    @Override
    public ILegacyStoreProperties getLegacyStoreProperties() {
        return this.legacyStoreProperties;
    }

    protected Form getForm(UIMessage screen) {
        if (screen instanceof DynamicFormUIMessage) {
            return ((DynamicFormUIMessage) screen).getForm();
        } else {
            return null;
        }
    }

    @Override
    protected void buildMainContent() {
        logAvailableLocalMenuItems();

        if (getLegacyUIModel() != null) {
            Integer timeout = getLegacyUIModel().getTimeout();
            if (timeout != null) {
                screen.setSessionTimeoutMillis(timeout);
            }
        }
    }

    protected String getDeviceId() {
    	return "#" + legacyStoreProperties.getWorkstationNumber() + "-" + legacyStoreProperties.getStoreNumber();
    }

    protected String getOperatorText() {
        if (properties != null && STATUS_BAR_USER_TEXT_FIRST_LAST_NAME.equals(properties.get(STATUS_BAR_USER_TEXT_PROP))) {
            return posSessionInfo.getOperatorName();
        } else {
            return posSessionInfo.getOperatorLoginId();
        }
    }

    protected String getScreenName() {
        ILegacyStatusBeanModel statusModel = legacyPOSBeanService.getLegacyStatusBeanModel(legacyScreen);
        if (statusModel != null && statusModel.getScreenName() != null) {
            return statusModel.getScreenName();
        } else {
            ILegacyAssignmentSpec statusPanelSpec = legacyPOSBeanService.getLegacyAssignmentSpec(legacyScreen, STATUS_PANEL_KEY);
            String labelTag = getSpecPropertyValue(statusPanelSpec, "screenNameTag", null);
            if (labelTag != null) {
                return legacyPOSBeanService.getLegacyUtilityManager(legacyScreen).retrieveText(statusPanelSpec.getBeanSpecName(),
                        getResourceBundleFilename(), labelTag, labelTag);
            } else {
                return null;
            }
        }
    }

    protected void logAvailableLocalMenuItems() {
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(LOCAL_NAV_PANEL_KEY);
        if (assignmentPanelSpec != null) {
            ILegacyBeanSpec localNavSpec = this.legacyPOSBeanService.getLegacyBeanSpec(this.legacyScreen,
                    assignmentPanelSpec.getBeanSpecName());
            Map<String, Boolean> enabledState = parseButtonStates(assignmentPanelSpec);

            if (logger.isDebugEnabled()) {
                Arrays.stream(localNavSpec.getButtons()).filter(
                        buttonSpec -> Optional.ofNullable(enabledState.get(buttonSpec.getActionName())).orElse(buttonSpec.getEnabled()))
                        .forEachOrdered(enabledButtonSpec -> {
                            logger.debug("Available local menu with label tag of: {}", enabledButtonSpec.getLabelTag());
                        });
            }
        }
    }

    protected ILegacyUIModel getLegacyUIModel() {
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(WORK_PANEL_KEY);
        ILegacyUIModel legacyUIModel = null;
        if (assignmentPanelSpec != null) {
            logger.trace("The work panel bean spec name was {}", assignmentPanelSpec.getBeanSpecName());

            legacyUIModel = this.legacyPOSBeanService.getLegacyUIModel(legacyScreen);

        }

        return legacyUIModel;
    }

    protected ILegacyBus getBus() {
        return this.legacyPOSBeanService.getLegacyBus(legacyScreen);
    }

    protected ActionItem getBackButton() {
        ActionItem backButton = null;
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(GLOBAL_NAV_PANEL_KEY);
        if (null != assignmentPanelSpec) {
            ILegacyBeanSpec globalNavSpec = this.legacyPOSBeanService.getLegacyBeanSpec(this.legacyScreen,
                    assignmentPanelSpec.getBeanSpecName());
            Map<String, Boolean> enabledState = parseButtonStates(assignmentPanelSpec);

            // Check if there is an override of the button state for the Undo button in the GlobalButtonBeanModel
            ILegacyPOSBaseBeanModel model = legacyPOSBeanService.getLegacyPOSBaseBeanModel(this.legacyScreen);
            if (model.getGlobalButtonBeanModel() != null) {
                ILegacyButtonSpec[] modifyButtons = model.getGlobalButtonBeanModel().getModifyButtons();
                if (modifyButtons != null) {
                    Arrays.stream(modifyButtons).filter( modBtnSpec ->
                            ("Undo".equals(modBtnSpec.getActionName()) || "Undo".equals(modBtnSpec.getLabelTag())) ||
                                    (cancelAsBack && (("Cancel".equals(modBtnSpec.getActionName()) || "Cancel".equals(modBtnSpec.getLabelTag())))))
                            .forEachOrdered( modBtnSpec -> {
                                Boolean specButtonState = enabledState.get(modBtnSpec.getActionName());
                                if (specButtonState != null) {
                                    if (! specButtonState.equals(modBtnSpec.getEnabled())) {
                                        logger.info("Changing enabled state of '{}' button from '{}' to '{}' due to override in screen bean model",
                                                modBtnSpec.getActionName(), specButtonState, modBtnSpec.getEnabled());
                                        enabledState.put(modBtnSpec.getActionName(), modBtnSpec.getEnabled());
                                    }
                                } else {
                                    enabledState.put(modBtnSpec.getActionName(), modBtnSpec.getEnabled());
                                }
                            });
                }
            }

            ILegacyButtonSpec backButtonSpec = Arrays.stream(globalNavSpec.getButtons())
                    .filter(buttonSpec -> Optional.ofNullable(enabledState.get(buttonSpec.getActionName())).orElse(buttonSpec.getEnabled()))
                    .filter(enabledButtonSpec -> ("Undo".equals(enabledButtonSpec.getLabelTag()) || (cancelAsBack && "Cancel".equals(enabledButtonSpec.getLabelTag())))).findFirst().orElse(null);

            if(backButtonSpec != null) {
                backButton = new ActionItem("Back", backButtonSpec.getActionName(), true);
            }
        }
        return backButton;
    }

    protected String getPanelPropertyValue(String panelName, String propertyName) {
        String propertyValue = propertyName; // default to propertyName
        ILegacyAssignmentSpec assignmentSpec = this.getLegacyAssignmentSpec(panelName);
        if (assignmentSpec != null) {
            propertyValue = this.getLegacyUtilityManager().retrieveText(assignmentSpec.getBeanSpecName(), getResourceBundleFilename(),
                    propertyName, propertyName);
        }
        return propertyValue;

    }

    protected String getWorkPanelPropertyValue(String propertyName, String defaultValue) {
        String propertyValue = defaultValue;
        ILegacyAssignmentSpec assignmentSpec = this.getLegacyAssignmentSpec();
        if (assignmentSpec != null) {
            propertyValue = this.getLegacyUtilityManager().retrieveText(assignmentSpec.getBeanSpecName(), getResourceBundleFilename(),
                    propertyName, defaultValue);
        }
        return propertyValue;
    }

    protected String getWorkPanelPropertyValue(String propertyName) {
        return this.getPanelPropertyValue(WORK_PANEL_KEY, propertyName);
    }

    protected String getSpecPropertyValue(ILegacyAssignmentSpec spec, String key, String modelValue) {
        if (isBlank(modelValue) && spec != null) {
            String propValue = spec.getPropertyValue(key);
            if (propValue != null) {
                return propValue;
            } else {
                return null;
            }
        } else {
            return modelValue;
        }
    }

    protected void updatePosSessionInfo() {
        if (legacyScreen != null) {
            ILegacyRegisterStatusService registerStatusService = this.legacyPOSBeanService.getLegacyRegisterStatusService(legacyScreen);
            if (registerStatusService.isStatusDeterminable()) {
                posSessionInfo.setRegisterOpen(Optional.of(registerStatusService.getRegisterStatus() == ILegacyRegisterStatusService.Status.OPEN));

                ILegacyRegisterStatusService.Status storeStatus = registerStatusService.getStoreStatus();
                if (storeStatus != ILegacyRegisterStatusService.Status.UNKNOWN) {
                    posSessionInfo.setStoreOpen(Optional.of((storeStatus == ILegacyRegisterStatusService.Status.OPEN)));
                }
            }

            ILegacyBus bus = legacyPOSBeanService.getLegacyBus(legacyScreen);
            ILegacyCargo cargo = bus.getLegacyCargo();
            if (cargo != null) {
                posSessionInfo.setOperatorName(cargo.getOperatorFirstLastName());
                if (cargo.getOperatorLoginId() != null) {
                    posSessionInfo.setOperatorLoginId(cargo.getOperatorLoginId());
                }

                if (bus.getMainCargo() != null) {
                    posSessionInfo.setTrainingMode(bus.getMainCargo().isTrainingMode());
                }
            }

        }
    }

    public ILegacyUtilityManager getLegacyUtilityManager() {
        if (legacyUtilityManager == null) {
            legacyUtilityManager = this.legacyPOSBeanService.getLegacyUtilityManager(legacyScreen);
        }

        return legacyUtilityManager;
    }

    protected String getResourceBundleFilename() {
        return this.legacyScreen.getResourceBundleFilename();
    }

    protected boolean isPanelButtonPresent(String panelKey, String actionName) {
        List<ILegacyButtonSpec> allPanelButtons = this.getPanelButtons(panelKey, Optional.of(false));
        return allPanelButtons.stream().anyMatch(buttonSpec -> actionName.equals(buttonSpec.getActionName()));
    }

    protected boolean isPanelButtonEnabled(String panelKey, String actionName) {
        List<ILegacyButtonSpec> enabledPanelButtons = this.getPanelButtons(panelKey, Optional.of(true));
        return enabledPanelButtons.stream().anyMatch(buttonSpec -> actionName.equals(buttonSpec.getActionName()));
    }

    protected List<ILegacyButtonSpec> getPanelButtons(String panelKey, Optional<Boolean> enabledButtonsOnlyOpt) {
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(panelKey);
        List<ILegacyButtonSpec> buttons = new ArrayList<>();

        if (assignmentPanelSpec != null) {
            ILegacyBeanSpec localNavSpec = this.legacyPOSBeanService.getLegacyBeanSpec(legacyScreen, assignmentPanelSpec.getBeanSpecName());
            Map<String, Boolean> buttonStateMap = parseButtonStates(assignmentPanelSpec);

            Arrays.stream(localNavSpec.getButtons()).filter(buttonSpec -> {
                if (enabledButtonsOnlyOpt.orElse(false)) {
                    // If only enabled buttons are wanted, use the button spec
                    // enabled/disabled status to
                    // only keep the enabled buttons
                    Boolean buttonState = buttonStateMap.get(buttonSpec.getActionName());
                    return buttonState != null ? buttonState : buttonSpec.getEnabled();
                } else { // Else don't filter any of the buttons
                    return true;
                }
            }).forEachOrdered(buttonSpec -> {
                buttons.add(buttonSpec);
            });

        }
        return buttons;
    }

    protected ILegacyAssignmentSpec getPromptAndResponseAssignmentSpec() {
        return getLegacyAssignmentSpec(PROMPT_RESPONSE_PANEL_KEY);
    }

    protected ILegacyAssignmentSpec getLegacyAssignmentSpec() {
        return getLegacyAssignmentSpec(WORK_PANEL_KEY);

    }

    public ILegacyPromptAndResponseModel getPromptAndResponseModel() {
        return this.legacyPOSBeanService.getLegacyPromptAndResponseModel(legacyScreen);
    }

    protected String retrieveText(String propName, Optional<String> defaultValue) {
        String resourceBundleName = this.getLegacyScreen().getResourceBundleFilename();
        String text = null;
        if (!StringUtils.isEmpty(resourceBundleName)) {
            text = legacyPOSBeanService.getLegacyUtilityManager(legacyScreen).retrieveText(this.legacyScreen.getSpecName(),
                    getResourceBundleFilename(), propName, null);
            if (text == null) {
                ILegacyAssignmentSpec panelSpec = legacyPOSBeanService.getLegacyAssignmentSpec(legacyScreen, LOCAL_NAV_PANEL_KEY);
                text = legacyPOSBeanService.getLegacyUtilityManager(legacyScreen).retrieveText(panelSpec.getBeanSpecName(),
                        getResourceBundleFilename(), propName, null);
            }
        }

        if (!StringUtils.isEmpty(text)) {
            return text;
        } else {
            return retrieveCommonText(propName, defaultValue);
        }
    }

    protected String retrieveCommonText(String propName, Optional<String> defaultValue) {
        String commonText = defaultValue.isPresent()
                ? this.legacyPOSBeanService.getLegacyUIUtilities().retrieveCommonText(propName, defaultValue.get())
                : this.legacyPOSBeanService.getLegacyUIUtilities().retrieveCommonText(propName);
        return commonText;
    }

    protected String toFormattedString(Properties resourceBundle, String propertyName, Optional<String[]> args) {
        String text = (String) resourceBundle.get(propertyName);
        if (args.isPresent() && isNotBlank(text)) {
            text = this.toFormattedString(text, args.get());
        }
        return text;
    }

    protected String toFormattedString(String stringToFormat, String[] args) {
        String formattedText = this.legacyPOSBeanService.getLegacyLocaleUtilities().formatComplexMessage(stringToFormat, args);
        return formattedText;
    }

    protected String toFormattedString(Properties resourceBundle, String propertyName, String[] args) {
        return toFormattedString(resourceBundle, propertyName, Optional.ofNullable(args));
    }

    protected ILegacyAssignmentSpec getLegacyAssignmentSpec(String panelKey) {
        ILegacyAssignmentSpec spec = this.legacyPOSBeanService.getLegacyAssignmentSpec(this.legacyScreen, panelKey);
        return spec;
    }

    protected Map<String, Boolean> parseButtonStates(ILegacyAssignmentSpec spec) {
        Map<String, Boolean> states = new HashMap<>();
        String propValue = spec.getPropertyValue("buttonStates");
        if (propValue != null) {
            String stateString = propValue;
            String[] tokens = stateString.split(",");
            for (String token : tokens) {
                token = token.trim();
                String key = token.substring(0, token.indexOf("["));
                String value = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
                states.put(key, new Boolean(value));
            }
        }
        return states;
    }

    protected <A extends ActionItem> List<A> generateUIActionsForLocalNavButtons(Class<A> actionClass, boolean filterDisabled,
                                                                                 String... excludedLabelTags) {
        Set<String> toExclude = new HashSet<>();
        if (excludedLabelTags != null) {
            for (String string : excludedLabelTags) {
                toExclude.add(string);
                logger.info("action will not be generated for labelTag '{}' because it is excluded", string);
            }
        }

        final List<A> generatedActions = new ArrayList<A>();
        ILegacyAssignmentSpec panelSpec = legacyPOSBeanService.getLegacyAssignmentSpec(legacyScreen, LOCAL_NAV_PANEL_KEY);
        if (panelSpec != null) {
            ILegacyBeanSpec localNavSpec = legacyPOSBeanService.getLegacyBeanSpec(legacyScreen, panelSpec.getBeanSpecName());
            ILegacyPOSBaseBeanModel model = legacyPOSBeanService.getLegacyPOSBaseBeanModel(legacyScreen);

            ILegacyNavigationButtonBeanModel buttonModel = model.getLegacyLocalButtonBeanModel();
            Map<String, Boolean> enabledState = parseButtonStates(panelSpec);

            ILegacyButtonSpec[] buttonSpecs;

            if (buttonModel != null) {
                // If we have newButtons, replace all buttons in the nav panel
                // with those buttons. I believe this matches the
                // logic in retailer specific impl of the
                // GlobalNavigationButtonBean
                if (buttonModel.getNewButtons() != null && buttonModel.getNewButtons().length > 0) {
                    buttonSpecs = buttonModel.getNewButtons();
                } else {
                    // Why was this adding in the new buttons?  That list is either null or empty here.
                    // buttonSpecs = ((ILegacyButtonSpec[]) ArrayUtils.addAll(localNavSpec.getButtons(), buttonModel.getNewButtons()));
                    buttonSpecs = localNavSpec.getButtons();
                }
            } else {
                buttonSpecs = localNavSpec.getButtons();
            }

            for (ILegacyButtonSpec buttonSpec : buttonSpecs) {
                if (buttonSpec != null &&
                        !(toExclude.contains(buttonSpec.getLabelTag())
                                || toExclude.contains(buttonSpec.getActionName()))) {
                    Boolean enabled = enabledState.get(buttonSpec.getActionName());
                    if (enabled == null) {
                        enabled = buttonSpec.getEnabled();
                    }

                    String labelTag = buttonSpec.getLabelTag();
                    String label = labelTag;
                    String action = buttonSpec.getActionName();
                    String keyName = buttonSpec.getKeyName();
                    try {
                        label = legacyPOSBeanService.getLegacyUtilityManager(legacyScreen).retrieveText(panelSpec.getBeanSpecName(),
                                getResourceBundleFilename(), labelTag, labelTag);
                    } catch (Exception e) {
                        logger.info("Failed to look up label for button: {}", labelTag);
                    }

                    if (buttonModel != null) {
                        // Modify existing buttons
                        ILegacyButtonSpec[] modifyButtonSpecs = buttonModel.getModifyButtons();

                        if (modifyButtonSpecs != null) {
                            for (ILegacyButtonSpec modifiedSpec : modifyButtonSpecs) {
                                if (modifiedSpec.getActionName().equals(action)) {
                                    if (modifiedSpec.getEnabledFlag() != null) {
                                        //  Discovered while implementing the GET_AMOUNT_FOR_GIFT_CARD
                                        //  screen that the enabledFlag may be null in the modified spec,
                                        //  which causes getEnabled() to return false.  Hoping that if I
                                        //  only override the enabled value when the enabledFlag is
                                        //  non-null that it will yield better results.
                                        enabled = modifiedSpec.getEnabledFlag();
                                    }
                                    if (isNotBlank(modifiedSpec.getLabel())) {
                                        label = modifiedSpec.getLabel();
                                    }
                                    if (enabled)  {
                                        //  Some of the buttons may appear more than once in the
                                        //  modified spec list.  This happens on Tender Options,
                                        //  for example, because we potentially have multiple
                                        //  scenarios like special order and lease in the same
                                        //  cart.  Once we find an active version of the given
                                        //  item, go on to the next button.  If we don't do this,
                                        //  then last one wins. -- JAB 09/20/2018
                                        break;
                                    }
                                }
                            }
                        }

                    }

                    logger.info("adding action with label tag: {}", labelTag);

                    A actionItem;
                    try {
                        actionItem = actionClass.newInstance();
                        actionItem.setTitle(label);
                        actionItem.setIcon(iconRegistry.get(labelTag));
                        actionItem.setEnabled(enabled);
                        actionItem.setAction(action);
                        actionItem.setKeybind(keyName);
                        if (actionOverrider != null) {
                            actionOverrider.hideOrOverride(legacyScreen, labelTag, actionItem);
                        }

                        if (actionItem.isEnabled() || !filterDisabled) {
                            generatedActions.add(actionItem);
                        } else {
                            logger.info("not generating action '{}' because it is disabled", labelTag);
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        logger.error(String.format("Failed to create action of type %s for action '%s'", actionClass.getName(), action), e);
                    }
                }
            }
        }

        return generatedActions;
    }

    protected String translate(String tag, String defaultValue, Object... params) {
        return null;
    }

    protected Optional<String> getPromptText(ILegacyUIModel uiModel, ILegacyAssignmentSpec promptResponsePanel,
                                             String resourceBundleFilename) {
        Optional<String> optPromptText = Optional.empty();
        try {
            ILegacyPromptAndResponseModel promptAndResponseModel = this.legacyPOSBeanService.getLegacyPromptAndResponseModel(legacyScreen);

            String promptTextTag = promptResponsePanel.getPropertyValue("promptTextTag");
            Properties resourceBundle = this.legacyPOSBeanService.getLegacyResourceBundleUtil().getText(resourceBundleFilename,
                    Locale.getDefault());
            String promptTextKey = String.format("%s.%s", "PromptAndResponsePanelSpec", promptTextTag);
            String formattedPromptText = null;
            formattedPromptText = toFormattedString(resourceBundle, promptTextKey,
                    promptAndResponseModel != null ? Optional.ofNullable(promptAndResponseModel.getArguments()) : Optional.empty());
            if (StringUtils.isBlank(formattedPromptText)) {
                promptTextKey = String.format("%s.%s", this.getPromptAndResponseAssignmentSpec().getBeanSpecName(), promptTextTag);
                formattedPromptText = toFormattedString(resourceBundle, promptTextKey,
                        promptAndResponseModel != null ? Optional.ofNullable(promptAndResponseModel.getArguments()) : Optional.empty());
            }
            optPromptText = Optional.ofNullable(formattedPromptText);
        } catch (Exception ex) {
            logger.error("Failed to get promptText for {}", uiModel.getModel().getClass());
        }
        return optPromptText;
    }

    public void setActionOverrider(IUIActionOverrider actionOverrider) {
        this.actionOverrider = actionOverrider;
    }

    public interface IUIActionOverrider {
        public boolean hideOrOverride(ILegacyScreen legacyScreen, String labelTag, ActionItem action);
    }

    protected List<ActionItem> getLocalMenuButtons() {
        return generateUIActionsForLocalNavButtons(ActionItem.class, true);
    }

    protected String getPromptTextFromBeanSpec() {
        ILegacyAssignmentSpec promptAndResponseBeanPanelSpec = legacyPOSBeanService.getLegacyAssignmentSpec(legacyScreen,
                PROMPT_RESPONSE_PANEL_KEY);
        ILegacyPromptAndResponseModel promptAndResponseBeanModel = legacyPOSBeanService.getLegacyPromptAndResponseModel(legacyScreen);
        String promptTextTag = promptAndResponseBeanPanelSpec.getPropertyValue("promptTextTag");
        String resourceText = this.legacyPOSBeanService.getLegacyUIUtilities().retrieveText(promptAndResponseBeanPanelSpec.getBeanSpecName(),
                legacyScreen.getResourceBundleFilename(), promptTextTag);
        String formattedPromptText = toFormattedString(resourceText,
                promptAndResponseBeanModel != null ? promptAndResponseBeanModel.getArguments() : null);
        return formattedPromptText;
    }

    public boolean isCancelAsBack() {
        return cancelAsBack;
    }

    public void setCancelAsBack(boolean cancelAsBack) {
        this.cancelAsBack = cancelAsBack;
    }
}

