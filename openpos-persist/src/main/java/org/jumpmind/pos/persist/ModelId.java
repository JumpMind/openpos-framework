package org.jumpmind.pos.persist;

import lombok.Builder;
import lombok.Singular;

import java.util.HashMap;
import java.util.Map;

@Builder
public class ModelId {

    @Singular
    protected Map<String, Object> keys = new HashMap<>();

    public ModelId(Object... values) {
        if (values != null && values.length > 1) {
            for (int i = 0; i < values.length; i = i + 2) {
                String key = values[i].toString();
                if (values.length > i + 1) {
                    Object value = values[i + 1];
                    keys.put(key, value);
                }
            }
        }
    }

    public Map<String, Object> getIdFields() {
        return keys;
    }

}
