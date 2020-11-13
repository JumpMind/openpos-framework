package org.jumpmind.pos.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;

public final class ResourceUtils {
    public static final String CLASS_PATH_ROOT = "classpath*:/";
    public static final String CONTENT_ROOT = "content/";

    public static Resource[] getResources(String locationPattern) throws IOException {
        return createResolver().getResources(CLASS_PATH_ROOT + locationPattern);
    }

    public static Resource getResource(String locationPattern) throws IOException {
        return Arrays.stream(getResources(locationPattern)).findFirst().orElse(null);
    }

    public static Resource[] getContentResources(String locationPattern) throws IOException {
        return getResources(CONTENT_ROOT + locationPattern);
    }

    public static Resource getContentResource(String locationPattern) throws IOException {
        return Arrays.stream(getContentResources(locationPattern)).findFirst().orElse(null);
    }

    private static ResourcePatternResolver createResolver() {
        ClassLoader classLoader = ResourceUtils.class.getClassLoader();
        return new PathMatchingResourcePatternResolver(classLoader);
    }
}
