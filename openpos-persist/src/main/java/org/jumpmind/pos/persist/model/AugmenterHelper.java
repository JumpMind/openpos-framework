package org.jumpmind.pos.persist.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.Augmented;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@Getter
@Setter
public class AugmenterHelper {

    @Autowired
    private AugmenterConfigs augmenterConfigs;

    public AugmenterConfig getAugmenterConfig(String name) {
        return augmenterConfigs.getConfigByName(name);
    }

    public AugmenterConfig getAugmenterConfig(Object object) {
        return getAugmenterConfig(object.getClass());
    }

    public AugmenterConfig getAugmenterConfig(Class<?> clazz) {
        Augmented augmented = clazz.getAnnotation(Augmented.class);
        if (augmented != null) {
            return augmenterConfigs.getConfigByName(augmented.name());
        }
        return null;
    }

    public void addAugments(IAugmentedModel augmentedModel, Map<String, Object> fields) {
        if (augmentedModel != null) {
            Map<String, String> augments = additionalFieldsToAugments(augmentedModel, fields);
            augmentedModel.setAugments(augments);
        }
    }

    protected Map<String, String> additionalFieldsToAugments(IAugmentedModel augmentedModel, Map<String, Object> additionalFields) {
        Map<String, String> augments = new CaseInsensitiveMap<>();
        AugmenterConfig config = getAugmenterConfig(augmentedModel);
        if (config == null) {
            log.warn("Missing augmenter config for class " + augmentedModel.getClass().getSimpleName());
            return Collections.emptyMap();
        }
        for (String columnName : additionalFields.keySet()) {
            String columnUpper = columnName.toUpperCase();
            if (columnUpper.startsWith(config.getPrefix())) {
                Object value = additionalFields.get(columnName);
                String augmentName = columnUpper.substring(config.getPrefix().length());
                augments.put(augmentName, Objects.toString(value ,null));
            }
        }

        return augments;
    }

    public Object getDefaultValue(String fieldName, AbstractModel model) {
        AugmenterConfig config = getAugmenterConfig(model);
        if (config != null && fieldName != null && fieldName.length() > config.getPrefix().length()) {
            AugmenterModel augmenter = config.getAugmenter(fieldName.substring(config.getPrefix().length()));
            if (augmenter != null) {
                return augmenter.getDefaultValue();
            }
            else {
                log.info("No augmenter found for field name " + fieldName + " in AugmenterConfig " + config);
            }
        }
        return null;
    }

    public Map<String, Object> getParametersForAugmentedModel(IAugmentedModel augmentedModel, Class<? extends IAugmentedModel> clazz) {
        Map<String, Object> params = new HashMap<>();
        AugmenterConfig config = getAugmenterConfig(clazz);
        if (config != null && config.getAugmenters() != null) {
            int counter = 1;
            for (AugmenterModel augmenter : config.getAugmenters()) {
                String columnValue = augmentedModel.getAugmentValue(augmenter.getName());
                String columnName = config.getPrefix() + augmenter.getName();
                String columnNameKey = String.format("augment%dColumnName", counter);
                String columnValueKey = String.format("augment%dValue", counter);

                params.put(columnNameKey, columnName);
                params.put(columnValueKey, columnValue);
                counter++;
            }
        }
        return params;
    }
}
