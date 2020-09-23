package org.jumpmind.pos.persist.model;

import java.util.Map;

public interface IAugmentedModel {

    default String getAugmentValue(String augmentName) {
        return getAugments().get(augmentName);
    }

    default void setAugmentValue(String augmentName, String value) {
        getAugments().put(augmentName, value);
    }

    default void clearAugmentValue(String augmentName) {
        getAugments().remove(augmentName);
    }

    void setAugments(Map<String, String> augments);

    Map<String, String> getAugments();
}
