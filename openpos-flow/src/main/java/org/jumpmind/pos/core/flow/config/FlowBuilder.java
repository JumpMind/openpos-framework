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

import org.jumpmind.pos.core.flow.CompleteState;

public class FlowBuilder implements IFlowBuilder {
    
    private StateConfig stateConfig;

    public static FlowBuilder addInitialState(Class<? extends Object> state) {
        FlowBuilder builder = addState(state);
        return builder;
    }
    
    public static FlowBuilder addState(Class<? extends Object> state) {
        FlowBuilder builder = new FlowBuilder();
        builder.stateConfig = new StateConfig();
        builder.stateConfig.setStateName(FlowUtil.getStateName(state));
        builder.stateConfig.setStateClass(state);
        return builder;
    }
    
    @Override
    public IFlowBuilder withTransition(String actionName, Class<? extends Object> destination) {
        stateConfig.getActionToStateMapping().put(actionName, destination);        
        return this;
    }

    @Override
    public StateConfig build() {
        return stateConfig;
    }
    
    @Override
    public IFlowBuilder withSubTransition(String actionName, Class<? extends Object> destination, String... returnActions) {
        FlowConfig flowConfig = new FlowConfig(destination.getSimpleName());
        FlowBuilder builder = FlowBuilder.addState(destination);
        for (String returnAction : returnActions) {
            builder.withTransition(returnAction, CompleteState.class);
        }
        flowConfig.setInitialState(builder.build());
        SubTransition subTransition = new SubTransition(returnActions, flowConfig);
        stateConfig.getActionToSubStateMapping().put(actionName, subTransition);
        return this;
    }

    @Override
    public IFlowBuilder withSubTransition(String actionName, FlowConfig flowConfig, String... returnActions) {
        
        for (String returnAction : returnActions) {
            if (flowConfig.getActionToStateMapping().get(returnAction) == null
                    && flowConfig.getActionToSubStateMapping().get(returnAction) == null) {
                flowConfig.getActionToStateMapping().put(returnAction, CompleteState.class);
            }
        }
        
        SubTransition subTransition = new SubTransition(returnActions, flowConfig);
        stateConfig.getActionToSubStateMapping().put(actionName, subTransition);
        return this;
    }
}
