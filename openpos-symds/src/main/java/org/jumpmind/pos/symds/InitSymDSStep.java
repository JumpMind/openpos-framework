package org.jumpmind.pos.symds;

import org.jumpmind.pos.core.flow.*;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.message.DialogUIMessage;
import org.jumpmind.pos.core.ui.messagepart.DialogHeaderPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;
import org.jumpmind.pos.util.AppUtils;
import org.jumpmind.symmetric.ISymmetricEngine;
import org.jumpmind.symmetric.service.INodeService;
import org.jumpmind.symmetric.service.IRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class InitSymDSStep implements ITransitionStep {

    @Autowired
    protected Environment env;

    @Autowired
    ISymmetricEngine symmetricEngine;

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @Autowired
    private AsyncExecutor asyncExecutor;

    Transition transition;

    boolean registered = false;

    @Override
    public boolean isApplicable(Transition transition) {
        this.transition = transition;
        return isApplicable();
    }

    boolean isApplicable() {
        INodeService nodeService = symmetricEngine.getNodeService();
        if ("true".equals(env.getProperty("openpos.symmetric.start", "false")) &&
                "true".equals(env.getProperty("openpos.symmetric.waitForInitialLoad", "false")) &&
                !nodeService.isRegistrationServer()) {

            IRegistrationService registrationService = symmetricEngine.getRegistrationService();
            return !registrationService.isRegisteredWithServer() || !nodeService.isDataLoadCompleted();
        }
        return false;
    }

    @ActionHandler
    void onCheckAgain() {
        if (isApplicable()) {
            check();
        } else {
            transition.proceed();
        }
    }

    @Override
    public void arrive(Transition transition) {
        this.transition = transition;
        check();
        asyncExecutor.execute(null, req-> {
            IRegistrationService registrationService = symmetricEngine.getRegistrationService();
            INodeService nodeService = symmetricEngine.getNodeService();
            do {
                AppUtils.sleep(5000);
                if (registrationService.isRegisteredWithServer() && !nodeService.isDataLoadCompleted() && !registered) {
                    registered = true;
                    stateManager.doAction("CheckAgain");
                }
            } while (!nodeService.isDataLoadCompleted());
            stateManager.doAction("CheckAgain");
            return null;
        }, res-> {}, err -> {});
    }

    protected void check() {
        IRegistrationService registrationService = symmetricEngine.getRegistrationService();
        INodeService nodeService = symmetricEngine.getNodeService();

        if (!registrationService.isRegisteredWithServer()) {
            showNotRegisteredUnit();
        } else if (!nodeService.isDataLoadCompleted()) {
            registered = true;
            showDataLoadInProgress();
        } else {
            transition.proceed();
        }
    }

    protected void showNotRegisteredUnit() {
        DialogUIMessage screen = new DialogUIMessage();
        screen.setId("NotRegisteredDialog");
        DialogHeaderPart headerPart = new DialogHeaderPart();
        headerPart.setHeaderText("This device is not registered for data replication");
        screen.addMessagePart(MessagePartConstants.DialogHeader, headerPart);
        screen.addButton(new ActionItem("CheckAgain", "Check Again"));
        stateManager.showScreen(screen);
    }

    protected void showDataLoadInProgress() {
        DialogUIMessage screen = new DialogUIMessage();
        screen.setId("DataLoadInProgressDialog");
        DialogHeaderPart headerPart = new DialogHeaderPart();
        headerPart.setHeaderText("The initial data load is in progress ...");
        screen.addMessagePart(MessagePartConstants.DialogHeader, headerPart);
        screen.addButton(new ActionItem("CheckAgain", "Check Again"));
        stateManager.showScreen(screen);
    }

}
