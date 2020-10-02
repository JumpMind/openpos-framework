package org.jumpmind.pos.persist.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AugmenterIndexConfig {
    private String name;
    private boolean unique;
    private List<String> columnNames;
}
