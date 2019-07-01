package org.jumpmind.pos.core.content;

import org.jumpmind.pos.core.flow.In;
import org.jumpmind.pos.core.flow.ScopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component("remoteContentProvider")
@Scope("device")
@ConfigurationProperties(prefix = "openpos.ui.content.remote")
public class RemoteContentProvider implements IContentProvider {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Map<String, List<String>>> urlsByKeyAndTag = new HashMap<>();
    private Map<String, InputStream> contentLookupMap = new HashMap<>();
    private List<String> propertiesForTags = new ArrayList<>();

    static Map<String, ContentIndex> deviceContent = new HashMap<>();

    @In(scope = ScopeType.Device, required = false)
    Map<String, String> personalizationProperties;

    @Override
    public String getContentUrl(String deviceId, String key) {
        if( !urlsByKeyAndTag.containsKey( key )){
            return null;
        }

        Map<String, Map<String, String>> configurations = new HashMap<>();
        List<List<String>> tagGroups = new ArrayList<>();
        List<String> tagsForSpecificity = new ArrayList<>();

        // Lookup the values for our tags
        propertiesForTags.forEach(s -> {
            if (personalizationProperties.containsKey(s)) {
                String value = personalizationProperties.get(s);
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

        // Look through each group of tags we created above and find the matching content entry
        List<String> urls = null;
        String tagKey = "";
        for(int i = 0; i <= tagGroups.size() - 1; ++i){
            List<String> tagGroup = tagGroups.get(i);
            tagKey = urlsByKeyAndTag.get(key).keySet().stream().filter( k -> tagGroup.stream().allMatch( t -> k.contains(t) )).findFirst().orElse(null);
            if( tagKey != null ) {
                urls = urlsByKeyAndTag.get(key).get(tagKey);
                break;
            }
        }

        // If we didn't find anything return null so we can fall back on another provider
        if( urls == null ) {
            return null;
        }

        Integer index = getDeviceIndex(deviceId, key, urls.size());

        String contentLookupKey = key+tagKey+index;

        try {
            URL url = new URL(urls.get(index));
            contentLookupMap.put(contentLookupKey, new BufferedInputStream(url.openStream()));
        } catch ( IOException e ) {
            // If our remote source fails return null so we can fall back on the next provider
            return null;
        }

        StringBuilder urlBuilder = new StringBuilder("${apiServerBaseUrl}/appId/${appId}/deviceId/${deviceId}/content?contentPath=");
        urlBuilder.append( contentLookupKey );
        urlBuilder.append("&provider=");
        urlBuilder.append("remoteContentProvider");

        return urlBuilder.toString();

    }

    @Override
    public InputStream getContentInputStream(String contentPath) throws IOException {
        return contentLookupMap.get(contentPath);
    }

    protected int getDeviceIndex(String deviceId, String key, int size) {
        Integer index = null;
        ContentIndex contentIndex = deviceContent.get(deviceId);

        if (contentIndex == null) {
            contentIndex = new ContentIndex(key, (1 % size));
            deviceContent.put(deviceId, contentIndex);
            index = 0;

        } else {
            index = contentIndex.getIndex(key);
            if (index == null) {
                contentIndex.setIndex(key, (1 % size));
                index = 0;
            } else {
                contentIndex.setIndex(key, ((index + 1) % size));
                index %= size;
            }
        }

        return index;
    }

    public class ContentIndex {
        private Map<String, Integer> contentIndex;

        private ContentIndex() {
            contentIndex = new HashMap<>();
        }

        private ContentIndex(String resource, int index) {
            contentIndex = new HashMap<>();
            contentIndex.put(resource, index);
        }

        private void setIndex(String key, int index) {
            contentIndex.put(key, index);
        }

        private Integer getIndex(String key) {
            return contentIndex.get(key);
        }
    }


    public List<String> getPropertiesForTags() {
        return propertiesForTags;
    }

    public void setPropertiesForTags(List<String> propertiesForTags) {
        this.propertiesForTags = propertiesForTags;
    }

    public Map<String, Map<String, List<String>>> getUrlsByKey() {
        return urlsByKeyAndTag;
    }

    public void setUrlsByKey(Map<String, Map<String, List<String>>> urlsByKey) {
        this.urlsByKeyAndTag = urlsByKey;
    }


}
