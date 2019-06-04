package org.jumpmind.pos.core.service;

import org.jumpmind.pos.core.flow.IScreenInterceptor;
import org.jumpmind.pos.core.screen.Toast;
import org.jumpmind.pos.core.ui.UIMessage;

public interface IScreenService {

    public void showScreen(String appId, String nodeId, UIMessage screen);

    public void showToast(String appId, String nodeId, Toast toast);

    public UIMessage getLastScreen(String appId, String nodeId);

    public UIMessage getLastDialog(String appId, String nodeId);

    public void addScreenInterceptor(IScreenInterceptor interceptor);

    public void removeScreenInterceptor(IScreenInterceptor interceptor);

    public UIMessage getLastPreInterceptedScreen(String appId, String deviceId);

    public UIMessage getLastPreInterceptedDialog(String appId, String deviceId);

}
