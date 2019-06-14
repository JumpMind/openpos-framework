package org.jumpmind.pos.core.content;

import java.io.IOException;
import java.io.InputStream;

public interface IContentProvider {

    public String getContentUrl(String deviceId, String key);

    public InputStream getContentInputStream(String contentPath) throws IOException;

}
