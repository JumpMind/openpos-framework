package org.jumpmind.pos.persist.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AugmenterModel {
    private String name;
    private String defaultValue;
    private Integer size;
}
