package org.jumpmind.pos.persist.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AugmenterConfig {
    private String name;
    private String prefix;
    private List<AugmenterModel> augmenters;

    public AugmenterModel getAugmenter(String name) {
        return Optional.ofNullable(augmenters).orElse(Collections.emptyList()).stream()
                .filter(a -> StringUtils.equalsIgnoreCase(a.getName(), name))
                .findFirst()
                .orElse(null);
    }

    public List<String> getAugmenterNames() {
        return Optional.ofNullable(augmenters).orElse(Collections.emptyList()).stream()
                .map(AugmenterModel::getName)
                .collect(Collectors.toList());
    }
}
