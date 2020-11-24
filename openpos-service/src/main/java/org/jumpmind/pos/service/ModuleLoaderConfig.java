package org.jumpmind.pos.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleLoaderConfig {
    private Set<String> includes;
    private Set<String> excludes;

    public boolean hasIncludes() {
        return CollectionUtils.isNotEmpty(includes);
    }

    public boolean hasExcludes() {
        return CollectionUtils.isNotEmpty(excludes);
    }

    public boolean includes(String name) {
        return hasIncludes() && includes.contains(name);
    }

    public boolean excludes(String name) {
        return hasExcludes() && excludes.contains(name);
    }
}
