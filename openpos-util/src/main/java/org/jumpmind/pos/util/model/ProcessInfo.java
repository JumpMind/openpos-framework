package org.jumpmind.pos.util.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProcessInfo {
    public static final String ALIVE_STATUS = "alive";

    private String status;
    private Integer port;
    private Integer pid;
}
