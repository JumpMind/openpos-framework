package org.jumpmind.pos.translate;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.ModeConstants;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.core.screen.DynamicFormScreen;
import org.jumpmind.pos.core.screen.IUIAction;
import org.jumpmind.pos.core.screen.MenuItem;
import org.jumpmind.pos.core.screen.Workstation;
import org.jumpmind.pos.core.template.SellTemplate;
import org.jumpmind.pos.translate.ILegacyRegisterStatusService.Status;

public abstract class AbstractLegacyScreenTranslator<T extends Screen> extends AbstractScreenTranslator<T>
        implements ILegacyBeanAccessor {

    public final static String LOCAL_NAV_PANEL_KEY = "LocalNavigationPanel";
    public final static String GLOBAL_NAV_PANEL_KEY = "GlobalNavigationPanel";
    public final static String PROMPT_RESPONSE_PANEL_KEY = "PromptAndResponsePanel";
    public final static String WORK_PANEL_KEY = "WorkPanel";
    public final static String STATUS_PANEL_KEY = "StatusPanel";

    public final static String USE_ON_SCREEN_KEYBOARD_PROP = "use.on.screen.keyboard";
    public final static String STATUS_BAR_USER_TEXT_PROP = "ui.statusbar.user";
    public final static String STATUS_BAR_USER_TEXT_FIRST_LAST_NAME = "firstLastName";

    private ILegacyUtilityManager legacyUtilityManager;

    protected ILegacyPOSBeanService legacyPOSBeanService;
    protected ILegacyStoreProperties legacyStoreProperties;

    protected IUIActionOverrider actionOverrider;

    public AbstractLegacyScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass) {
        super(legacyScreen, screenClass);
    }

    public AbstractLegacyScreenTranslator(ILegacyScreen legacyScreen, Class<T> screenClass, String appId, Properties properties) {
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

    protected Form getForm(Screen screen) {
        if (screen instanceof DynamicFormScreen) {
            return ((DynamicFormScreen) screen).getForm();
        } else {
            return null;
        }
    }

    @Override
    protected void buildMainContent() {
        buildBackButton();
        logAvailableLocalMenuItems();
        buildStatusItems();
        if (legacyStoreProperties != null && screen.getTemplate() instanceof SellTemplate) {
            SellTemplate template = screen.getTemplate();
            Workstation workstation = new Workstation();
            workstation.setStoreId(legacyStoreProperties.getStoreNumber());
            workstation.setWorkstationId(legacyStoreProperties.getWorkstationNumber());
            template.setWorkstation(workstation);
        }
        setScreenProperties();

        if (getLegacyUIModel() != null) {
            Integer timeout = getLegacyUIModel().getTimeout();
            if (timeout != null) {
                screen.setSessionTimeoutMillis(timeout);
            }
        }

        if (this.isSelfCheckout()) {
            this.screen.getTemplate().disableDevMenu();
        }

    }

    protected void setScreenProperties() {
        if (properties != null && screen != null) {
            boolean useOnScreenKeyboard = Boolean.valueOf((String) properties.get(USE_ON_SCREEN_KEYBOARD_PROP));
            if (useOnScreenKeyboard) {
                screen.setUseOnScreenKeyboard(useOnScreenKeyboard);
            }
        }
    }

    protected void buildStatusItems() {
        screen.setName(getScreenName());
        
        if (isBlank(screen.getIcon())) {
            screen.setIcon(iconRegistry.get(legacyScreen.getSpecName()));
        }
        
        if (screen.getTemplate() instanceof SellTemplate) {
            SellTemplate template = screen.getTemplate();
            String operatorText;
            if (properties != null && STATUS_BAR_USER_TEXT_FIRST_LAST_NAME.equals(properties.get(STATUS_BAR_USER_TEXT_PROP))) {
                operatorText = posSessionInfo.getOperatorName();
            } else {
                operatorText = posSessionInfo.getOperatorLoginId();
            }
            template.setOperatorText(WordUtils.capitalizeFully(operatorText));
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

    protected void buildBackButton() {
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(GLOBAL_NAV_PANEL_KEY);
        if (null != assignmentPanelSpec) {
            ILegacyBeanSpec globalNavSpec = this.legacyPOSBeanService.getLegacyBeanSpec(this.legacyScreen,
                    assignmentPanelSpec.getBeanSpecName());
            Map<String, Boolean> enabledState = parseButtonStates(assignmentPanelSpec);

            Arrays.stream(globalNavSpec.getButtons())
                    .filter(buttonSpec -> Optional.ofNullable(enabledState.get(buttonSpec.getActionName())).orElse(buttonSpec.getEnabled()))
                    .forEachOrdered(enabledButtonSpec -> {
                        if ("Undo".equals(enabledButtonSpec.getLabelTag())) {
                            screen.setBackButton(new MenuItem("Back", enabledButtonSpec.getActionName(), true));
                        }
                    });

        }
    }

    protected void clearTransactionMenuItems() {
        if (screen.getTemplate() instanceof SellTemplate) {
            SellTemplate sellTemplate = screen.getTemplate();
            sellTemplate.getTransactionMenuItems().clear();
        }
    }

    protected void addTransactionMenuItem(MenuItem menuItem) {
        if (screen.getTemplate() instanceof SellTemplate) {
            SellTemplate sellTemplate = screen.getTemplate();
            sellTemplate.addTransactionMenuItem(menuItem);
        }
    }

    protected void addLocalMenuItem(MenuItem menuItem) {
        if (screen.getTemplate() instanceof SellTemplate) {
            SellTemplate sellTemplate = screen.getTemplate();
            sellTemplate.addLocalMenuItem(menuItem);
        }
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
                posSessionInfo.setRegisterOpen(Optional.of(registerStatusService.getRegisterStatus() == Status.OPEN));

                Status storeStatus = registerStatusService.getStoreStatus();
                if (storeStatus != Status.UNKNOWN) {
                    posSessionInfo.setStoreOpen(Optional.of((storeStatus == Status.OPEN)));
                }
            }

            ILegacyBus bus = legacyPOSBeanService.getLegacyBus(legacyScreen);
            ILegacyCargo cargo = bus.getLegacyCargo();
            if (cargo != null) {
                posSessionInfo.setOperatorName(cargo.getOperatorFirstLastName());
                posSessionInfo.setOperatorLoginId(cargo.getOperatorLoginId());

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

    @Override
    protected void chooseLocale() {
        getScreen().setLocale(this.getLegacyPOSBeanService().getLegacyLocaleUtilities().getCurrentLocale().toLanguageTag());
    }

    @Override
    protected void chooseScreenName() {
        String screenName = null;
        ILegacyStatusBeanModel statusModel = this.legacyPOSBeanService.getLegacyStatusBeanModel(legacyScreen);
        if (statusModel != null && statusModel.getScreenName() != null) {
            screenName = statusModel.getScreenName();
        } else {
            String labelTag = getSpecPropertyValue(getLegacyAssignmentSpec(STATUS_PANEL_KEY), "screenNameTag", null);
            if (labelTag != null) {
                screenName = this.getLegacyUtilityManager().retrieveText("StatusPanelSpec", getResourceBundleFilename(), labelTag, labelTag);
            }
        }

        if (getScreen().getName() == null) {
            getScreen().setName(screenName);
        }
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

    protected <A extends IUIAction> List<A> generateUIActionsForLocalNavButtons(Class<A> actionClass, boolean filterDisabled,
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
                    buttonSpecs = ((ILegacyButtonSpec[]) ArrayUtils.addAll(localNavSpec.getButtons(), buttonModel.getNewButtons()));
                }
            } else {
                buttonSpecs = localNavSpec.getButtons();
            }

            for (ILegacyButtonSpec buttonSpec : buttonSpecs) {
                if (buttonSpec != null && !toExclude.contains(buttonSpec.getLabelTag())) {
                    Boolean enabled = enabledState.get(buttonSpec.getActionName());
                    if (enabled == null) {
                        enabled = buttonSpec.getEnabled();
                    }

                    String labelTag = buttonSpec.getLabelTag();
                    String label = labelTag;
                    String action = buttonSpec.getActionName();
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
                                        // Discovered while implementing the
                                        // GET_AMOUNT_FOR_GIFT_CARD screen that
                                        // the enabledFlag
                                        // may be null in the modified spec,
                                        // which causes getEnabled() to return
                                        // false. Hoping
                                        // that if I only override the enabled
                                        // value when the enabledFlag is
                                        // non-null, that
                                        // it will yield better results.
                                        enabled = modifiedSpec.getEnabledFlag();
                                    }
                                    if (isNotBlank(modifiedSpec.getLabel())) {
                                        label = modifiedSpec.getLabel();
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
        public boolean hideOrOverride(ILegacyScreen legacyScreen, String labelTag, IUIAction action);
    }

    protected void addLocalMenuButtons() {
        List<MenuItem> localNavButtons = generateUIActionsForLocalNavButtons(MenuItem.class, true);
        if (screen.getTemplate() instanceof SellTemplate) {
            SellTemplate template = screen.getTemplate();
            template.setLocalMenuItems(localNavButtons);
        }
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
}
