package org.jumpmind.pos.core.service;

import org.jumpmind.pos.core.ui.CloseToast;
import org.jumpmind.pos.core.ui.Toast;
import org.jumpmind.pos.core.ui.UIDataMessage;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.UIDataMessageProvider;

import java.util.Map;

public interface IScreenService {

    public void showScreen(String nodeId, UIMessage screen, Map<String, UIDataMessageProvider<?>> dataMessageProvider);

    public void showScreen(String nodeId, UIMessage screen);

    public void showToast(String nodeId, Toast toast);

    public void closeToast(String nodeId, CloseToast toast);

    public UIMessage getLastScreen(String nodeId);

    public UIMessage getLastDialog(String nodeId);

//    public void addToastInterceptor(IMessageInterceptor<Toast> interceptor);
//
//    public void removeToastInterceptor(IMessageInterceptor<Toast> interceptor);
//
//    public void addScreenInterceptor(IMessageInterceptor<UIMessage> interceptor);
//
//    public void removeScreenInterceptor(IMessageInterceptor<UIMessage> interceptor);

    public UIMessage getLastPreInterceptedScreen(String deviceId);

    public UIMessage getLastPreInterceptedDialog(String deviceId);

}
