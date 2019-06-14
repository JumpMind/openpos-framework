package org.jumpmind.pos.core.model;

import java.util.List;

public interface IDynamicListField {
    default public void setDynamicListEnabled(boolean enabled) {}
    default public boolean isDynamicListEnabled() { return true; }
    public List<String> searchValues(String searchTerm, Integer sizeLimit);
}
