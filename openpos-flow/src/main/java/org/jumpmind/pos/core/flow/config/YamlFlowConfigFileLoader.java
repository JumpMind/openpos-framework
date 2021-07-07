/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 * <p>
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * http://www.gnu.org/licenses.
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.core.flow.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.FlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("unchecked")
public class YamlFlowConfigFileLoader {

    static final Logger log = LoggerFactory.getLogger(YamlFlowConfigFileLoader.class);

    protected final String GLOBAL_EVENT_HANDLERS = "EventHandlers";

    public List<YamlFlowConfig> loadYamlFlowConfigs(String path) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path);
        return loadYamlFlowConfigs(inputStream);
    }

    public List<YamlFlowConfig> loadYamlFlowConfigs(InputStream inputStream) {
        Yaml yaml = new Yaml();

        List<YamlFlowConfig> yamlFlowConfigs = new ArrayList<>();

        Map<String, Object> yamlDoc = (Map<String, Object>) yaml.load(inputStream);

        for (String flowName : yamlDoc.keySet()) {
            YamlFlowConfig flowConfig = new YamlFlowConfig();
            flowConfig.setFlowName(flowName);

            List<Map<String, Object>> stateConfigs = (List<Map<String, Object>>) yamlDoc.get(flowName);
            for (Map<String, Object> stateConfig : stateConfigs) {
                for (String stateName : stateConfig.keySet()) {
                    parseStateConfig(flowConfig, new YamlStateConfig(stateName), (Map<String, Object>) stateConfig.get(stateName));
                }
            }

            yamlFlowConfigs.add(flowConfig);
        }

        return yamlFlowConfigs;
    }

    public void parseStateConfig(YamlFlowConfig flowConfig, YamlStateConfig currentStateConfig, Map<String, Object> stateActionMapping) {
        flowConfig.getFlowStateConfigs().add(currentStateConfig);

        if (stateActionMapping == null) {
            return;
        }

        for (String actionName : stateActionMapping.keySet()) {
            Object stateReferenceOrConfig = stateActionMapping.get(actionName);
            if (actionName.equals(GLOBAL_EVENT_HANDLERS)) {
                if (stateReferenceOrConfig instanceof List) {
                    List<String> eventHandlers = (List)stateReferenceOrConfig;
                    flowConfig.setGlobalEventHandlers(eventHandlers);
                    log.debug("Added event handers: {} to: {}", stateReferenceOrConfig, flowConfig.getFlowName());
                }
            } else if (stateReferenceOrConfig instanceof String) {
                YamlStateConfig stateConfig = new YamlStateConfig((String) stateReferenceOrConfig);
                flowConfig.getFlowStateConfigs().add(stateConfig);
                currentStateConfig.getActionToStateConfigs().put(actionName, stateConfig);
            } else {
                Map<String, Object> nestedStateConfig = (Map<String, Object>) stateReferenceOrConfig;
                if (nestedStateConfig.containsKey("subflow")) {
                    YamlStateConfig subStateConfig = loadSubstateReference(actionName, nestedStateConfig);
                    flowConfig.getFlowStateConfigs().add(subStateConfig);
                    currentStateConfig.getActionToStateConfigs().put(actionName, subStateConfig);
                } else {
                    String nestedStateName = (String) nestedStateConfig.keySet().toArray()[0];
                    YamlStateConfig inlineState = new YamlStateConfig(nestedStateName);
                    // OK here, a concrete action would get added as an action reference as well as top level.
                    if (nestedStateConfig.get(nestedStateName) instanceof Map) {
                        parseStateConfig(flowConfig, inlineState, (Map<String, Object>) nestedStateConfig.get(nestedStateName));  //recurse
                    } else {
                        throw new FlowException("Malformed yml state flow config. "
                                + "We expected actions on nested state but didn't find any. Nested state name: " + nestedStateName + " related to action name: " + actionName);
                    }
                    currentStateConfig.getActionToStateConfigs().put(actionName, inlineState);
                    // TODO here we are dropping the action name / mapping to the inlined 
                }
            }
        }
    }

    public YamlStateConfig loadSubstateReference(String actionName, Map<String, Object> nestedStateConfig) {
        String subFlowRefOrState = (String) nestedStateConfig.get("subflow");
        if (subFlowRefOrState == null) {
            throw new FlowException("A subflow reference is required here. e.g.  {subflow: ItemInquiryFlow, ReturnAction: ItemInquiryFinished}");
        }
        Map<String, String> configScope = (Map<String, String>) nestedStateConfig.get("ConfigScope");

        YamlStateConfig subStateConfig = new YamlStateConfig(subFlowRefOrState);
        subStateConfig.setSubTransition(true);

        List<String> returnActions = getReturnActions(nestedStateConfig);
        subStateConfig.setReturnActions(returnActions);

        if (configScope != null) {
            subStateConfig.setConfigScope(configScope);
        }

        return subStateConfig;
    }

    protected List<String> getReturnActions(Map<String, Object> nestedStateConfig) {
        String returnActionSingle = (String) nestedStateConfig.get("ReturnAction");
        String returnActionsMultiple = (String) nestedStateConfig.get("ReturnActions");

        if (!StringUtils.isEmpty(returnActionSingle) && !StringUtils.isEmpty(returnActionsMultiple)) {
            throw new FlowException("Invalid flow config: both ReturnAction and ReturnActions (plural) defined "
                    + "for a substate. Only one or of the other can be specified. substae: " + nestedStateConfig);
        }

        String returnActions = null;
        if (!StringUtils.isEmpty(returnActionSingle)) {
            returnActions = returnActionSingle;
        } else {
            returnActions = returnActionsMultiple;
        }

        return Arrays.asList(returnActions.split(";")).stream().
                map(s -> s.trim()).collect(Collectors.toList());
    }
}
