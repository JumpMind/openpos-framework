package org.jumpmind.pos.persist.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TagHelper {

    protected static final int DISQUALIFIED = Integer.MIN_VALUE;
    
    @Autowired
    private TagConfig tagConfig;
    
    @SuppressWarnings("unchecked")
    public <T extends ITaggedModel> T getMostSpecific(List<? extends ITaggedModel> taggedElements, Map<String, String> specifiedTags) {
        
        List<TagValue> tagValues = buildTagValues(specifiedTags, tagConfig);
        
        int bestMatchScore = -1;
        ITaggedModel mostSpecific = null;

        String clazzName = taggedElements.size() > 0 ? taggedElements.get(0).getClass().getSimpleName() : null;
        if (clazzName != null) {
            log.debug("Calculating most specific for {}.  There are {} options.  Tag values are: {}", clazzName, taggedElements.size(), tagValues);
        }
        
        for (ITaggedModel taggedElement : taggedElements) {
            int score = 0;
            log.debug("Element has the following tags: {}", taggedElement.getTags());
            for (TagValue tagValue : tagValues) {
                String taggedElementValue = taggedElement.getTagValue(tagValue.tagName);
                if (taggedElementValue == null 
                        || StringUtils.equals(taggedElementValue, TagModel.TAG_ALL)) {
                    continue;
                } else if (StringUtils.equals(taggedElementValue, tagValue.tagValue)) {
                    score += tagValue.weight;
                } else {
                    score = DISQUALIFIED;
                    break;
                }
            }
            
            if (score > bestMatchScore) {
                mostSpecific = taggedElement;
                bestMatchScore = score;
            }
        }
        if (mostSpecific != null) {
            log.debug("Most specific has the following tags: {}", mostSpecific.getTags());
        }
        return (T)mostSpecific;
    }

    protected List<TagValue> buildTagValues(Map<String, String> specifiedTags, TagConfig tagConfig) {
        Map<String, List<TagModel>> tagsByGroup = tagConfig.getTagsByGroup();
        List<TagValue> tagValues = new ArrayList<>(); 
        
        tagsByGroup.values().forEach(tagDefinitions -> {
            int counter = 1;
            for (TagModel tagDefinition : tagDefinitions) {                
                TagValue tagValue = new TagValue();
                tagValue.tagName = tagDefinition.getName();
                tagValue.tagValue = specifiedTags.get(tagValue.tagName);
                tagValue.weight = counter++; 
                tagValues.add(tagValue);
            }
        });
        return tagValues;
    }

    @ToString
     class TagValue {
        private String tagName;
        private String tagValue;
        private int weight;
    }
     
     public void addTags(ITaggedModel taggedElement, Map<String, Object> fields) {
         if (taggedElement != null) {
             Map<String, String> tags = additionalFieldsToTags(fields);
             taggedElement.setTags(tags);
         }        
     }
     
     public Map<String, Object> getParamsToQueryTaggedModel(Date currentTime,  Map<String, String> tags) {
         Map<String, Object> params = new LinkedHashMap<>();
         params.put("currentTime", currentTime);
         
         int counter = 1;
         for (String tagName : tags.keySet()) {
             String tagValue = tags.get(tagName);
             String tagColumnName = TagModel.TAG_PREFIX + tagName; 
             String tagColumnNameKey = String.format("tag%dColumnName", counter);
             String tagColumnValueKey = String.format("tag%dValue", counter);
             
             params.put(tagColumnNameKey, tagColumnName);
             params.put(tagColumnValueKey, tagValue);
             
             counter++;
         }
         
         return params;
     }
     
     protected Map<String, String> additionalFieldsToTags(Map<String, Object> additionalFields) {
         Map<String, String> tags = new CaseInsensitiveMap<>();
         for (String columnName : additionalFields.keySet()) {
             String columnUpper = columnName.toUpperCase();
             if (columnUpper.startsWith(TagModel.TAG_PREFIX)) {
                 Object value = additionalFields.get(columnName);
                 String tagName = columnUpper.substring(TagModel.TAG_PREFIX.length());
                 tags.put(tagName, value.toString());
             }
         }
         
         return tags;
     }

    public TagConfig getTagConfig() {
        return tagConfig;
    }

    public void setTagConfig(TagConfig tagConfig) {
        this.tagConfig = tagConfig;
    }
     

    
}
