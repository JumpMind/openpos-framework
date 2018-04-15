package org.jumpmind.pos.translate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.device.IDeviceMessageDispatcher;
import org.jumpmind.pos.core.device.IDeviceRequest;
import org.jumpmind.pos.core.device.IDeviceResponse;
import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.POSSessionInfo;
import org.jumpmind.pos.core.screen.AbstractScreen;
import org.jumpmind.pos.core.screen.NoOpScreen;
import org.jumpmind.pos.translate.InteractionMacro.AbortMacro;
import org.jumpmind.pos.translate.InteractionMacro.DoOnActiveScreen;
import org.jumpmind.pos.translate.InteractionMacro.DoOnScreen;
import org.jumpmind.pos.translate.InteractionMacro.SendLetter;
import org.jumpmind.pos.translate.InteractionMacro.WaitForScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslationManagerServer implements ITranslationManager, IDeviceMessageDispatcher {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private Class<?> subsystemClass;

    private ILegacySubsystem legacySubsystem;

    private ITranslatorFactory screenTranslatorFactory;

    private ILegacyScreenInterceptor screenInterceptor;

    private InteractionMacro activeMacro;

    private Map<String, ITranslator> lastTranslatorByAppId = new HashMap<>();

    private Map<String, ITranslationManagerSubscriber> subscriberByAppId = new HashMap<>();

    private POSSessionInfo posSessionInfo = new POSSessionInfo();
    
    private boolean lastScreenWasNoOp = false;

    public TranslationManagerServer(ILegacyScreenInterceptor interceptor, ITranslatorFactory screenTranslatorFactory,
            Class<?> subsystemClass) {
        this.subsystemClass = subsystemClass;
        this.posSessionInfo = new POSSessionInfo();
        this.screenInterceptor = interceptor;
        this.screenTranslatorFactory = screenTranslatorFactory;
    }

    @Override
    public void ping() {
    }

    @Override
    public void showActiveScreen() {
        showLegacyScreen(getLegacyUISubsystem().getActiveScreen());
    }

    @Override
    public void setTranslationManagerSubscriber(ITranslationManagerSubscriber subscriber) {
        ITranslationManagerSubscriber currentSubscriber = this.subscriberByAppId.get(subscriber.getAppId());
        
        if (currentSubscriber == null) {
            this.subscriberByAppId.put(subscriber.getAppId(), subscriber);
            getLegacyUISubsystem().setLegacyScreenListener(this);
        }
    }

    @Override
    public void doAction(String appId, Action action, Form formResults) {
        ITranslator lastTranslator = this.lastTranslatorByAppId.get(appId);
        logger.debug("lastTranslator = {}", lastTranslator);
        if (lastTranslator != null) {
            lastTranslator.handleAction(subscriberByAppId.get(appId), this, action, formResults);
        } else {
            sendAction(action.getName());
        }
    }

    @Override
    public void showLegacyScreen(ILegacyScreen screen) {
        if (screen != null && screen.isStatusUpdate()) {
            if (!this.lastScreenWasNoOp) {
                // We don't currently handle updates to the status panel only
                logger.info("Suppressing SHOW_STATUS_ONLY update for screen {}, sending NoOp", screen.getSpecName());
                /*
                 * Some scenarios (such as 'Print Store Address' on Item
                 * Inventory screen) end the flow with simply a status update.
                 * This would leave clients waiting for a response. Send a no-op
                 * response so that clients know the server is still alive.
                 */
                show(new NoOpScreen());
                this.lastScreenWasNoOp = true;
            }
        } else {
            if (executeActiveMacro(screen)) {
                translateAndShow(screen);
                lastScreenWasNoOp = false;
            }
        }
    }

    public void executeMacro(InteractionMacro macro) {
        this.activeMacro = macro;
        executeActiveMacro(null);
    }

    protected boolean executeActiveMacro(ILegacyScreen screen) {
        boolean showScreen = true;
        if (activeMacro != null) {
            showScreen = false;
            List<Object> objects = activeMacro.getInteractionQueue();
            if (objects != null && objects.size() > 0) {
                Object current = objects.get(0);
                while (current != null) {
                    if (current instanceof SendLetter) {
                        sendAction(((SendLetter) current).getLetter());
                        current = next(objects);
                    } else if (current instanceof WaitForScreen && screen != null) {
                        WaitForScreen wait = (WaitForScreen) current;
                        String currentScreenName = null;
                        boolean match = false;
                        if (screen.isDialog()) {
                            currentScreenName = screen.getDialogResourceId();
                        } else {
                            currentScreenName = screen.getSpecName();
                        }
                        match = wait.getScreenNames().contains(currentScreenName);
                        if (!match) {
                            break;
                        } else {
                            current = next(objects);
                        }
                    } else if (current instanceof DoOnScreen && screen != null) {
                        Object toProcess = current;
                        current = next(objects);
                        ((DoOnScreen) toProcess).doOnScreen(screen);

                    } else if (current instanceof DoOnActiveScreen) {
                        Object toProcess = current;
                        current = next(objects);
                        ((DoOnActiveScreen) toProcess).doOnScreen(this.getActiveScreen());

                    } else if (current instanceof AbortMacro && screen != null) {
                        if (((AbortMacro) current).abort(screen)) {
                            activeMacro = null;
                            showScreen = true;
                            break;
                        } else {
                            current = next(objects);
                        }
                    } else {
                        break;
                    }
                }

                if (objects.size() == 0) {
                    activeMacro = null;
                }
            } else {
                activeMacro = null;
            }
        }
        return showScreen;
    }

    private Object next(List<Object> objects) {
        if (objects.size() > 0) {
            objects.remove(0);
            return objects.size() > 0 ? objects.get(0) : null;
        } else {
            return null;
        }
    }

    protected void show(AbstractScreen screen) {
        for (ITranslationManagerSubscriber subscriber : this.subscriberByAppId.values()) {
            if (screen != null && subscriber.isInTranslateState()) {
                subscriber.showScreen(screen);
            }
        }
    }

    protected void translateAndShow(ILegacyScreen legacyScreen) {
        for (ITranslationManagerSubscriber subscriber : this.subscriberByAppId.values()) {
            if (legacyScreen != null && subscriber.isInTranslateState()) {
                ITranslator lastTranslator = this.lastTranslatorByAppId.get(subscriber.getAppId());
                
                ILegacyScreen previousScreen = null;
                if (lastTranslator instanceof AbstractScreenTranslator<?>) {
                    previousScreen = ((AbstractScreenTranslator<?>) lastTranslator).getLegacyScreen();
                }
                
                if (!screenInterceptor.intercept(legacyScreen, previousScreen, subscriber, this, posSessionInfo)) {
                    ITranslator newTranslator = screenTranslatorFactory.createScreenTranslator(legacyScreen, subscriber.getAppId(),
                            subscriber.getProperties());
                    
                    if (newTranslator != null) {
                        newTranslator.setPosSessionInfo(posSessionInfo);
                    }
                    if (newTranslator instanceof AbstractScreenTranslator<?>) {
                        AbstractScreenTranslator<?> screenTranslator = (AbstractScreenTranslator<?>) newTranslator;                        
                        AbstractScreen screen = screenTranslator.build();
                        subscriber.showScreen(screen);
                    } else if (newTranslator instanceof IActionTranslator) {
                        ((IActionTranslator)newTranslator).translate(this, subscriber);
                    }                    

                    this.lastTranslatorByAppId.put(subscriber.getAppId(), newTranslator);

                } else {
                    this.lastTranslatorByAppId.put(subscriber.getAppId(), null);
                }
            }
        }
    }

    public ILegacyScreen getActiveScreen() {
        ILegacyScreen screen = getLegacyUISubsystem().getActiveScreen();
        return screen;
    }

    public ILegacySubsystem getLegacyUISubsystem() {
        if (legacySubsystem == null) {
            try {
                Method method = subsystemClass.getMethod("getInstance");
                legacySubsystem = (ILegacySubsystem) method.invoke(null);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return legacySubsystem;
    }

    public void sendAction(String action) {
        getLegacyUISubsystem().sendAction(action);
    }

    public void sendAction(String action, int number) {
        getLegacyUISubsystem().sendAction(action, number);
    }

    @Override
    public IDeviceResponse sendDeviceRequest(IDeviceRequest request) {
        IDeviceResponse response = null;
        for (ITranslationManagerSubscriber subscriber : this.subscriberByAppId.values()) {
            response = subscriber.sendDeviceRequest(request);
            break;
        }

        return response;
    }

}
