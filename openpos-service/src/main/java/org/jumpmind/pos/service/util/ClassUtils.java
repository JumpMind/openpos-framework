package org.jumpmind.pos.service.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.service.model.ModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public final class ClassUtils {
    
    private ClassUtils() {
    }
    
    protected static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    public static List<Class<?>> getClassesForPackageAndAnnotation(String packageName, Class<? extends Annotation> annotation) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(ModuleModel.class);
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
        for (BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
            try {
                final Class<?> clazz = Class.forName(bd.getBeanClassName());
                classes.add(clazz);
            } catch (ClassNotFoundException ex) {
                logger.error(ex.getMessage());
            }
        }
        return classes;
    }
}
