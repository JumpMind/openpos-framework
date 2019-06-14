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
package org.jumpmind.pos.core.flow.config;

import java.util.ArrayList;
import java.util.List;

public class YamlFlowConfig {
    
    private String flowName;

    List<YamlStateConfig> flowStateConfigs = new ArrayList<>();
    
    public String getFlowName() {
        return flowName;
    }
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
    public List<YamlStateConfig> getFlowStateConfigs() {
        return flowStateConfigs;
    }
    public void setFlowStateConfigs(List<YamlStateConfig> flowStateConfigs) {
        this.flowStateConfigs = flowStateConfigs;
    }
    @Override
    public String toString() {
        return "YamlFlowConfig [flowName=" + flowName + ", flowStateConfigs=" + flowStateConfigs + "]";
    }
}
