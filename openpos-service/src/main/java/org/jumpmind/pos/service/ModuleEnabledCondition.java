package org.jumpmind.pos.service;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Map;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ModuleEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        boolean include = !env.containsProperty("openpos.modules.include[0]");
        if (env != null) {
            for (int i = 0; i < 50; i++) {
                String includedModules = env.getProperty(String.format("openpos.modules.include[%d]", i));
                if (isNotBlank(includedModules)) {
                    Map<String, Object> attrs = metadata.getAnnotationAttributes(Configuration.class.getName());
                    include = includedModules.contains((String) attrs.get("value"));
                }

                String excludedModules = env.getProperty(String.format("openpos.modules.exclude[%d]", i));
                if (isNotBlank(excludedModules)) {
                    Map<String, Object> attrs = metadata.getAnnotationAttributes(Configuration.class.getName());
                    include &= !excludedModules.contains((String) attrs.get("value"));
                }
            }

        }
        return include;
    }

}