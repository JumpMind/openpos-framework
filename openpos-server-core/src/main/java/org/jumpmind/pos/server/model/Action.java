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

import org.jumpmind.pos.util.DefaultObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.ToString;

@ToString(onlyExplicitlyIncluded = true)
@Data
public class Action implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    
    public static final Action ACTION_TIMEOUT = new Action("Timeout");
    
    @ToString.Include
    private String name;
    private Object data;
    private String type;
    private String requiredPermissionId;
    @ToString.Include
    private boolean doNotBlockForResponse;
    private transient Action causedBy; // Used when renaming an action during a substate return.
    
    static ObjectMapper mapper = DefaultObjectMapper.build();

    public Action() {
        this(null);
    }
    
    public Action(String actionName) {
        this(actionName, null);
    }
    
    public Action(String actionName, Object data) {
        this(actionName, data, null);
    }
    
    public static <T> T convertActionData(Object actionData, Class<T> convertToInstanceOf) {
        return mapper.convertValue(actionData, convertToInstanceOf);
    }
    
    public Action(String actionName, Object data, String requiredPermissionId) {
        this.name = actionName;
        this.data = data;
        this.requiredPermissionId = requiredPermissionId;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T)data;
    }

    
    public String toDataString() {
        return data != null ? data.toString() : null;
    }


}
