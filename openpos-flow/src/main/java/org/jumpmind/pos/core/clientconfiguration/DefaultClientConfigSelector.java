package org.jumpmind.pos.core.clientconfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.Array;
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

        List<List<String>> uniquePermuations = uniqueTagGroupCombinations(tagsForSpecificity);
        //sort so that they are in order of least specificity to most specificity ("a" comes before "a, b, c")
        uniquePermuations.sort(Comparator.comparingInt(List::size));
        tagGroups.addAll(uniquePermuations);

        Map<List<String>, Map<String, Map<String,String>>> clientConfigsByTagsAndName = new HashMap<>();
        clientConfigSets.forEach(clientConfigurationSet -> {
            clientConfigurationSet.getTags().sort(String::compareTo);
            clientConfigsByTagsAndName.put(clientConfigurationSet.getTags(), clientConfigurationSet.getConfigsForTags());
        });

        tagGroups.forEach(tags -> {
            if( clientConfigsByTagsAndName.containsKey(tags) && clientConfigsByTagsAndName.get(tags) != null){
                configurations.putAll(clientConfigsByTagsAndName.get(tags));
            }
        });

        return configurations;
    }

    public static List<List<String>> uniqueTagGroupCombinations(List<String> tags) {
        List<List<String>> results = new ArrayList<List<String>>();
        for(int i = 0; i < tags.size(); i++) {
            int resultsLength = results.size();
            for(int j = 0; j < resultsLength; j++) {
                List<String> newList = new ArrayList<>(results.get(j));
                newList.add(tags.get(i));
                results.add(newList);
            }
            results.add(Arrays.asList(tags.get(i)));
        }
        return results;
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
