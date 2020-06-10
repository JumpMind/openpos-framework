package org.jumpmind.pos.core.ui.messagepart;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
public class ScanPart implements Serializable {
    private static final long serialVersionUID = 1L;

    private String scanActionName = "Scan";

    public ScanPart(String scanActionName) {
        this.scanActionName = scanActionName;
    }

}
