package org.jumpmind.pos.persist;

import java.util.Map;

public interface EntityId {

    default Map<String, Object> getIdFields() {
        return null;
    }
    
}
