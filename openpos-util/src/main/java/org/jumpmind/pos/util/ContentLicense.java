package org.jumpmind.pos.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentLicense {
    private String key;
    private String author;
    private String title;
    private String source;
    private String filename;
    private String license;
    private String licenseUri;
}
