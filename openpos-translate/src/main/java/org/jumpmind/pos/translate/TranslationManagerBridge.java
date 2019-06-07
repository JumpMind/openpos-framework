package org.jumpmind.pos.translate;

import static java.lang.String.format;

import org.jumpmind.pos.core.device.IDeviceRequest;
import org.jumpmind.pos.core.device.IDeviceResponse;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.util.RMICallbackProxyManager;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class TranslationManagerBridge implements ITranslationManager {
    static private final Logger logger = LoggerFactory.getLogger(TranslationManagerBridge.class);

    @Autowired
    ILegacyStartupService headlessStartupService;
    
    @Autowired
    RMICallbackProxyManager rmiCallbackProxyManager;

    @Value("${openpos.translate.rmi.registry.port:9598}")
    int rmiRegistryPort;

    @Value("${openpos.translate.external.process.enabled:false}")
    boolean externalProcessEnabled;
    
    ITranslationManagerSubscriber subscriber;

    String nodeId;

    @Override
    public void setTranslationManagerSubscriber(ITranslationManagerSubscriber subscriber) {
        this.subscriber = subscriber;
        this.nodeId = subscriber.getNodeId();
        ITranslationManager implementation = headlessStartupService.getTranslationManagerRef(nodeId);
        logger.debug( "ITranslationManager implementation for nodeId {} is: {}", nodeId, implementation );
        if (implementation == null) {
            headlessStartupService.start(subscriber.getNodeId());
        }

        // Only create a Remote-able proxy TranslactionManagerSubscriber if we are running
        // in external mode, otherwise use reference to local TranslactionManagerSubscriber
        exportTranslationManagerSubscriber();

        setTranslationManagerSubscriber();
    }

    @Override
    public void doAction(String appId, Action action, Form formResults) {
        try {
            ITranslationManager implementation = headlessStartupService.getTranslationManagerRef(nodeId);
            implementation.doAction(appId, action, formResults);
        } catch (RemoteConnectFailureException e) {
            headlessStartupService.start(nodeId);
            setTranslationManagerSubscriber();
            ITranslationManager implementation = headlessStartupService.getTranslationManagerRef(nodeId);
            implementation.doAction(appId, action, formResults);
        }
    }

    @Override
    public void showActiveScreen() {
        try {
            ITranslationManager implementation = headlessStartupService.getTranslationManagerRef(nodeId);
            implementation.showActiveScreen();
        } catch (RemoteConnectFailureException e) {
            headlessStartupService.start(nodeId);
            setTranslationManagerSubscriber();
            ITranslationManager implementation = headlessStartupService.getTranslationManagerRef(nodeId);
            implementation.showActiveScreen();
        }
    }
    
    @Override
    public IDeviceResponse sendDeviceRequest(IDeviceRequest request) {
        try {
            ITranslationManager implementation = headlessStartupService.getTranslationManagerRef(nodeId);
            return implementation.sendDeviceRequest(request);
        } catch (RemoteConnectFailureException e) {
            headlessStartupService.start(nodeId);
            setTranslationManagerSubscriber();
            ITranslationManager implementation = headlessStartupService.getTranslationManagerRef(nodeId);
            return implementation.sendDeviceRequest(request);
        }
    }
    
    
    @Override
    public void ping() {
    }

    private void exportTranslationManagerSubscriber() {
        if ( this.externalProcessEnabled ) {
        	logger.debug( "externalProcess mode detected, creating Remotable version of given TranslationManagerSubscriber {}", this.subscriber );
            // Wrap the client-side subscriber in a dynamically created Remote wrapper so that we can make callbacks to it
        	// from the remote process
            this.subscriber = rmiCallbackProxyManager.registerAndExport(this.subscriber, ITranslationManagerSubscriber.class);
        } else {
            logger.debug( "externalProcess mode not detected, bypassing creation of RMI proxy for {}", this.subscriber );
        }
    }

    private void setTranslationManagerSubscriber() {
        ITranslationManager implementation = headlessStartupService.getTranslationManagerRef(nodeId);
        if (implementation != null) {
            implementation.setTranslationManagerSubscriber(this.subscriber);
            
            logger.debug( "TranslationManagerSubscriber set to {} on TranslationManager: {}", this.subscriber, implementation );
        } else {
            throw new IllegalStateException(format("Failed to find a translator for %s", nodeId));
        }
    }

	@Override
	public boolean processLegacyScreen(ILegacyScreen screen) {
		return false;
		
	}

	@Override
	public void executeMacro(InteractionMacro macro) {
	}

	@Override
	public void sendAction(String action) {
	}

	@Override
	public boolean showLegacyScreen(ILegacyScreen screen) {
		return false;
	}


}
