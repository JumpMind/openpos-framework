package org.jumpmind.pos.persist.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix="openpos.augmenterconfigs")
public class AugmenterConfigs {
    private List<AugmenterConfig> configs;

    public List<String> getConfigsNames() {
        return Optional.ofNullable(configs).orElse(Collections.emptyList())
                .stream()
                .map(AugmenterConfig::getName)
                .collect(Collectors.toList());
    }

    public List<AugmenterConfig> getConfigsByNames(String... names) {
        return Optional.ofNullable(configs).orElse(Collections.emptyList())
                .stream()
                .filter(config -> Arrays.stream(names).anyMatch(g -> g.equals(config.getName())))
                .collect(Collectors.toList());
    }

    public AugmenterConfig getConfigByName(String name) {
        return getConfigsByNames(name).stream().findFirst().orElse(null);
    }
}
