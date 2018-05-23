package org.jumpmind.pos.context.model;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jumpmind.pos.context.ContextException;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Repository
@DependsOn(value = { "ContextModule" })
public class ContextRepository {

    private static Logger log = Logger.getLogger(ContextRepository.class);

    protected static final int DISQUALIFIED = Integer.MIN_VALUE;

    private Query<ConfigModel> configLookup = new Query<ConfigModel>()
            .named("configLookup")
            .result(ConfigModel.class);
    
    private static TagConfig tagConfig;

    @Autowired
    @Lazy
    DBSession contextSession;
    
//    @PostConstruct
//    public void init() {
//        TagConfig tagConfig = getTagConfig();
//        System.out.println(tagConfig);
//    }

    public Node findNode(String nodeId) {
        Node node = contextSession.findByNaturalId(Node.class, nodeId);
        if (node != null) {
            for (String columnName : node.getAdditionalFields().keySet()) {
                String columnUpper = columnName.toUpperCase();
                if (columnUpper.startsWith(TagModel.TAG_PREFIX)) {
                    Object value = node.getAdditionalFields().get(columnName);
                    node.setTagValue(columnUpper, value != null ? value.toString() : null);
                }
            }
        }
        return node;
    }

    public ConfigModel findConfigValue(Date currentTime, Map<String, String> tags, String configName) {

        Map<String, Object> params = new HashMap<>();
        params.put("configName", configName);
        params.put("currentTime", currentTime);
        List<ConfigModel> configs = contextSession.query(configLookup, params);
        if (CollectionUtils.isEmpty(configs)) {
            if (log.isDebugEnabled()) {                
                log.debug("No configuration found for " + configName);
            }
            return null;
        }

        ConfigModel config = findMostSpecificConfig(tags, configs);
        if (config != null) {
            return config;
        } else {            
            log.debug("No matching configuration found for " + configName + " and tags " + tags);
            return null;
        }
    }
    
    public static TagConfig getTagConfig() {
        if (tagConfig == null) {            
            try {
                URL url = Thread.currentThread().getContextClassLoader().getResource("tags.yaml");
                if (url != null) {
                    log.info(String.format("Loading %s...", url.toString()));
                    InputStream queryYamlStream = url.openStream();
                    tagConfig = new Yaml(new Constructor(TagConfig.class)).load(queryYamlStream);
                    return tagConfig;
                } else {
                    log.info("Could not locate tags.yaml on the classpath.");
                    tagConfig = new TagConfig();
                    return tagConfig;
                }
            } catch (Exception ex) {
                throw new ContextException("Failed to load tags.yaml", ex);
            }
        } else {
            return tagConfig;
        }
    }    

    protected ConfigModel findMostSpecificConfig(Map<String, String> tags, List<ConfigModel> configs) {
        //        if (tags == null) {
        //            throw new ContextException("tags cannot be null");
        //        }
        //        if (configs == null) {
        //            throw new ContextException("configs cannot be null");
        //        }
        //        
        //        ConfigModel bestMatchConfig = null;
        //        
        //        int maxMatchScore = -1;
        //        
        //        for (ConfigModel config : configs) {
        //            int matchScore = 0; 
        //            if (config.getLocationType() != null) {
        //                if (!matchLocation(config, tags)) {
        //                    continue;
        //                }
        //            }
        //            matchScore += evaluateLocation(config);
        //            matchScore += evaluateTag(config.getBrandId(), tags.get(TAG_BRAND_ID), TAG_BRAND_ID, 1000);
        //            matchScore += evaluateTag(config.getStoreType(), tags.get(TAG_STORE_TYPE), TAG_STORE_TYPE, 500);
        //            matchScore += evaluateTag(config.getDepartmentId(), tags.get(TAG_DEPARTMENT_ID), TAG_DEPARTMENT_ID, 250);
        //            matchScore += evaluateTag(config.getDeviceType(), tags.get(TAG_DEVICE_TYPE), TAG_DEVICE_TYPE, 50);
        //            
        //            if (matchScore > maxMatchScore) {
        //                bestMatchConfig = config;
        //                maxMatchScore = matchScore;
        //            }
        //        }
        //        
        //        if (bestMatchConfig == null) {
        //            return null;
        //        } else {
        //            return bestMatchConfig;
        //        } 

        return null;
    }
    //    
    //    protected int evaluateLocation(ConfigModel config) {
    //        if (config.getLocationType() == null) {
    //            return 0;
    //        }
    //        
    //        switch (config.getLocationType()) {
    //            case REGION:
    //                return 1;
    //            case COUNTRY:
    //                return 2;
    //            case STATE:
    //                return 3;
    //            case STORE:
    //                return 4;
    //            case NODE_ID:
    //                return 5;                
    //        }
    //        return 0;
    //    }
    //
    //    protected boolean matchLocation(ConfigModel config, Map<String, String> tags) {
    //        String actualLocationValue = tags.get(config.getLocationType().name());
    //        
    //        if (StringUtils.isEmpty(actualLocationValue)) {
    //            throw new ContextException("Can't find tag value for location type: " + config.getLocationType() + " in tags: " + tags);
    //        }
    //        
    //        return actualLocationValue.equals(config.getLocationValue());
    //    }
    //
    //    protected int evaluateTag(String configValue, String nodeValue, final String tagName, int points) {
    //        if (nodeValue != null) {
    //            if (StringUtils.isEmpty(configValue) || configValue.equals(ConfigModel.TAG_ALL)) {
    //                return 0;
    //            } else if (configValue.equals(nodeValue)) {
    //                return points;
    //            } else {
    //                return DISQUALIFIED;
    //            }
    //        } else {
    //            return 0;
    //        }
    //    }

}
