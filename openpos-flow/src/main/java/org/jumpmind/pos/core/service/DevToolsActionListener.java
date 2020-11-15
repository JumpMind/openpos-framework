package org.jumpmind.pos.core.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.jumpmind.pos.core.flow.ScopeValue;
import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.flow.config.StateConfig;
import org.jumpmind.pos.core.javapos.SimulatedScannerService;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.core.model.OpenposBarcodeType;
import org.jumpmind.pos.core.model.ScanData;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IActionListener;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.AudioLicense;
import org.jumpmind.pos.util.AudioLicenseUtil;
import org.jumpmind.pos.util.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jpos.events.DataEvent;

@Component
public class DevToolsActionListener implements IActionListener {

    private static final String SAVE_PATH = "./savepoints";

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    IStateManagerContainer stateManagerFactory;
    
    @Autowired
    IMessageService messageService;

    @Override
    public Collection<String> getRegisteredTypes() {        
        return Arrays.asList(new String[] { "DevTools" });
    }
    
    @Override
    public void actionOccured(String appId, String deviceId, Action action) {
        IStateManager stateManager = stateManagerFactory.retrieve(appId, deviceId);
        
        if (action.getName().contains("DevTools::Scan")) {
            SimulatedScannerService service = SimulatedScannerService.instance;
            if (service != null) {
                service.setScanData(((String)action.getData()).getBytes());
                service.getCallbacks().fireDataEvent(new DataEvent(this, 1));
            } else {
                ScanData scanData = new ScanData();
                scanData.setData(action.getData());
                scanData.setType(OpenposBarcodeType.CODE128);
                stateManager.doAction(new Action("Scan", scanData));
            }
        } else if (action.getName().contains("::Remove")) {
            Map<String, String> element = action.getData();
            String scopeId = element.get("ID");
            if (action.getName().contains("::Node")) {
                stateManager.getApplicationState().getScope().removeDeviceScope(scopeId);
            } else if (action.getName().contains("::Session")) {
                stateManager.getApplicationState().getScope().removeSessionScope(scopeId);
            } else if (action.getName().contains("::Conversation")) {
                stateManager.getApplicationState().getScope().removeConversationScope(scopeId);
            } else if (action.getName().contains("::Config")) {
                stateManager.getApplicationState().getCurrentContext().getFlowConfig().getConfigScope().remove(scopeId);
            } else if (action.getName().contains("::Flow")) {
                stateManager.getApplicationState().getCurrentContext().removeFlowScope(scopeId);
            }

        }

        messageService.sendMessage(appId, deviceId, createMessage(stateManager));
    }
    
    private Message createMessage(IStateManager sm) {
        Message message = new Message();
        message.setType(MessageType.DevTools);
        message.put("name", "DevTools::Get");
        message.put("audioLicenses", getAudioLicenses());
        message.put("audioLicenseLabels", getAudioLicenseLabels());
        setScopes(sm, message);
        setCurrentStateAndActions(sm, message);
        return message;
    }

    private List<AudioLicense> getAudioLicenses() {
        try {
            return AudioLicenseUtil.getLicenses();
        } catch(IOException e) {
            logger.warn("Unable to load audio licenses", e);
        }

        return null;
    }

    private Map<String, String> getAudioLicenseLabels() {
        return new HashMap<String, String>() {
            {
                put("key", "Content Key:");
                put("author", "Author:");
                put("title", "Title:");
                put("sourceUri", "Source URI:");
                put("filename", "File Name:");
                put("license", "License:");
                put("licenseUri", "License URI:");
                put("comments", "Comments:");
            }
        };
    }

    private void setCurrentStateAndActions(IStateManager sm, Message message) {
        loadSaveFiles(message);

        List<String> actions = new ArrayList<>();
        Object currentState = sm.getCurrentState();
        FlowConfig fc = sm.getApplicationState().getCurrentContext().getFlowConfig();
        if (fc != null) {
            StateConfig sc = fc.getStateConfig(currentState);
            Set<String> keySet = sc.getActionToStateMapping().keySet();
            actions.clear();
            for (String key : keySet) {
                actions.add(key);
                actions.add(sc.getActionToStateMapping().get(key).toString().replace("class ", ""));
            }
            message.put("actions", actions);
            message.put("actionsSize", actions.size());
            message.put("currentState", sm.getApplicationState().getCurrentContext().getFlowConfig().getStateConfig(currentState));
        }
    }

    private void loadSaveFiles(Message message) {

        List<String> saveFiles = new ArrayList<>();
        File dir = null;
        try {
            dir = new File(SAVE_PATH);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                int i = 0;
                for (File child : directoryListing) {
                    saveFiles.add(child.getName().replaceAll(".json", ""));
                    i++;
                }
                logger.info("Loaded " + i + " save files from " + dir);
            } else {
                logger.warn("No save files found in save directory.");
            }
        } catch (Exception ex) {
            logger.warn("Cannot load save files from " + SAVE_PATH);
        }

        message.put("saveFiles", saveFiles);
    }

    private void setScopes(IStateManager sm, Message message) {
        try {
            Map<String, List<ScopeField>> scopes = new HashMap<>();
            scopes.put("ConversationScope", buildScope(sm.getApplicationState().getScope().getConversationScope()));
            scopes.put("DeviceScope", buildScope(sm.getApplicationState().getScope().getDeviceScope()));
            scopes.put("SessionScope", buildScope(sm.getApplicationState().getScope().getSessionScope()));
            scopes.put("FlowScope", buildScope(sm.getApplicationState().getCurrentContext().getFlowScope()));
            scopes.put("ConfigScope", buildConfigScope(sm.getApplicationState().getCurrentContext().getFlowConfig().getConfigScope()));
            message.put("scopes", scopes);
        } catch (Exception ex) {
            logger.warn("Error loading in Developer Tool application scopes. Deleting local storage may fix this.", ex);
        }
    }

    private List<ScopeField> buildScope(Map<String, ScopeValue> map) {
        Set<String> keys = map.keySet();
        List<ScopeField> res = new ArrayList<>();
        for (String key : keys) {
            res.add(new ScopeField(key, map.get(key).getCreatedTime().toString(),
                    map.get(key).getCreatedStackTrace().replaceAll("at ", "\nat_")));
        }
        return res;
    }

    private List<ScopeField> buildConfigScope(Map<String, Object> map) {
        Set<String> keys = map.keySet();
        List<ScopeField> res = new ArrayList<>();
        for (String key : keys) {
            res.add(new ScopeField(key, map.get(key).toString(), map.get(key).toString().replaceAll("at ", "\nat_")));
        }
        return res;
    }



}
