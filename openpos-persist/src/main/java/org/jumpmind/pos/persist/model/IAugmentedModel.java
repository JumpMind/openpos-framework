package org.jumpmind.pos.persist.model;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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

    default void addAugments(Map<String, String> augments) {
        Optional.ofNullable(augments).orElse(Collections.emptyMap()).entrySet().stream().forEach(entry -> getAugments().put(entry.getKey(), entry.getValue()));
    }

    void setAugments(Map<String, String> augments);

    Map<String, String> getAugments();
}
