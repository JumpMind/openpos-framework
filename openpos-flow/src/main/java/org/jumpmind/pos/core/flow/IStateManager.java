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

import org.jumpmind.pos.core.ui.Toast;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.model.Action;


public interface IStateManager {

    public void keepAlive();
    public void init(String appId, String deviceId);
    @Deprecated
    public String getNodeId();
    public String getDeviceId();
    public String getAppId();
    public void doAction(String action);
    public void doAction(String action, Map<String, String> params);
    public void doAction(Action action);    
    public void transitionTo(Action action, Object newState);
    public void timeout();
    public void endConversation();
    public void endSession();
    public void showScreen(UIMessage screen);
    public void showToast(Toast toast);
    public void refreshScreen();
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
    public void sendConfigurationChangedMessage();
    
}
