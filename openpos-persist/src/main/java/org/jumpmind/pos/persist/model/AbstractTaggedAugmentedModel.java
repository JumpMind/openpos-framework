package org.jumpmind.pos.persist.model;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;

public class AbstractTaggedAugmentedModel extends AbstractTaggedModel implements IAugmentedModel {

    private Map<String, String> augments = new CaseInsensitiveMap<>();

    @Override
    public void setAugments(Map<String, String> augments) {
        this.augments = augments;
    }

    @Override
    public Map<String, String> getAugments() {
        return augments;
    }
}
