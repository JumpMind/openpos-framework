package org.jumpmind.pos.app.state;

import static org.jumpmind.pos.context.model.TagModel.BRAND_ID_TAG;

import org.jumpmind.pos.context.model.Node;
import org.jumpmind.pos.context.service.ContextService;
import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.flow.IState;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.In;
import org.jumpmind.pos.core.flow.InOut;
import org.jumpmind.pos.core.flow.ScopeType;
import org.jumpmind.pos.i18n.service.i18nService;
import org.springframework.beans.factory.annotation.Autowired;

abstract public class AbstractState implements IState {

    @In(scope = ScopeType.Node)
    protected IStateManager stateManager;

    @Autowired
    protected i18nService i18nService;

    @Autowired
    protected ContextService contextService;

    @InOut(scope = ScopeType.Node)
    protected Node node;

    @Override
    public void arrive(Action action) {
        if (node == null) {
            String nodeId = stateManager.getNodeId();
            this.node = contextService.getNode(nodeId).getNode();
            if (this.node == null) {
                throw new IllegalStateException(String.format("Could not find a row in ctx_node with an id of %s.  It is required", nodeId));
            }
        }
    }

    protected String getStoreId() {
        String nodeId = stateManager.getNodeId();
        String[] parts = nodeId.split("-");
        return parts[0];
    }

    protected String getWorstationId() {
        String nodeId = stateManager.getNodeId();
        String[] parts = nodeId.split("-");
        return parts[1];
    }

    protected abstract String getDefaultBundleName();

    protected String resource(String key) {
        return i18nService.getString(getDefaultBundleName(), key, node.getLocale(), node.getTagValue(BRAND_ID_TAG));
    }

    protected String commonResource(String key) {
        return i18nService.getString("common", key, node.getLocale(), node.getTagValue(BRAND_ID_TAG));
    }

}
