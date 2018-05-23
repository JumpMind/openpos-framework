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

import java.util.HashMap;
import java.util.Map;

public class Scope {

    private Map<String, ScopeValue> nodeScope = new HashMap<String, ScopeValue>();
    private Map<String, ScopeValue> sessionScope = new HashMap<String, ScopeValue>();
    private Map<String, ScopeValue> conversationScope = new HashMap<String, ScopeValue>();
    
    public void clearConversationScope() {
        conversationScope.clear();
    }
    public void clearSessionScope() {
        clearConversationScope();
        sessionScope.clear();
    }

    public ScopeValue resolve(String name) {
        if (conversationScope.containsKey(name)) {
            return conversationScope.get(name);
        } else if (sessionScope.containsKey(name)) {
            return sessionScope.get(name);
        } else if (nodeScope.containsKey(name)) {
            return nodeScope.get(name);
        }
        return null;
    }
    
    public void setNodeScope(String name, Object value) {
        setScope(nodeScope, name, value);
    }
    public void setSessionScope(String name, Object value) {
        setScope(sessionScope, name, value);
    }
    public void setConversationScope(String name, Object value) {
        setScope(conversationScope, name, value);
    }
    
    protected void setScope(Map<String, ScopeValue> scope, String name, Object value) {
        ScopeValue scopeValue = new ScopeValue();
        scopeValue.setValue(value);
        scope.put(name, scopeValue);
    }
    public Map<String, ScopeValue> getNodeScope() {
        return nodeScope;
    }
    public Map<String, ScopeValue> getSessionScope() {
        return sessionScope;
    }
    public Map<String, ScopeValue> getConversationScope() {
        return conversationScope;
    }

}
