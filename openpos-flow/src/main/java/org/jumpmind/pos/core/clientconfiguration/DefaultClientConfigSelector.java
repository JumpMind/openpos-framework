package org.jumpmind.pos.core.clientconfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

import java.util.*;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "openpos.client-configuration")
@Scope("prototype")
public class DefaultClientConfigSelector implements IClientConfigSelector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<String> propertiesForTags = new ArrayList<>();
    private List<ClientConfigurationSet> clientConfigSets = new ArrayList<>();

    @Override
    public Map<String, Map<String, String>> getConfigurations(Map<String, String> properties, List<String> additionalTags) {

        Map<String, Map<String, String>> configurations = new HashMap<>();
        List<List<String>> tagGroups = new ArrayList<>();
        List<String> tagsForSpecificity = new ArrayList<>();

        if( additionalTags != null ) {
            // Pass along all the additional tags
            tagsForSpecificity.addAll(additionalTags);
        }

        // Lookup the values for our tags
        propertiesForTags.forEach(s -> {
            if (properties.containsKey(s)) {
                String value = properties.get(s);
                if (value != null) {
                    tagsForSpecificity.add(value);
                }
            } else {
                logger.warn("Could not find personalization parameter {}", s);
            }
        });

        tagsForSpecificity.sort(String::compareTo);

        // Start with default
        tagGroups.add(Arrays.asList("default"));

        // Add each tag individually
        tagGroups.addAll(tagsForSpecificity.stream().map(Arrays::asList).collect(Collectors.toList()));

        // Create Permutations (tag3, tag2) (tag2, tag1) (tag3, tag2, tag1)
        for (int i = tagsForSpecificity.size() - 1; i >= 0; i--) {
            for (int j = i+1; j < tagsForSpecificity.size(); j++) {
                tagGroups.add(tagsForSpecificity.subList(i, j));
            }
        }

        Map<List<String>, Map<String, Map<String,String>>> clientConfigsByTagsAndName = new HashMap<>();
        clientConfigSets.forEach(clientConfigurationSet -> {
            clientConfigurationSet.getTags().sort(String::compareTo);
            clientConfigsByTagsAndName.put(clientConfigurationSet.getTags(), clientConfigurationSet.getConfigsForTags());
        });

        tagGroups.forEach(tags -> {
            if( clientConfigsByTagsAndName.containsKey(tags)){
                configurations.putAll(clientConfigsByTagsAndName.get(tags));
            }
        });

        return configurations;
    }

    public List<ClientConfigurationSet> getClientConfigSets() {
        return clientConfigSets;
    }

    public void setClientConfigSets(List<ClientConfigurationSet> clientConfigSets) {
        this.clientConfigSets = clientConfigSets;
    }

    public List<String> getPropertiesForTags() {
        return propertiesForTags;
    }

    public void setPropertiesForTags(List<String> propertiesForTags) {
        this.propertiesForTags = propertiesForTags;
    }
}
