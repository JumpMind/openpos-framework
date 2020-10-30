/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.core.flow;

import java.util.Map;

import org.jumpmind.pos.core.error.IErrorHandler;
import org.jumpmind.pos.core.ui.CloseToast;
import org.jumpmind.pos.core.ui.Toast;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.UIDataMessageProvider;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.util.model.PrintMessage;


public interface IStateManager {

    public void keepAlive();

    void stop();

    public void init(String appId, String deviceId);
    public String getDeviceId();
    public String getAppId();
    public String getDeviceMode();
    public void setDeviceMode(String mode);
    public void doAction(String action);
    public void doAction(String action, Map<String, String> params);
    public void doAction(Action action);    
    public void transitionTo(Action action, Object newState);
    public void endConversation();
    public void endSession();
    public void showScreen(UIMessage screen, Map<String, UIDataMessageProvider<?>> dataMessageProviderMap);
    public void showScreen(UIMessage screen);
    public void showToast(Toast toast);
    public void closeToast(Toast toast);
    public void refreshScreen();
    public void reset();
    public Object getCurrentState();
    public ApplicationState getApplicationState();
	public void performOutjections(Object object);
	public void setApplicationState(ApplicationState applicationState);
	public void performInjections(Object object);
	public void performInjectionsOnSpringBean(Object object);
	public void setSessionAuthenticated(String sessionId, boolean authenticated);
	public boolean isSessionAuthenticated(String sessionId);
	public boolean areAllSessionsAuthenticated();
    public void setSessionCompatible(String sessionId, boolean compatible);
    public boolean isSessionCompatible(String sessionId);
    public boolean areAllSessionsCompatible();
    public void registerQueryParams(Map<String,Object> queryParams);
    public void registerPersonalizationProperties(Map<String, String> personalizationProperties);
    public Injector getInjector();
    public boolean isAtRest();
    public void setErrorHandler(IErrorHandler errorHandler);
    public void sendConfigurationChangedMessage();
    public void setClientContext(Map<String,String> context);
    public Map<String, String> getClientContext();
    public long getLastActionTimeInMs();
    public void sendStartupCompleteMessage();
    public void sendPrintMessage(PrintMessage message);
}
