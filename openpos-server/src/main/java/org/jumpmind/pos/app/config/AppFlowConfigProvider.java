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
package org.jumpmind.pos.app.config;

import org.jumpmind.pos.app.state.HomeScreenState;
import org.jumpmind.pos.app.state.SellState;
import org.jumpmind.pos.app.state.user.UserLoginState;
import org.jumpmind.pos.core.flow.config.FlowBuilder;
import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.flow.config.IFlowConfigProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class AppFlowConfigProvider implements IFlowConfigProvider {

    @Override
    public FlowConfig getConfig(String appId, String nodeId) {
        FlowConfig config = new FlowConfig();
        config.setInitialState(FlowBuilder.addState(HomeScreenState.class)
                .withTransition("Sell", SellState.class).build());

        config.add(FlowBuilder.addState(UserLoginState.class).build());
        
        return config;
    }
    

}
