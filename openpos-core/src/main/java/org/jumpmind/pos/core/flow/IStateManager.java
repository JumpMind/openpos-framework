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

import org.jumpmind.pos.core.screen.Screen;


public interface IStateManager {

    public void keepAlive();
    public void init(String appId, String nodeId);
    public String getNodeId();
    public String getAppId();
    public void doAction(String action);
    public void doAction(String action, Map<String, String> params);
    public void doAction(Action action);    
    public void transitionTo(Action action, IState newState);
    public void endConversation();
    public void endSession();
    public <T> T getScopeValue(String name);
    public void showScreen(Screen screen);
    public void refreshScreen();
    public IState getCurrentState();
    public IUI getUI();
    
}
