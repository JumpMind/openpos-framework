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
package org.jumpmind.jumppos.pos.state;

import java.util.List;
import java.util.Map;

import org.jumpmind.jumppos.core.flow.Action;
import org.jumpmind.jumppos.core.flow.ActionHandler;
import org.jumpmind.jumppos.core.flow.IState;
import org.jumpmind.jumppos.core.flow.IStateManager;
import org.jumpmind.jumppos.core.model.Form;
import org.jumpmind.jumppos.core.model.FormButton;
import org.jumpmind.jumppos.core.model.FormField;
import org.jumpmind.jumppos.core.model.IScreen;
import org.jumpmind.jumppos.core.model.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NodePersonalizationState implements IState {

    Logger logger = LoggerFactory.getLogger(getClass());

    static final String TEMPORARY_NODE_ID = "TEMPNODEID-";

    @Autowired
    IStateManager stateManager;

    @Override
    public void arrive() {
        if (stateManager.getNodeId().startsWith(TEMPORARY_NODE_ID)) {
            stateManager.showScreen(buildParams());
        } else {
            stateManager.doAction(new Action("Complete"));
        }
    }

    protected IScreen buildParams() {
        IScreen screen = new Screen(){};
        screen.setName("NodePersonalization");        
        screen.put("form", buildForm());
        screen.setType(IScreen.FORM_SCREEN_TYPE);
        return screen;
    }

    protected Form buildForm() {
        Form form = new Form();
        {
            FormField field = new FormField();
            field.setFieldId("nodeId");
            field.setLabel("Node Id:");
            field.setPlaceholder("e.g. 100-1");
            form.getFormElements().add(field);
        }
        {
            FormButton button = new FormButton();
            button.setLabel("Save");
            button.setButtonAction("SavePersonalization");
            form.addFormElement(button);
        }
        return form;
    }

    @SuppressWarnings("unchecked")
    @ActionHandler
    public void onSavePersonalization(Action action) {
        // TODO need some binding/form model support here.
        // Map<String, Object> formJson = (Map<String, Object>)action.getData();
        List<Map<String, Object>> formActions = (List<Map<String, Object>>) action.getData();
        String nodeId = (String) formActions.get(0).get("value");
        // TODO validate.
        stateManager.setNodeId(nodeId);
        stateManager.doAction(new Action("Complete"));
    }
}
