package org.jumpmind.pos.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AudioLicense {
    private String key;
    private String author;
    private String title;
    private String sourceUri;
    private String filename;
    private String license;
    private String licenseUri;
    private String comments;
}
