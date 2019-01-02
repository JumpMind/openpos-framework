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
package org.jumpmind.pos.server.model;

import java.io.Serializable;

public class Action implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    
    public static final Action ACTION_TIMEOUT = new Action("Timeout");
    
    private String name;
    private Object data;
    private String type;
    private transient Action causedBy; // Used when renaming an action during a substate return.

    public Action() {
        this(null);
    }
    
    public Action(String actionName) {
        this(actionName, null);
    }
    
    public Action(String actionName, Object data) {
        this.name = actionName;
        this.data = data;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T)data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    public String toDataString() {
        return data != null ? data.toString() : null;
    }

    public Action getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(Action causedBy) {
        this.causedBy = causedBy;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Action [name=" + name + "]";
    }
}
